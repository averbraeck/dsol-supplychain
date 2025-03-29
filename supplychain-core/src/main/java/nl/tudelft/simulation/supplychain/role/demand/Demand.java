package nl.tudelft.simulation.supplychain.role.demand;

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
 * Object that can model the demand for a certain amount of product.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Demand implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the product. */
    private Product product;

    /** the interval between demand requests. */
    private DistContinuousDuration intervalDistribution;

    /** the amount of products to order or make. */
    private Dist amountDistribution;

    /** the earliest delivery date relative to the current simulator time. */
    private DistContinuousDuration earliestDeliveryDurationDistribution;

    /** the latest delivery date relative to the current simulator time. */
    private DistContinuousDuration latestDeliveryDurationDistribution;

    /** a default stream for DistConstant. */
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
        this.product = product;
        this.intervalDistribution = interval;
        this.amountDistribution = new DistConstant(Demand.stream, amount);
        this.earliestDeliveryDurationDistribution =
                new DistContinuousDuration(new DistConstant(Demand.stream, earliestDeliveryDuration.si), DurationUnit.SI);
        this.latestDeliveryDurationDistribution =
                new DistContinuousDuration(new DistConstant(Demand.stream, latestDeliveryDuration.si), DurationUnit.SI);
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
        this.product = product;
        this.intervalDistribution = interval;
        this.amountDistribution = new DistDiscreteConstant(Demand.stream, amount);
        this.earliestDeliveryDurationDistribution =
                new DistContinuousDuration(new DistConstant(Demand.stream, earliestDeliveryDate.si), DurationUnit.SI);
        this.latestDeliveryDurationDistribution =
                new DistContinuousDuration(new DistConstant(Demand.stream, latestDeliveryDate.si), DurationUnit.SI);
    }

    /**
     * @return the amount distribution.
     */
    public Dist getAmountDistribution()
    {
        return this.amountDistribution;
    }

    /**
     * @return the interval.
     */
    public DistContinuousDuration getIntervalDistribution()
    {
        return this.intervalDistribution;
    }

    /**
     * @return the product.
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * @return the earliestDeliveryDate.
     */
    public DistContinuousDuration getEarliestDeliveryDurationDistribution()
    {
        return this.earliestDeliveryDurationDistribution;
    }

    /**
     * @return the latestDeliveryDate.
     */
    public DistContinuousDuration getLatestDeliveryDurationDistribution()
    {
        return this.latestDeliveryDurationDistribution;
    }

}
