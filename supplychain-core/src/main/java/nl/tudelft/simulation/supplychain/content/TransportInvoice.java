package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * The transport invoice represents a document that asks for payment for transport. It contains a pointer to TransportQuote to
 * see for which exact service the actor is invoiced.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the invoice
 * @param receiver the receiver of the invoice
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param transportQuote the order to which this invoice belongs
 * @param finalPaymentDate the simulation time for final payment
 */
public record TransportInvoice(FinancingActor sender, FinancingActor receiver, Time timestamp, long uniqueId, long groupingId,
        TransportQuote transportQuote, Time finalPaymentDate) implements GroupedContent
{
    public TransportInvoice(final FinancingActor sender, final FinancingActor receiver, final TransportQuote transportQuote,
            final Time finalPaymentDate)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), transportQuote.groupingId(),
                transportQuote, finalPaymentDate);
    }

    /**
     * Return the price.
     * @return the price
     */
    public Money price()
    {
        return this.transportQuote.price();
    }
}
