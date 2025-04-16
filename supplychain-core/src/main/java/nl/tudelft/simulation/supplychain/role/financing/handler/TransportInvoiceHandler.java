package nl.tudelft.simulation.supplychain.role.financing.handler;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.TransportInvoice;
import nl.tudelft.simulation.supplychain.content.TransportPayment;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * The TransportTransportInvoiceHandler is a simple implementation of the business logic to pay a transport invoice. Four
 * different policies are available in this version -- which can be extended, of course: paying immediately, paying on time,
 * paying early, and paying late.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportInvoiceHandler extends ContentHandler<TransportInvoice, FinancingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the payment handler to use. */
    private PaymentPolicyEnum paymentPolicy;

    /** the delay distribution to use with certain policies, to be added or subtracted. */
    private DistContinuousDuration paymentDelay;

    /**
     * Constructs a new TransportInvoiceHandler with possibilities to pay early or late.
     * @param owner the owner of the handler.
     * @param paymentPolicy the payment handler to use (early, late, etc.).
     * @param paymentDelay the delay to use in early or late payment
     */
    public TransportInvoiceHandler(final FinancingRole owner, final PaymentPolicyEnum paymentPolicy,
            final DistContinuousDuration paymentDelay)
    {
        super("TransportInvoiceHandler", owner, TransportInvoice.class);
        Throw.whenNull(paymentPolicy, "paymentPolicy cannot be null");
        Throw.whenNull(paymentDelay, "paymentDelay cannot be null");
        this.paymentPolicy = paymentPolicy;
        this.paymentDelay = paymentDelay;
    }

    @Override
    public boolean handleContent(final TransportInvoice invoice)
    {
        if (!isValidContent(invoice))
        {
            return false;
        }
        // schedule the payment
        Time currentTime = getSimulatorTime();
        Time paymentTime = invoice.finalPaymentDate();
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
        getSimulator().scheduleEventAbs(paymentTime, this, "pay", new Object[] {invoice});
        return true;
    }

    /**
     * Try to pay. If it does not succeed, try later.
     * @param transportInvoice - the invoice to pay.
     */
    protected void pay(final TransportInvoice transportInvoice)
    {
        var payment = new TransportPayment(transportInvoice);
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
