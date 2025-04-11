package nl.tudelft.simulation.supplychain.role.consuming;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.consuming.process.DemandGeneratingProcess;

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
    public static final EventType DEMAND_GENERATED_EVENT = new EventType("DEMAND_GENERATED_EVENT",
            new MetaData("demand", "generated demand", new ObjectDescriptor("demand", "demand", Demand.class)));

    /** map of Product - Demand pairs. */
    private Map<Product, DemandGeneratingProcess> demandGenerators = new LinkedHashMap<Product, DemandGeneratingProcess>();

    /** the administrative delay when sending messages. */
    private DistContinuousDuration administrativeDelay;

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers = Set.of();

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<ConsumingRole>>> necessaryAutonomousProcesses =
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
     * Add a demand generator for a product.
     * @param demandGenerator the demand generator
     */
    public void addDemandGenerator(final DemandGeneratingProcess demandGenerator)
    {
        this.demandGenerators.put(demandGenerator.getProduct(), demandGenerator);
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


    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }


    @Override
    protected Set<Class<? extends AutonomousProcess<ConsumingRole>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }

}
