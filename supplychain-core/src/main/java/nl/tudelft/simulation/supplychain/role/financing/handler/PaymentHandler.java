package nl.tudelft.simulation.supplychain.role.financing.handler;

import nl.tudelft.simulation.supplychain.content.BankTransfer;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * The PaymentHandler is a simple implementation of the business logic for a Payment that comes in.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PaymentHandler extends ContentHandler<Payment, FinancingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Constructs a new PaymentHandler.
     * @param owner the owner of the handler.
     */
    public PaymentHandler(final FinancingActor owner)
    {
        super("PaymentHandler", owner.getFinancingRole(), Payment.class);
    }

    @Override
    public boolean handleContent(final Payment payment)
    {
        if (!isValidContent(payment))
        {
            return false;
        }

        var bankTransfer = new BankTransfer(getRole().getActor(), getRole().getBank().getActor(), payment.receiver(),
                payment.invoice().price());
        sendContent(bankTransfer, getHandlingTime().draw());
        return true;
    }

}
