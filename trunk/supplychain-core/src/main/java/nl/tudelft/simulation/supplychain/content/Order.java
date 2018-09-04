package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Money;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * An Order indicates: I want a certain amount of products on a certain date for a certain price. The four attributes "product",
 * "amount", "date" and "price" make up the order. Several implementations of the order can be made, i.e. a version that is
 * based on a Quote, or a version where the Order is the start of the entire chain. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class Order extends Content
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the delivery date as ordered */
    private Time deliveryDate;

    /**
     * Constructor for an order. This abstract constructor has to be called by every extending class.
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemandID the internal demand that triggered the order
     * @param deliveryDate the intended delivery date of the products
     */
    public Order(final SupplyChainActor sender, final SupplyChainActor receiver, final Serializable internalDemandID,
            final Time deliveryDate)
    {
        super(sender, receiver, internalDemandID);
        this.deliveryDate = deliveryDate;
    }

    /** {@inheritDoc} */
    @Override
    public abstract Product getProduct();

    /**
     * @return Returns the amount.
     */
    public abstract double getAmount();

    /**
     * @return Returns the price.
     */
    public abstract Money getPrice();

    /**
     * @return Returns the proposedDeliveryDate.
     */
    public Time getDeliveryDate()
    {
        return this.deliveryDate;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " units of product " + this.getProduct().getName();
    }
}
