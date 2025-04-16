package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.djutils.base.Identifiable;
import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;

/**
 * The Role is a template for a consistent set of handlers for contents, representing a certain part of the organization, such
 * as sales, inventory, finance, or purchasing. When a Role receives content such as a message, it goes first through the
 * ContentReceiver that can cause delay (e.g., schedules, use of resources). After that, the Role dispatches the conent to one
 * or more ContentHandler classes that handle the content.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <R> the specific role
 */
public abstract class Role<R extends Role<R>> extends LocalEventProducer implements Identifiable, Serializable, ActorMethods
{
    /** */
    private static final long serialVersionUID = 20221121L;

    /** the id of the role. */
    private final String id;

    /** the actor to which this role belongs. */
    private final Actor actor;

    /** the content handler. */
    private final ContentReceiver contentReceiver;

    /** the handlers for incoming content. */
    private final Map<Class<? extends Content>, ContentHandler<? extends Content, R>> contentHandlers = new LinkedHashMap<>();

    /** the autonomous processes for this role. */
    private final Set<AutonomousProcess<R>> autonomousProcesses = new LinkedHashSet<>();

    /**
     * Create a new Role.
     * @param id the id of the role
     * @param actor the actor to which this role belongs
     * @param contentReceiver the content handler to use for processing the contents
     */
    public Role(final String id, final Actor actor, final ContentReceiver contentReceiver)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(actor, "actor cannot be null");
        Throw.whenNull(contentReceiver, "contentReceiver cannot be null");
        this.id = id;
        this.actor = actor;
        this.contentReceiver = contentReceiver;
        this.contentReceiver.setRole(this);
    }

    /**
     * Set a handler for a content type, possibly overwriting the previous content handler.
     * @param handler the handler to set for the implicit content type
     */
    public void setContentHandler(final ContentHandler<? extends Content, R> handler)
    {
        Throw.whenNull(handler, "handler cannot be null");
        this.contentHandlers.put(handler.getContentClass(), handler);
    }

    /**
     * Set a handler for a content type, possibly overwriting the previous content handler.
     * @param process the handler to set for the implicit content type
     */
    public void addAutonomousProcess(final AutonomousProcess<R> process)
    {
        Throw.whenNull(process, "process cannot be null");
        this.autonomousProcesses.add(process);
    }

    /**
     * This is the core processing of a content that was received. All appropriate handlers role are executed.
     * @param content the conent to process
     * @param <C> The content class to ensure that the content and handler align
     * @return whether the ContentHandler processed the content or not
     */
    public <C extends Content> boolean handleContent(final C content)
    {
        return handleContentClass(content, content.getClass());
    }

    @SuppressWarnings("unchecked")
    private <C extends Content> boolean handleContentClass(final C content, final Class<? extends Content> contentClass)
    {
        // Note: Content.class.isAssignableFrom(Order.class) --> true
        if (this.contentHandlers.containsKey(contentClass))
        {
            this.contentReceiver.receiveContent(content, (ContentHandler<C, R>) this.contentHandlers.get(contentClass));
            return true;
        }
        boolean received = false;
        for (var intf : contentClass.getInterfaces())
        {
            if (Content.class.isAssignableFrom(intf))
            {
                received |= handleContentClass(content, (Class<? extends Content>) intf);
            }
        }
        if (!received)
        {
            Class<?> superClass = contentClass.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass))
            {
                if (Content.class.isAssignableFrom(superClass))
                {
                    received |= handleContentClass(content, (Class<? extends Content>) superClass);
                }
            }
        }
        return received;
    }

    /**
     * Return the actor to which this role belongs.
     * @return the actor to which this role belongs
     */
    @Override
    public Actor getActor()
    {
        return this.actor;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.actor, this.id);
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
        Role<?> other = (Role<?>) obj;
        return Objects.equals(this.actor, other.actor) && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString()
    {
        return "Role " + this.actor.getId() + "." + this.id;
    }

}
