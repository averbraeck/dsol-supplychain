package nl.tudelft.simulation.supplychain.role.financing.handler;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.content.TransportConfirmation;
import nl.tudelft.simulation.supplychain.content.TransportInvoice;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * The TransportConfirmationHandler takes care of sending the invoice after the goods have been loaded for transport.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportConfirmationHandler extends ContentHandler<TransportConfirmation, FinancingRole>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The payment term: after how many days does the invoice need to be paid. */
    private Duration paymentTerm = new Duration(7.0, DurationUnit.DAY);

    /**
     * Construct a new TransportConfirmation handler.
     * @param owner the owner of this handler
     */
    public TransportConfirmationHandler(final FinancingActor owner)
    {
        super("TransportConfirmationHandler", owner.getFinancingRole(), TransportConfirmation.class);
    }

    @Override
    public boolean handleContent(final TransportConfirmation transportConfirmation)
    {
        if (!isValidContent(transportConfirmation))
        {
            return false;
        }

        // make and send an Invoice
        var invoice = new TransportInvoice(getRole().getActor(), transportConfirmation.shipment().getReceivingActor(),
                transportConfirmation.transportQuote(), transportConfirmation.shipment(),
                getSimulatorTime().plus(this.paymentTerm));
        sendContent(invoice, getHandlingTime().draw());
        return true;
    }

    /**
     * Return the payment term: after how many days does the invoice need to be paid.
     * @return the payment term: after how many days does the invoice need to be paid
     */
    public Duration getPaymentTerm()
    {
        return this.paymentTerm;
    }

    /**
     * Set a new value for the payment term: after how many days does the invoice need to be paid.
     * @param paymentTerm a new value for the payment terms: after how many days does the invoice need to be paid
     */
    public void setPaymentTerm(final Duration paymentTerm)
    {
        this.paymentTerm = paymentTerm;
    }

}
