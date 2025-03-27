package nl.tudelft.simulation.supplychain.message.receiver;

import java.io.Serializable;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.policy.MessagePolicy;

/**
 * MessageReceiver contains the base implementation of a message receiver. A message receiver simulates the queuing method for
 * incoming messages before they are processed. Receiving can be done immediately, after a delay, periodically, or after the
 * appropriate resources are available.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class MessageReceiver implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221126L;

    /** An id for the message receiver. */
    private final String id;

    /** The Role to which this message receiver belongs. */
    private Role role;

    /**
     * Create a new message receiver for an actor.
     * @param id String; an id for the message receiver
     */
    public MessageReceiver(final String id)
    {
        Throw.whenNull(id, "id cannot be null");
        this.id = id;
    }

    /**
     * Set the role to which this receiver belongs; can only be called once, preferably in the constructor of the Role.
     * @param role Role; the Role to which this message receiver belongs
     * @throws IllegalStateException when the role has already been initialized
     */
    public void setRole(final Role role)
    {
        Throw.whenNull(role, "role cannot be null");
        Throw.when(this.role != null, IllegalStateException.class, "MessageReceiver.role already initialized");
        this.role = role;
    }

    /**
     * This is the core dispatching method for the processing of a message that was received.
     * @param message M; the message to process
     * @param messagePolicy MessagePolicy&lt;M&gt;; the policy to execute on the message
     * @param <M> The message type to ensure that the message and policy align
     */
    public abstract <M extends Message> void receiveMessage(M message, MessagePolicy<M> messagePolicy);

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the role to which this message receiver belongs.
     * @return Role; the role to which this message receiver belongs
     */
    public Role getRole()
    {
        return this.role;
    }

}
