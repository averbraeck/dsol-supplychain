package nl.tudelft.simulation.supplychain.role.warehousing.process;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;

/**
 * This restocking service looks at the difference between ordered and stock on hand on one hand, and the committed stock on the
 * other hand. If we committed more than we ordered and have on hand, we overreact and order more products than strictly
 * necessary. This reaction tends to lead in an oscillation of order sizes upstream the supply chain.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RestockingProcessOscillation extends RestockingProcessFixed
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** The oscillation margin. */
    private double oscillationMargin = 0.0;

    /**
     * Construct a new restocking service based on a safety stock level.
     * @param role the warehousing role to which the restocking process belongs
     * @param inventory the inventory for which the service holds
     * @param product the product that has to be restocked
     * @param checkInterval the interval time for restocking
     * @param ceiling fixed ceiling (true) or fixed amount (false)
     * @param amount the amount with which or to which stock is supplemented
     * @param includeClaims whether to include the claims in the stock or not
     * @param overReactionMargin the over reaction margin
     * @param maxDeliveryTime the maximum delivery time to use
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public RestockingProcessOscillation(final WarehousingRole role, final Inventory inventory, final Product product,
            final Duration checkInterval, final boolean ceiling, final double amount, final boolean includeClaims,
            final double overReactionMargin, final Duration maxDeliveryTime)
    {
        super(role, inventory, product, checkInterval, ceiling, amount, includeClaims, maxDeliveryTime);
        this.oscillationMargin = overReactionMargin;
    }

    @Override
    protected void checkInventoryLevel()
    {
        // just create an demand and send it to the owner
        double orderAmount = 0.0;
        double stockLevel = getInventory().getActualAmount(getProduct()) + getInventory().getOrderedAmount(getProduct());
        if (isIncludeClaims())
        {
            stockLevel -= getInventory().getReservedAmount(getProduct());
        }
        orderAmount = Math.max(0.0, getAmount() - stockLevel);

        if (stockLevel <= 0.0)
        {
            // let's overreact!
            double old = orderAmount;
            orderAmount = Math.ceil(orderAmount + (Math.abs(stockLevel) * (this.oscillationMargin)));
            System.out.println(getInventory().getActor().getName() + " overreacted: was: " + old + " new: " + orderAmount);
        }

        if (orderAmount > 0.0)
        {
            super.createDemand(orderAmount);
        }
    }

    /**
     * @return the overReactionMargin.
     */
    public double getOscillationMargin()
    {
        return this.oscillationMargin;
    }

    /**
     * @param overReactionMargin The overReactionMargin to set.
     */
    public void setOscillationMargin(final double overReactionMargin)
    {
        this.oscillationMargin = overReactionMargin;
    }
}
