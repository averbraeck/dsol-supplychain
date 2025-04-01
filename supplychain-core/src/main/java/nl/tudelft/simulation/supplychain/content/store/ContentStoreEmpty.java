package nl.tudelft.simulation.supplychain.content.store;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Content;

/**
 * ContentStoreEmpty is a content store that does not store any messages (e.g., for the yellow pages).
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
     * Create a content store that does not store any messages (e.g., for the yellow pages).
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
    public Actor getOwner()
    {
        return this.owner;
    }

}
