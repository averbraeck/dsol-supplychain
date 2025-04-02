package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.buying.BuyingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;

/**
 * TA Shipment is the information for an amount of products that can be transferred from the Stock of one actor to the Stock of
 * another actor.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the shipment
 * @param receiver the receiver of the shipment
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param order the order for which this was the confirmation
 * @param totalCargoValue the total value of the cargo
 */
public record Shipment(SellingActor sender, BuyingActor receiver, Time timestamp, long uniqueId, long groupingId, Order order,
        Money totalCargoValue) implements GroupedContent, ProductContent
{
    public Shipment(final SellingActor sender, final BuyingActor receiver, final Order order, final Money totalCargoValue)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(), order.groupingId(), order,
                totalCargoValue);
    }

    @Override
    public Product product()
    {
        return this.order.product();
    }

    @Override
    public double amount()
    {
        return this.order.amount();
    }
}
