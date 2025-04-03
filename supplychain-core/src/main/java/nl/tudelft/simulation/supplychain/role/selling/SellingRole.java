package nl.tudelft.simulation.supplychain.role.selling;

import java.util.Set;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;

/**
 * The selling role is a role that can handle several types of message content: order and payment in the minimum form. Depending
 * on the type of handling by the seller, several other messages can be handled as well. Examples are to be able to handle an
 * RFQ.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SellingRole extends Role<SellingRole>
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<SellingRole>>> necessaryAutonomousProcesses = Set.of();

    /**
     * Create a SellingRole object for an actor.
     * @param owner the owner of this role
     */
    public SellingRole(final SellingActor owner)
    {
        super("selling", owner, new ContentReceiverDirect());
    }

    @Override
    protected Set<Class<? extends AutonomousProcess<SellingRole>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }
}
