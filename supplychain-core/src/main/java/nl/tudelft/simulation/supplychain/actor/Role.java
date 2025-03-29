package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.content.Message;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.policy.MessagePolicy;
import nl.tudelft.simulation.supplychain.message.receiver.MessageReceiver;

/**
 * Role is a template for a consistent set of policies for handling messages, representing a certain part of the organization,
 * such as sales, inventory, finance, or purchasing. When a Role receives a message, it goes first through the MessageReceiver
 * that can cause delay (e.g., schedules, use of resources). After that, the Role dispatches the messages to one or more
 * MessagePolicy classes that handle the messages.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Role implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221121L;

    /** the id of the role. */
    private final String id;

    /** the actor to which this role belongs. */
    private final Actor actor;

    /** the message handler. */
    private final MessageReceiver messageReceiver;

    /** the message handling policies. */
    private final Map<Class<? extends Message>, MessagePolicy<? extends Message>> messagePolicies = new LinkedHashMap<>();

    /**
     * Create a new Role.
     * @param id String; the id of the role
     * @param actor Actor; the actor to which this role belongs
     * @param messageReceiver MessageReceiver; the message handler to use for processing the messages
     */
    public Role(final String id, final Actor actor, final MessageReceiver messageReceiver)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(actor, "actor cannot be null");
        Throw.whenNull(messageReceiver, "messageReceiver cannot be null");
        this.id = id;
        this.actor = actor;
        this.messageReceiver = messageReceiver;
        this.messageReceiver.setRole(this);
        actor.addRole(this);
    }

    /**
     * Set a message handling policy for a message type, possibly overwriting the previous message handling policy.
     * @param policy MessagePolicyInterface&lt;M&gt;; the policy to add
     * @param <M> the message type
     */
    public <M extends Message> void setMessagePolicy(final MessagePolicy<M> policy)
    {
        Throw.whenNull(policy, "policy cannot be null");
        this.messagePolicies.put(policy.getMessageClass(), policy);
    }

    /**
     * Remove a message handling policy for a mesage type.
     * @param messageClass Class&lt;M&gt;; the message class of the policy to remove
     * @param <M> the message type
     */
    public <M extends Message> void removeMessagePolicy(final Class<M> messageClass)
    {
        Throw.whenNull(messageClass, "messageClass cannot be null");
        this.messagePolicies.remove(messageClass);
    }

    /**
     * This is the core processing of a message that was received. All appropriate policies of the actor or role are executed.
     * @param message M; the message to process
     * @param <M> The message class to ensure that the message and policy align
     * @return boolean; whether the PolicyHandler processed the message or not
     */
    @SuppressWarnings("unchecked")
    public <M extends Message> boolean handleMessage(final M message)
    {
        if (!this.messagePolicies.containsKey(message.getClass()))
        {
            return false;
        }
        this.messageReceiver.receiveMessage(message, (MessagePolicy<M>) this.messagePolicies.get(message.getClass()));
        return true;
    }

    /**
     * Return the actor to which this role belongs.
     * @return owner Actor; the actor to which this role belongs
     */
    public Actor getActor()
    {
        return this.actor;
    }

    /**
     * Return the simulator to schedule simulation events on.
     * @return SupplyChainSimulatorInterface; the simulator
     */
    public SupplyChainSimulatorInterface getSimulator()
    {
        return getActor().getSimulator();
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.actor, this.id);
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
        Role other = (Role) obj;
        return Objects.equals(this.actor, other.actor) && Objects.equals(this.id, other.id);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.actor.getId() + "." + this.id;
    }

}
