package nl.tudelft.simulation.supplychain.role.consuming.process;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.TimedEvent;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.Dist;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.consuming.ConsumingRole;

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
     * @param amountDistribution the amount of product to order (discrete or continuous)
     * @param earliestDeliveryDurationDistribution the earliest delivery date distribution
     * @param latestDeliveryDurationDistribution the latest delivery date distribution
     */
    public DemandGeneratingProcess(final ConsumingRole role, final Product product, final DistContinuousDuration interval,
            final Dist amountDistribution, final DistContinuousDuration earliestDeliveryDurationDistribution,
            final DistContinuousDuration latestDeliveryDurationDistribution)
    {
        super(role);
        this.product = product;
        this.intervalDistribution = interval;
        this.amountDistribution = amountDistribution;
        this.earliestDeliveryDurationDistribution = earliestDeliveryDurationDistribution;
        this.latestDeliveryDurationDistribution = latestDeliveryDurationDistribution;
        role.getSimulator().scheduleEventRel(this.intervalDistribution.draw(), this, "generateDemand", null);
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
     * Generate demand and send it to the PurchasingActor.
     */
    protected void generateDemand()
    {
        try
        {
            double amount = this.amountDistribution instanceof DistContinuous
                    ? ((DistContinuous) this.amountDistribution).draw() : ((DistDiscrete) this.amountDistribution).draw();
            Demand demand = new Demand(getActor(), this.product, amount,
                    getSimulatorTime().plus(this.earliestDeliveryDurationDistribution.draw()),
                    getSimulatorTime().plus(this.latestDeliveryDurationDistribution.draw()));
            getActor().sendContent(demand, getRole().getAdministrativeDelay().draw());
            getSimulator().scheduleEventRel(this.intervalDistribution.draw(), this, "generateDemand", null);

            // we might collect some statistics for the demand
            getActor().fireEvent(new TimedEvent<Time>(ConsumingRole.DEMAND_GENERATED_EVENT, demand, getSimulatorTime()));
        }
        catch (Exception e)
        {
            Logger.error(e, "createDemand");
        }
    }

    /**
     * Return the amount distribution (discrete or continuous).
     * @return the amount distribution
     */
    public Dist getAmountDistribution()
    {
        return this.amountDistribution;
    }

    /**
     * Return the demand generation interval distribution.
     * @return the demand generation interval distribution
     */
    public DistContinuousDuration getIntervalDistribution()
    {
        return this.intervalDistribution;
    }

    /**
     * Return the product to be generated.
     * @return the product to be generated
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * Return the earliest delivery date distribution function (returns a Duration).
     * @return the earliest delivery date distribution function (returns a Duration)
     */
    public DistContinuousDuration getEarliestDeliveryDurationDistribution()
    {
        return this.earliestDeliveryDurationDistribution;
    }

    /**
     * Return the latest delivery date distribution function (returns a Duration).
     * @return the latest delivery date distribution function (returns a Duration)
     */
    public DistContinuousDuration getLatestDeliveryDurationDistribution()
    {
        return this.latestDeliveryDurationDistribution;
    }

}
