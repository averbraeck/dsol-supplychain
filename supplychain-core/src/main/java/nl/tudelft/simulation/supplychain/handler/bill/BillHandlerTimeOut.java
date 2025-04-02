package nl.tudelft.simulation.supplychain.handler.bill;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * A Bill handler which has a restriction that after a time out the bill is paid automatically if not paid yet.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BillHandlerTimeOut extends BillHandler
{
    /** the serial version uid. */
    private static final long serialVersionUID = 11L;

    /** the maximum time out for a shipment. */
    private Duration maximumTimeOut = Duration.ZERO;

    /** true for debug. */
    private boolean debug = true;

    /**
     * Construct a new BillHandlerTimeOut.
     * @param owner the owner
     * @param paymentPolicy the payment policy
     * @param paymentDelay the payment delay
     * @param maximumTimeOut the maximum time out for a bill
     */
    public BillHandlerTimeOut(final FinancingRole owner, final PaymentPolicyEnum paymentPolicy,
            final DistContinuousDuration paymentDelay, final Duration maximumTimeOut)
    {
        super(owner, paymentPolicy, paymentDelay);
        this.maximumTimeOut = maximumTimeOut;
    }

    /**
     * Construct a new BillHandlerTimeOut that takes care of paying exactly on time.
     * @param owner the owner of the policy.
     * @param maximumTimeOut the maximum time out for a bill
     */
    public BillHandlerTimeOut(final FinancingRole owner, final Duration maximumTimeOut)
    {
        this(owner, PaymentPolicyEnum.PAYMENT_ON_TIME,
                new DistContinuousDuration(new DistConstant(owner.getActor().getModel().getDefaultStream(), 0.0)),
                maximumTimeOut);
    }

    @Override
    public boolean handleContent(final Bill bill)
    {
        if (super.handleContent(bill))
        {
            try
            {
                getSimulator().scheduleEventAbs(bill.finalPaymentDate().plus(this.maximumTimeOut), this, "checkPayment",
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
     * Check if this bill is paid.
     * @param bill the bill
     */
    protected void checkPayment(final Bill bill)
    {
        // check if the bill is still in the content store and there is no payment with the same groupingID.
        var store = getRole().getActor().getContentStore();
        if (store.contains(bill) && !store.contains(bill.groupingId(), Payment.class))
        {
            // sad moment, we have to pay...
            this.forcedPay(bill);
        }
    }

    /**
     * Pay, irrespective of the balance.
     * @param bill the bill to pay.
     */
    private void forcedPay(final Bill bill)
    {
        getRole().getBank().withdrawFromBalance(getRole().getActor(), bill.price());
        Payment payment = new Payment(bill);
        getRole().getActor().sendContent(payment, Duration.ZERO);
        if (this.debug)
        {
            System.out.println("DEBUG -- BILLTIMEOUTHANDLER: FORCED PAYMENT IMPOSED: ");
        }
    }
}
