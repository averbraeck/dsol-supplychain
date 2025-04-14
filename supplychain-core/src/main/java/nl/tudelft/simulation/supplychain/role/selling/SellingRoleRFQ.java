package nl.tudelft.simulation.supplychain.role.selling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.InventoryQuote;
import nl.tudelft.simulation.supplychain.content.InventoryReservation;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.QuoteNo;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.TransportQuote;
import nl.tudelft.simulation.supplychain.content.TransportQuoteRequest;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Sku;
import nl.tudelft.simulation.supplychain.role.transporting.TransportMode;
import nl.tudelft.simulation.supplychain.role.transporting.TransportOptionStep;

/**
 * The selling role is a role that can handle several types of message content: order and payment in the minimum form. Depending
 * on the type of handling by the seller, several other messages can be handled as well. This version of the role handles the
 * seling of a product based on a RFQ-Quote process.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SellingRoleRFQ extends SellingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /** whether to send negative quotes or not. */
    private boolean sendNegativeQuotes = false;

    /** quote validity time. */
    private Duration quoteValidityTime = new Duration(48.0, DurationUnit.HOUR);

    /** the RFQs for which transport quote requests have been sent. */
    private Map<RequestForQuote, QuoteData> quoteDataMap = new LinkedHashMap<>();

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers = Set.of(RequestForQuote.class, InventoryQuote.class,
            TransportQuote.class, Order.class, InventoryReservation.class);

    /**
     * Constructs a new SellingRole for RFQ - Order - Payment.
     * @param owner the owner this role
     */
    public SellingRoleRFQ(final SellingActor owner)
    {
        super(owner);
    }

    /**
     * Add a record for storing transport quotes till they are all in, or the cutoff date is met.
     * @param iq the inventory quote indicating inventory is available
     * @param cutoffDate the cutoff date at which transport will be evaluated
     */
    public void addTransportQuoteRequestRecord(final InventoryQuote iq, final Time cutoffDate)
    {
        var quoteData = new QuoteData(iq.inventoryQuoteRequest().rfq(), iq, new ArrayList<>(), new ArrayList<>(), cutoffDate);
        this.quoteDataMap.put(quoteData.rfq, quoteData);
        getSimulator().scheduleEventAbs(cutoffDate, this, "checkTransportQuotes", new Object[] {quoteData});
    }

    /**
     * Add a sent transport quote request.
     * @param transportQuoteRequest the sent transport quote request
     */
    public void addSentTransportRequestQuote(final TransportQuoteRequest transportQuoteRequest)
    {
        var rfq = transportQuoteRequest.rfq();
        if (this.quoteDataMap.containsKey(rfq))
        {
            var quoteData = this.quoteDataMap.get(rfq);
            quoteData.transportQuoteRequestList.add(transportQuoteRequest);
        }
    }

    /**
     * Add an incoming transport quote. Check whether the QuoteDataRecord still exists.
     * @param transportQuote the incoming transport quote
     */
    public void addReceivedTransportQuote(final TransportQuote transportQuote)
    {
        var rfq = transportQuote.transportQuoteRequest().rfq();
        if (this.quoteDataMap.containsKey(rfq))
        {
            var quoteData = this.quoteDataMap.get(rfq);
            quoteData.transportQuoteList.add(transportQuote);
        }
    }

    /**
     * Check the received transport quotes and respond with a Quote to the RFQ if there is at least one.
     * @param quoteData the quote data to check for a valid transport quote.
     */
    protected void checkTransportQuotes(final QuoteData quoteData)
    {
        var rfq = quoteData.rfq();
        if (quoteData.transportQuoteList().size() == 0)
        {
            // no quotes.
            if (isSendNegativeQuotes())
            {
                var quoteNo = new QuoteNo(rfq);
                sendContent(quoteNo, Duration.ZERO);
            }
        }

        // quotes came in. First check the preferred transport modes
        List<TransportQuote> options = new ArrayList<>();
        for (var tq : quoteData.transportQuoteList)
        {
            // the main mode of transport is the transport step with the longest distance
            TransportMode mode = tq.transportOption().getTransportSteps().stream()
                    .max(Comparator.comparing(TransportOptionStep::getTransportDistance)).get().getTransportMode();
            if (rfq.transportPreference().preferredTransportModes().size() == 0
                    || rfq.transportPreference().preferredTransportModes().contains(mode))
            {
                options.add(tq);
            }
        }
        if (options.size() == 0)
        {
            // no match on mode -- restore the 'unwanted' options
            options.addAll(quoteData.transportQuoteList);
        }

        TransportQuote bestTransportQuote = null;
        if (options.size() == 1)
        {
            // only on option
            bestTransportQuote = options.get(0);
        }
        else
        {
            Sku sku = rfq.product().getSku();
            double min = Double.MAX_VALUE;
            for (var tq : quoteData.transportQuoteList)
            {
                double value = switch (rfq.transportPreference().importance())
                {
                    case COST -> tq.transportOption().estimatedTotalTransportCost(sku).getAmount();
                    case TIME -> tq.transportOption().estimatedTotalTransportDuration(sku).si;
                    case DISTANCE -> tq.transportOption().totalTransportDistance().si;
                    case NONE -> tq.transportOption().estimatedTotalTransportCost(sku).getAmount();
                };
                if (value < min)
                {
                    min = value;
                    bestTransportQuote = tq;
                }
            }
            // we have a best transport quote, and an inventory quote; determine the profit margin.
            double profitMargin = getActor().getDirectingRoleSelling().getProfitMargin(rfq.product());
            Money totalPrice = quoteData.inventoryQuote.priceWithoutProfit().plus(bestTransportQuote.price())
                    .multiplyBy(1.0 + profitMargin);
            Duration qvt = Duration.max(getQuoteValidityTime(), getSimulatorTime().minus(rfq.cutoffDate()));
            var quote = new Quote(rfq, totalPrice, rfq.earliestDeliveryDate(), bestTransportQuote.transportOption(),
                    getSimulatorTime().plus(qvt));
            sendContent(quote, Duration.ZERO);
        }

        // remove the record -- late transport quotes are void
        this.quoteDataMap.remove(rfq);
    }

    /**
     * Return whether to send negative quotes or not.
     * @return whether to send negative quotes or not
     */
    public boolean isSendNegativeQuotes()
    {
        return this.sendNegativeQuotes;
    }

    /**
     * Set whether to send negative quotes or not.
     * @param sendNegativeQuotes set whether to send negative quotes or not
     */
    public void setSendNegativeQuotes(final boolean sendNegativeQuotes)
    {
        this.sendNegativeQuotes = sendNegativeQuotes;
    }

    /**
     * @return quoteValidityTime
     */
    protected Duration getQuoteValidityTime()
    {
        return this.quoteValidityTime;
    }

    /**
     * @param quoteValidityTime set quoteValidityTime
     */
    protected void setQuoteValidityTime(final Duration quoteValidityTime)
    {
        this.quoteValidityTime = quoteValidityTime;
    }

    @Override
    public String getId()
    {
        return getActor().getName() + "-SELLING(RFQ)";
    }

    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }

    /**
     * The record containing the information to store transport quotes till they are all in.
     * @param rfq the RFQ to which the data record belongs
     * @param inventoryQuote the inventory quote that indicates inventory will be available
     * @param transportQuoteRequestList the sent requests for transport
     * @param transportQuoteList the received quotes for transport
     * @param cutoffDate the cutoff date at which transport will be evaluated
     */
    public record QuoteData(RequestForQuote rfq, InventoryQuote inventoryQuote,
            List<TransportQuoteRequest> transportQuoteRequestList, List<TransportQuote> transportQuoteList, Time cutoffDate)
    {
    }
}
