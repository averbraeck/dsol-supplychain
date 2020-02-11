package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.Payment;

/**
 * The PaymentHandler is a simple implementation of the business logic for a Payment that comes in. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class PaymentHandler extends SupplyChainHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the bank account to use */
    private BankAccount bankAccount = null;

    /**
     * Constructs a new PaymentHandler.
     * @param owner the owner of the handler.
     * @param bankAccount the bankaccount to use.
     */
    public PaymentHandler(final SupplyChainActor owner, final BankAccount bankAccount)
    {
        super(owner);
        this.bankAccount = bankAccount;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (!isValidContent(content))
        {
            return false;
        }
        Payment payment = (Payment) content;
        // TODO: later, a check for the exact amount could be built in.
        this.bankAccount.addToBalance(payment.getPayment());
        payment.getBill().setPaid(true);

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Content> getContentClass()
    {
        return Payment.class;
    }

}