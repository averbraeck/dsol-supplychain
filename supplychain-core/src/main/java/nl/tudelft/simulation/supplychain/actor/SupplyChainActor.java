package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.DirectedPoint2d;
import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.ImmutableLinkedHashSet;
import org.djutils.immutablecollections.ImmutableSet;
import org.djutils.logger.CategoryLogger;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;

/**
 * SupplyChainActor is the abstract class for an Actor that implements the behavior of a 'communicating' object, that is able to
 * exchange messages with other actors and process the incoming messages through the policies that are present in the Roles that
 * the Actor fulfills. The Actor delegates the handling of its messages to it roles.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SupplyChainActor implements Actor
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the id of the actor. */
    private final String id;

    /** the longer name of the actor. */
    private final String name;

    /** the location description of the actor (e.g., a city, country). */
    private final String locationDescription;

    /** the model. */
    private final SupplyChainModelInterface model;

    /** the roles. */
    private ImmutableSet<Role> roles = new ImmutableLinkedHashSet<>(new LinkedHashSet<>());

    /** the embedded event producer. */
    private final EventProducer eventProducer;

    /** the location of the actor. */
    private final DirectedPoint2d location;

    /** the bounds of the object (size and relative height in the animation). */
    private Bounds2d bounds = new Bounds2d(-1.0, 1.0, -1.0, 1.0);

    /** the store for the content to use. */
    private final TradeMessageStoreInterface messageStore;

    /** the event to indicate that information has been sent. E.g., for animation. */
    public static final EventType SEND_MESSAGE_EVENT = new EventType("SEND_MESSAGE_EVENT",
            new MetaData("sent message", "sent message", new ObjectDescriptor("message", "message", Message.class)));

    /**
     * Construct a new Actor.
     * @param id String, the unique id of the actor
     * @param name String; the longer name of the actor
     * @param model SupplyChainModelInterface; the model
     * @param location OrientedPoint2d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     * @param eventProducer EventProducer; a special EventProducer to use, e.g., a RmiEventProducer
     * @param messageStore TradeMessageStoreInterface; the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public SupplyChainActor(final String id, final String name, final SupplyChainModelInterface model,
            final DirectedPoint2d location, final String locationDescription, final EventProducer eventProducer,
            final TradeMessageStoreInterface messageStore) throws ActorAlreadyDefinedException
    {
        Throw.whenNull(model, "model cannot be null");
        Throw.whenNull(id, "name cannot be null");
        Throw.when(id.length() == 0, IllegalArgumentException.class, "id of actor cannot be null");
        Throw.whenNull(name, "name cannot be null");
        Throw.whenNull(location, "location cannot be null");
        Throw.whenNull(locationDescription, "locationDescription cannot be null");
        Throw.whenNull(eventProducer, "eventProducer cannot be null");
        Throw.whenNull(messageStore, "messageStore cannot be null");
        this.id = id;
        this.name = name;
        this.locationDescription = locationDescription;
        this.model = model;
        this.location = location;
        this.eventProducer = eventProducer;
        this.messageStore = messageStore;
        this.messageStore.setOwner(this);
        model.registerActor(this);
    }

    /**
     * Construct a new Actor with a LocalEventProducer. The actor delegates all message handling to one or more Roles.
     * @param id String, the unique id of the actor
     * @param name String; the longer name of the actor
     * @param model SupplyChainModelInterface; the model
     * @param location OrientedPoint2d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     * @param messageStore TradeMessageStoreInterface; the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public SupplyChainActor(final String id, final String name, final SupplyChainModelInterface model,
            final DirectedPoint2d location, final String locationDescription, final TradeMessageStoreInterface messageStore)
            throws ActorAlreadyDefinedException
    {
        this(id, name, model, location, locationDescription, new LocalEventProducer(), messageStore);
    }

    /** {@inheritDoc} */
    @Override
    public void addRole(final Role role)
    {
        Throw.whenNull(role, "role cannot be null");
        Set<Role> newRoles = this.roles.toSet();
        newRoles.add(role);
        this.roles = new ImmutableLinkedHashSet<>(newRoles);
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableSet<Role> getRoles()
    {
        return this.roles;
    }

    /** {@inheritDoc} */
    @Override
    public void receiveMessage(final Message message)
    {
        checkNecessaryRoles();
        if (!message.getReceiver().equals(this))
        {
            CategoryLogger.always().warn("Message " + message + " not meant for receiver " + toString());
        }
        else
        {
            boolean processed = false;
            for (Role role : getRoles())
            {
                processed |= role.handleMessage(message);
            }
            if (!processed)
            {
                Logger.warn(this.toString() + " does not have a handler for " + message.getClass().getSimpleName());
            }
        }
        if (message instanceof TradeMessage)
        {
            this.messageStore.addMessage((TradeMessage) message, false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(final Message message, final Duration delay)
    {
        if (!message.getSender().equals(this))
        {
            CategoryLogger.always().warn("Message " + message + " not originating from sender " + toString());
        }
        getSimulator().scheduleEventRel(delay, message.getReceiver(), "receiveMessage", new Object[] {message});
        if (message instanceof TradeMessage)
        {
            this.messageStore.addMessage((TradeMessage) message, true);
        }
        fireEvent(SEND_MESSAGE_EVENT, new Object[] {message});
    }

    /** {@inheritDoc} */
    @Override
    public void fireEvent(final EventType eventType, final Serializable value)
    {
        try
        {
            this.eventProducer.fireEvent(eventType, value);
        }
        catch (RemoteException e)
        {
            CategoryLogger.always().error(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Comparable<C> & Serializable> void fireTimedEvent(final EventType eventType, final Serializable value,
            final C time)
    {
        try
        {
            this.eventProducer.fireTimedEvent(eventType, value, time);
        }
        catch (RemoteException e)
        {
            CategoryLogger.always().error(e);
        }
    }

    /**
     * Return the short id of the actor.
     * @return String; the short id of the actor
     */
    @Override
    public String getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public String getLocationDescription()
    {
        return this.locationDescription;
    }

    /** {@inheritDoc} */
    @Override
    public TradeMessageStoreInterface getMessageStore()
    {
        return this.messageStore;
    }

    /** {@inheritDoc} */
    @Override
    public SupplyChainModelInterface getModel()
    {
        return this.model;
    }

    /** {@inheritDoc} */
    @Override
    public EventListenerMap getEventListenerMap() throws RemoteException
    {
        return this.eventProducer.getEventListenerMap();
    }

    /** {@inheritDoc} */
    @Override
    public DirectedPoint2d getLocation()
    {
        return this.location;
    }

    @Override
    public void setBounds(final Bounds2d bounds)
    {
        this.bounds = bounds;
    }

    /** {@inheritDoc} */
    @Override
    public Bounds2d getBounds()
    {
        return this.bounds;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SupplyChainActor other = (SupplyChainActor) obj;
        return Objects.equals(this.id, other.id);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.id;
    }

}
