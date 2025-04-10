package nl.tudelft.simulation.supplychain.role.directing;

import java.util.Set;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;

/**
 * Directing takes care of setting the most important variables for running the organization. What profit margins do we use?
 * What products do we sell? In which markets?
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class DirectingRole extends Role<DirectingRole>
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers = Set.of();

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<DirectingRole>>> necessaryAutonomousProcesses = Set.of();

    /**
     * Create a new Directing role.
     * @param owner the actor that owns the Directing role
     */
    public DirectingRole(final DirectingActorSelling owner)
    {
        super("directing", owner, new ContentReceiverDirect());
    }

    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }

    @Override
    protected Set<Class<? extends AutonomousProcess<DirectingRole>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }

}
