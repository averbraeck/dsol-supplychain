package nl.tudelft.simulation.supplychain.message.handler;

import java.io.Serializable;

import org.djutils.base.Identifiable;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.Message;

/**
 * MessageHandlerInterface priovides the contract for a message (mailbox) handler. A message handler delegates the messages to
 * one or more policies that are able to handle the message. This can be done immediately, after a delay, periodically, or after
 * the appropriate resources are available.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface MessageHandlerInterface extends Identifiable, Serializable
{
    /**
     * Return the Actor to which this message handler belongs.
     * @return Actor; the Actor to which this message handler belongs
     */
    Actor getActor();

    /**
     * Handle an incoming message. This can be storing the message, handling it with a delay, or immediately handling it.
     * Typically, messages are handled by a MessagePolicy.
     * @param message Message; the message to handle
     */
    void handleMessageReceipt(Message message);

    /**
     * Set the owner; can only be called once, preferably in the constructor of the Actor.
     * @param owner Actor; the Actor to which this message handler belongs
     */
    void setOwner(Actor owner);

}
