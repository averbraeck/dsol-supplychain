package nl.tudelft.simulation.supplychain.policy.quote;

import java.util.Comparator;
import java.util.List;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;

/**
 * The QuoteHandlerAll just waits patiently till all the Quotes are in for each RequestForQuote that has been sent out. When
 * that happens, it chooses the best offer, based on price and distance.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class QuotePolicyAll extends QuotePolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** for debugging. */
    private static final boolean DEBUG = false;

    /**
     * Constructor of the QuoteHandlerAll with a user defined comparator for quotes.
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuotePolicyAll(final Role owner, final Comparator<Quote> comparator,
            final DistContinuousDuration handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super("QuotePolicyAll", owner, comparator, handlingTime, maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandlerAll with a one of the predefined comparators for quotes.
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the minimal amount margin
     */
    public QuotePolicyAll(final Role owner, final QuoteComparatorEnum comparatorType,
            final DistContinuousDuration handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super("QuotePolicyAll", owner, comparatorType, handlingTime, maximumPriceMargin, minimumAmountMargin);
    }

    @Override
    public boolean handleContent(final Quote quote)
    {
        if (!isValidMessage(quote))
        {
            return false;
        }
        // look if all quotes are there for the RFQs that we sent out
        long id = quote.getDemandId();
        TradeMessageStoreInterface messageStore = getActor().getContentStore();
        if (messageStore.getMessageList(id, Quote.class).size() == messageStore.getMessageList(id, RequestForQuote.class)
                .size())
        {
            // All quotes are in. Select the best and place an order

            if (QuotePolicyAll.DEBUG)
            {
                System.err.println("t=" + getSimulator().getSimulatorTime() + " DEBUG -- QuoteHandlerAll of actor " + getActor()
                        + ", size=" + messageStore.getMessageList(id, Quote.class).size());
            }

            List<Quote> quotes = messageStore.getMessageList(id, Quote.class);
            Quote bestQuote = selectBestQuote(quotes);
            if (bestQuote == null)
            {
                Logger.warn("{}.QuoteHandlerAll could not find best quote within margins while quoteList.size was {}",
                        getActor().getName(), quotes.size());
                return false;
            }

            if (QuotePolicyAll.DEBUG)
            {
                System.err.println("t=" + getSimulator().getSimulatorTime() + " DEBUG -- QuoteHandlerAll of actor " + getActor()
                        + ", bestQuote=" + bestQuote);
            }

            Order order = new OrderBasedOnQuote(getActor(), bestQuote.getSender(), bestQuote.getProposedDeliveryDate(),
                    bestQuote, bestQuote.getTransportOption());
            sendMessage(order, getHandlingTime().draw());
        }
        return true;
    }
}
