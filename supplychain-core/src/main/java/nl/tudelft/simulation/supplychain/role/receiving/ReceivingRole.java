package nl.tudelft.simulation.supplychain.role.receiving;

import java.util.Set;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.product.Shipment;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The receiving role is responsible for receiving shipped products from a transporter and ensuring they go into the warehouse.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ReceivingRole extends Role<ReceivingRole>
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<ReceivingRole>>> necessaryAutonomousProcesses = Set.of();

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers = Set.of(Shipment.class);

    /**
     * Create a SellingRole object for an actor.
     * @param owner the owner of this role
     */
    public ReceivingRole(final WarehousingActor owner)
    {
        super("receiving", owner, new ContentReceiverDirect());
    }

    @Override
    protected Set<Class<? extends AutonomousProcess<ReceivingRole>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }

    /** {@inheritDoc} */
    @Override
    public WarehousingActor getActor()
    {
        return (WarehousingActor) super.getActor();
    }

    /** {@inheritDoc} */
    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }

}
