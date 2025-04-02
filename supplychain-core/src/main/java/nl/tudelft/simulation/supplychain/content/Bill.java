package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * The bill represents a document that asks for payment for a product or service. It contains a pointer to an Order to see for
 * which exact order the actor is invoiced.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the bill
 * @param receiver the receiver of the bill
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param order the order to which this bill belongs
 * @param finalPaymentDate the simulation time for final payment
 * @param price the price that has to be paid
 * @param description the description
 */
public record Bill(FinancingActor sender, FinancingActor receiver, Time timestamp, long uniqueId, long groupingId, Order order,
        Time finalPaymentDate, Money price, String description) implements GroupedContent
{
    public Bill(final FinancingActor sender, final FinancingActor receiver, final Order order, final Time finalPaymentDate,
            final Money price, final String description)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(), order.groupingId(), order,
                finalPaymentDate, price, description);
    }
}
