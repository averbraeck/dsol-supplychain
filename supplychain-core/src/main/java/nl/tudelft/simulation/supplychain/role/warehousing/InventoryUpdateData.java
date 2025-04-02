package nl.tudelft.simulation.supplychain.role.warehousing;

import java.io.Serializable;

/**
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InventoryUpdateData implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the product name. */
    private String productName;

    /** the actual number of units. */
    private double actualAmount;

    /** the claimed amount of units. */
    private double claimedAmount;

    /** the ordered number of product units. */
    private double orderedAmount;

    /**
     * @param productName the product name
     * @param actualAmount the actual amount
     * @param claimedAmount the claimed amount
     * @param orderedAmount the ordered amount
     */
    public InventoryUpdateData(final String productName, final double actualAmount, final double claimedAmount,
            final double orderedAmount)
    {
        this.productName = productName;
        this.actualAmount = actualAmount;
        this.claimedAmount = claimedAmount;
        this.orderedAmount = orderedAmount;
    }

    /**
     * @return the actualAmount.
     */
    public double getActualAmount()
    {
        return this.actualAmount;
    }

    /**
     * @return the claimedAmount.
     */
    public double getClaimedAmount()
    {
        return this.claimedAmount;
    }

    /**
     * @return the orderedAmount.
     */
    public double getOrderedAmount()
    {
        return this.orderedAmount;
    }

    /**
     * @return the productName.
     */
    public String getProductName()
    {
        return this.productName;
    }
}
