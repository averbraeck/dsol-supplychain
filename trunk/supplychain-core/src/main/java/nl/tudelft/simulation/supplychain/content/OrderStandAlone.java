package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * This is a stand alone order, that is not based on an RFQ and Quote, but which is directly placed to another actor. It
 * <i>might be </i> based on a Quote, but the order is not explicitly saying so. It can also be an order to a well-known supply
 * chain partner, with whom long-term price arrangements have been made. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class OrderStandAlone extends Order
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the product we want */
    private Product product;

    /** the amount of the product, in units for that product */
    private double amount;

    /** the price we want to pay for the product */
    private double price;

    /**
     * The constructor for the OrderStandAlone
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemandID the internal demand that triggered the order
     * @param deliveryDate the intended delivery date of the products
     * @param product the ordered product
     * @param amount the amount ordered
     * @param price the price to pay
     */
    public OrderStandAlone(final SupplyChainActor sender, final SupplyChainActor receiver, final Serializable internalDemandID,
            final Time deliveryDate, final Product product, final double amount, final double price)
    {
        super(sender, receiver, internalDemandID, deliveryDate);
        this.product = product;
        this.amount = amount;
        this.price = price;
    }

    /** {@inheritDoc} */
    @Override
    public double getAmount()
    {
        return this.amount;
    }

    /** {@inheritDoc} */
    @Override
    public double getPrice()
    {
        return this.price;
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.product;
    }
}
