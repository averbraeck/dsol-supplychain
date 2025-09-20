package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.banking.BankingActor;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * The BankTransfer is a request to the BankingActor to transfer money between the account of the sender and the account of a
 * payee. Note that the payee is not the receiver of the message. The message it to the bank, to transfer the money between
 * sender and payee.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the paymnt
 * @param receiver the receiver of the payment
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param payee the beneficiary of the bank transfer
 * @param money the amount of money being transfered
 */
public record BankTransfer(FinancingActor sender, BankingActor receiver, Time timestamp, long uniqueId, FinancingActor payee,
        Money money) implements Content
{
    public BankTransfer(final FinancingActor sender, final BankingActor receiver, final FinancingActor payee, final Money money)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), payee, money);
    }
}
