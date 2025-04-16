package nl.tudelft.simulation.supplychain.actor;

import java.util.Collection;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

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
public interface Actor extends NamedLocation, EventProducer
{

    /** the event to indicate that information has been sent. E.g., for animation. */
    EventType SEND_CONTENT_EVENT = new EventType("SEND_CONTENT_EVENT",
            new MetaData("sent content", "sent content", new ObjectDescriptor("content", "content", Content.class)));

    /**
     * Add a role to the actor. If the role already exists, the current role replaces the earlier role.
     * @param roleClass the class to register the role with
     * @param role the role to add to the actor
     * @param <R> the role type
     */
    <R extends Role<R>> void registerRole(Class<R> roleClass, R role);

    /**
     * Return the collection of roles for this actor.
     * @return the roles of this actor
     */
    Collection<Role<?>> getRoles();

    /**
     * Get a role of a specific type.
     * @param roleClass the class of the role to retrieve
     * @return the role belonging to that class or null when not present
     * @param <R> the role type
     */
    <R extends Role<R>> R getRole(Class<R> roleClass);

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

}
