package nl.tudelft.simulation.supplychain.content.store;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Content;

/**
 * A ContentStore is taking care of storing content for later use, for instance for matching purposes. It acts as an ERP or
 * database system for the supply chain actor. In this implementation, all the content are linked to an InternalDemand, as this
 * sets off the whole chain of content, no matter whether it is a purchase, internal production, or stock replenishment: in all
 * cases the InternalDemand triggers all the other content.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface ContentStoreInterface extends Serializable
{
    /**
     * Set the owner for the content store after is has been created. The reason for explicitly having to set the owner and not
     * include the owner in the constructor is that the Actor needs a ContentStore in its constructor, so the
     * ContentStore cannot be constructed with the owner.
     * @param owner the owner
     */
    void setOwner(Actor owner);

    /**
     * Method addContent stores a new content object into the store.
     * @param content the content to add
     * @param sent sent or not
     */
    void addContent(Content content, boolean sent);

    /**
     * Method removeContent removes a Content object from the store.
     * @param content the content to remove
     * @param sent indicates whether the content was sent or received
     */
    void removeContent(Content content, boolean sent);

    /**
     * Method removeSentReceivedContent removes a Content object from the sent / received store.
     * @param content the content to remove
     * @param sent indicates whether the content was sent or received
     */
    void removeSentReceivedContent(Content content, boolean sent);

    /**
     * Return the owner.
     * @return the owner
     */
    Actor getOwner();
}
