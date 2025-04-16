package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;

/**
 * An Order indicates: I want a certain amount of products on a certain date for a certain price. The four attributes "product",
 * "amount", "date" and "price" make up the order. Several implementations of the order can be made, i.e. a version that is
 * based on a Quote, or a version where the Order is the start of the entire chain.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface Order extends GroupedContent, ProductContent
{
    /**
     * Return the sender.
     * @return the sender
     */
    @Override
    PurchasingActor sender();

    /**
     * Return the receiver.
     * @return the receiver
     */
    @Override
    SellingActor receiver();

    /**
     * Return the delivery date as ordered.
     * @return the delivery date as ordered
     */
    Time deliveryDate();

    /**
     * Return the accepted transport quote.
     * @return the accepted transport quote
     */
    TransportQuote transportQuote();

    /**
     * Return the price we plan to pay for the product.
     * @return the price we plan to pay for the product
     */
    Money price();
}
