package nl.tudelft.simulation.supplychain.role.consuming;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.EventType;
import org.djutils.event.TimedEvent;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;
import nl.tudelft.simulation.supplychain.role.financing.FixedCostProcess;

/**
 * The consuming role is a role for customers, markets, and other actors that have an autonomous generation of demand for
 * products. This is different from the warehousing role, where demand generation is triggered by depletion of stock.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ConsumingRole extends Role<ConsumingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221206L;

    /** an event fired in case demand has been generated. */
    public static final EventType DEMAND_GENERATED_EVENT = new EventType("DEMAND_GENERATED_EVENT");

    /** map of Product - Demand pairs. */
    private Map<Product, DemandGeneratingProcess> demandGenerators = new LinkedHashMap<Product, DemandGeneratingProcess>();

    /** the administrative delay when sending messages. */
    private DistContinuousDuration administrativeDelay;

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers = Set.of();

    /** the necessary content handlers. */
    private static Set<Class<? extends AutonomousProcess<FinancingRole>>> necessaryAutonomousProcesses =
            Set.of(DemandGeneratingProcess.class);

    /**
     * @param owner the actor that has this role
     * @param administrativeDelay the administrative delay when sending messages
     */
    public ConsumingRole(final ConsumingActor owner, final DistContinuousDuration administrativeDelay)
    {
        super("consuming", owner, new ContentReceiverDirect());
        this.administrativeDelay = administrativeDelay;
    }

    /**
     * @param product the product
     * @param demand the demand
     */
    public void addDemandGenerator(final Product product, final DemandGeneratingProcess demand)
    {
        this.demandGenerators.put(product, demand);
        try
        {
            Serializable[] args = {product, demand};
            getSimulator().scheduleEventRel(demand.getIntervalDistribution().draw(), this, "createInternalDemand", args);
        }
        catch (Exception e)
        {
            Logger.error(e, "addDemandGenerator");
        }
    }

    /**
     * Method getDemandGenerator.
     * @param product the product to return the demand generator for
     * @return a demand, or null if it could not be found
     */
    public DemandGeneratingProcess getDemandGenerator(final Product product)
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
    protected void createInternalDemand(final Product product, final DemandGeneratingProcess demand)
    {
        // is the (same) demand still there?
        if (this.demandGenerators.get(product).equals(demand))
        {
            try
            {
                double amount = demand.getAmountDistribution() instanceof DistContinuous
                        ? ((DistContinuous) demand.getAmountDistribution()).draw()
                        : ((DistDiscrete) demand.getAmountDistribution()).draw();
                InternalDemand id = new InternalDemand(getActor(), product, amount,
                        getSimulator().getAbsSimulatorTime().plus(demand.getEarliestDeliveryDurationDistribution().draw()),
                        getSimulator().getAbsSimulatorTime().plus(demand.getLatestDeliveryDurationDistribution().draw()));
                getActor().sendContent(id, this.administrativeDelay.draw());
                Serializable[] args = {product, demand};
                Time time = getSimulator().getAbsSimulatorTime().plus(demand.getIntervalDistribution().draw());
                getSimulator().scheduleEventAbs(time, this, "createInternalDemand", args);

                // we might collect some statistics for the internal demand
                getActor().fireEvent(
                        new TimedEvent<Time>(ConsumingRole.DEMAND_GENERATED_EVENT, id, getSimulator().getAbsSimulatorTime()));
            }
            catch (Exception e)
            {
                Logger.error(e, "createInternalDemand");
            }
        }
    }

    @Override
    public String getId()
    {
        return getActor().getId() + "-DEMAND(periodic)";
    }

    /**
     * @return the administrativeDelay.
     */
    public DistContinuousDuration getAdministrativeDelay()
    {
        return this.administrativeDelay;
    }

    /**
     * @param administrativeDelay The administrativeDelay to set.
     */
    public void setAdministrativeDelay(final DistContinuousDuration administrativeDelay)
    {
        this.administrativeDelay = administrativeDelay;
    }

    @Override
    public String toString()
    {
        return getId();
    }

}
