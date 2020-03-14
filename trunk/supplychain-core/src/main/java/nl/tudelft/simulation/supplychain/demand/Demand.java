package nl.tudelft.simulation.supplychain.demand;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.Dist;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
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
    private DistContinuousDuration intervalDistribution;

    /** the amount of products to order or make */
    private Dist amountDistribution;

    /** the earliest delivery date relative to the current simulator time */
    private DistContinuousDuration earliestDeliveryDurationDistribution;

    /** the latest delivery date relative to the current simulator time */
    private DistContinuousDuration latestDeliveryDurationDistribution;

    /** a default stream for DistConstant */
    private static StreamInterface stream = new Java2Random();

    /**
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDurationDistribution the earliest delivery date distribution
     * @param latestDeliveryDurationDistribution the latest delivery date distribution
     */
    public Demand(final Product product, final DistContinuousDuration interval, final DistContinuous amount,
        final DistContinuousDuration earliestDeliveryDurationDistribution,
        final DistContinuousDuration latestDeliveryDurationDistribution)
    {
        super();
        this.product = product;
        this.intervalDistribution = interval;
        this.amountDistribution = amount;
        this.earliestDeliveryDurationDistribution = earliestDeliveryDurationDistribution;
        this.latestDeliveryDurationDistribution = latestDeliveryDurationDistribution;
    }

    /**
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDuration the earliest delivery date
     * @param latestDeliveryDuration the latest delivery date
     */
    public Demand(final Product product, final DistContinuousDuration interval, final double amount,
        final Duration earliestDeliveryDuration, final Duration latestDeliveryDuration)
    {
        super();
        this.product = product;
        this.intervalDistribution = interval;
        this.amountDistribution = new DistConstant(Demand.stream, amount);
        this.earliestDeliveryDurationDistribution = new DistContinuousDuration(new DistConstant(Demand.stream,
            earliestDeliveryDuration.si), DurationUnit.SI);
        this.latestDeliveryDurationDistribution = new DistContinuousDuration(new DistConstant(Demand.stream,
            latestDeliveryDuration.si), DurationUnit.SI);
    }

    /**
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDate the earliest delivery date distribution
     * @param latestDeliveryDate the latest delivery date distribution
     */
    public Demand(final Product product, final DistContinuousDuration interval, final DistDiscrete amount,
        final DistContinuousDuration earliestDeliveryDate, final DistContinuousDuration latestDeliveryDate)
    {
        super();
        this.product = product;
        this.intervalDistribution = interval;
        this.amountDistribution = amount;
        this.earliestDeliveryDurationDistribution = earliestDeliveryDate;
        this.latestDeliveryDurationDistribution = latestDeliveryDate;
    }

    /**
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDate the earliest delivery date
     * @param latestDeliveryDate the latest delivery date
     */
    public Demand(final Product product, final DistContinuousDuration interval, final long amount,
        final Duration earliestDeliveryDate, final Duration latestDeliveryDate)
    {
        super();
        this.product = product;
        this.intervalDistribution = interval;
        this.amountDistribution = new DistDiscreteConstant(Demand.stream, amount);
        this.earliestDeliveryDurationDistribution = new DistContinuousDuration(new DistConstant(Demand.stream,
            earliestDeliveryDate.si), DurationUnit.SI);
        this.latestDeliveryDurationDistribution = new DistContinuousDuration(new DistConstant(Demand.stream,
            latestDeliveryDate.si), DurationUnit.SI);
    }

    /**
     * @return Returns the amount distribution.
     */
    public Dist getAmountDistribution()
    {
        return this.amountDistribution;
    }

    /**
     * @param amountDistribution The amount distribution to set.
     */
    public void setAmount(final Dist amountDistribution)
    {
        this.amountDistribution = amountDistribution;
    }

    /**
     * @return Returns the interval.
     */
    public DistContinuousDuration getIntervalDistribution()
    {
        return this.intervalDistribution;
    }

    /**
     * @param interval The interval to set.
     */
    public void setInterval(final DistContinuousDuration interval)
    {
        this.intervalDistribution = interval;
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
    public DistContinuousDuration getEarliestDeliveryDurationDistribution()
    {
        return this.earliestDeliveryDurationDistribution;
    }

    /**
     * @param earliestDeliveryDurationDistribution The earliestDeliveryDate to set.
     */
    public void setEarliestDeliveryDuration(final DistContinuousDuration earliestDeliveryDurationDistribution)
    {
        this.earliestDeliveryDurationDistribution = earliestDeliveryDurationDistribution;
    }

    /**
     * @return Returns the latestDeliveryDate.
     */
    public DistContinuousDuration getLatestDeliveryDuration()
    {
        return this.latestDeliveryDurationDistribution;
    }

    /**
     * @param latestDeliveryDuration The latestDeliveryDate to set.
     */
    public void setLatestDeliveryDUration(final DistContinuousDuration latestDeliveryDuration)
    {
        this.latestDeliveryDurationDistribution = latestDeliveryDuration;
    }
}
