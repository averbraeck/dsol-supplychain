package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;
import java.util.List;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * A ContentStore is taking care of storing content for later use, for instance for matching purposes. It acts as an ERP or
 * database system for the supply chain. In this implementation, all the messages are linked to an InternalDemand, as this sets
 * off the whole chain of messages, no matter whether it is a purchase, internal production, or stock replenishment: in all
 * cases the InternalDemand triggers all the other messages. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface ContentStoreInterface extends Serializable
{
    /**
     * Method addContent stores a new Content object into the store.
     * @param content the content to add
     * @param sent sent or not
     */
    void addContent(final Content content, final boolean sent);

    /**
     * Method removeContent removes a Content object from the store.
     * @param content the content to remove
     * @param sent indicates whether the content was sent or received
     */
    void removeContent(final Content content, final boolean sent);

    /**
     * Method removeSentReceivedContent removes a Content object from the sent / received store.
     * @param content the content to remove
     * @param sent indicates whether the content was sent or received
     */
    void removeSentReceivedContent(final Content content, final boolean sent);

    /**
     * Method removeAllContent removes an exisiting Content object from the store. No error message is given when the content
     * was not there; this is just ignored.
     * @param internalDemandID the identifier of the internal demand
     */
    void removeAllContent(final Serializable internalDemandID);

    /**
     * Method getContentList returns a list of Content objects of type clazz based on the internalDemandID.
     * @param internalDemandID the identifier of the content
     * @param clazz the content class to look for
     * @return returns a list of content of type class based on the internalDemandID
     */
    <C extends Content> List<C> getContentList(final Serializable internalDemandID, final Class<C> clazz);

    /**
     * Method getContentList returns the Content object of type clazz based on the internalDemandID, for either sent or received
     * items.
     * @param internalDemandID the identifier of the content
     * @param clazz the content class to look for
     * @param sent indicates whether the content was sent or received
     * @return returns a list of content of type class based on the internalDemandID
     */
    <C extends Content> List<C> getContentList(final Serializable internalDemandID, final Class<C> clazz, final boolean sent);

    /**
     * @return Returns the owner.
     */
    SupplyChainActor getOwner();
}
