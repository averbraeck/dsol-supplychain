package nl.tudelft.simulation.supplychain.content.store;

import java.io.Serializable;
import java.util.List;

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
     * include the owner in the constructor is that the Actor needs a ContentStore in its constructor, so the ContentStore
     * cannot be constructed with the owner.
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
     * Remove all contents belonging to a groupingId from the store. No error content is given when no contents belonging to the
     * groupingId were found.
     * @param groupingId the grouping identifier for the transaction
     */
    void removeAllContents(long groupingId);

    /**
     * Method getContentList returns a list of Content objects of class 'contentClass' based on the groupingId.
     * @param groupingId the grouping identifier for the transaction
     * @param contentClass the class of the content to look for
     * @return a list of content items of class 'contentClass' belonging to the groupingId
     * @param <T> the type of content we are looking for
     */
    <T extends Content> List<T> getContentList(long groupingId, Class<T> contentClass);

    /**
     * Method getContentList returns the Content object of class 'contentClass' based on the groupingId, for either sent or
     * received items.
     * @param groupingId the identifier of the grouping for which the contents need to be retrieved
     * @param contentClass the class of the content to look for
     * @param sent indicates whether the content was sent or received
     * @return a list of content items of class 'contentClass' belonging to the groupingId
     * @param <T> the type of content we are looking for
     */
    <T extends Content> List<T> getContentList(long groupingId, Class<T> contentClass, boolean sent);

    /**
     * Return whether the store contains the given content.
     * @param content the content to look up
     * @return whether the store contains the given content
     */
    boolean contains(Content content);
    
    /**
     * Return whether the store contains a message of the given class with this groupingId.
     * @param groupingId the groupingId to look up
     * @param contentClass the content class to look up
     * @return whether the store contains a message of the given class with this groupingId
     */
    default boolean contains(final long groupingId, final Class<? extends Content> contentClass)
    {
        return getContentList(groupingId, contentClass).size() > 0;
    }
    
    /**
     * Return the owner of this content store.
     * @return the owner of this content store
     */
    Actor getOwner();

}
