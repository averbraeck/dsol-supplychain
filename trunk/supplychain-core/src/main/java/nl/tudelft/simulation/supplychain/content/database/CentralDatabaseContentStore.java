package nl.tudelft.simulation.supplychain.content.database;

import java.io.Serializable;
import java.util.List;

import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.ContentStoreInterface;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class CentralDatabaseContentStore extends EventProducer implements ContentStoreInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the owner */
    private SupplyChainActor owner;

    /** the ERP system for the game */
    private DatabaseWorkerInterface databaseWorker = null;

    /**
     * Constructs a new ContentStore
     * @param owner the owner
     * @param databaseWorker the ERP system for the game
     */
    public CentralDatabaseContentStore(final SupplyChainActor owner, final DatabaseWorkerInterface databaseWorker)
    {
        super();
        this.owner = owner;
        this.databaseWorker = databaseWorker;
    }

    /**
     * @see nl.tudelft.simulation.supplychain.content.ContentStoreInterface#addContent(nl.tudelft.simulation.supplychain.content.Content,
     *      boolean)
     */
    public void addContent(Content content, boolean sent)
    {
        this.databaseWorker.addContent(content, sent);
    }

    /**
     * @see nl.tudelft.simulation.supplychain.content.ContentStoreInterface#removeContent(nl.tudelft.simulation.supplychain.content.Content,
     *      boolean)
     */
    public void removeContent(Content content, boolean sent)
    {
        this.databaseWorker.removeContent(content, sent);
    }

    /**
     * @see nl.tudelft.simulation.supplychain.content.ContentStoreInterface#removeSentReceivedContent(nl.tudelft.simulation.supplychain.content.Content,
     *      boolean)
     */
    public void removeSentReceivedContent(Content content, boolean sent)
    {
        this.databaseWorker.removeSentReceivedContent(content, sent);
    }

    /**
     * @see nl.tudelft.simulation.supplychain.content.ContentStoreInterface#removeAllContent(java.io.Serializable)
     */
    public void removeAllContent(Serializable internalDemandID)
    {
        this.databaseWorker.removeAllContent(internalDemandID);
    }

    /**
     * @see nl.tudelft.simulation.supplychain.content.ContentStoreInterface#getContentList(java.io.Serializable,
     *      java.lang.Class)
     */
    public List<Content> getContentList(Serializable internalDemandID, Class clazz)
    {
        return this.databaseWorker.getContentList(internalDemandID, clazz, this.owner.getName());
    }

    /**
     * @see nl.tudelft.simulation.supplychain.content.ContentStoreInterface#getContentList(java.io.Serializable,
     *      java.lang.Class, boolean)
     */
    public List<Content> getContentList(Serializable internalDemandID, Class clazz, boolean sent)
    {
        return this.databaseWorker.getContentList(internalDemandID, clazz, this.owner.getName(), sent);
    }

    /**
     * @see nl.tudelft.simulation.supplychain.content.ContentStoreInterface#getOwner()
     */
    public SupplyChainActor getOwner()
    {
        return this.owner;
    }
}
