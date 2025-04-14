package nl.tudelft.simulation.supplychain.actor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;
import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;

/**
 * SupplyChainActor is the abstract class for an Actor that implements the behavior of a 'communicating' object, that is able to
 * exchange messages with other actors and process the incoming messages through the policies that are present in the Roles that
 * the Actor fulfills. The Actor delegates the handling of its messages to it roles.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SupplyChainActor extends LocalEventProducer implements Actor
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the id of the actor. */
    private final String id;

    /** the longer name of the actor. */
    private final String name;

    /** the model. */
    private final SupplyChainModelInterface model;

    /** the roles. */
    private Map<Class<? extends Role<?>>, Role<?>> roles = new LinkedHashMap<>();

    /** cached check whether all roles have been initialized with handlers and processes. */
    private boolean rolesComplete = false;

    /** the bounds of the object (size and relative height in the animation). */
    private Bounds2d bounds = new Bounds2d(-1.0, 1.0, -1.0, 1.0);

    /** the store for the content to use. */
    private final ContentStoreInterface contentStore;

    /** the geographical details of the actor. */
    private Geography geography;

    /**
     * Construct a new Actor, give it a message store, and register it in the model.
     * @param id String, the unique id of the actor
     * @param name the longer name of the actor
     * @param model the model
     * @param geography the geographical details of the actor
     * @param contentStore the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    public SupplyChainActor(final String id, final String name, final SupplyChainModelInterface model,
            final Geography geography, final ContentStoreInterface contentStore) throws ActorAlreadyDefinedException
    {
        Throw.whenNull(model, "model cannot be null");
        Throw.whenNull(id, "name cannot be null");
        Throw.when(id.length() == 0, IllegalArgumentException.class, "id of actor cannot be empty");
        Throw.whenNull(name, "name cannot be null");
        Throw.whenNull(geography, "geography cannot be null");
        Throw.whenNull(contentStore, "messageStore cannot be null");
        this.id = id;
        this.name = name;
        this.model = model;
        this.geography = geography;
        this.contentStore = contentStore;
        this.contentStore.setOwner(this);
        model.registerActor(this);
    }

    @Override
    public <R extends Role<R>> void registerRole(final Class<R> roleClass, final R role)
    {
        Throw.whenNull(roleClass, "roleClass cannot be null");
        Throw.whenNull(role, "role cannot be null");
        this.roles.put(roleClass, role);
    }

    @Override
    public Collection<Role<?>> getRoles()
    {
        return this.roles.values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Role<R>> R getRole(final Class<R> roleClass)
    {
        return (R) this.roles.get(roleClass);
    }

    @Override
    public boolean checkRolesComplete()
    {
        if (this.rolesComplete)
        {
            return true;
        }
        boolean check = true;
        for (Role<?> role : getRoles())
        {
            if (!role.checkHandlersProcessesComplete())
            {
                check = false;
            }
        }
        this.rolesComplete = check;
        return check;
    }

    @Override
    public void receiveContent(final Content content)
    {
        if (!this.rolesComplete)
        {
            throw new IllegalStateException("The roles for " + this + " are not complete");
        }
        if (!content.receiver().equals(this))
        {
            CategoryLogger.always().warn("Message " + content + " not meant for receiver " + toString());
        }
        else
        {
            boolean processed = false;
            for (Role<?> role : getRoles())
            {
                processed |= role.handleContent(content);
            }
            if (!processed)
            {
                CategoryLogger.always().warn(toString() + " does not have a handler for " + content.getClass().getSimpleName());
            }
        }
        this.contentStore.addContent(content, false);
    }

    @Override
    public void sendContent(final Content content, final Duration delay)
    {
        if (!content.sender().equals(this))
        {
            CategoryLogger.always().warn("Message " + content + " not originating from sender " + toString());
        }
        getSimulator().scheduleEventRel(delay, content.receiver(), "receiveContent", new Object[] {content});
        this.contentStore.addContent(content, true);
        fireEvent(SEND_CONTENT_EVENT, new Object[] {content});
    }

    /**
     * Return the short id of the actor.
     * @return the short id of the actor
     */
    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public Geography getGeography()
    {
        return this.geography;
    }

    @Override
    public String getLocationDescription()
    {
        return this.geography.locationDescription();
    }

    @Override
    public ContentStoreInterface getContentStore()
    {
        return this.contentStore;
    }

    @Override
    public SupplyChainModelInterface getModel()
    {
        return this.model;
    }

    @Override
    public Point2d getLocation()
    {
        return this.geography.location();
    }

    @Override
    public void setBounds(final Bounds2d bounds)
    {
        this.bounds = bounds;
    }

    @Override
    public Bounds2d getBounds()
    {
        return this.bounds;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }

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

    @Override
    public String toString()
    {
        return this.id;
    }

}
