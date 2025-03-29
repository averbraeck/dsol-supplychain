package nl.tudelft.simulation.supplychain.message.trade;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.buying.BuyingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.transport.TransportOption;

/**
 * An Order indicates: I want a certain amount of products on a certain date for a certain price. The four attributes "product",
 * "amount", "date" and "price" make up the order. Several implementations of the order can be made, i.e. a version that is
 * based on a Quote, or a version where the Order is the start of the entire chain.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Order extends TradeMessage
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the InternalDemand. */
    private final InternalDemand internalDemand;

    /** the delivery date as ordered. */
    private final Time deliveryDate;

    /** the accepted transport option. */
    private final TransportOption transportOption;

    /**
     * Constructor for an order. This abstract constructor has to be called by every extending class.
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemand the internal demand that triggered the order
     * @param deliveryDate the intended delivery date of the products
     * @param transportOption the accepted transport option
     */
    public Order(final BuyingActor sender, final SellingActor receiver, final InternalDemand internalDemand,
            final Time deliveryDate, final TransportOption transportOption)
    {
        super(sender, receiver, internalDemand.getInternalDemandId());
        this.internalDemand = internalDemand;
        this.deliveryDate = deliveryDate;
        this.transportOption = transportOption;
    }

    @Override
    public abstract Product getProduct();

    /**
     * @return the internalDemand
     */
    protected InternalDemand getInternalDemand()
    {
        return this.internalDemand;
    }

    /**
     * @return the amount.
     */
    public abstract double getAmount();

    /**
     * @return the price.
     */
    public abstract Money getPrice();

    /**
     * @return the proposedDeliveryDate.
     */
    public Time getDeliveryDate()
    {
        return this.deliveryDate;
    }

    /**
     * @return transportOption
     */
    public TransportOption getTransportOption()
    {
        return this.transportOption;
    }

    @Override
    public BuyingActor getSender()
    {
        return (BuyingActor) super.getSender();
    }

    @Override
    public SellingActor getReceiver()
    {
        return (SellingActor) super.getReceiver();
    }

    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " units of product " + this.getProduct().getName();
    }
}
