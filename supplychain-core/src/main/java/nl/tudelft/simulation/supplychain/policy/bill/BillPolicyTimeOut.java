package nl.tudelft.simulation.supplychain.policy.bill;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.money.BankAccount;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * A Bill handler which has a restriction that after a time out the bill is paid automatically if not paid yet.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BillPolicyTimeOut extends BillPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 11L;

    /** the maximum time out for a shipment. */
    private Duration maximumTimeOut = Duration.ZERO;

    /** true for debug. */
    private boolean debug = true;

    /**
     * constructs a new BillTimeOutHandler.
     * @param owner the owner
     * @param bankAccount the bank account
     * @param paymentPolicy the payment policy
     * @param paymentDelay the payment delay
     * @param maximumTimeOut the maximum time out for a bill
     */
    public BillPolicyTimeOut(final FinancingRole owner, final BankAccount bankAccount, final PaymentPolicyEnum paymentPolicy,
            final DistContinuousDuration paymentDelay, final Duration maximumTimeOut)
    {
        super(owner, bankAccount, paymentPolicy, paymentDelay);
        this.maximumTimeOut = maximumTimeOut;
    }

    /**
     * Constructs a new BillHandler that takes care of paying exactly on time.
     * @param owner the owner of the policy.
     * @param bankAccount the bankaccount to use.
     * @param maximumTimeOut the maximum time out for a bill
     */
    public BillPolicyTimeOut(final FinancingRole owner, final BankAccount bankAccount, final Duration maximumTimeOut)
    {
        this(owner, bankAccount, PaymentPolicyEnum.PAYMENT_ON_TIME, null, maximumTimeOut);
    }

    @Override
    public boolean handleContent(final Bill bill)
    {
        if (super.handleContent(bill))
        {
            try
            {
                getSimulator().scheduleEventAbs(bill.getFinalPaymentDate().plus(this.maximumTimeOut), this, "checkPayment",
                        new Serializable[] {bill});
            }
            catch (Exception exception)
            {
                Logger.error(exception, "handleContent");
            }
            return true;
        }
        return false;
    }

    /**
     * @param bill the bill
     */
    protected void checkPayment(final Bill bill)
    {
        if (!bill.isPaid())
        {
            // sad moment, we have to pay...
            this.forcedPay(bill);
        }
    }

    /**
     * Pay.
     * @param bill the bill to pay.
     */
    private void forcedPay(final Bill bill)
    {
        // make a payment to send out
        super.bankAccount.withdrawFromBalance(bill.getPrice());
        Payment payment = new Payment(bill.getReceiver(), bill.getSender(), bill.getInternalDemandId(), bill, bill.getPrice());
        sendMessage(payment, Duration.ZERO);
        if (this.debug)
        {
            System.out.println("DEBUG -- BILLTIMEOUTHANDLER: FORCED PAYMENT IMPOSED: ");
        }
    }
}
