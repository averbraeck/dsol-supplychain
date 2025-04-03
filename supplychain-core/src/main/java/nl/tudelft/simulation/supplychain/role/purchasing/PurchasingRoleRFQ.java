package nl.tudelft.simulation.supplychain.role.purchasing;

import nl.tudelft.simulation.supplychain.handler.demand.DemandHandlerRFQ;
import nl.tudelft.simulation.supplychain.handler.invoice.InvoiceHandler;
import nl.tudelft.simulation.supplychain.handler.orderconfirmation.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.handler.quote.QuoteHandler;
import nl.tudelft.simulation.supplychain.handler.shipment.ShipmentHandler;

/**
 * The purchasing role based on a RFQ is a role that organizes the purchasing based on a RequestForQuote that is sent to a fixed set of
 * possible suppliers, and continues from there.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PurchasingRoleRFQ extends PurchasingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Constructs a new PurchasingRole for Demand - Quote - Confirmation - Shipment - Invoice.
     * @param owner the actor to which this role belongs
     * @param demandHandler the demand handler, results in sending out an RFQ
     * @param quoteHandler the quote handler
     * @param orderConfirmationHandler the order confirmation handler
     * @param shipmentHandler the shipment handler
     * @param invoiceHandler the invoice handler
     */
    public PurchasingRoleRFQ(final PurchasingActor owner, final DemandHandlerRFQ demandHandler, final QuoteHandler quoteHandler,
            final OrderConfirmationHandler orderConfirmationHandler, final ShipmentHandler shipmentHandler,
            final InvoiceHandler invoiceHandler)
    {
        super(owner);
        setContentHandler(demandHandler);
        setContentHandler(quoteHandler);
        setContentHandler(orderConfirmationHandler);
        setContentHandler(shipmentHandler);
        setContentHandler(invoiceHandler);
    }

    @Override
    public String getId()
    {
        return getActor().getId() + "-BUYING(RFQ)";
    }

}
