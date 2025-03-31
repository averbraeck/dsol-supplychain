package nl.tudelft.simulation.supplychain.content.receiver;

import java.io.Serializable;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;

/**
 * MessageReceiver contains the base implementation of a message receiver. A message receiver simulates the queuing method for
 * incoming messages before they are processed. Receiving can be done immediately, after a delay, periodically, or after the
 * appropriate resources are available.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class ContentReceiver implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221126L;

    /** An id for the message receiver. */
    private final String id;

    /** The Role to which this message receiver belongs. */
    private Role role;

    /**
     * Create a new content receiver for an actor.
     * @param id an id for the message receiver
     */
    public ContentReceiver(final String id)
    {
        Throw.whenNull(id, "id cannot be null");
        this.id = id;
    }

    /**
     * Set the role to which this receiver belongs; can only be called once, preferably in the constructor of the Role.
     * @param role the Role to which this message receiver belongs
     * @throws IllegalStateException when the role has already been initialized
     */
    public void setRole(final Role role)
    {
        Throw.whenNull(role, "role cannot be null");
        Throw.when(this.role != null, IllegalStateException.class, "ContentReceiver.role already initialized");
        this.role = role;
    }

    /**
     * This is the core dispatching method for the processing of content that was received.
     * @param content the content to process
     * @param contentPolicy the policy to execute on the conntent
     * @param <C> The content type to ensure that the content and policy align
     */
    public abstract <C extends Content> void receiveContent(C content, ContentHandler<C> contentPolicy);

    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the role to which this message receiver belongs.
     * @return the role to which this message receiver belongs
     */
    public Role getRole()
    {
        return this.role;
    }

}
