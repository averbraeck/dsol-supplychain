package nl.tudelft.simulation.supplychain.message.trade;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.buying.BuyingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.transport.TransportOption;

/**
 * This is a stand alone order, that is not based on an RFQ and Quote, but which is directly placed to another actor. It
 * <i>might be </i> based on a Quote, but the order is not explicitly saying so. It can also be an order to a well-known supply
 * chain partner, with whom long-term price arrangements have been made.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderStandalone extends Order
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the product we want. */
    private Product product;

    /** the amount of the product, in units for that product. */
    private double amount;

    /** the price we want to pay for the product. */
    private Money price;

    /**
     * The constructor for the OrderStandAlone.
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemand the internal demand that triggered the order
     * @param deliveryDate the intended delivery date of the products
     * @param product the ordered product
     * @param amount the amount ordered
     * @param price the price to pay
     * @param transportOption the accepted transport option
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public OrderStandalone(final BuyingActor sender, final SellingActor receiver, final InternalDemand internalDemand,
            final Time deliveryDate, final Product product, final double amount, final Money price,
            final TransportOption transportOption)
    {
        super(sender, receiver, internalDemand, deliveryDate, transportOption);
        this.product = product;
        this.amount = amount;
        this.price = price;
    }

    @Override
    public double getAmount()
    {
        return this.amount;
    }

    @Override
    public Money getPrice()
    {
        return this.price;
    }

    @Override
    public Product getProduct()
    {
        return this.product;
    }
}
