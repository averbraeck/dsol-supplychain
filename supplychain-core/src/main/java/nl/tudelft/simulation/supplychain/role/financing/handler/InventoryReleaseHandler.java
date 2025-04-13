package nl.tudelft.simulation.supplychain.role.financing.handler;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.content.Invoice;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.InventoryRelease;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * InventoryReleaseHandler.java.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InventoryReleaseHandler extends ContentHandler<InventoryRelease, FinancingRole>
{
    /** */
    private static final long serialVersionUID = 1L;
    
    /** The payment terms: after how many days does the invoice need to be paid. */
    private Duration paymentTerms = new Duration(7.0, DurationUnit.DAY);

    /**
     * Construct a new InventoryRelease handler.
     * @param owner the role belonging to this handler
     */
    public InventoryReleaseHandler(final FinancingRole owner)
    {
        super("InventoryReleaseHandler", owner, InventoryRelease.class);
    }

    @Override
    public boolean handleContent(final InventoryRelease inventoryRelease)
    {
        if (!isValidContent(inventoryRelease))
        {
            return false;
        }

        // make and send an Invoice
        Order order = inventoryRelease.inventoryReleaseRequest().inventoryReservation().inventoryReservationRequest().order();
        var invoice = new Invoice(getRole().getActor(), order.sender(), order, getSimulatorTime().plus(this.paymentTerms));
        sendContent(invoice, getHandlingTime().draw());
        return true;
    }

    /**
     * Return the payment terms: after how many days does the invoice need to be paid.
     * @return the payment terms: after how many days does the invoice need to be paid
     */
    public Duration getPaymentTerms()
    {
        return this.paymentTerms;
    }

    /**
     * Set a new value for the payment terms: after how many days does the invoice need to be paid.
     * @param paymentTerms a new value for the payment terms: after how many days does the invoice need to be paid
     */
    public void setPaymentTerms(final Duration paymentTerms)
    {
        this.paymentTerms = paymentTerms;
    }

}
