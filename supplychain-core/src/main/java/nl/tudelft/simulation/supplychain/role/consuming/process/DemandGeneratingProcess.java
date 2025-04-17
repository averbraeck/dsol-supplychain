package nl.tudelft.simulation.supplychain.role.consuming.process;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.TimedEvent;
import org.djutils.exceptions.Throw;
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
import nl.tudelft.simulation.supplychain.role.consuming.ConsumingActor;
import nl.tudelft.simulation.supplychain.role.consuming.ConsumingRole;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

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

    /** the duration till the first generation. */
    private Time startTime;

    /** the interval between demand requests. */
    private DistContinuousDuration intervalDistribution;

    /** the amount of products to order or make. */
    private Dist amountDistribution;

    /** the earliest delivery date relative to the current simulator time. */
    private DistContinuousDuration earliestDeliveryDurationDistribution;

    /** the latest delivery date relative to the current simulator time. */
    private DistContinuousDuration latestDeliveryDurationDistribution;

    /** the maximum number of generations, e.g. for testing. */
    private int maxNumberGenerations = Integer.MAX_VALUE;

    /** the number of generations, e.g. for testing. */
    private int numberGenerations = 0;

    /** the stop time of the enerating process. */
    private Time stopTime = Time.instantiateSI(Double.MAX_VALUE);

    /**
     * Make a demand generating process.
     * @param actor the actor to which this process belongs
     * @param product the product
     */
    public DemandGeneratingProcess(final ConsumingActor actor, final Product product)
    {
        super(actor.getConsumingRole());
        Throw.whenNull(product, "product should not be null");
        this.product = product;
        this.startTime = getSimulatorTime();
        this.amountDistribution = new DistDiscreteConstant(getDefaultStream(), 1);
    }

    /**
     * Start the generation process. Should always be the last method call in the method chain.
     */
    public void start()
    {
        Throw.when(this.intervalDistribution == null, IllegalStateException.class,
                "intervalDistribution has not been initialized");
        getRole().addAutonomousProcess(this);
        getRole().getSimulator().scheduleEventAbs(this.startTime, this, "generateDemand", null);
    }

    /**
     * Set the duration distribution till the first generation.
     * @param startDurationDistribution the duration distribution till the first generation
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setStartAfter(final DistContinuousDuration startDurationDistribution)
    {
        Throw.whenNull(startDurationDistribution, "startDurationDistribution should not be null");
        this.startTime = getSimulatorTime().plus(startDurationDistribution.draw());
        return this;
    }

    /**
     * Set the duration till the first generation.
     * @param startDuration the duration till the first generation
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setStartAfter(final Duration startDuration)
    {
        Throw.whenNull(startDuration, "startDuration should not be null");
        Throw.when(startDuration.si < 0, IllegalArgumentException.class, "startDuration cannot be negative");
        this.startTime = getSimulatorTime().plus(startDuration);
        return this;
    }

    /**
     * Set the duration till the first generation equal to the interval distribution.
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setStartAfterInterval()
    {
        Throw.when(this.intervalDistribution == null, IllegalStateException.class,
                "setStartDurationToInterval called, but interval == null");
        this.startTime = getSimulatorTime().plus(this.intervalDistribution.draw());
        return this;
    }

    /**
     * Set the duration till the first generation equal to zero.
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setStartNow()
    {
        this.startTime = getSimulatorTime();
        return this;
    }

    /**
     * Set the time of the first generation.
     * @param startAtTime the time of the first generation
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setStartAt(final Time startAtTime)
    {
        Throw.whenNull(startAtTime, "startTime should not be null");
        Throw.when(startAtTime.si < getSimulatorTime().si, IllegalArgumentException.class,
                "startTime cannot be before current time");
        this.startTime = startAtTime;
        return this;
    }

    /**
     * Set a new value for stopTime.
     * @param stopAtTime set a new value for stopTime
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setStopAt(final Time stopAtTime)
    {
        Throw.whenNull(stopAtTime, "stopTime should not be null");
        Throw.when(stopAtTime.si < getSimulatorTime().si, IllegalArgumentException.class,
                "stopTime cannot be before current time");
        Throw.when(stopAtTime.si < this.startTime.si, IllegalArgumentException.class, "stopTime cannot be before startTime");
        this.stopTime = stopAtTime;
        return this;
    }

    /**
     * Set the duration for the generator to work after the startTime.
     * @param workingDuration the duration for the generator to work after the startTime
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setStopAfter(final Duration workingDuration)
    {
        Throw.whenNull(workingDuration, "workingDuration should not be null");
        Throw.when(workingDuration.si < 0, IllegalArgumentException.class, "workingDuration cannot be negative");
        this.stopTime = this.startTime.plus(workingDuration);
        return this;
    }

    /**
     * Set a new value for the interval between demand requests.
     * @param newIntervalDistribution a new value for the interval between demand requests
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setIntervalDistribution(final DistContinuousDuration newIntervalDistribution)
    {
        Throw.whenNull(newIntervalDistribution, "newIntervalDistribution should not be null");
        this.intervalDistribution = newIntervalDistribution;
        return this;
    }

    /**
     * Set a new value for amountDistribution.
     * @param newAmountDistribution set a new value for amountDistribution
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setAmountDistribution(final Dist newAmountDistribution)
    {
        Throw.whenNull(newAmountDistribution, "newAmountDistribution should not be null");
        this.amountDistribution = newAmountDistribution;
        return this;
    }

    /**
     * Set a new value for the amount of generated products.
     * @param amount a new value for the amount of generated products
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setAmount(final int amount)
    {
        Throw.when(amount <= 0, IllegalArgumentException.class, "amount should be positive");
        this.amountDistribution = new DistDiscreteConstant(getDefaultStream(), amount);
        return this;
    }

    /**
     * Set a new value for the amount of generated products.
     * @param amount a new value for the amount of generated products
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setAmount(final double amount)
    {
        Throw.when(amount <= 0, IllegalArgumentException.class, "amount should be positive");
        this.amountDistribution = new DistConstant(getDefaultStream(), amount);
        return this;
    }

    /**
     * Set a new value for earliestDeliveryDurationDistribution.
     * @param newEarliestDeliveryDurationDistribution set a new value for earliestDeliveryDurationDistribution
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setEarliestDeliveryDurationDistribution(
            final DistContinuousDuration newEarliestDeliveryDurationDistribution)
    {
        Throw.whenNull(newEarliestDeliveryDurationDistribution, "newEarliestDeliveryDurationDistribution should not be null");
        this.earliestDeliveryDurationDistribution = newEarliestDeliveryDurationDistribution;
        return this;
    }

    /**
     * Set a new value for earliestDeliveryDuration.
     * @param earliestDeliveryDuration set a new value for earliestDeliveryDuration
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setEarliestDeliveryDuration(final Duration earliestDeliveryDuration)
    {
        Throw.whenNull(earliestDeliveryDuration, "earliestDeliveryDuration should not be null");
        Throw.when(earliestDeliveryDuration.si < 0, IllegalArgumentException.class,
                "earliestDeliveryDuration cannot be negative");
        this.earliestDeliveryDurationDistribution = new DistConstantDuration(earliestDeliveryDuration);
        return this;
    }

    /**
     * Set a new value for latestDeliveryDurationDistribution.
     * @param newLatestDeliveryDurationDistribution set a new value for latestDeliveryDurationDistribution
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setLatestDeliveryDurationDistribution(
            final DistContinuousDuration newLatestDeliveryDurationDistribution)
    {
        Throw.whenNull(newLatestDeliveryDurationDistribution, "newLatestDeliveryDurationDistribution should not be null");
        this.latestDeliveryDurationDistribution = newLatestDeliveryDurationDistribution;
        return this;
    }

    /**
     * Set a new value for latestDeliveryDuration.
     * @param latestDeliveryDuration set a new value for latestDeliveryDuration
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setLatestDeliveryDuration(final Duration latestDeliveryDuration)
    {
        Throw.whenNull(latestDeliveryDuration, "latestDeliveryDuration should not be null");
        Throw.when(latestDeliveryDuration.si < 0, IllegalArgumentException.class, "latestDeliveryDuration cannot be negative");
        this.latestDeliveryDurationDistribution = new DistConstantDuration(latestDeliveryDuration);
        return this;
    }

    /**
     * Set a new value for the maximum number of time the generation process takes place.
     * @param newMaxNumberGenerations the new value for maxNumberGenerations
     * @return the object for method chaining
     */
    public DemandGeneratingProcess setMaxNumberGenerations(final int newMaxNumberGenerations)
    {
        Throw.when(newMaxNumberGenerations < 0, IllegalArgumentException.class, "newMaxNumberGenerations cannot be negative");
        this.maxNumberGenerations = newMaxNumberGenerations;
        return this;
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
            amount = Math.max(0.0, amount);
            var ed = Duration.max(Duration.ZERO, this.earliestDeliveryDurationDistribution.draw());
            var ld = Duration.max(ed, this.latestDeliveryDurationDistribution.draw());
            Demand demand =
                    new Demand(getActor(), this.product, amount, getSimulatorTime().plus(ed), getSimulatorTime().plus(ld));
            getActor().sendContent(demand, getRole().getAdministrativeDelay().draw());
            this.numberGenerations++;
            if (this.numberGenerations < this.maxNumberGenerations && getSimulatorTime().lt(this.stopTime))
            {
                getSimulator().scheduleEventRel(this.intervalDistribution.draw(), this, "generateDemand", null);
            }

            // we might collect some statistics for the demand
            getActor().fireEvent(new TimedEvent<Time>(ConsumingRole.DEMAND_GENERATED_EVENT, demand, getSimulatorTime()));
        }
        catch (Exception e)
        {
            Logger.error(e, "createDemand");
        }
    }

    /**
     * Return the startTime of the demand generator.
     * @return the startTime of the demand generator
     */
    public Time getStartTime()
    {
        return this.startTime;
    }

    /**
     * Return the stopTime.
     * @return stopTime
     */
    public Time getStopTime()
    {
        return this.stopTime;
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

    /**
     * Return the maxNumberGenerations.
     * @return maxNumberGenerations
     */
    public int getMaxNumberGenerations()
    {
        return this.maxNumberGenerations;
    }

    /**
     * Return the numberGenerations.
     * @return numberGenerations
     */
    public int getNumberGenerations()
    {
        return this.numberGenerations;
    }

}
