package nl.tudelft.simulation.supplychain.handler;

import java.io.Serializable;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;

/**
 * ContentHandlers work on behalf of a Role and take care of processing incoming content (messages, news, shipments).
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <C> the content class for which this handler applies
 */
public abstract class ContentHandler<C extends Content> implements Identifiable, Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221126L;

    /** the id of the handler. */
    private final String id;

    /** the role that owns this handler. */
    private final Role<?> role;

    /** the content class for which this handler applies. */
    private final Class<C> contentClass;

    /**
     * constructs a new content handler.
     * @param id the id of the handler
     * @param role the role that owns this handler
     * @param contentClass the content type that this handler can process
     */
    public ContentHandler(final String id, final Role<?> role, final Class<C> contentClass)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(role, "role cannot be null");
        Throw.whenNull(contentClass, "contentClass cannot be null");
        this.id = id;
        this.role = role;
        this.contentClass = contentClass;
    }

    /**
     * Handle the content.
     * @param content the content to be handled
     * @return true or false
     */
    public abstract boolean handleContent(C content);

    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the role to which this handler belongs.
     * @return the role to which this handler belongs
     */
    public Role<?> getRole()
    {
        return this.role;
    }

    /**
     * Return the content class for which this handler applies.
     * @return the content class for which this handler applies
     */
    public Class<C> getContentClass()
    {
        return this.contentClass;
    }

}
