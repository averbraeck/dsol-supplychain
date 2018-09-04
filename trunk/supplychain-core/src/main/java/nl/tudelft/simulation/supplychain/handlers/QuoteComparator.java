package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.util.Comparator;

import javax.vecmath.Point3d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Quote;

/**
 * Class for comparing quotes. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class QuoteComparator implements Comparator<Quote>, Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** comparatorType indicates the sorting order for the comparator */
    private int comparatorType = 0;

    /** ownerPosition stores the position of the owner */
    private Point3d ownerPosition;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(QuoteComparator.class);

    /**
     * @param owner the supply chain actor
     * @param comparatorType the type of comparator to use
     */
    public QuoteComparator(final SupplyChainActor owner, final int comparatorType)
    {
        super();
        this.comparatorType = comparatorType;
        this.ownerPosition = owner.getLocation();
    }

    /**
     * Method compare
     * @param quote1 the first quote
     * @param quote2 the second quote
     * @return returns 1 or -1
     */
    public int compare(final Quote quote1, final Quote quote2)
    {
        double price0 = quote1.getPrice();
        double price1 = quote2.getPrice();
        int priceCompare = Double.compare(price0, price1);
        double date0 = quote1.getProposedDeliveryDate();
        double date1 = quote2.getProposedDeliveryDate();
        int dateCompare = Double.compare(date0, date1);
        double distance0 = quote1.getSender().getLocation().distance(this.ownerPosition);
        double distance1 = quote2.getSender().getLocation().distance(this.ownerPosition);
        int distanceCompare = Double.compare(distance0, distance1);
        switch (this.comparatorType)
        {
            case QuoteHandler.SORT_DATE_DISTANCE_PRICE:
                if (dateCompare != 0)
                {
                    return dateCompare;
                }
                else if (distanceCompare != 0)
                {
                    return distanceCompare;
                }
                else
                {
                    return priceCompare;
                }
            case QuoteHandler.SORT_DATE_PRICE_DISTANCE:
                if (dateCompare != 0)
                {
                    return dateCompare;
                }
                else if (priceCompare != 0)
                {
                    return priceCompare;
                }
                else
                {
                    return distanceCompare;
                }
            case QuoteHandler.SORT_DISTANCE_DATE_PRICE:
                if (distanceCompare != 0)
                {
                    return distanceCompare;
                }
                else if (dateCompare != 0)
                {
                    return dateCompare;
                }
                else
                {
                    return priceCompare;
                }
            case QuoteHandler.SORT_DISTANCE_PRICE_DATE:
                if (distanceCompare != 0)
                {
                    return distanceCompare;
                }
                else if (priceCompare != 0)
                {
                    return priceCompare;
                }
                else
                {
                    return dateCompare;
                }
            case QuoteHandler.SORT_PRICE_DATE_DISTANCE:
                if (priceCompare != 0)
                {
                    return priceCompare;
                }
                else if (dateCompare != 0)
                {
                    return dateCompare;
                }
                else
                {
                    return distanceCompare;
                }
            case QuoteHandler.SORT_PRICE_DISTANCE_DATE:
                if (priceCompare != 0)
                {
                    return priceCompare;
                }
                else if (distanceCompare != 0)
                {
                    return distanceCompare;
                }
                else
                {
                    return dateCompare;
                }
            default:
                logger.fatal("QuoteHandler$compare", "Illegal comparator type=" + this.comparatorType);
                break;
        }
        return 0;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (this.comparatorType == QuoteHandler.SORT_DATE_DISTANCE_PRICE)
        {
            return "SORT_DATE_DISTANCE_PRICE";
        }
        if (this.comparatorType == QuoteHandler.SORT_DATE_PRICE_DISTANCE)
        {
            return "SORT_DATE_PRICE_DISTANCE";
        }
        if (this.comparatorType == QuoteHandler.SORT_DISTANCE_DATE_PRICE)
        {
            return "SORT_DISTANCE_DATE_PRICE";
        }
        if (this.comparatorType == QuoteHandler.SORT_DISTANCE_PRICE_DATE)
        {
            return "SORT_DISTANCE_PRICE_DATE";
        }
        if (this.comparatorType == QuoteHandler.SORT_PRICE_DATE_DISTANCE)
        {
            return "SORT_PRICE_DATE_DISTANCE";
        }
        if (this.comparatorType == QuoteHandler.SORT_PRICE_DISTANCE_DATE)
        {
            return "SORT_PRICE_DISTANCE_DATE";
        }
        return "Unknown comparator type: " + this.comparatorType;
    }
}
