package nl.tudelft.simulation.supplychain.handler.bill;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * BillHandler.java.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
/**
 * The BillHandler is a simple implementation of the business logic to pay a bill. Four different policies are available in this
 * version -- which can be extended, of course: paying immediately, paying on time, paying early, and paying late.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BillHandler extends ContentHandler<Bill, FinancingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the payment handler to use. */
    private PaymentPolicyEnum paymentPolicy;

    /** the delay distribution to use with certain policies, to be added or subtracted. */
    private DistContinuousDuration paymentDelay;

    /**
     * Constructs a new BillHandler with possibilities to pay early or late.
     * @param owner the owner of the handler.
     * @param paymentPolicy the payment handler to use (early, late, etc.).
     * @param paymentDelay the delay to use in early or late payment
     */
    public BillHandler(final FinancingRole owner, final PaymentPolicyEnum paymentPolicy,
            final DistContinuousDuration paymentDelay)
    {
        super("BillHandler", owner, Bill.class);
        Throw.whenNull(paymentPolicy, "paymentPolicy cannot be null");
        Throw.whenNull(paymentDelay, "paymentDelay cannot be null");
        this.paymentPolicy = paymentPolicy;
        this.paymentDelay = paymentDelay;
    }

    @Override
    public boolean handleContent(final Bill bill)
    {
        if (!isValidContent(bill))
        {
            return false;
        }
        // schedule the payment
        Time currentTime = getSimulatorTime();
        Time paymentTime = bill.finalPaymentDate();
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
                Logger.warn("handleContant - unknown paymentHandler: {}", this.paymentPolicy);
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
        if (getRole().getBank().getBalance(getActor()).lt(bill.price()))
        {
            // the bank account balance is not sufficient. Try one day later.
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
        getRole().getBank().withdrawFromBalance(getActor(), bill.price());
        Payment payment = new Payment(bill);
        sendContent(payment);
    }

    /**
     * @param paymentDelay The paymentDelay to set.
     */
    public void setPaymentDelay(final DistContinuousDuration paymentDelay)
    {
        this.paymentDelay = paymentDelay;
    }

    /**
     * Change the payment policy.
     * @param paymentPolicy The payment policy to set.
     */
    public void setPaymentPolicy(final PaymentPolicyEnum paymentPolicy)
    {
        this.paymentPolicy = paymentPolicy;
    }

}
