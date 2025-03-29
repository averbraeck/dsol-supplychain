package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Role;

/**
 * An abstract definition of a content policy with a role as the owner.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <C> the content class for which this policy applies
 */
public abstract class ContentPolicy<C extends Content> implements Identifiable, Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221126L;

    /** the id of the policy. */
    private final String id;

    /** the role that owns this policy. */
    private final Role role;

    /** the content class for which this policy applies. */
    private final Class<C> contentClass;

    /**
     * constructs a new content policy.
     * @param id String; the id of the policy
     * @param role Role; the role that owns this policy
     * @param contentClass Class&lt;M&gt;; the content type that this policy can process
     */
    public ContentPolicy(final String id, final Role role, final Class<C> contentClass)
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
     * @return a boolean acknowledgement; true or false
     */
    public abstract boolean handleContent(C content);

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the role to which this handler belongs.
     * @return role Role; the role to which this handler belongs
     */
    public Role getRole()
    {
        return this.role;
    }

    /**
     * Return the content class for which this policy applies.
     * @return Class&lt;? extends M&gt;; the content class for which this policy applies
     */
    public Class<C> getContentClass()
    {
        return this.contentClass;
    }

}
