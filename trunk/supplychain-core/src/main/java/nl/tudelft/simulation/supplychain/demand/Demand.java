package nl.tudelft.simulation.supplychain.demand;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

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
    private DistContinuousDurationUnit interval;

    /** the amount of products to order or make */
    private DistContinuous amount;

    /** the earliest delivery date relative to the current simulator time */
    private DistContinuousDurationUnit earliestDeliveryDuration;

    /** the latest delivery date relative to the current simulator time */
    private DistContinuousDurationUnit latestDeliveryDuration;

    /** a default stream for DistConstant */
    private static StreamInterface stream = new Java2Random();

    /**
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDate the earliest delivery date distribution
     * @param latestDeliveryDate the latest delivery date distribution
     */
    public Demand(final Product product, final DistContinuousDurationUnit interval, final DistContinuous amount,
            final DistContinuousDurationUnit earliestDeliveryDate, final DistContinuousDurationUnit latestDeliveryDate)
    {
        super();
        this.product = product;
        this.interval = interval;
        this.amount = amount;
        this.earliestDeliveryDuration = earliestDeliveryDate;
        this.latestDeliveryDuration = latestDeliveryDate;
    }

    /**
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDate the earliest delivery date
     * @param latestDeliveryDate the latest delivery date
     */
    public Demand(final Product product, final DistContinuousDurationUnit interval, final double amount, final Duration earliestDeliveryDate,
            final Duration latestDeliveryDate)
    {
        super();
        this.product = product;
        this.interval = interval;
        this.amount = new DistConstant(Demand.stream, amount);
        this.earliestDeliveryDuration = new DistContinuousDurationUnit(new DistConstant(Demand.stream, earliestDeliveryDate.si), DurationUnit.SI);
        this.latestDeliveryDuration = new DistContinuousDurationUnit(new DistConstant(Demand.stream, latestDeliveryDate.si), DurationUnit.SI);
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
    public DistContinuousDurationUnit getInterval()
    {
        return this.interval;
    }

    /**
     * @param interval The interval to set.
     */
    public void setInterval(final DistContinuousDurationUnit interval)
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
    public DistContinuousDurationUnit getEarliestDeliveryDuration()
    {
        return this.earliestDeliveryDuration;
    }

    /**
     * @param earliestDeliveryDuration The earliestDeliveryDate to set.
     */
    public void setEarliestDeliveryDuration(final DistContinuousDurationUnit earliestDeliveryDuration)
    {
        this.earliestDeliveryDuration = earliestDeliveryDuration;
    }

    /**
     * @return Returns the latestDeliveryDate.
     */
    public DistContinuousDurationUnit getLatestDeliveryDuration()
    {
        return this.latestDeliveryDuration;
    }

    /**
     * @param latestDeliveryDuration The latestDeliveryDate to set.
     */
    public void setLatestDeliveryDUration(final DistContinuousDurationUnit latestDeliveryDuration)
    {
        this.latestDeliveryDuration = latestDeliveryDuration;
    }
}
