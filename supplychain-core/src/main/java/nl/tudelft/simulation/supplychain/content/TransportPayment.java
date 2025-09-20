package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * The TrasnportPayment follows on a TransportInvoice, and it contains a pointer to the TransportInvoice for which it is the
 * payment. The TransportPayment leads to a BankTransfer.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the payment of the transport
 * @param receiver the receiver of the payment of the transport
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param invoice the transport invoice to which this payment belongs
 */
public record TransportPayment(FinancingActor sender, FinancingActor receiver, Time timestamp, long uniqueId, long groupingId,
        TransportInvoice invoice) implements GroupedContent
{
    public TransportPayment(final TransportInvoice invoice)
    {
        this(invoice.receiver(), invoice.sender(), invoice.sender().getSimulatorTime(),
                invoice.sender().getModel().getUniqueContentId(), invoice.groupingId(), invoice);
    }
}
