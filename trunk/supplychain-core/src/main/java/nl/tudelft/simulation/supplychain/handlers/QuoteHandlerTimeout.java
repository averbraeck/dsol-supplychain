package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * The QuoteHandlerTimeout handles quotes until a certain timeout is reached. When all Quotes are in, it reacts. It schedules
 * the timeout date when the FIRST Quote comes in, because it makes no sense to cut off the negotiation process without any
 * received Quote. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class QuoteHandlerTimeout extends QuoteHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** a set of internal demand IDs for which we did not yet answer */
    private Set<Serializable> unansweredIDs = new HashSet<Serializable>();

    /**
     * Constructor of the QuoteHandlerTimeout with a user defined comparator for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandlerTimeout(final SupplyChainActor owner, final Comparator<Quote> comparator,
            final DistContinuousDurationUnit handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner, comparator, handlingTime, maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandlerTimeout with a user defined comparator for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandlerTimeout(final SupplyChainActor owner, final Comparator<Quote> comparator, final Duration handlingTime,
            final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner, comparator, handlingTime, maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandlerTimeout with a predefined comparator for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the minimal amount margin
     */
    public QuoteHandlerTimeout(final SupplyChainActor owner, final QuoteComparatorEnum comparatorType,
            final DistContinuousDurationUnit handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner, comparatorType, handlingTime, maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandlerTimeout with a predefined comparator for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the minimal amount margin
     */
    public QuoteHandlerTimeout(final SupplyChainActor owner, final QuoteComparatorEnum comparatorType,
            final Duration handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner, comparatorType, handlingTime, maximumPriceMargin, minimumAmountMargin);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (!isValidContent(content))
        {
            return false;
        }
        Quote quote = (Quote) content;
        Serializable internalDemandID = quote.getInternalDemandID();
        ContentStoreInterface contentStore = getOwner().getContentStore();
        int numberQuotes = contentStore.getContentList(internalDemandID, Quote.class).size();
        int numberRFQs = contentStore.getContentList(internalDemandID, RequestForQuote.class).size();
        // when the first quote comes in, schedule the timeout
        if (numberQuotes == 1)
        {
            try
            {
                this.unansweredIDs.add(internalDemandID);
                Serializable[] args = new Serializable[] { internalDemandID };

                // calculate the actual time out
                Time time = Time.max(getOwner().getSimulatorTime(), quote.getRequestForQuote().getCutoffDate());
                getOwner().getSimulator().scheduleEventAbs(time, this, this, "createOrder", args);
            }
            catch (Exception exception)
            {
                Logger.error(exception, "handleContent");
                return false;
            }
        }
        // look if all quotes are there for the RFQs that we sent out
        if (numberQuotes == numberRFQs)
        {
            createOrder(internalDemandID);
        }
        return true;
    }

    /**
     * All quotes are in, or time is over. Select the best quote, and place an order. The set of unansweredIDs is used to
     * determine if we already answered with an Order -- in many cases, the createOrder method is scheduled twice: once when all
     * the quotes are in, and once when the timeout is there.
     * @param internalDemandID the original demand linked to the quotes
     */
    protected void createOrder(final Serializable internalDemandID)
    {
        if (this.unansweredIDs.contains(internalDemandID))
        {
            this.unansweredIDs.remove(internalDemandID);
            ContentStoreInterface contentStore = getOwner().getContentStore();
            List<Quote> quotes = contentStore.getContentList(internalDemandID, Quote.class);

            // the size of the quotes is at least one
            // since the invocation of this method is scheduled after a first
            // quote has been received (see handleContent() of this class)
            Quote bestQuote = this.selectBestQuote(quotes);
            if (bestQuote != null)
            {
                Order order = new OrderBasedOnQuote(getOwner(), bestQuote.getSender(), internalDemandID,
                        bestQuote.getProposedDeliveryDate(), bestQuote);
                getOwner().sendContent(order, this.handlingTime.draw());
            }
        }
    }
}
