package nl.tudelft.simulation.supplychain.role.selling;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.SupplyChainRole;

/**
 * The selling role is a role that can handle several types of message content: order and payment in the minimum form. Depending
 * on the type of handling by the seller, several other messages can be handled as well. Examples are to be able to handle an
 * RFQ.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SellingRole extends SupplyChainRole
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /**
     * Create a SellingRole object for an actor.
     * @param owner SupplyChainActorInterface; the owner of this role
     */
    public SellingRole(final SupplyChainActor owner)
    {
        super(owner);
    }

}
