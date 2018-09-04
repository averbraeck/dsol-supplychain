package nl.tudelft.simulation.supplychain.stock;

import java.io.Serializable;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class StockUpdateData implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the product name */
    private String productName;

    /** the actual number of units */
    private double actualAmount;

    /** the claimed amount of units */
    private double claimedAmount;

    /** the ordered number of product units */
    private double orderedAmount;

    /**
     * @param productName the product name
     * @param actualAmount the actual amount
     * @param claimedAmount the claimed amount
     * @param orderedAmount the ordered amount
     */
    public StockUpdateData(final String productName, final double actualAmount, final double claimedAmount,
            final double orderedAmount)
    {
        this.productName = productName;
        this.actualAmount = actualAmount;
        this.claimedAmount = claimedAmount;
        this.orderedAmount = orderedAmount;
    }

    /**
     * @return Returns the actualAmount.
     */
    public double getActualAmount()
    {
        return this.actualAmount;
    }

    /**
     * @return Returns the claimedAmount.
     */
    public double getClaimedAmount()
    {
        return this.claimedAmount;
    }

    /**
     * @return Returns the orderedAmount.
     */
    public double getOrderedAmount()
    {
        return this.orderedAmount;
    }

    /**
     * @return Returns the productName.
     */
    public String getProductName()
    {
        return this.productName;
    }
}
