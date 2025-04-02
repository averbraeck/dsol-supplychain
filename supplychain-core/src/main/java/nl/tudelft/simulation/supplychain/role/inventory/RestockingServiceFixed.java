package nl.tudelft.simulation.supplychain.role.inventory;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * This RestockingService either orders fixed amounts of goods at the times indicated by the 'checkInterval', or supplements the
 * number of products till a fixed amount is reached.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RestockingServiceFixed extends AbstractRestockingService
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** fixed ceiling (true) or fixed amount (false). */
    private boolean ceiling;

    /** whether to include the claims in the inventory or not. */
    private boolean includeClaims;

    /** the amount in the service. */
    private double amount;

    /**
     * Construct a new restocking service, which works with fixed amounts.
     * @param inventory the inventory for which the service holds
     * @param product the product that has to be restocked
     * @param frequency the frequency distribution for restocking
     * @param ceiling fixed ceiling (true) or fixed amount (false)
     * @param amount the amount with which or to which stock is supplemented
     * @param includeClaims whether to include the claims in the stock or not
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingServiceFixed(final Inventory inventory, final Product product, final DistContinuousDuration frequency,
            final boolean ceiling, final double amount, final boolean includeClaims, final Duration maxDeliveryTime)
    {
        super(inventory, product, frequency, maxDeliveryTime);
        this.ceiling = ceiling;
        this.amount = amount;
        this.includeClaims = includeClaims;
    }

    @Override
    protected void checkInventoryLevel()
    {
        // just create an demand and send it to the owner
        double orderAmount = 0.0;
        if (this.ceiling)
        {
            double inventoryLevel =
                    getInventory().getActualAmount(getProduct()) + getInventory().getOrderedAmount(getProduct());
            if (this.includeClaims)
            {
                inventoryLevel -= getInventory().getClaimedAmount(getProduct());
            }
            orderAmount = Math.max(0.0, this.amount - inventoryLevel);
        }
        else
        {
            orderAmount = this.amount;
        }
        if (orderAmount > 0.0)
        {
            createDemand(orderAmount);
        }
    }

    /**
     * @return the amount (ceiling or amount).
     */
    protected double getAmount()
    {
        return this.amount;
    }

    /**
     * @return whether we work with a ceiling or fixed amount.
     */
    protected boolean isCeiling()
    {
        return this.ceiling;
    }

    /**
     * @return whether we include claims in the stock level or not.
     */
    protected boolean isIncludeClaims()
    {
        return this.includeClaims;
    }

}
