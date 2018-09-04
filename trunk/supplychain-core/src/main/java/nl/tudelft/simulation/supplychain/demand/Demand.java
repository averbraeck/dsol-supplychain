package nl.tudelft.simulation.supplychain.demand;

import java.io.Serializable;

import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The Demand. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Demand implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the product */
    private Product product;

    /** the interval between demand requests */
    private DistContinuous interval;

    /** the amount of products to order or make */
    private DistContinuous amount;

    /** the earliest delivery date relative to the current simulator time */
    private DistContinuous earliestDeliveryDate;

    /** the latest delivery date relative to the current simulator time */
    private DistContinuous latestDeliveryDate;

    /** a default stream for DistConstant */
    private static StreamInterface stream = new Java2Random();

    /**
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDate the earliest delivery date distribution
     * @param latestDeliveryDate the latest delivery date distribution
     */
    public Demand(final Product product, final DistContinuous interval, final DistContinuous amount,
            final DistContinuous earliestDeliveryDate, final DistContinuous latestDeliveryDate)
    {
        super();
        this.product = product;
        this.interval = interval;
        this.amount = amount;
        this.earliestDeliveryDate = earliestDeliveryDate;
        this.latestDeliveryDate = latestDeliveryDate;
    }

    /**
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDate the earliest delivery date
     * @param latestDeliveryDate the latest delivery date
     */
    public Demand(final Product product, final DistContinuous interval, final double amount, final double earliestDeliveryDate,
            final double latestDeliveryDate)
    {
        super();
        this.product = product;
        this.interval = interval;
        this.amount = new DistConstant(Demand.stream, amount);
        this.earliestDeliveryDate = new DistConstant(Demand.stream, earliestDeliveryDate);
        this.latestDeliveryDate = new DistConstant(Demand.stream, latestDeliveryDate);
    }

    /**
     * @return Returns the amount.
     */
    public DistContinuous getAmount()
    {
        return this.amount;
    }

    /**
     * @param amount The amount to set.
     */
    public void setAmount(final DistContinuous amount)
    {
        this.amount = amount;
    }

    /**
     * @return Returns the interval.
     */
    public DistContinuous getInterval()
    {
        return this.interval;
    }

    /**
     * @param interval The interval to set.
     */
    public void setInterval(final DistContinuous interval)
    {
        this.interval = interval;
    }

    /**
     * @return Returns the product.
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * @param product The product to set.
     */
    public void setProduct(final Product product)
    {
        this.product = product;
    }

    /**
     * @return Returns the earliestDeliveryDate.
     */
    public DistContinuous getEarliestDeliveryDate()
    {
        return this.earliestDeliveryDate;
    }

    /**
     * @param earliestDeliveryDate The earliestDeliveryDate to set.
     */
    public void setEarliestDeliveryDate(final DistContinuous earliestDeliveryDate)
    {
        this.earliestDeliveryDate = earliestDeliveryDate;
    }

    /**
     * @return Returns the latestDeliveryDate.
     */
    public DistContinuous getLatestDeliveryDate()
    {
        return this.latestDeliveryDate;
    }

    /**
     * @param latestDeliveryDate The latestDeliveryDate to set.
     */
    public void setLatestDeliveryDate(final DistContinuous latestDeliveryDate)
    {
        this.latestDeliveryDate = latestDeliveryDate;
    }
}
