package nl.tudelft.simulation.supplychain.handler.payment;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * A payment handler where a check is performed whether the payment was paid on time. If not, a fine is imposed.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PaymentHandlerFine extends PaymentHandler
{
    /** the serial version uid. */
    private static final long serialVersionUID = 11L;

    /** the margin for the fine. */
    private final double fineMarginPerDay;

    /** the fixed fine. */
    private final Money fixedFinePerDay;

    /**
     * constructs a new PaymentFineHandler.
     * @param role the role that implements this handler
     * @param fineMarginPerDay the fine margin per day
     * @param fixedFinePerDay the fixed fine per day
     */
    public PaymentHandlerFine(final FinancingRole role, final double fineMarginPerDay, final Money fixedFinePerDay)
    {
        super(role);
        this.fineMarginPerDay = fineMarginPerDay;
        this.fixedFinePerDay = fixedFinePerDay;
    }

    @Override
    public String getId()
    {
        return "PaymentHandlerFine";
    }

    @Override
    public boolean handleContent(final Payment payment)
    {
        if (super.handleContent(payment))
        {
            Time time = getSimulatorTime();
            if (time.gt(payment.bill().finalPaymentDate()))
            {
                Money fine = this.fixedFinePerDay.plus(payment.bill().price().multiplyBy(this.fineMarginPerDay)
                        .multiplyBy((time.minus(payment.bill().finalPaymentDate()).getInUnit(DurationUnit.DAY))));
                payment.sender().getFinancingRole().getBank().withdrawFromBalance(payment.sender(), fine);
                payment.receiver().getFinancingRole().getBank().addToBalance(payment.receiver(), fine);
            }
            return true;
        }
        return false;
    }
}
