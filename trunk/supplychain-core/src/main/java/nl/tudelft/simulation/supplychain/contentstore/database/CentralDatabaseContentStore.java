package nl.tudelft.simulation.supplychain.contentstore.database;

import java.io.Serializable;
import java.util.List;

import org.djutils.event.EventProducer;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;

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
     * @param databaseWorker the ERP system for the game
     */
    public CentralDatabaseContentStore(final DatabaseWorkerInterface databaseWorker)
    {
        super();
        this.databaseWorker = databaseWorker;
    }

    /** {@inheritDoc} */
    @Override
    public void setOwner(final SupplyChainActor owner)
    {
        Throw.when(this.owner != null, RuntimeException.class,
                "ContentStore - setting owner for %s while it has been set before", owner.toString());
        this.owner = owner;
    }

    /** {@inheritDoc} */
    @Override
    public void addContent(Content content, boolean sent)
    {
        Throw.whenNull(this.owner, "CentralDatabaseContentStore - owner has not been initialized");
        this.databaseWorker.addContent(content, sent);
    }

    /** {@inheritDoc} */
    @Override
    public void removeContent(Content content, boolean sent)
    {
        Throw.whenNull(this.owner, "CentralDatabaseContentStore - owner has not been initialized");
        this.databaseWorker.removeContent(content, sent);
    }

    /** {@inheritDoc} */
    @Override
    public void removeSentReceivedContent(Content content, boolean sent)
    {
        Throw.whenNull(this.owner, "CentralDatabaseContentStore - owner has not been initialized");
        this.databaseWorker.removeSentReceivedContent(content, sent);
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllContent(Serializable internalDemandID)
    {
        Throw.whenNull(this.owner, "CentralDatabaseContentStore - owner has not been initialized");
        this.databaseWorker.removeAllContent(internalDemandID);
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Content> List<C> getContentList(Serializable internalDemandID, Class<C> clazz)
    {
        return this.databaseWorker.getContentList(internalDemandID, clazz, this.owner.getName());
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Content> List<C> getContentList(Serializable internalDemandID, Class<C> clazz, boolean sent)
    {
        return this.databaseWorker.getContentList(internalDemandID, clazz, this.owner.getName(), sent);
    }

    /** {@inheritDoc} */
    @Override
    public SupplyChainActor getOwner()
    {
        return this.owner;
    }
}
