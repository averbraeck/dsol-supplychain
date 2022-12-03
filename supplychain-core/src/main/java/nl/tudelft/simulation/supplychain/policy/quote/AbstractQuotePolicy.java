package nl.tudelft.simulation.supplychain.policy.quote;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.trade.Quote;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;

/**
 * The abstract QuoteHandler can be extended into several ways how to deal with Quotes. One is the QuoteHandlerAll that waits
 * till every RequestForQuote has been answered with a Quote. Another one is the QuoteHandlerTime, that waits either till every
 * RequestForQuote is in within the timeout time, or just takes the list that is available at the timeout time.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractQuotePolicy extends SupplyChainPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** for debugging. */
    private static final boolean DEBUG = false;

    /** the time to handle quotes when they are in and to place an order. */
    protected DistContinuousDuration handlingTime;

    /** the comparator to sort the quotes. */
    private Comparator<Quote> quoteComparator = null;

    /** the maximum price margin. */
    private double maximumPriceMargin = 0.0;

    /** the minimal amount margin. */
    private double minimumAmountMargin = 0.0;

    /**
     * Constructor of the QuoteHandler with a one of the predefined comparators for quotes.
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public AbstractQuotePolicy(final SupplyChainActor owner, final QuoteComparatorEnum comparatorType,
            final DistContinuousDuration handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner);
        this.quoteComparator = new QuoteComparator(owner, comparatorType);
        this.handlingTime = handlingTime;
        this.maximumPriceMargin = maximumPriceMargin;
        this.minimumAmountMargin = minimumAmountMargin;
    }

    /**
     * Constructor of the QuoteHandler with a user defined comparator for quotes.
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public AbstractQuotePolicy(final SupplyChainActor owner, final Comparator<Quote> comparator,
            final DistContinuousDuration handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner);
        this.quoteComparator = comparator;
        this.handlingTime = handlingTime;
        this.maximumPriceMargin = maximumPriceMargin;
        this.minimumAmountMargin = minimumAmountMargin;
    }

    /** {@inheritDoc} */
    @Override
    public abstract boolean handleMessage(Message message);

    /**
     * Method getQuoteComparator.
     * @return returns the quote comparator
     */
    protected Comparator<Quote> getQuoteComparator()
    {
        return this.quoteComparator;
    }

    /**
     * Method setQuoteComparator.
     * @param quoteComparator the comparator to set
     */
    protected void setQuoteComparator(final Comparator<Quote> quoteComparator)
    {
        this.quoteComparator = quoteComparator;
    }

    /**
     * Select the best quote from a list of quotes, based on the ordering sequence as indicated in the constructor of the
     * handler.
     * @param quotes the list of quotes to select from
     * @return Quote the best quote according to the sorting criterion or null of no quote passed the validity tests
     */
    protected Quote selectBestQuote(final List<Quote> quotes)
    {
        SortedSet<Quote> sortedQuotes = new TreeSet<>(this.quoteComparator);
        Iterator<Quote> quoteIterator = quotes.iterator();
        while (quoteIterator.hasNext())
        {
            Quote quote = quoteIterator.next();
            // only take valid quotes...
            if (quote.getValidityTime().gt(getOwner().getSimulatorTime()) && quote.getAmount() > 0.0)
            {
                if (((quote.getPrice().getAmount() / quote.getAmount()))
                        / quote.getProduct().getUnitMarketPrice().getAmount() <= (1.0 + this.maximumPriceMargin))
                {
                    if (quote.getAmount() <= quote.getRequestForQuote().getAmount() && ((quote.getRequestForQuote().getAmount()
                            / quote.getAmount()) <= (1.0 + this.minimumAmountMargin)))
                    {
                        if ((quote.getProposedDeliveryDate().le(quote.getRequestForQuote().getLatestDeliveryDate())))
                        // && (quote.getProposedDeliveryDate() >= quote
                        // .getRequestForQuote()
                        // .getEarliestDeliveryDate()))
                        {
                            sortedQuotes.add(quote);
                        }
                        else
                        {
                            if (AbstractQuotePolicy.DEBUG)
                            {
                                System.err.println("QuoteHandler: quote: + prop delivery date: "
                                        + quote.getProposedDeliveryDate() + " earliest delivery date: "
                                        + quote.getRequestForQuote().getEarliestDeliveryDate() + " latest delivery date: "
                                        + quote.getRequestForQuote().getLatestDeliveryDate());
                                System.err.println("Quote: " + quote);
                                System.err.println("Owner of quote handler: " + getOwner().getName());
                            }
                        }

                    }
                    else
                    {
                        if (AbstractQuotePolicy.DEBUG)
                        {
                            {
                                System.err.println("DEBUG -- QuoteHandler: " + " Quote: " + quote + " has invalid amount : "
                                        + quote.getAmount() + ">" + quote.getRequestForQuote().getAmount());
                            }
                        }
                    }
                }
                else
                {
                    if (AbstractQuotePolicy.DEBUG)
                    {
                        {
                            System.err.println("DEBUG -- QuoteHandler: " + " Price of quote: " + quote + " is too high: "
                                    + (((quote.getPrice().getAmount() / quote.getAmount()))
                                            / quote.getProduct().getUnitMarketPrice().getAmount() + "> "
                                            + (1.0 + this.maximumPriceMargin)));
                        }
                    }
                }
            }
            else
            {
                if (AbstractQuotePolicy.DEBUG)
                {
                    {
                        System.err.println("DEBUG -- QuoteHandler: " + " Quote: " + quote + " is invalid (before simtime) : "
                                + quote.getValidityTime() + " < " + getOwner().getSimulatorTime());
                    }
                }
            }
        }
        if (sortedQuotes.size() == 0)
        {
            return null;
        }
        return sortedQuotes.first();
    }

    /**
     * @param handlingTime The handlingTime to set.
     */
    public void setHandlingTime(final DistContinuousDuration handlingTime)
    {
        this.handlingTime = handlingTime;
    }
}
