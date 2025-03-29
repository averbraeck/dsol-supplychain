package nl.tudelft.simulation.supplychain.message.policy;

import java.io.Serializable;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Message;

/**
 * An abstract definition of a message policy with a role as the owner.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <M> the message class for which this policy applies
 */
public abstract class MessagePolicy<M extends Message> implements Identifiable, Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221126L;

    /** the id of the policy. */
    private final String id;

    /** the role that owns this policy. */
    private final Role role;

    /** the class of messages for which this policy applies. */
    private final Class<M> messageClass;

    /**
     * constructs a new message policy.
     * @param id String; the id of the policy
     * @param role Role; the role that owns this policy
     * @param messageClass Class&lt;M&gt;; the message type that this policy can process
     */
    public MessagePolicy(final String id, final Role role, final Class<M> messageClass)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(role, "role cannot be null");
        Throw.whenNull(messageClass, "messageClass cannot be null");
        this.id = id;
        this.role = role;
        this.messageClass = messageClass;
    }

    /**
     * Handle the content of the message.
     * @param message M; the message to be handled
     * @return a boolean acknowledgement; true or false
     */
    public abstract boolean handleMessage(M message);

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
     * Return the class of messages for which this policy applies.
     * @return Class&lt;? extends M&gt;; the class of messages for which this policy applies
     */
    public Class<M> getMessageClass()
    {
        return this.messageClass;
    }
    
    
}
