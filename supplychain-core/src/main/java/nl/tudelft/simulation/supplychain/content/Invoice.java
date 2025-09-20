package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * The invoice represents a document that asks for payment for a product or service. It contains a pointer to an Order to see
 * for which exact order the actor is invoiced.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the invoice, always the FinancingActor of the sending organization
 * @param receiver the receiver of the invoice, always the FinancingActor of the receiving organization
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param order the order to which this invoice belongs
 * @param finalPaymentDate the simulation time for final payment
 */
public record Invoice(FinancingActor sender, FinancingActor receiver, Time timestamp, long uniqueId, long groupingId,
        Order order, Time finalPaymentDate) implements GroupedContent
{
    public Invoice(final FinancingActor sender, final FinancingActor receiver, final Order order, final Time finalPaymentDate)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), order.groupingId(), order,
                finalPaymentDate);
    }

    /**
     * Return the price.
     * @return the price
     */
    public Money price()
    {
        return this.order.price();
    }
}
