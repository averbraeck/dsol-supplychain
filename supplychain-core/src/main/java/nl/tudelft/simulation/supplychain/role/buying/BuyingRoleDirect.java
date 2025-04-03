package nl.tudelft.simulation.supplychain.role.buying;

import nl.tudelft.simulation.supplychain.handler.demand.DemandHandlerOrder;
import nl.tudelft.simulation.supplychain.handler.invoice.InvoiceHandler;
import nl.tudelft.simulation.supplychain.handler.orderconfirmation.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.handler.shipment.ShipmentHandler;

/**
 * The direct buying role is a role that organizes the buying based on a single supplier, and continues from there.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BuyingRoleDirect extends BuyingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Construct a new BuyingRole for Generic Demand - Confirmation - Shipment - Invoice.
     * @param owner the actor to which this role belongs
     * @param demandHandler the demand handler
     * @param orderConfirmationHandler the order confirmation handler
     * @param shipmentHandler the shipment handler
     * @param invoiceHandler the invoice handler
     */
    public BuyingRoleDirect(final BuyingActor owner, final DemandHandlerOrder demandHandler,
            final OrderConfirmationHandler orderConfirmationHandler, final ShipmentHandler shipmentHandler,
            final InvoiceHandler invoiceHandler)
    {
        super(owner);
        setContentHandler(demandHandler);
        setContentHandler(orderConfirmationHandler);
        setContentHandler(shipmentHandler);
        setContentHandler(invoiceHandler);
    }

    @Override
    public String getId()
    {
        return getActor().getId() + "-BUYING(direct)";
    }

}
