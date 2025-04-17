package nl.tudelft.simulation.supplychain.role.shipping.handler;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.ShippingOrder;
import nl.tudelft.simulation.supplychain.content.TransportOrder;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.product.Shipment;
import nl.tudelft.simulation.supplychain.role.shipping.ShippingRole;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The ShippingOrderHandler take care of outbound shipments from the warehouse. It handles the ShippingOrder message.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShippingOrderHandler extends ContentHandler<ShippingOrder, ShippingRole>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The payment terms: after how many days does the invoice need to be paid. */
    private Duration paymentTerms = new Duration(7.0, DurationUnit.DAY);

    /**
     * Construct a new ShippingOrder handler.
     * @param owner the actor belonging to this handler
     */
    public ShippingOrderHandler(final WarehousingActor owner)
    {
        super("ShippingOrderHandler", owner.getShippingRole(), ShippingOrder.class);
    }

    @Override
    public boolean handleContent(final ShippingOrder shippingOrder)
    {
        if (!isValidContent(shippingOrder))
        {
            return false;
        }

        // there should be a transport option that was agreed by the transporter
        Order order = shippingOrder.order();
        var transportQuote = order.transportQuote();

        // The value of the cargo now includes the tarnsport cost and the profit margin of the seller.
        Shipment shipment =
                new Shipment(getRole().getActor(), order.sender().getReceivingRole().getActor(), order, order.price());
        TransportOrder transportOrder = new TransportOrder(transportQuote, shipment, order);
        sendContent(transportOrder, getHandlingTime().draw());
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
