package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.MoneyUnit;
import org.djunits.value.vdouble.scalar.Money;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.content.Payment;

/**
 * A payment handler where a check is performed whether the payment was paid on time. If not, a fine is imposed. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class PaymentFineHandler extends PaymentHandler
{
    /** true for debug */
    private static final boolean DEBUG = false;

    /** the serial version uid */
    private static final long serialVersionUID = 11L;

    /** the margin for the fine */
    private double fineMarginPerDay = 0.0;

    /** the fixed fine */
    private Money fixedFinePerDay = new Money(0.0, MoneyUnit.USD);

    /**
     * constructs a new PaymentFineHandler
     * @param owner the owner
     * @param bankAccount the bank account
     * @param fineMarginPerDay the fine margin per day
     * @param fixedFinePerDay the fixed fine per day
     */
    public PaymentFineHandler(final SupplyChainActor owner, final BankAccount bankAccount, final double fineMarginPerDay,
            final Money fixedFinePerDay)
    {
        super(owner, bankAccount);
        this.fineMarginPerDay = fineMarginPerDay;
        this.fixedFinePerDay = fixedFinePerDay;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        try
        {
            if (super.handleContent(content))
            {
                Payment payment = (Payment) content;
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
                      getOwner().sendContent(payment, Duration.ZERO);
                      getOwner().sendContent(bill, Duration.ZERO);
                     */
                    // do a forced payment
                    payment.getSender().getBankAccount().withdrawFromBalance(fine);
                    payment.getReceiver().getBankAccount().addToBalance(fine);

                    if (PaymentFineHandler.DEBUG)
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
