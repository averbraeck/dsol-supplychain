package nl.tudelft.simulation.supplychain.stock.policies;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * This restocking policy looks at a safety stock level. As long as the stock level is above the safety stock level, do nothing.
 * Otherwise, order either a fixed amount or replenish until a certain level. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class RestockingPolicySafety extends RestockingPolicyFixed
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** The safety stock level */
    protected double safetyAmount;

    /**
     * Construct a new restocking policy based on a safety stock level.
     * @param stock the stock for which the policy holds
     * @param product the product that has to be restocked
     * @param frequency the frequency distribution for restocking
     * @param ceiling fixed ceiling (true) or fixed amount (false)
     * @param amount the amount with which or to which stock is supplemented
     * @param includeClaims whether to include the claims in the stock or not
     * @param safetyAmount the safety stock level for the product
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicySafety(final StockInterface stock, final Product product, final DistContinuousDuration frequency,
            final boolean ceiling, final double amount, final boolean includeClaims, final double safetyAmount,
            final Duration maxDeliveryTime)
    {
        super(stock, product, frequency, ceiling, amount, includeClaims, maxDeliveryTime);
        this.safetyAmount = safetyAmount;
    }

    /**
     * Construct a new restocking policy based on a safety stock level.
     * @param stock the stock for which the policy holds
     * @param product the product that has to be restocked
     * @param frequency the fixed frequency for restocking
     * @param ceiling fixed ceiling (true) or fixed amount (false)
     * @param amount the amount with which or to which stock is supplemented
     * @param includeClaims whether to include the claims in the stock or not
     * @param safetyAmount the safety stock level for the product
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicySafety(final StockInterface stock, final Product product, final Duration frequency,
            final boolean ceiling, final double amount, final boolean includeClaims, final double safetyAmount,
            final Duration maxDeliveryTime)
    {
        super(stock, product, frequency, ceiling, amount, includeClaims, maxDeliveryTime);
        this.safetyAmount = safetyAmount;
    }

    /** {@inheritDoc} */
    @Override
    protected void checkStockLevel()
    {
        // check if below safety level; if so, call super.checkStockLevel()
        double stockLevel = super.stock.getActualAmount(super.product) + super.stock.getOrderedAmount(super.product);
        if (this.includeClaims)
        {
            stockLevel -= super.stock.getClaimedAmount(super.product);
        }
        if (stockLevel < this.safetyAmount)
        {
            super.checkStockLevel();
        }
    }

    /**
     * @return Returns the safetyAmount.
     */
    public double getSafetyAmount()
    {
        return this.safetyAmount;
    }

    /**
     * @param safetyAmount The safetyAmount to set.
     */
    public void setSafetyAmount(final double safetyAmount)
    {
        this.safetyAmount = safetyAmount;
    }
}
