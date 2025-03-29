package nl.tudelft.simulation.supplychain.message.store;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Message;

/**
 * A MessageStore is taking care of storing messages for later use, for instance for matching purposes. It acts as an ERP or
 * database system for the supply chain actor. In this implementation, all the messages are linked to an InternalDemand, as this
 * sets off the whole chain of messages, no matter whether it is a purchase, internal production, or stock replenishment: in all
 * cases the InternalDemand triggers all the other messages.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface MessageStore extends Serializable
{
    /**
     * Set the owner for the message store after is has been created. The reason for explicitly having to set the owner and not
     * include the owner in the constructor is that the Actor needs a MessageStore in its constructor, so the
     * MessageStore cannot be constructed with the owner.
     * @param owner Actor; the owner
     */
    void setOwner(Actor owner);

    /**
     * Method addMessage stores a new message object into the store.
     * @param message the message to add
     * @param sent sent or not
     */
    void addMessage(Message message, boolean sent);

    /**
     * Method removeMessage removes a Message object from the store.
     * @param message the message to remove
     * @param sent indicates whether the message was sent or received
     */
    void removeMessage(Message message, boolean sent);

    /**
     * Method removeSentReceivedMessage removes a Message object from the sent / received store.
     * @param message the message to remove
     * @param sent indicates whether the message was sent or received
     */
    void removeSentReceivedMessage(Message message, boolean sent);

    /**
     * Return the owner.
     * @return Actor; the owner
     */
    Actor getOwner();
}
