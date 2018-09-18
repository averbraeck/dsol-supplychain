package nl.tudelft.simulation.supplychain.animation;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventListenerInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Content;

/**
 * ContentAnimator.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ContentAnimator implements EventListenerInterface
{
    /** the simulator. */
    private final DEVSSimulatorInterface.TimeDoubleUnit simulator;

    /**
     * Create an animator for the moving content that listens to the SupplyChainActor.SEND_CONTENT_EVENT
     * @param simulator the simulator
     */
    public ContentAnimator(final DEVSSimulatorInterface.TimeDoubleUnit simulator)
    {
        this.simulator = simulator;
    }

    /**
     * Subscribe ourselves to the SupplyChainActor.SEND_CONTENT_EVENT.
     * @param sca the actor that can send messages
     */
    public void subscribe(final SupplyChainActor sca)
    {
        sca.addListener(this, SupplyChainActor.SEND_CONTENT_EVENT);
    }
    
    /** {@inheritDoc} */
    @Override
    public void notify(EventInterface event) throws RemoteException
    {
        if (event.getType().equals(SupplyChainActor.SEND_CONTENT_EVENT))
        {
            if (this.simulator instanceof AnimatorInterface)
            {
                Object[] content = (Object[]) event.getContent();
                new ContentAnimation((Content) content[0], (Duration) content[1]);
            }
        }
    }

    /**
     * @return the simulator
     */
    public DEVSSimulatorInterface.TimeDoubleUnit getSimulator()
    {
        return this.simulator;
    }

}
