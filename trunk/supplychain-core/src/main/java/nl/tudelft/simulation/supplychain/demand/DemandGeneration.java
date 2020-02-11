package nl.tudelft.simulation.supplychain.demand;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.EventType;
import org.djutils.event.TimedEvent;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.actor.InternalActor;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * Demand generation.<br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DemandGeneration extends InternalActor
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** an event fired in case demand has been generated */
    public static final EventType DEMAND_GENERATED_EVENT = new EventType("DEMAND_GENERATED_EVENT");

    /** map of Product - Demand pairs */
    protected Map<Product, Demand> demandGenerators = new HashMap<Product, Demand>();

    /** the administrative delay when sending messages */
    private DistContinuousDurationUnit administrativeDelay;

    /** the owner of the role */
    protected SupplyChainActor owner = null;

    /** the default stream to use for the time delays */
    protected StreamInterface stream = null;

    /**
     * @param owner the actor that has this role
     * @param simulator the simulator to schedule on
     * @param administrativeDelay the administrative delay when sending messages
     */
    public DemandGeneration(final SupplyChainActor owner, final DEVSSimulatorInterface.TimeDoubleUnit simulator,
            final DistContinuousDurationUnit administrativeDelay)
    {
        super(owner.getName() + "-DEMAND", simulator);
        this.owner = owner;
        this.administrativeDelay = administrativeDelay;
    }

    /**
     * @param product the product
     * @param demand the demand
     */
    public void addDemandGenerator(final Product product, final Demand demand)
    {
        this.demandGenerators.put(product, demand);
        try
        {
            Serializable[] args = { product, demand };
            super.simulator.scheduleEventRel(demand.getInterval().draw().multiplyBy(0.5), this, this, "createInternalDemand", args);
        }
        catch (Exception e)
        {
            Logger.error(e, "addDemandGenerator");
        }
    }

    /**
     * Method getDemandGenerator
     * @param product the product to return the demand generator for
     * @return Returns a demand, or null if it could not be found
     */
    public Demand getDemandGenerator(final Product product)
    {
        return this.demandGenerators.get(product);
    }

    /**
     * @param product the product
     */
    public void removeDemandGenerator(final Product product)
    {
        this.demandGenerators.remove(product);
    }

    /**
     * @param product the product
     * @param demand the demand
     */
    protected void createInternalDemand(final Product product, final Demand demand)
    {
        // is the (same) demand still there?
        if (this.demandGenerators.get(product).equals(demand))
        {
            try
            {
                InternalDemand id = new InternalDemand(getOwner(), product, demand.getAmount().draw(),
                        super.simulator.getSimulatorTime().plus(demand.getEarliestDeliveryDuration().draw()),
                        super.simulator.getSimulatorTime().plus(demand.getLatestDeliveryDuration().draw()));
                getOwner().sendContent(id, this.administrativeDelay.draw());
                Serializable[] args = { product, demand };
                Time time = super.simulator.getSimulatorTime().plus(demand.getInterval().draw());
                super.simulator.scheduleEventAbs(time, this, this, "createInternalDemand", args);

                // we collect some statistics for the internal demand
                super.fireEvent(new TimedEvent<Time>(DemandGeneration.DEMAND_GENERATED_EVENT, this, id,
                        super.simulator.getSimulatorTime()));
            }
            catch (Exception e)
            {
                Logger.error(e, "createInternalDemand");
            }
        }
    }

    /**
     * @return owner
     */
    public SupplyChainActor getOwner()
    {
        return this.owner;
    }

    /**
     * @return Returns the administrativeDelay.
     */
    public DistContinuousDurationUnit getAdministrativeDelay()
    {
        return this.administrativeDelay;
    }

    /**
     * @param administrativeDelay The administrativeDelay to set.
     */
    public void setAdministrativeDelay(final DistContinuousDurationUnit administrativeDelay)
    {
        this.administrativeDelay = administrativeDelay;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.owner.getName() + "-DemandGenerationRole";
    }
}
