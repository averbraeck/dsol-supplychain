package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import java.util.Comparator;
import java.util.List;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;

/**
 * The QuoteHandlerAll just waits patiently till all the Quotes are in for each RequestForQuote that has been sent out. When
 * that happens, it chooses the best offer, based on price and distance.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class QuoteHandlerAll extends QuoteHandler
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** for debugging. */
    private static final boolean DEBUG = false;

    /**
     * Constructor of the QuoteHandlerAll with a user defined comparator for quotes.
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandlerAll(final PurchasingRole owner, final Comparator<Quote> comparator, final double maximumPriceMargin,
            final double minimumAmountMargin)
    {
        super("QuoteHandlerAll", owner, comparator, maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandlerAll with a one of the predefined comparators for quotes.
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the minimal amount margin
     */
    public QuoteHandlerAll(final PurchasingRole owner, final QuoteComparatorEnum comparatorType,
            final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super("QuoteHandlerAll", owner, comparatorType, maximumPriceMargin, minimumAmountMargin);
    }

    @Override
    public boolean handleContent(final Quote quote)
    {
        if (!isValidContent(quote))
        {
            return false;
        }
        // look if all quotes are there for the RFQs that we sent out
        long id = quote.groupingId();
        ContentStoreInterface messageStore = getActor().getContentStore();
        if (messageStore.getContentList(id, Quote.class).size() == messageStore.getContentList(id, RequestForQuote.class)
                .size())
        {
            // All quotes are in. Select the best and place an order

            if (QuoteHandlerAll.DEBUG)
            {
                System.err.println("t=" + getSimulator().getSimulatorTime() + " DEBUG -- QuoteHandlerAll of actor " + getActor()
                        + ", size=" + messageStore.getContentList(id, Quote.class).size());
            }

            List<Quote> quotes = messageStore.getContentList(id, Quote.class);
            Quote bestQuote = selectBestQuote(quotes);
            if (bestQuote == null)
            {
                Logger.warn("{}.QuoteHandlerAll could not find best quote within margins while quoteList.size was {}",
                        getActor().getName(), quotes.size());
                return false;
            }

            if (QuoteHandlerAll.DEBUG)
            {
                System.err.println("t=" + getSimulator().getSimulatorTime() + " DEBUG -- QuoteHandlerAll of actor " + getActor()
                        + ", bestQuote=" + bestQuote);
            }

            var order = new OrderBasedOnQuote(bestQuote, bestQuote.proposedDeliveryDate());
            sendContent(order, getHandlingTime().draw());
        }
        return true;
    }
}
