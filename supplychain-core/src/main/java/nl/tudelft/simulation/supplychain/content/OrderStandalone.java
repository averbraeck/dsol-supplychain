package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.transporting.TransportOption;

/**
 * This is a stand alone order, that is not based on an RFQ and Quote, but which is directly placed to another actor. It
 * <i>might be </i> based on a Quote, but the order is not explicitly saying so. It can also be an order to a well-known supply
 * chain partner, with whom long-term price arrangements have been made.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the standalone order
 * @param receiver the receiver of the standalone order
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param deliveryDate the intended delivery date of the products
 * @param product the ordered product
 * @param amount the amount of the product, in units for that product
 * @param price the price we want to pay for the product
 * @param transportOption the accepted transport option
 */
public record OrderStandalone(PurchasingActor sender, SellingActor receiver, Time timestamp, long uniqueId, long groupingId,
        Time deliveryDate, Product product, double amount, Money price, TransportOption transportOption) implements Order
{
    public OrderStandalone(final PurchasingActor sender, final SellingActor receiver, final Time deliveryDate,
            final Product product, final double amount, final Money price, final TransportOption transportOption)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(),
                sender.getModel().getUniqueContentId(), deliveryDate, product, amount, price, transportOption);
    }

}
