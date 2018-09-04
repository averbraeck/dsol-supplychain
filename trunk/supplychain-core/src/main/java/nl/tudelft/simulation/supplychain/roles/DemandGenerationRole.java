package nl.tudelft.simulation.supplychain.roles;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.event.TimedEvent;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.demand.Demand;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DemandGenerationRole extends Role
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** an event fired in case demand has been generated */
    public static final EventType DEMAND_GENERATED_EVENT = new EventType("DEMAND_GENERATED_EVENT");

    /** map of Product - Demand pairs */
    protected Map<Product, Demand> demandGenerators = new HashMap<Product, Demand>();

    /** the administrative delay when sending messages */
    private DistContinuousDurationUnit administrativeDelay;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(DemandGenerationRole.class);

    /**
     * @param owner the actor that has this role
     * @param simulator the simulator to schedule on
     * @param administrativeDelay the administrative delay when sending messages
     */
    public DemandGenerationRole(final SupplyChainActor owner, final DEVSSimulatorInterfaceUnit simulator,
            final DistContinuousDurationUnit administrativeDelay)
    {
        super(owner, simulator);
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
            super.simulator.scheduleEventNow(this, this, "createInternalDemand", args);
        }
        catch (Exception e)
        {
            logger.warn("init", e);
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
                        super.simulator.getSimulatorTime().get().plus(demand.getEarliestDeliveryDuration().draw()),
                        super.simulator.getSimulatorTime().get().plus(demand.getLatestDeliveryDuration().draw()));
                getOwner().sendContent(id, this.administrativeDelay.draw());
                Serializable[] args = { product, demand };
                Time time = super.simulator.getSimulatorTime().get().plus(demand.getInterval().draw());
                super.simulator.scheduleEventAbs(time, this, this, "createInternalDemand", args);

                // we collect some statistics for the internal demand
                super.fireEvent(new TimedEvent<Time>(DemandGenerationRole.DEMAND_GENERATED_EVENT, this, id,
                        super.simulator.getSimulatorTime().get()));
            }
            catch (Exception e)
            {
                logger.warn("createInternalDemand", e);
            }
        }
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
