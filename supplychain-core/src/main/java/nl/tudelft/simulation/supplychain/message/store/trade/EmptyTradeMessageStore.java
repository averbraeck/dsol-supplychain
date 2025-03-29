package nl.tudelft.simulation.supplychain.message.store.trade;

import java.util.List;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;

/**
 * The EmptyMessageStore does not store anything. E.g., for the YellowPage that does not need to keep track of messages.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class EmptyTradeMessageStore implements TradeMessageStoreInterface
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the owner. */
    private Actor owner;

    /**
     * Create a content store that does not store anything. E.g., for the YellowPage that does not need to keep track of
     * messages.
     */
    public EmptyTradeMessageStore()
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
    public void addMessage(final TradeMessage message, final boolean sent)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeMessage(final TradeMessage message, final boolean sent)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeSentReceivedMessage(final TradeMessage message, final boolean sent)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllMessages(final long internalDemandId)
    {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public <T extends TradeMessage> List<T> getMessageList(final long internalDemandId, final Class<T> messageClass)
    {
        return List.of();
    }

    /** {@inheritDoc} */
    @Override
    public <T extends TradeMessage> List<T> getMessageList(final long internalDemandId, final Class<T> messageClass,
            final boolean sent)
    {
        return List.of();
    }

}
