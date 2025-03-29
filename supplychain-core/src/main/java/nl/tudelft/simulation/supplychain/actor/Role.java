package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.ContentPolicy;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;

/**
 * Role is a template for a consistent set of policies for handling messages, representing a certain part of the organization,
 * such as sales, inventory, finance, or purchasing. When a Role receives a message, it goes first through the MessageReceiver
 * that can cause delay (e.g., schedules, use of resources). After that, the Role dispatches the messages to one or more
 * MessagePolicy classes that handle the messages.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
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
    private final ContentReceiver messageReceiver;

    /** the message handling policies. */
    private final Map<Class<? extends Content>, ContentPolicy<? extends Content>> contentPolicies = new LinkedHashMap<>();

    /**
     * Create a new Role.
     * @param id the id of the role
     * @param actor the actor to which this role belongs
     * @param messageReceiver the message handler to use for processing the messages
     */
    public Role(final String id, final Actor actor, final ContentReceiver messageReceiver)
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
     * @param policy the policy to add
     * @param <C> the message type
     */
    public <C extends Content> void setMessagePolicy(final ContentPolicy<C> policy)
    {
        Throw.whenNull(policy, "policy cannot be null");
        this.contentPolicies.put(policy.getContentClass(), policy);
    }

    /**
     * Remove a message handling policy for a mesage type.
     * @param messageClass the message class of the policy to remove
     * @param <C> the message type
     */
    public <C extends Content> void removeMessagePolicy(final Class<C> messageClass)
    {
        Throw.whenNull(messageClass, "messageClass cannot be null");
        this.contentPolicies.remove(messageClass);
    }

    /**
     * This is the core processing of a message that was received. All appropriate policies of the actor or role are executed.
     * @param content the message to process
     * @param <C> The message class to ensure that the message and policy align
     * @return whether the PolicyHandler processed the message or not
     */
    @SuppressWarnings("unchecked")
    public <C extends Content> boolean handleContent(final C content)
    {
        if (!this.contentPolicies.containsKey(content.getClass()))
        {
            return false;
        }
        this.messageReceiver.receiveContent(content, (ContentPolicy<C>) this.contentPolicies.get(content.getClass()));
        return true;
    }

    /**
     * Return the actor to which this role belongs.
     * @return the actor to which this role belongs
     */
    public Actor getActor()
    {
        return this.actor;
    }

    /**
     * Return the simulator to schedule simulation events on.
     * @return the simulator
     */
    public SupplyChainSimulatorInterface getSimulator()
    {
        return getActor().getSimulator();
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
        Role other = (Role) obj;
        return Objects.equals(this.actor, other.actor) && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString()
    {
        return this.actor.getId() + "." + this.id;
    }

}
