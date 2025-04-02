package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * The Payment follows on a Bill, and it contains a pointer to the Bill for which it is the payment.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the paymnt
 * @param receiver the receiver of the payment
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the internalDemandId or the orderId
 * @param bill the bill to which this payment belongs
 */
public record Payment(FinancingActor sender, FinancingActor receiver, Time timestamp, long uniqueId, long groupingId, Bill bill)
        implements GroupedContent
{
    public Payment(final Bill bill)
    {
        this(bill.receiver(), bill.sender(), bill.sender().getSimulatorTime(), bill.sender().getModel().getUniqueMessageId(),
                bill.groupingId(), bill);
    }
}
