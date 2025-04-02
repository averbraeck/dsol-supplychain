package nl.tudelft.simulation.supplychain.handler.quote;

/**
 * Predefined comparator types for initialization.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public enum QuoteComparatorEnum
{
    /** Comparator type for quotes, sort on price, distance, date. */
    SORT_PRICE_DISTANCE_DATE,

    /** Comparator type for quotes, sort on price, date, distance. */
    SORT_PRICE_DATE_DISTANCE,

    /** Comparator type for quotes, sort on distance, price, date. */
    SORT_DISTANCE_PRICE_DATE,

    /** Comparator type for quotes, sort on distance, date, price. */
    SORT_DISTANCE_DATE_PRICE,

    /** Comparator type for quotes, sort on date, price, distance. */
    SORT_DATE_PRICE_DISTANCE,

    /** Comparator type for quotes, sort on date, distance, price. */
    SORT_DATE_DISTANCE_PRICE;

}
