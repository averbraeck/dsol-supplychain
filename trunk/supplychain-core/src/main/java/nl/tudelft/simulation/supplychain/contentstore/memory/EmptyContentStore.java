package nl.tudelft.simulation.supplychain.contentstore.memory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;

/**
 * The EmptyContentStore does not store anything. E.g., for the YellowPage that does not need to keep track of messages. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class EmptyContentStore implements ContentStoreInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the owner */
    private SupplyChainActor owner;

    /**
     * Create a content store that does not store anything. E.g., for the YellowPage that does not need to keep track of
     * messages.
     */
    public EmptyContentStore()
    {
        // nothing to do.
    }

    /** {@inheritDoc} */
    @Override
    public void setOwner(SupplyChainActor owner)
    {
        this.owner = owner;
    }

    /** {@inheritDoc} */
    @Override
    public void addContent(Content content, boolean sent)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeContent(Content content, boolean sent)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeSentReceivedContent(Content content, boolean sent)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllContent(Serializable internalDemandID)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Content> List<C> getContentList(Serializable internalDemandID, Class<C> clazz)
    {
        return new ArrayList<C>();
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Content> List<C> getContentList(Serializable internalDemandID, Class<C> clazz, boolean sent)
    {
        return new ArrayList<C>();
    }

    /** {@inheritDoc} */
    @Override
    public SupplyChainActor getOwner()
    {
        return this.owner;
    }

}
