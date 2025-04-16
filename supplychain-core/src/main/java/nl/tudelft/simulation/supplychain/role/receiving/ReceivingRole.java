package nl.tudelft.simulation.supplychain.role.receiving;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
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

    /**
     * Create a SellingRole object for an actor.
     * @param owner the owner of this role
     */
    public ReceivingRole(final WarehousingActor owner)
    {
        super("receiving", owner, new ContentReceiverDirect());
    }

    @Override
    public WarehousingActor getActor()
    {
        return (WarehousingActor) super.getActor();
    }

}
