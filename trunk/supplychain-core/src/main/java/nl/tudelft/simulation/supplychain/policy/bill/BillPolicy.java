package nl.tudelft.simulation.supplychain.policy.bill;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.policy.SupplyChainHandler;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;

/**
 * The BillHandler is a simple implementation of the business logic to pay a bill. Four different policies are available in this
 * version -- which can be extended, of course: paying immediately, paying on time, paying early, and paying late. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BillPolicy extends SupplyChainHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the bank account to use. */
    protected BankAccount bankAccount;

    /** the payment policy to use. */
    private PaymentPolicyEnum paymentPolicy;

    /** the delay distribution to use with certain policies, to be added or subtracted. */
    private DistContinuousDuration paymentDelay;

    /**
     * Constructs a new BillHandler with possibilities to pay early or late.
     * @param owner the owner of the handler.
     * @param bankAccount the bankaccount to use.
     * @param paymentPolicy the payment policy to use (early, late, etc.).
     * @param paymentDelay the delay to use in early or late payment
     */
    public BillPolicy(final SupplyChainActor owner, final BankAccount bankAccount, final PaymentPolicyEnum paymentPolicy,
            final DistContinuousDuration paymentDelay)
    {
        super(owner);
        this.bankAccount = bankAccount;
        this.paymentPolicy = paymentPolicy;
        this.paymentDelay = paymentDelay;
    }

    /**
     * Constructs a new BillHandler that takes care of paying exactly on time
     * @param owner the owner of the handler.
     * @param bankAccount the bankaccount to use.
     */
    public BillPolicy(final SupplyChainActor owner, final BankAccount bankAccount)
    {
        this(owner, bankAccount, PaymentPolicyEnum.PAYMENT_ON_TIME, null);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (!isValidContent(content))
        {
            return false;
        }
        Bill bill = (Bill) content;
        // schedule the payment
        Time currentTime = Time.ZERO;
        currentTime = getOwner().getSimulator().getSimulatorTime();
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
        // check if payment is still possible, if it already should have taken
        // place, schedule it for now.
        paymentTime = Time.max(paymentTime, currentTime);
        try
        {
            Serializable[] args = new Serializable[] { bill };
            getOwner().getSimulator().scheduleEventAbs(paymentTime, this, this, "pay", args);
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
                Serializable[] args = new Serializable[] { bill };
                getOwner().getSimulator().scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, this, "pay", args);
            }
            catch (SimRuntimeException exception)
            {
                Logger.error(exception, "handleContent");
            }
            return;
        }
        // make a payment to send out
        this.bankAccount.withdrawFromBalance(bill.getPrice());
        Payment payment = new Payment(getOwner(), bill.getSender(), bill.getInternalDemandID(), bill, bill.getPrice());
        getOwner().sendContent(payment, Duration.ZERO);
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

    /** {@inheritDoc} */
    @Override
    public Class<? extends Content> getContentClass()
    {
        return Bill.class;
    }

}
