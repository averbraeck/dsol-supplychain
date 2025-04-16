package nl.tudelft.simulation.supplychain.animation;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.Event;
import org.djutils.event.EventListener;

import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;

/**
 * ContentAnimator.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContentAnimator implements EventListener
{
    /** */
    private static final long serialVersionUID = 20230409L;
    
    /** the simulator. */
    private final SupplyChainSimulatorInterface simulator;

    /**
     * Create an animator for the moving content that listens to the Actor.SEND_CONTENT_EVENT.
     * @param simulator the simulator
     */
    public ContentAnimator(final SupplyChainSimulatorInterface simulator)
    {
        this.simulator = simulator;
    }

    /**
     * Subscribe ourselves to the Actor.SEND_CONTENT_EVENT.
     * @param sca the actor that can send messages
     * @throws RemoteException on network error
     */
    public void subscribe(final Actor sca) throws RemoteException
    {
        sca.addListener(this, Actor.SEND_CONTENT_EVENT);
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(Actor.SEND_CONTENT_EVENT))
        {
            if (this.simulator instanceof AnimatorInterface)
            {
                Content content = (Content) event.getContent();
                new ContentAnimation(content, Duration.instantiateSI(3600));
            }
        }
    }

    /**
     * @return the simulator
     */
    public SupplyChainSimulatorInterface getSimulator()
    {
        return this.simulator;
    }

}
