package nl.tudelft.simulation.supplychain.message.store;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Message;

/**
 * The EmptyMessageStore does not store anything. E.g., for the YellowPage that does not need to keep track of messages.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class EmptyMessageStore implements MessageStore
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the owner. */
    private Actor owner;

    /**
     * Create a content store that does not store anything. E.g., for the YellowPage that does not need to keep track of
     * messages.
     */
    public EmptyMessageStore()
    {
        // nothing to do.
    }

    /** {@inheritDoc} */
    @Override
    public void setOwner(final Actor owner)
    {
        this.owner = owner;
    }

    /** {@inheritDoc} */
    @Override
    public Actor getOwner()
    {
        return this.owner;
    }

    /** {@inheritDoc} */
    @Override
    public void addMessage(final Message message, final boolean sent)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeMessage(final Message message, final boolean sent)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeSentReceivedMessage(final Message message, final boolean sent)
    {
        // do nothing
    }

}
