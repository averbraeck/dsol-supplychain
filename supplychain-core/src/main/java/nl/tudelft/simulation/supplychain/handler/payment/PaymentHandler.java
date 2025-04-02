package nl.tudelft.simulation.supplychain.handler.payment;

import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
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
     * @param owner the owner of the policy.
     */
    public PaymentHandler(final FinancingRole owner)
    {
        super("PaymentHandler", owner, Payment.class);
    }

    @Override
    public boolean handleContent(final Payment payment)
    {
        if (!isValidContent(payment))
        {
            return false;
        }
        getRole().getBank().addToBalance(getActor(), payment.bill().price());
        return true;
    }

}
