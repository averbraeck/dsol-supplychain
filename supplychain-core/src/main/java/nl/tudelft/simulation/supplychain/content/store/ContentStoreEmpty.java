package nl.tudelft.simulation.supplychain.content.store;

import java.util.List;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Content;

/**
 * ContentStoreEmpty is a content store that does not store any messages (e.g., for the searchs).
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContentStoreEmpty implements ContentStoreInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the owner. */
    private Actor owner;

    /**
     * Create a content store that does not store any messages (e.g., for the searchs).
     */
    public ContentStoreEmpty()
    {
        // nothing to do
    }

    @Override
    public void setOwner(final Actor owner)
    {
        this.owner = owner;
    }

    @Override
    public void addContent(final Content content, final boolean sent)
    {
        // do nothing
    }

    @Override
    public void removeContent(final Content content, final boolean sent)
    {
        // do nothing
    }

    @Override
    public void removeSentReceivedContent(final Content content, final boolean sent)
    {
        // do nothing
    }

    @Override
    public void removeAllContent(final long groupingId)
    {
        // do nothing
    }

    @Override
    public <T extends Content> List<T> getContentList(final long groupingId, final Class<T> contentClass)
    {
        return List.of();
    }

    @Override
    public <T extends Content> List<T> getContentList(final long groupingId, final Class<T> contentClass, final boolean sent)
    {
        return List.of();
    }

    @Override
    public boolean contains(final Content content)
    {
        return false;
    }

    @Override
    public Actor getOwner()
    {
        return this.owner;
    }

}
