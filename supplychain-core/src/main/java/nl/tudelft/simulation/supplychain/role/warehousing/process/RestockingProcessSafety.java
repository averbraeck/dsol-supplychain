package nl.tudelft.simulation.supplychain.role.warehousing.process;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * This restocking service looks at a safety stock level. As long as the stock level is above the safety stock level, do
 * nothing. Otherwise, order either a fixed amount or replenish until a certain level.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RestockingProcessSafety extends RestockingProcessFixed
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** The safety stock level. */
    private double safetyAmount;

    /**
     * Construct a new restocking service based on a safety stock level.
     * @param actor the warehousing actor to which the restocking process belongs
     * @param inventory the inventory for which the service holds
     * @param product the product that has to be restocked
     * @param checkInterval the interval time for restocking
     * @param ceiling fixed ceiling (true) or fixed amount (false)
     * @param amount the amount with which or to which stock is supplemented
     * @param includeClaims whether to include the claims in the stock or not
     * @param safetyAmount the safety stock level for the product
     * @param maxDeliveryTime the maximum delivery time to use
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public RestockingProcessSafety(final WarehousingActor actor, final Inventory inventory, final Product product,
            final Duration checkInterval, final boolean ceiling, final double amount, final boolean includeClaims,
            final double safetyAmount, final Duration maxDeliveryTime)
    {
        super(actor, inventory, product, checkInterval, ceiling, amount, includeClaims, maxDeliveryTime);
        this.safetyAmount = safetyAmount;
    }

    @Override
    protected void checkInventoryLevel()
    {
        // check if below safety level; if so, call super.checkStockLevel()
        double inventoryLevel = getInventory().getActualAmount(getProduct()) + getInventory().getOrderedAmount(getProduct());
        if (isIncludeClaims())
        {
            inventoryLevel -= getInventory().getReservedAmount(getProduct());
        }
        if (inventoryLevel < this.safetyAmount)
        {
            super.checkInventoryLevel();
        }
    }

    /**
     * @return the safetyAmount.
     */
    protected double getSafetyAmount()
    {
        return this.safetyAmount;
    }
}
