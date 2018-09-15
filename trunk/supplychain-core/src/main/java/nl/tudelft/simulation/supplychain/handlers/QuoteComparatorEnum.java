package nl.tudelft.simulation.supplychain.handlers;

/**
 * Predefined comparator types for initialization. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public enum QuoteComparatorEnum
{
    /** Comparator type for quotes, sort on price, distance, date */
    SORT_PRICE_DISTANCE_DATE,

    /** Comparator type for quotes, sort on price, date, distance */
    SORT_PRICE_DATE_DISTANCE,

    /** Comparator type for quotes, sort on distance, price, date */
    SORT_DISTANCE_PRICE_DATE,

    /** Comparator type for quotes, sort on distance, date, price */
    SORT_DISTANCE_DATE_PRICE,

    /** Comparator type for quotes, sort on date, price, distance */
    SORT_DATE_PRICE_DISTANCE,

    /** Comparator type for quotes, sort on date, distance, price */
    SORT_DATE_DISTANCE_PRICE;

}
