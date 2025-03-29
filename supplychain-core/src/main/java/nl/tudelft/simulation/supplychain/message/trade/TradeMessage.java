package nl.tudelft.simulation.supplychain.message.trade;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Message;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * TradeMessage is the generic content for a Message that is related to an InternalDemandId. In addition to the
 * InternalDemandId, it also has a unique Id for itself, that is also unique over networks and in distributed settings, since it
 * is provided by te central SupplyChainModel. Furthermore, it knows nothing more than a sender and a receiver. Content is
 * abstract, as it should be subclassed to give it a sensible 'payload'.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class TradeMessage extends Message
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** unique id of the InternalDemand that triggered the message chain. */
    private long internalDemandId;

    /**
     * Constructs a new TradeMessage object.
     * @param sender Actor; the sending actor of the message content
     * @param receiver Actor; the receiving actor of the message content
     * @param internalDemandId long; the InternalDemandId that triggered the chain
     */
    public TradeMessage(final Actor sender, final Actor receiver, final long internalDemandId)
    {
        super(sender, receiver);
        this.internalDemandId = internalDemandId;
    }

    /**
     * Constructs a new TradeMessage object, specifically an InternalDemand object, where the internalDemandId is set to the
     * value of the uniqueId.
     * @param sender Actor; the sending actor of the message content
     * @param receiver Actor; the receiving actor of the message content
     */
    public TradeMessage(final Actor sender, final Actor receiver)
    {
        super(sender, receiver);
        this.internalDemandId = getUniqueId();
    }

    /**
     * Return the product for which this trade message applies.
     * @return Product; the product for which this trade message applies
     */
    public abstract Product getProduct();

    /**
     * Return the internalDemandId.
     * @return long; the id of the internal demand that triggered the TtradeMessage chain.
     */
    public long getInternalDemandId()
    {
        return this.internalDemandId;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + " from " + this.getSender().getName() + " to " + this.getReceiver().getName();
    }

}
