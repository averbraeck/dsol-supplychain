package nl.tudelft.simulation.supplychain.message.store.trade;

import java.io.Serializable;
import java.util.List;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;

/**
 * A TradeMessageStore is taking care of storing trade messages for later use, for instance for matching purposes. It acts as an
 * ERP or database system for the supply chain actor. In this implementation, all the messages are linked to an InternalDemand,
 * as this sets off the whole chain of messages, no matter whether it is a purchase, internal production, or stock
 * replenishment: in all cases the InternalDemand triggers all the other messages.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface TradeMessageStoreInterface extends Serializable
{
    /**
     * Set the owner for the message store after is has been created. The reason for explicitly having to set the owner and not
     * include the owner in the constructor is that the Actor needs a MessageStore in its constructor, so the
     * MessageStore cannot be constructed with the owner.
     * @param owner Actor; the owner
     */
    void setOwner(Actor owner);

    /**
     * Add a new message object to the store.
     * @param message TradeMessage; the message to add
     * @param sent boolean; indicates whether the message was sent or received
     */
    void addMessage(TradeMessage message, boolean sent);

    /**
     * Remove a Message object from the store. No error message is given when the message was not found in the store
     * @param message TradeMessage; the message to remove
     * @param sent boolean; indicates whether the message was sent or received
     */
    void removeMessage(TradeMessage message, boolean sent);

    /**
     * Remove a Message object from the sent / received store. No error message is given when the message was not found in the
     * store.
     * @param message TradeMessage; the message to remove
     * @param sent boolean; indicates whether the message was sent or received
     */
    void removeSentReceivedMessage(TradeMessage message, boolean sent);

    /**
     * Return the owner.
     * @return Actor; the owner
     */
    Actor getOwner();

    /**
     * Remove all messages belonging to an internalDemandId from the store. No error message is given when no messages belonging
     * to the internalDemandId were found.
     * @param internalDemandId long; the identifier of the internal demand
     */
    void removeAllMessages(long internalDemandId);

    /**
     * Method getMessageList returns a list of Message objects of class 'messageClass' based on the internalDemandId.
     * @param internalDemandId long; the identifier of the message
     * @param messageClass Class&lt;T&gt;; the class of the message to look for
     * @return List&ltT&gt;; a list of messages of class 'messageClass' belonging to the internalDemandId
     * @param <T> the type of message we are looking for
     */
    <T extends TradeMessage> List<T> getMessageList(long internalDemandId, Class<T> messageClass);

    /**
     * Method getMessageList returns the Message object of class 'messageClass' based on the internalDemandId, for either sent
     * or received items.
     * @param internalDemandId long; the identifier of the internalDemand for which the messages need to be retrieved
     * @param messageClass Class&lt;T&gt;; the class of the message to look for
     * @param sent boolean; indicates whether the message was sent or received
     * @return List&ltT&gt;; a list of messages of class 'messageClass' belonging to the internalDemandId
     * @param <T> the type of message we are looking for
     */
    <T extends TradeMessage> List<T> getMessageList(long internalDemandId, Class<T> messageClass, boolean sent);

}
