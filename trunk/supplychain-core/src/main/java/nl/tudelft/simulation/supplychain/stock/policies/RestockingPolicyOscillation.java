package nl.tudelft.simulation.supplychain.stock.policies;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * This restocking policy looks at the difference between ordered and stock on hand on one hand, and the committed stock on the
 * other hand. If we committed more than we ordered and have on hand, we overreact and order more products than strictly
 * necessary. This reaction tends to lead in an oscillation of order sizes upstream the supply chain. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class RestockingPolicyOscillation extends RestockingPolicyFixed
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** The oscillation margin */
    protected double oscillationMargin = 0.0;

    /**
     * Construct a new restocking policy based on a safety stock level.
     * @param stock the stock for which the policy holds
     * @param product the product that has to be restocked
     * @param frequency the frequency distribution for restocking
     * @param ceiling fixed ceiling (true) or fixed amount (false)
     * @param amount the amount with which or to which stock is supplemented
     * @param includeClaims whether to include the claims in the stock or not
     * @param overReactionMargin the over reaction margin
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicyOscillation(final StockInterface stock, final Product product, final DistContinuous frequency,
            final boolean ceiling, final double amount, final boolean includeClaims, final double overReactionMargin,
            final double maxDeliveryTime)
    {
        super(stock, product, frequency, ceiling, amount, includeClaims, maxDeliveryTime);
        this.oscillationMargin = overReactionMargin;
    }

    /**
     * Construct a new restocking policy based on a safety stock level.
     * @param stock the stock for which the policy holds
     * @param product the product that has to be restocked
     * @param frequency the fixed frequency for restocking
     * @param ceiling fixed ceiling (true) or fixed amount (false)
     * @param amount the amount with which or to which stock is supplemented
     * @param includeClaims whether to include the claims in the stock or not
     * @param overReactionMargin the over reaction margin
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicyOscillation(final StockInterface stock, final Product product, final double frequency,
            final boolean ceiling, final double amount, final boolean includeClaims, final double overReactionMargin,
            final double maxDeliveryTime)
    {
        super(stock, product, frequency, ceiling, amount, includeClaims, maxDeliveryTime);
        this.oscillationMargin = overReactionMargin;
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.policies.RestockingPolicy#checkStockLevel()
     */
    @Override
    protected void checkStockLevel()
    {
        // just create an internal demand and send it to the owner
        double orderAmount = 0.0;
        double stockLevel = super.stock.getActualAmount(super.product) + super.stock.getOrderedAmount(super.product);
        if (this.includeClaims)
        {
            stockLevel -= super.stock.getClaimedAmount(super.product);
        }
        orderAmount = Math.max(0.0, this.amount - stockLevel);

        if (stockLevel <= 0.0)
        {
            // let's overreact!
            double old = orderAmount;
            orderAmount = Math.ceil(orderAmount + (Math.abs(stockLevel) * (this.oscillationMargin)));
            System.out.println(super.stock.getOwner().getName() + " overreacted: was: " + old + " new: " + orderAmount);
        }

        if (orderAmount > 0.0)
        {
            super.createInternalDemand(orderAmount);
        }
    }

    /**
     * @return Returns the overReactionMargin.
     */
    public double getOscillationMargin()
    {
        return this.oscillationMargin;
    }

    /**
     * @param overReactionMargin The overReactionMargin to set.
     */
    public void setOscillationMargin(double overReactionMargin)
    {
        this.oscillationMargin = overReactionMargin;
    }
}
