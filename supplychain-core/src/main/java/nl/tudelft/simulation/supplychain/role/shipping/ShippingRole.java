package nl.tudelft.simulation.supplychain.role.shipping;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
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

}
