package nl.tudelft.simulation.supplychain.policy.bill;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.message.trade.Bill;
import nl.tudelft.simulation.supplychain.message.trade.Payment;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * The BillHandler is a simple implementation of the business logic to pay a bill. Four different policies are available in this
 * version -- which can be extended, of course: paying immediately, paying on time, paying early, and paying late.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BillPolicy extends SupplyChainPolicy<Bill>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the bank account to use. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected BankAccount bankAccount;

    /** the payment policy to use. */
    private PaymentPolicyEnum paymentPolicy;

    /** the delay distribution to use with certain policies, to be added or subtracted. */
    private DistContinuousDuration paymentDelay;

    /**
     * Constructs a new BillHandler with possibilities to pay early or late.
     * @param owner Role; the owner of the policy.
     * @param bankAccount the bankaccount to use.
     * @param paymentPolicy the payment policy to use (early, late, etc.).
     * @param paymentDelay the delay to use in early or late payment
     */
    public BillPolicy(final FinancingRole owner, final BankAccount bankAccount, final PaymentPolicyEnum paymentPolicy,
            final DistContinuousDuration paymentDelay)
    {
        super("BillPolicy", owner, Bill.class);
        this.bankAccount = bankAccount;
        this.paymentPolicy = paymentPolicy;
        this.paymentDelay = paymentDelay;
    }

    /**
     * Constructs a new BillHandler that takes care of paying exactly on time.
     * @param owner Role; the owner of the policy.
     * @param bankAccount the bankaccount to use.
     */
    public BillPolicy(final FinancingRole owner, final BankAccount bankAccount)
    {
        this(owner, bankAccount, PaymentPolicyEnum.PAYMENT_ON_TIME, null);
    }

    @Override
    public boolean handleContent(final Bill bill)
    {
        if (!isValidMessage(bill))
        {
            return false;
        }
        // schedule the payment
        Time currentTime = Time.ZERO;
        currentTime = getSimulator().getAbsSimulatorTime();
        Time paymentTime = bill.getFinalPaymentDate();
        switch (this.paymentPolicy)
        {
            case PAYMENT_ON_TIME:
                // do nothing, we pay on the requested date
                break;
            case PAYMENT_EARLY:
                paymentTime = paymentTime.minus(this.paymentDelay.draw());
                break;
            case PAYMENT_LATE:
                paymentTime = paymentTime.plus(this.paymentDelay.draw());
                break;
            case PAYMENT_IMMEDIATE:
                paymentTime = currentTime;
                break;
            default:
                Logger.warn("handleContant - unknown paymentPolicy: {}", this.paymentPolicy);
                break;
        }
        // check if payment is still possible, if it already should have taken place, schedule it immediately.
        paymentTime = Time.max(paymentTime, currentTime);
        try
        {
            Serializable[] args = new Serializable[] {bill};
            getSimulator().scheduleEventAbs(paymentTime, this, "pay", args);
        }
        catch (SimRuntimeException exception)
        {
            Logger.error(exception, "handleContent");
            return false;
        }
        return true;
    }

    /**
     * Try to pay. If it does not succeed, try later.
     * @param bill - the bill to pay.
     */
    protected void pay(final Bill bill)
    {
        if (this.bankAccount.getBalance().lt(bill.getPrice()))
        {
            // the bank account is not enough. Try one day later.
            try
            {
                Serializable[] args = new Serializable[] {bill};
                getSimulator().scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, "pay", args);
            }
            catch (SimRuntimeException exception)
            {
                Logger.error(exception, "handleContent");
            }
            return;
        }
        // make a payment to send out
        this.bankAccount.withdrawFromBalance(bill.getPrice());
        Payment payment =
                new Payment(bill.getReceiver(), bill.getSender(), bill.getInternalDemandId(), bill, bill.getPrice());
        sendMessage(payment, Duration.ZERO);
    }

    /**
     * @param paymentDelay The paymentDelay to set.
     */
    public void setPaymentDelay(final DistContinuousDuration paymentDelay)
    {
        this.paymentDelay = paymentDelay;
    }

    /**
     * @param paymentPolicy The paymentPolicy to set.
     */
    public void setPaymentPolicy(final PaymentPolicyEnum paymentPolicy)
    {
        this.paymentPolicy = paymentPolicy;
    }

}
