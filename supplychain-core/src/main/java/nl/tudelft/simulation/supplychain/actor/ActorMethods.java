package nl.tudelft.simulation.supplychain.actor;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;

/**
 * ActorMethods provides access to a number of 'convenience' methods through the actor.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface ActorMethods
{
    /**
     * Return the actor to which this role belongs.
     * @return the actor to which this role belongs
     */
    Actor getActor();
    
    /**
     * Return the ContentStore for the Actor.
     * @return the content store.
     */
    default ContentStoreInterface getContentStore()
    {
        return getActor().getContentStore();
    }

    /**
     * Return the model that the actor is a part of.
     * @return the model
     */
    default SupplyChainModelInterface getModel()
    {
        return getActor().getModel();
    }

    /**
     * Return the default stream for a distribution.
     * @return the the default stream
     */
    default StreamInterface getDefaultStream()
    {
        return getModel().getDefaultStream();
    }

    /**
     * Return the simulator to schedule simulation events on.
     * @return the simulator
     */
    default SupplyChainSimulatorInterface getSimulator()
    {
        return getModel().getSimulator();
    }

    /**
     * Return the current simulation time.
     * @return the current simulation time
     */
    default Time getSimulatorTime()
    {
        return getSimulator().getAbsSimulatorTime();
    }
    
    /**
     * Send content to another actor or this actor with a delay. This method is public, so Roles, Policies, Departments, and
     * other sub-components of the Actor can send content on its behalf. The method has the risk that the conent is sent from
     * the wrong actor. When this happens, i.e., when the message is not originating from this actor, a log warning is given,
     * but the content itself is sent.
     * @param content the content to send to another actor or to this actor
     * @param delay the time it takes between sending and receiving
     */
    default void sendContent(final Content content, final Duration delay)
    {
        getActor().sendContent(content, delay);
    }

    /**
     * Send content to another actor or to this actor without a delay.
     * @param content the content to send to another actor or to this actor
     */
    default void sendContent(final Content content)
    {
        getActor().sendContent(content);
    }
}
