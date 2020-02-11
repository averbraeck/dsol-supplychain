package nl.tudelft.simulation.supplychain.finance;

import java.io.Serializable;

/**
 * MoneyUnit.java.
 * <p>
 * Copyright (c) 2019-2019 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class MoneyUnit implements Serializable
{
    /** */
    private static final long serialVersionUID = 20200211L;
    
    private final String name;
    private final String symbol;
    
    public static final MoneyUnit USD = new MoneyUnit("USD", "$");
    public static final MoneyUnit EUR = new MoneyUnit("EUR", "\u20AC");
    public static final MoneyUnit LBP = new MoneyUnit("LBP", "\u00A3");
    /**
     * @param name
     * @param symbol
     */
    public MoneyUnit(String name, String symbol)
    {
        super();
        this.name = name;
        this.symbol = symbol;
    }
    /**
     * @return name
     */
    public String getName()
    {
        return this.name;
    }
    /**
     * @return symbol
     */
    public String getSymbol()
    {
        return this.symbol;
    }
    
    

}

