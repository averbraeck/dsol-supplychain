package nl.tudelft.simulation.supplychain.finance;

import java.io.Serializable;

import org.djutils.exceptions.Throw;

/**
 * MoneyUnit implements a currency unit, such as the Euro or US Dollar.
 * <p>
 * Copyright (c) 2019-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MoneyUnit implements Serializable
{
    /** */
    private static final long serialVersionUID = 20200211L;

    /** The (short) name of the money unit. */
    private final String name;

    /** The symbol to use when displaying the money unit. */
    private final String symbol;

    /** Default USD money unit. */
    public static final MoneyUnit USD = new MoneyUnit("USD", "$");

    /** Default EUR money unit. */
    public static final MoneyUnit EUR = new MoneyUnit("EUR", "\u20AC");

    /** Default GBP money unit. */
    public static final MoneyUnit GBP = new MoneyUnit("GBP", "\u00A3");

    /**
     * Create a new money unit.
     * @param name String; the (short) name of the money unit
     * @param symbol String; the symbol to use when displaying the money unit
     */
    public MoneyUnit(final String name, final String symbol)
    {
        Throw.whenNull(name, "name cannot be null");
        Throw.whenNull(symbol, "symbol cannot be null");
        Throw.when(name.length() == 0, IllegalArgumentException.class, "name length cannot be 0");
        Throw.when(symbol.length() == 0, IllegalArgumentException.class, "symbol length cannot be 0");
        this.name = name;
        this.symbol = symbol;
    }

    /**
     * Return the (short) name of the money unit.
     * @return String; the (short) name of the money unit
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Return the symbol to use when displaying the money unit.
     * @return String; the symbol to use when displaying the money unit
     */
    public String getSymbol()
    {
        return this.symbol;
    }

}
