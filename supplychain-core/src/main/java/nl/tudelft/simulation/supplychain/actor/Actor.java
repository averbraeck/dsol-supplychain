package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.base.Identifiable;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;
import org.djutils.event.EventProducer;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;

/**
 * The Actor interface defines the behavior of a 'communicating' object, that is able to exchange messages with other actors and
 * process the incoming messages through the policies that are present in the Roles that the Actor fulfills. The Actor delegates
 * the handling of its messages to it roles.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface Actor extends EventProducer, Locatable, Identifiable, Serializable
{
    /**
     * Add a role to the actor. If the role already exists, the current role replaces the earlier role.
     * @param role the role to add to the actor
     */
    void addRole(Role<?> role);

    /**
     * Return the set of roles for this actor.
     * @return the roles of this actor
     */
    Set<Role<?>> getRoles();

    /**
     * Check whether the necessary roles are set and filled with the required handlers and processes before executing a
     * role-dependent method.
     * @return whether the necessary roles are set and filled with handlers and processes
     */
    boolean checkRolesComplete();

    /**
     * Receive content, e.g. a message, from another actor, and handle it (storing or handling, depending on the
     * MessageReceiver). When the content is not intended for this actor, a log warning is given, and the content is not
     * processed.
     * @param content the content to handle
     */
    void receiveContent(Content content);

    /**
     * Send content to another actor or this actor with a delay. This method is public, so Roles, Policies, Departments, and
     * other sub-components of the Actor can send content on its behalf. The method has the risk that the conent is sent from
     * the wrong actor. When this happens, i.e., when the message is not originating from this actor, a log warning is given,
     * but the content itself is sent.
     * @param content the content to send to another actor or to this actor
     * @param delay the time it takes between sending and receiving
     */
    void sendContent(Content content, Duration delay);

    /**
     * Send content to another actor or to this actor without a delay.
     * @param content the content to send to another actor or to this actor
     */
    default void sendContent(final Content content)
    {
        sendContent(content, Duration.ZERO);
    }

    /**
     * Return the longer name of the actor.
     * @return the longer name of the actor
     */
    String getName();

    /**
     * Return the location description of the actor (e.g., a city, country).
     * @return the location description of the actor
     */
    String getLocationDescription();

    /**
     * Return the ContentStore for the Actor.
     * @return the content store.
     */
    ContentStoreInterface getContentStore();

    /**
     * Return the model that this actor is a part of.
     * @return the model
     */
    SupplyChainModelInterface getModel();

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
     * Return the geography of the actor with the access to transfer locations of different modes of transport.
     * @return the geography of the actor with the access to transfer locations of different modes of transport
     */
    Geography getGeography();
    
    @Override
    Point2d getLocation();

    /**
     * Return the z-value of the location, or 0.0 when the location is in 2 dimensions, avoiding the RemoteException.
     * @return the z-value of the location, or 0.0 when the location is in 2 dimensions, or when getLocation() returns null
     */
    @Override
    default double getZ()
    {
        return 0.0;
    }

    /**
     * Return the z-direction of the location in radians, or 0.0 when the location has no direction, , avoiding the
     * RemoteException.
     * @return the z-direction of the location in radians, or 0.0 when the location has no direction, or when getLocation()
     *         returns null
     */
    @Override
    default double getDirZ()
    {
        return 0.0;
    }

    @Override
    Bounds2d getBounds();

    /**
     * @param bounds
     */
    void setBounds(Bounds2d bounds);

}
