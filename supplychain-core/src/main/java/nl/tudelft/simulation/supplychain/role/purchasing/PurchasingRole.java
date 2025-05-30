package nl.tudelft.simulation.supplychain.role.purchasing;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;

/**
 * The purchasing role is a role that can handle several types of message content: demand, order confirmation, invoice, and
 * shipment. Depending on the extension of the PurchasingRole, which actually indicates the type if DemandHandler used, several
 * other messages can be handled as well. For the DemandHandlerOrder, no extra types are necessary. For the DemandhandlerRFQ, a
 * Quote has to be handled as well. For an DemandhandlerSearch, a SearchAnswer can be received, and has to be handled.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class PurchasingRole extends Role<PurchasingRole>
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /**
     * Create a PurchasingRole object for an actor with a default message receiver.
     * @param owner the owner of this role
     */
    public PurchasingRole(final PurchasingActor owner)
    {
        this(owner, new ContentReceiverDirect());
    }

    /**
     * Create a PurchasingRole object for an actor with a specific message receiver.
     * @param owner the owner of this role
     * @param messageReceiver the message receiver to use
     */
    public PurchasingRole(final PurchasingActor owner, final ContentReceiver messageReceiver)
    {
        super("purchasing", owner, messageReceiver);
    }

    @Override
    public PurchasingActor getActor()
    {
        return (PurchasingActor) super.getActor();
    }

}
