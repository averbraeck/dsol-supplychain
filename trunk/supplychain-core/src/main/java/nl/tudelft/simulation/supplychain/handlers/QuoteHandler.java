package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.unit.dist.DistConstantDurationUnit;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * The abstract QuoteHandler can be extended into several ways how to deal with Quotes. One is the QuoteHandlerAll that waits
 * till every RequestForQuote has been answered with a Quote. Another one is the QuoteHandlerTime, that waits either till every
 * RequestForQuote is in within the timeout time, or just takes the list that is available at the timeout time. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class QuoteHandler extends SupplyChainHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** for debugging */
    private static final boolean DEBUG = false;

    /** the time to handle quotes when they are in and to place an order */
    protected DistContinuousDurationUnit handlingTime;

    /** the comparator to sort the quotes */
    private Comparator<Quote> quoteComparator = null;

    /** the maximum price margin */
    private double maximumPriceMargin = 0.0;

    /** the minimal amount margin */
    private double minimumAmountMargin = 0.0;

    /**
     * Constructor of the QuoteHandler with a one of the predefined comparators for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandler(final SupplyChainActor owner, final QuoteComparatorEnum comparatorType,
        final DistContinuousDurationUnit handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner);
        this.quoteComparator = new QuoteComparator(owner, comparatorType);
        this.handlingTime = handlingTime;
        this.maximumPriceMargin = maximumPriceMargin;
        this.minimumAmountMargin = minimumAmountMargin;
    }

    /**
     * Constructor of the QuoteHandler with a one of the predefined comparators for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandler(final SupplyChainActor owner, final QuoteComparatorEnum comparatorType, final Duration handlingTime,
        final double maximumPriceMargin, final double minimumAmountMargin)
    {
        this(owner, comparatorType, new DistConstantDurationUnit(handlingTime), maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandler with a user defined comparator for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandler(final SupplyChainActor owner, final Comparator<Quote> comparator,
        final DistContinuousDurationUnit handlingTime, final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(owner);
        this.quoteComparator = comparator;
        this.handlingTime = handlingTime;
        this.maximumPriceMargin = maximumPriceMargin;
        this.minimumAmountMargin = minimumAmountMargin;
    }

    /**
     * Constructor of the QuoteHandler with a user defined comparator for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandler(final SupplyChainActor owner, final Comparator<Quote> comparator, final Duration handlingTime,
        final double maximumPriceMargin, final double minimumAmountMargin)
    {
        this(owner, comparator, new DistConstantDurationUnit(handlingTime), maximumPriceMargin, minimumAmountMargin);
    }

    /** {@inheritDoc} */
    @Override
    public abstract boolean handleContent(final Serializable content);

    /**
     * Method getQuoteComparator
     * @return returns the quote comparator
     */
    protected Comparator<Quote> getQuoteComparator()
    {
        return this.quoteComparator;
    }

    /**
     * Method setQuoteComparator
     * @param quoteComparator the comparator to set
     */
    protected void setQuoteComparator(Comparator<Quote> quoteComparator)
    {
        this.quoteComparator = quoteComparator;
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Content> getContentClass()
    {
        return Quote.class;
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
                if (((quote.getPrice().getAmount() / quote.getAmount())) / quote.getProduct().getUnitMarketPrice()
                    .getAmount() <= (1.0 + this.maximumPriceMargin))
                {
                    if (quote.getAmount() <= quote.getRequestForQuote().getAmount() && ((quote.getRequestForQuote()
                        .getAmount() / quote.getAmount()) <= (1.0 + this.minimumAmountMargin)))
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
                            if (QuoteHandler.DEBUG)
                            {
                                System.err.println("QuoteHandler: quote: + prop delivery date: " + quote
                                    .getProposedDeliveryDate() + " earliest delivery date: " + quote.getRequestForQuote()
                                        .getEarliestDeliveryDate() + " latest delivery date: " + quote.getRequestForQuote()
                                            .getLatestDeliveryDate());
                                System.err.println("Quote: " + quote);
                                System.err.println("Owner of quote handler: " + getOwner().getName());
                            }
                        }

                    }
                    else
                    {
                        if (QuoteHandler.DEBUG)
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
                    if (QuoteHandler.DEBUG)
                    {
                        {
                            System.err.println("DEBUG -- QuoteHandler: " + " Price of quote: " + quote + " is too high: "
                                + (((quote.getPrice().getAmount() / quote.getAmount())) / quote.getProduct()
                                    .getUnitMarketPrice().getAmount() + "> " + (1.0 + this.maximumPriceMargin)));
                        }
                    }
                }
            }
            else
            {
                if (QuoteHandler.DEBUG)
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
    public void setHandlingTime(final DistContinuousDurationUnit handlingTime)
    {
        this.handlingTime = handlingTime;
    }
}
