package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * The QuoteHandlerAll just waits patiently till all the Quotes are in for each RequestForQuote that has been sent out. When
 * that happens, it chooses the best offer, based on price and distance. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class QuoteHandlerAll extends QuoteHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** for debugging */
    private static final boolean DEBUG = false;

    /**
     * Constructor of the QuoteHandlerAll with a user defined comparator for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandlerAll(final SupplyChainActor owner, final Comparator<Quote> comparator,
            final DistContinuousDurationUnit handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner, comparator, handlingTime, maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandlerAll with a user defined comparators for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandlerAll(final SupplyChainActor owner, final Comparator<Quote> comparator, final Duration handlingTime,
            final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner, comparator, handlingTime, maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandlerAll with a one of the predefined comparators for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the minimal amount margin
     */
    public QuoteHandlerAll(final SupplyChainActor owner, final int comparatorType,
            final DistContinuousDurationUnit handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner, comparatorType, handlingTime, maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandlerAll with a one of the predefined comparators for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the minimal amount margin
     */
    public QuoteHandlerAll(final SupplyChainActor owner, final int comparatorType, final Duration handlingTime,
            final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner, comparatorType, handlingTime, maximumPriceMargin, minimumAmountMargin);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        Quote quote = (Quote) checkContent(content);
        if (!isValidContent(quote))
        {
            return false;
        }
        // look if all quotes are there for the RFQs that we sent out
        Serializable id = quote.getInternalDemandID();
        ContentStoreInterface contentStore = getOwner().getContentStore();
        if (contentStore.getContentList(id, Quote.class).size() == contentStore.getContentList(id, RequestForQuote.class)
                .size())
        {
            // All quotes are in. Select the best and place an order

            if (QuoteHandlerAll.DEBUG)
            {
                System.err.println("t=" + getOwner().getSimulatorTime() + " DEBUG -- QuoteHandlerAll of actor " + getOwner()
                        + ", size=" + contentStore.getContentList(id, Quote.class).size());
            }

            List<Quote> quotes = contentStore.getContentList(id, Quote.class);
            Quote bestQuote = selectBestQuote(quotes);

            if (QuoteHandlerAll.DEBUG)
            {
                System.err.println("t=" + getOwner().getSimulatorTime() + " DEBUG -- QuoteHandlerAll of actor " + getOwner()
                        + ", bestQuote=" + bestQuote);
            }

            Order order = new OrderBasedOnQuote(getOwner(), bestQuote.getSender(), id, bestQuote.getProposedDeliveryDate(),
                    bestQuote);
            getOwner().sendContent(order, this.handlingTime.draw());
        }
        return true;
    }
}
