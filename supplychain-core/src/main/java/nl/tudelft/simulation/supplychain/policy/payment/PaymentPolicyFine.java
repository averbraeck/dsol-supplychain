package nl.tudelft.simulation.supplychain.policy.payment;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.money.BankAccount;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;

/**
 * A payment handler where a check is performed whether the payment was paid on time. If not, a fine is imposed.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PaymentPolicyFine extends PaymentPolicy
{
    /** true for debug. */
    private static final boolean DEBUG = false;

    /** the serial version uid. */
    private static final long serialVersionUID = 11L;

    /** the margin for the fine. */
    private double fineMarginPerDay = 0.0;

    /** the fixed fine. */
    private Money fixedFinePerDay = new Money(0.0, MoneyUnit.USD);

    /**
     * constructs a new PaymentFineHandler.
     * @param owner the owner
     * @param bankAccount the bank account
     * @param fineMarginPerDay the fine margin per day
     * @param fixedFinePerDay the fixed fine per day
     */
    public PaymentPolicyFine(final Role owner, final BankAccount bankAccount, final double fineMarginPerDay,
            final Money fixedFinePerDay)
    {
        super(owner, bankAccount);
        this.fineMarginPerDay = fineMarginPerDay;
        this.fixedFinePerDay = fixedFinePerDay;
    }

    @Override
    public String getId()
    {
        return "PaymentPolicyFine";
    }

    @Override
    public boolean handleContent(final Payment payment)
    {
        try
        {
            if (super.handleContent(payment))
            {
                Time time = payment.getSender().getSimulatorTime();
                if (time.gt(payment.getBill().getFinalPaymentDate()))
                {
                    // YES!! we can fine! Finally we earn some money
                    Money fine = this.fixedFinePerDay.plus(payment.getPayment().multiplyBy(this.fineMarginPerDay)
                            .multiplyBy((time.minus(payment.getBill().getFinalPaymentDate()).getInUnit(DurationUnit.DAY))));

                    /*-
                      // send the bill for the fine
                      Bill bill = new Bill(getOwner(), payment.getSender(), payment.getInternalDemandID(), payment.getBill()
                          .getOrder(), getOwner().getSimulatorTime().plus(new Duration(14.0, DurationUnit.DAY), fine, "FINE");
                      sendMessage(payment, Duration.ZERO);
                      sendMessage(bill, Duration.ZERO);
                     */
                    // do a forced payment
                    payment.getSender().getBankAccount().withdrawFromBalance(fine);
                    payment.getReceiver().getBankAccount().addToBalance(fine);

                    if (PaymentPolicyFine.DEBUG)
                    {
                        System.out.println("DEBUG -- PAYMENTFINEHANDLER: FINE IMPOSED: " + fine);
                    }
                }
                return true;
            }
            return false;
        }
        catch (Exception exception)
        {
            Logger.error(exception, "handleContent");
            return false;
        }
    }
}
