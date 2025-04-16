package nl.tudelft.simulation.supplychain.role.shipping;

import java.util.Set;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.ShippingOrder;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The ShippingRole is concerned with booking transport and taking products out of the warehouse to have them shipped by a
 * transporter.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShippingRole extends Role<ShippingRole>
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<ShippingRole>>> necessaryAutonomousProcesses = Set.of();

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers = Set.of(ShippingOrder.class);

    /**
     * Create a SellingRole object for an actor.
     * @param owner the owner of this role
     */
    public ShippingRole(final WarehousingActor owner)
    {
        super("shipping", owner, new ContentReceiverDirect());
    }

    @Override
    public WarehousingActor getActor()
    {
        return (WarehousingActor) super.getActor();
    }

    @Override
    protected Set<Class<? extends AutonomousProcess<ShippingRole>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }

    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }
}
