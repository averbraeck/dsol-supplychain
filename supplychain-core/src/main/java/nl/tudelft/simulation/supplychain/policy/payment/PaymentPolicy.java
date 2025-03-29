package nl.tudelft.simulation.supplychain.policy.payment;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.message.trade.Payment;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;

/**
 * The PaymentHandler is a simple implementation of the business logic for a Payment that comes in.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PaymentPolicy extends SupplyChainPolicy<Payment>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the bank account to use. */
    private BankAccount bankAccount = null;

    /**
     * Constructs a new PaymentHandler.
     * @param owner the owner of the policy.
     * @param bankAccount the bankaccount to use.
     */
    public PaymentPolicy(final Role owner, final BankAccount bankAccount)
    {
        super("PaymentPolicy", owner, Payment.class);
        this.bankAccount = bankAccount;
    }

    @Override
    public boolean handleContent(final Payment payment)
    {
        if (!isValidMessage(payment))
        {
            return false;
        }
        // TODO: later, a check for the exact amount could be built in.
        this.bankAccount.addToBalance(payment.getPayment());
        payment.getBill().setPaid(true);

        return true;
    }

}
