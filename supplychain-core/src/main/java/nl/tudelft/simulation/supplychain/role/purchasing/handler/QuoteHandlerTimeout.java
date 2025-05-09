package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;

/**
 * The QuoteHandlerTimeout handles quotes until a certain timeout is reached. When all Quotes are in, it reacts. It schedules
 * the timeout date when the FIRST Quote comes in, because it makes no sense to cut off the negotiation process without any
 * received Quote.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class QuoteHandlerTimeout extends QuoteHandler
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** a set of demand IDs for which we did not yet answer. */
    private Set<Serializable> unansweredIDs = new LinkedHashSet<Serializable>();

    /**
     * Constructor of the QuoteHandlerTimeout with a user defined comparator for quotes.
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandlerTimeout(final PurchasingActor owner, final Comparator<Quote> comparator, final double maximumPriceMargin,
            final double minimumAmountMargin)
    {
        super("QuoteHandlerTimeout", owner, comparator, maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandlerTimeout with a predefined comparator for quotes.
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the minimal amount margin
     */
    public QuoteHandlerTimeout(final PurchasingActor owner, final QuoteComparatorEnum comparatorType,
            final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super("QuoteHandlerTimeout", owner, comparatorType, maximumPriceMargin, minimumAmountMargin);
    }

    @Override
    public boolean handleContent(final Quote quote)
    {
        if (!isValidContent(quote))
        {
            return false;
        }
        long groupingId = quote.groupingId();
        var role = getRole();

        // add the quote to the list
        role.addQuoteToMap(quote);

        long demandId = quote.groupingId();
        ContentStoreInterface messageStore = getActor().getContentStore();
        int numberQuotes = messageStore.getContentList(demandId, Quote.class).size();
        int numberRFQs = messageStore.getContentList(demandId, RequestForQuote.class).size();
        // when the first quote comes in, schedule the timeout
        if (numberQuotes == 1)
        {
            try
            {
                this.unansweredIDs.add(demandId);
                Serializable[] args = new Serializable[] {demandId};

                // calculate the actual time out
                Time time = Time.max(getSimulatorTime(), quote.rfq().cutoffDate());
                getSimulator().scheduleEventAbs(time, this, "createOrder", args);
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
            createOrder(demandId);
        }
        return true;
    }

    /**
     * All quotes are in, or time is over. Select the best quote, and place an order. The set of unansweredIDs is used to
     * determine if we already answered with an Order -- in many cases, the createOrder method is scheduled twice: once when all
     * the quotes are in, and once when the timeout is there.
     * @param demandId the original demand linked to the quotes
     */
    protected void createOrder(final long demandId)
    {
        if (this.unansweredIDs.contains(demandId))
        {
            this.unansweredIDs.remove(demandId);
            ContentStoreInterface messageStore = getActor().getContentStore();
            List<Quote> quotes = messageStore.getContentList(demandId, Quote.class);

            // the size of the quotes is at least one
            // since the invocation of this method is scheduled after a first
            // quote has been received (see handleContent() of this class)
            Quote bestQuote = this.selectBestQuote(quotes);
            if (bestQuote != null)
            {
                var order = new OrderBasedOnQuote(bestQuote, bestQuote.proposedDeliveryDate());
                sendContent(order, this.getHandlingTime().draw());
            }
        }
    }
}
