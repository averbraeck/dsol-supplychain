package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRoleRFQ;

/**
 * The abstract QuoteHandler can be extended into several ways how to deal with Quotes. One is the QuoteHandlerAll that waits
 * till every RequestForQuote has been answered with a Quote. Another one is the QuoteHandlerTime, that waits either till every
 * RequestForQuote is in within the timeout time, or just takes the list that is available at the timeout time.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class QuoteHandler extends ContentHandler<Quote, PurchasingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** for debugging. */
    private static final boolean DEBUG = false;

    /** the comparator to sort the quotes. */
    private Comparator<Quote> quoteComparator = null;

    /** the maximum price margin. */
    private double maximumPriceMargin = 0.0;

    /** the minimal amount margin. */
    private double minimumAmountMargin = 0.0;

    /**
     * Constructor of the QuoteHandler with a one of the predefined comparators for quotes.
     * @param id the id of the handler
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandler(final String id, final PurchasingActor owner, final QuoteComparatorEnum comparatorType,
            final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(id, owner.getPurchasingRole(), Quote.class);
        this.quoteComparator = new QuoteComparator(owner.getPurchasingRole(), comparatorType);
        this.maximumPriceMargin = maximumPriceMargin;
        this.minimumAmountMargin = minimumAmountMargin;
    }

    /**
     * Constructor of the QuoteHandler with a user defined comparator for quotes.
     * @param id the id of the handler
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandler(final String id, final PurchasingActor owner, final Comparator<Quote> comparator,
            final double maximumPriceMargin, final double minimumAmountMargin)
    {
        super(id, owner.getPurchasingRole(), Quote.class);
        this.quoteComparator = comparator;
        this.maximumPriceMargin = maximumPriceMargin;
        this.minimumAmountMargin = minimumAmountMargin;
    }

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
            if (quote.validityTime().gt(getSimulator().getAbsSimulatorTime()) && quote.amount() > 0.0)
            {
                if (((quote.price().getAmount() / quote.amount()))
                        / quote.product().getUnitMarketPrice().getAmount() <= (1.0 + this.maximumPriceMargin))
                {
                    if (quote.amount() <= quote.rfq().amount()
                            && ((quote.rfq().amount() / quote.amount()) <= (1.0 + this.minimumAmountMargin)))
                    {
                        if ((quote.proposedDeliveryDate().le(quote.rfq().latestDeliveryDate())))
                        {
                            sortedQuotes.add(quote);
                        }
                        else
                        {
                            if (QuoteHandler.DEBUG)
                            {
                                System.err.println("QuoteHandler: quote: + prop delivery date: " + quote.proposedDeliveryDate()
                                        + " earliest delivery date: " + quote.rfq().earliestDeliveryDate()
                                        + " latest delivery date: " + quote.rfq().latestDeliveryDate());
                                System.err.println("Quote: " + quote);
                                System.err.println("Owner of quote handler: " + getActor().getName());
                            }
                        }

                    }
                    else
                    {
                        if (QuoteHandler.DEBUG)
                        {
                            System.err.println("DEBUG -- QuoteHandler: " + " Quote: " + quote + " has invalid amount : "
                                    + quote.amount() + ">" + quote.rfq().amount());
                        }
                    }
                }
                else
                {
                    if (QuoteHandler.DEBUG)
                    {
                        System.err.println("DEBUG -- QuoteHandler: " + " Price of quote: " + quote + " is too high: "
                                + (((quote.price().getAmount() / quote.amount()))
                                        / quote.product().getUnitMarketPrice().getAmount() + "> "
                                        + (1.0 + this.maximumPriceMargin)));
                    }
                }
            }
            else
            {
                if (QuoteHandler.DEBUG)
                {
                    System.err.println("DEBUG -- QuoteHandler: " + " Quote: " + quote + " is invalid (before simtime) : "
                            + quote.validityTime() + " < " + getSimulator().getSimulatorTime());
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
     * @return maximumPriceMargin
     */
    protected double getMaximumPriceMargin()
    {
        return this.maximumPriceMargin;
    }

    /**
     * @return minimumAmountMargin
     */
    protected double getMinimumAmountMargin()
    {
        return this.minimumAmountMargin;
    }

    @Override
    public PurchasingRoleRFQ getRole()
    {
        return (PurchasingRoleRFQ) super.getRole();
    }

}
