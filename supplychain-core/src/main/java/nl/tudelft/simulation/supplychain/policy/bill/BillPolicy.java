package nl.tudelft.simulation.supplychain.handler.bill;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.money.BankAccount;
import nl.tudelft.simulation.supplychain.handler.SupplyChainHandler;
import nl.tudelft.simulation.supplychain.handler.payment.PaymentHandlerEnum;
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
public class BillHandler extends ContentHandler<Bill>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the bank account to use. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected BankAccount bankAccount;

    /** the payment handler to use. */
    private PaymentHandlerEnum paymentHandler;

    /** the delay distribution to use with certain policies, to be added or subtracted. */
    private DistContinuousDuration paymentDelay;

    /**
     * Constructs a new BillHandler with possibilities to pay early or late.
     * @param owner the owner of the handler.
     * @param bankAccount the bankaccount to use.
     * @param paymentHandler the payment handler to use (early, late, etc.).
     * @param paymentDelay the delay to use in early or late payment
     */
    public BillHandler(final FinancingRole owner, final BankAccount bankAccount, final PaymentHandlerEnum paymentHandler,
            final DistContinuousDuration paymentDelay)
    {
        super("BillHandler", owner, Bill.class);
        this.bankAccount = bankAccount;
        this.paymentHandler = paymentHandler;
        this.paymentDelay = paymentDelay;
    }

    /**
     * Constructs a new BillHandler that takes care of paying exactly on time.
     * @param owner the owner of the handler.
     * @param bankAccount the bankaccount to use.
     */
    public BillHandler(final FinancingRole owner, final BankAccount bankAccount)
    {
        this(owner, bankAccount, PaymentHandlerEnum.PAYMENT_ON_TIME, null);
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
        switch (this.paymentHandler)
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
                Logger.warn("handleContant - unknown paymentHandler: {}", this.paymentHandler);
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
     * @param paymentHandler The paymentHandler to set.
     */
    public void setPaymentHandler(final PaymentHandlerEnum paymentHandler)
    {
        this.paymentHandler = paymentHandler;
    }

}
