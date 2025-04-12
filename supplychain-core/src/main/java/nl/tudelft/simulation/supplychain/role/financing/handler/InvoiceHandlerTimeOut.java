package nl.tudelft.simulation.supplychain.role.financing.handler;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.Invoice;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * A Invoice handler which has a restriction that after a time out the invoice is paid automatically if not paid yet.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InvoiceHandlerTimeOut extends InvoiceHandler
{
    /** the serial version uid. */
    private static final long serialVersionUID = 11L;

    /** the maximum time out for a shipment. */
    private Duration maximumTimeOut = Duration.ZERO;

    /** true for debug. */
    private boolean debug = true;

    /**
     * Construct a new InvoiceHandlerTimeOut.
     * @param owner the owner
     * @param paymentPolicy the payment handler
     * @param paymentDelay the payment delay
     * @param maximumTimeOut the maximum time out for a invoice
     */
    public InvoiceHandlerTimeOut(final FinancingRole owner, final PaymentPolicyEnum paymentPolicy,
            final DistContinuousDuration paymentDelay, final Duration maximumTimeOut)
    {
        super(owner, paymentPolicy, paymentDelay);
        this.maximumTimeOut = maximumTimeOut;
    }

    /**
     * Construct a new InvoiceHandlerTimeOut that takes care of paying exactly on time.
     * @param owner the owner of the handler.
     * @param maximumTimeOut the maximum time out for a invoice
     */
    public InvoiceHandlerTimeOut(final FinancingRole owner, final Duration maximumTimeOut)
    {
        this(owner, PaymentPolicyEnum.PAYMENT_ON_TIME,
                new DistContinuousDuration(new DistConstant(owner.getActor().getModel().getDefaultStream(), 0.0)),
                maximumTimeOut);
    }

    @Override
    public boolean handleContent(final Invoice invoice)
    {
        if (super.handleContent(invoice))
        {
            try
            {
                getSimulator().scheduleEventAbs(invoice.finalPaymentDate().plus(this.maximumTimeOut), this, "checkPayment",
                        new Serializable[] {invoice});
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
     * Check if this invoice is paid.
     * @param invoice the invoice
     */
    protected void checkPayment(final Invoice invoice)
    {
        // check if the invoice is still in the content store and there is no payment with the same groupingID.
        var store = getContentStore();
        if (store.contains(invoice) && !store.contains(invoice.groupingId(), Payment.class))
        {
            // sad moment, we have to pay...
            this.forcedPay(invoice);
        }
    }

    /**
     * Pay, irrespective of the balance.
     * @param invoice the invoice to pay.
     */
    private void forcedPay(final Invoice invoice)
    {
        getRole().getBank().withdrawFromBalance(getActor(), invoice.price());
        Payment payment = new Payment(invoice);
        sendContent(payment);
        if (this.debug)
        {
            System.out.println("DEBUG -- BILLTIMEOUTHANDLER: FORCED PAYMENT IMPOSED: ");
        }
    }
}
