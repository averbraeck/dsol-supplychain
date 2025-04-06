package nl.tudelft.simulation.supplychain.role.transporting;

import java.util.Set;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.TransportPickup;
import nl.tudelft.simulation.supplychain.content.TransportQuoteRequest;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;

/**
 * The Transporting role takes care of making transport quotes, doing the actual transporting, and sending a transport invoice.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportingRole extends Role<TransportingRole>
{
    /** */
    private static final long serialVersionUID = 20250406L;

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers =
            Set.of(TransportQuoteRequest.class, TransportPickup.class);

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<TransportingRole>>> necessaryAutonomousProcesses = Set.of();

    /**
     * Create a new Search role.
     * @param owner the actor that owns the Search role
     */
    public TransportingRole(final TransportingActor owner)
    {
        super("transporting", owner, new ContentReceiverDirect());
    }

    /** {@inheritDoc} */
    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }

    /** {@inheritDoc} */
    @Override
    protected Set<Class<? extends AutonomousProcess<TransportingRole>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }

}
