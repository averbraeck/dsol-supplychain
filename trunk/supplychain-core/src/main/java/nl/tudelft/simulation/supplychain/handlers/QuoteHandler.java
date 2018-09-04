package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.dsol.experiment.TimeUnitInterface;
import nl.tudelft.simulation.dsol.simtime.TimeUnit;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Quote;

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

    // predefined comparator types for initialization
    /** Comparator type for quotes, sort on price, distance, date */
    public static final int SORT_PRICE_DISTANCE_DATE = 1;

    /** Comparator type for quotes, sort on price, date, distance */
    public static final int SORT_PRICE_DATE_DISTANCE = 2;

    /** Comparator type for quotes, sort on distance, price, date */
    public static final int SORT_DISTANCE_PRICE_DATE = 3;

    /** Comparator type for quotes, sort on distance, date, price */
    public static final int SORT_DISTANCE_DATE_PRICE = 4;

    /** Comparator type for quotes, sort on date, price, distance */
    public static final int SORT_DATE_PRICE_DISTANCE = 5;

    /** Comparator type for quotes, sort on date, distance, price */
    public static final int SORT_DATE_DISTANCE_PRICE = 6;

    /** the time to handle quotes when they are in and to place an order */
    protected DistContinuous handlingTime;

    /** the comparator to sort the quotes */
    private Comparator<Quote> quoteComparator = null;

    /** the maximum price margin */
    private double maximumPriceMargin = 0.0;

    /** the minimal amount margin */
    private double minimumAmountMargin = 0.0;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(QuoteHandler.class);

    /**
     * Constructor of the QuoteHandler with a one of the predefined comparators for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparatorType the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandler(final SupplyChainActor owner, final int comparatorType, final DistContinuous handlingTime,
            final double maximumPriceMargin, final double minimumAmountMargin)
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
    public QuoteHandler(final SupplyChainActor owner, final int comparatorType, final double handlingTime,
            final double maximumPriceMargin, final double minimumAmountMargin)
    {
        this(owner, comparatorType, new DistConstant(new Java2Random(), handlingTime), maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * Constructor of the QuoteHandler with a user defined comparator for quotes
     * @param owner the actor for this QuoteHandler.
     * @param comparator the predefined sorting comparator type.
     * @param handlingTime the time to handle the quotes
     * @param maximumPriceMargin the maximum margin (e.g. 0.4 for 40 % above unitprice) above the unitprice of a product
     * @param minimumAmountMargin the margin within which the offered amount may differ from the requested amount.
     */
    public QuoteHandler(final SupplyChainActor owner, final Comparator<Quote> comparator, final DistContinuous handlingTime,
            final double maximumPriceMargin, final double minimumAmountMargin)
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
    public QuoteHandler(final SupplyChainActor owner, final Comparator<Quote> comparator, final double handlingTime,
            final double maximumPriceMargin, final double minimumAmountMargin)
    {
        this(owner, comparator, new DistConstant(new Java2Random(), handlingTime), maximumPriceMargin, minimumAmountMargin);
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface#handleContent(java.io.Serializable)
     */
    public abstract boolean handleContent(final Serializable content);

    /**
     * @see nl.tudelft.simulation.supplychain.handlers.SupplyChainHandler#checkContentClass(java.io.Serializable)
     */
    protected boolean checkContentClass(final Serializable content)
    {
        return (content instanceof Quote);
    }

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

    /**
     * Select the best quote from a list of quotes, based on the ordering sequence as indicated in the constructor of the
     * handler.
     * @param quotes the list of quotes to select from
     * @return Quote the best quote according to the sorting criterion or null of no quote passed the validity tests
     */
    protected Quote selectBestQuote(final List quotes)
    {
        SortedSet<Quote> sortedQuotes = new TreeSet<Quote>(this.quoteComparator);
        Iterator i = quotes.iterator();
        while (i.hasNext())
        {
            Quote quote = (Quote) i.next();
            // only take valid quotes...
            if (quote.getValidityTime() > getOwner().getSimulatorTime() && quote.getAmount() > 0.0)
            {
                if (((quote.getPrice() / quote.getAmount()))
                        / quote.getProduct().getUnitMarketPrice() <= (1.0 + this.maximumPriceMargin))
                {
                    if (quote.getAmount() <= quote.getRequestForQuote().getAmount() && ((quote.getRequestForQuote().getAmount()
                            / quote.getAmount()) <= (1.0 + this.minimumAmountMargin)))
                    {
                        if ((quote.getProposedDeliveryDate() <= quote.getRequestForQuote().getLatestDeliveryDate()))
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
                                System.err.println("QuoteHandler: quote: + prop delivery date: "
                                        + this.makeDate(quote.getProposedDeliveryDate(), getOwner().getDEVSSimulator())
                                        + " earliest delivery date: "
                                        + this.makeDate(quote.getRequestForQuote().getEarliestDeliveryDate(),
                                                getOwner().getDEVSSimulator())
                                        + " latest delivery date: "
                                        + this.makeDate(quote.getRequestForQuote().getLatestDeliveryDate(),
                                                getOwner().getDEVSSimulator()));
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
                                    + (((quote.getPrice() / quote.getAmount())) / quote.getProduct().getUnitMarketPrice() + "> "
                                            + (1.0 + this.maximumPriceMargin)));
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
     * @param date the date
     * @param simulator the simulator
     * @return returns a DateIntData object
     */
    private String makeDate(final double date, final SimulatorInterface simulator)
    {
        Calendar calendar = Calendar.getInstance();
        long time = 0L;
        try
        {
            time = (long) TimeUnit.convert(date, simulator.getReplication().getRunControl().getTreatment().getTimeUnit(),
                    TimeUnitInterface.MILLISECOND);

            calendar.setTimeInMillis((time) + simulator.getReplication().getRunControl().getTreatment().getStartTime());

        }
        catch (RemoteException exception)
        {
            logger.fatal("makeDate", exception);
        }
        return calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.DATE);
    }

    /**
     * @param handlingTime The handlingTime to set.
     */
    public void setHandlingTime(final DistContinuous handlingTime)
    {
        this.handlingTime = handlingTime;
    }
}
