package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;

/**
 * The OrderConfirmation is the response when an Actor sends in an Order to another actor. The conformation can be positive or
 * negative, and when it is negative, it contains a reason for not being able to satisfy the Order.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the order confirmation
 * @param receiver the receiver of the order confirmation
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param order the order for which this was the confirmation
 */
public record PrepareInvoice(SellingActor sender, FinancingActor receiver, Time timestamp, long uniqueId, long groupingId,
        Order order) implements GroupedContent, ProductContent
{
    public PrepareInvoice(final Order order)
    {
        this(order.receiver(), order.receiver(), order.sender().getSimulatorTime(),
                order.sender().getModel().getUniqueContentId(), order.groupingId(), order);
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
