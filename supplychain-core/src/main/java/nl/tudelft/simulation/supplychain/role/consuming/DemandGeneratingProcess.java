package nl.tudelft.simulation.supplychain.role.consuming;

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
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * Object that can model the demand for a certain amount of product.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemandGeneratingProcess extends AutonomousProcess<ConsumingRole>
{
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

    /**
     * Make a demand generating process.
     * @param role the role to which this process belongs
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDurationDistribution the earliest delivery date distribution
     * @param latestDeliveryDurationDistribution the latest delivery date distribution
     */
    public DemandGeneratingProcess(final ConsumingRole role, final Product product, final DistContinuousDuration interval,
            final DistContinuous amount, final DistContinuousDuration earliestDeliveryDurationDistribution,
            final DistContinuousDuration latestDeliveryDurationDistribution)
    {
        super(role);
        this.product = product;
        this.intervalDistribution = interval;
        this.amountDistribution = amount;
        this.earliestDeliveryDurationDistribution = earliestDeliveryDurationDistribution;
        this.latestDeliveryDurationDistribution = latestDeliveryDurationDistribution;
    }

    /**
     * Make a demand generating process.
     * @param role the role to which this process belongs
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDuration the earliest delivery date
     * @param latestDeliveryDuration the latest delivery date
     */
    public DemandGeneratingProcess(final ConsumingRole role, final Product product, final DistContinuousDuration interval,
            final double amount, final Duration earliestDeliveryDuration, final Duration latestDeliveryDuration)
    {
        this(role, product, interval, new DistConstant(role.getActor().getModel().getDefaultStream(), amount),
                new DistContinuousDuration(
                        new DistConstant(role.getActor().getModel().getDefaultStream(), earliestDeliveryDuration.si),
                        DurationUnit.SI),
                new DistContinuousDuration(
                        new DistConstant(role.getActor().getModel().getDefaultStream(), latestDeliveryDuration.si),
                        DurationUnit.SI));
    }

    /**
     * Make a demand generating process.
     * @param role the role to which this process belongs
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDurationDistribution the earliest delivery date distribution
     * @param latestDeliveryDurationDistribution the latest delivery date distribution
     */
    public DemandGeneratingProcess(final ConsumingRole role, final Product product, final DistContinuousDuration interval,
            final DistDiscrete amount, final DistContinuousDuration earliestDeliveryDurationDistribution,
            final DistContinuousDuration latestDeliveryDurationDistribution)
    {
        super(role);
        this.product = product;
        this.intervalDistribution = interval;
        this.amountDistribution = amount;
        this.earliestDeliveryDurationDistribution = earliestDeliveryDurationDistribution;
        this.latestDeliveryDurationDistribution = latestDeliveryDurationDistribution;
    }

    /**
     * Make a demand generating process.
     * @param role the role to which this process belongs
     * @param product the product
     * @param interval the distribution for the demand generation interval
     * @param amount the amount of product to order
     * @param earliestDeliveryDuration the earliest delivery date
     * @param latestDeliveryDuration the latest delivery date
     */
    public DemandGeneratingProcess(final ConsumingRole role, final Product product, final DistContinuousDuration interval,
            final long amount, final Duration earliestDeliveryDuration, final Duration latestDeliveryDuration)
    {
        this(role, product, interval, new DistDiscreteConstant(role.getActor().getModel().getDefaultStream(), amount),
                new DistContinuousDuration(
                        new DistConstant(role.getActor().getModel().getDefaultStream(), earliestDeliveryDuration.si),
                        DurationUnit.SI),
                new DistContinuousDuration(
                        new DistConstant(role.getActor().getModel().getDefaultStream(), latestDeliveryDuration.si),
                        DurationUnit.SI));
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
