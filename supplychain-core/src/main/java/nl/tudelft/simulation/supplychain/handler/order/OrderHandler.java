package nl.tudelft.simulation.supplychain.handler.order;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.content.Invoice;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;

/**
 * The OrderHandler contains the business logic for handling an incoming Order. It will send out a positive or negative
 * confirmation, based on the conditions at the firm at the moment when the order is received. In its most basic form, the Order
 * will put a claim on finished goods in the store of the owner, and schedule the release of these goods. The sending of the
 * invoice has also to be decided here. All in all, the OrderHandler is one of the most complex handlers, because it involves a
 * number of different content types and a lot of possible parameters. <br>
 * In general, when an Order comes in, it puts a claim on the stock of that product (make to stock), or it is specifically
 * fabricated for that order (make to order). Both are implemented as a separate subclass as can differ considerably. When the
 * fabrication or reservation is successful, an OrderConfirmation is sent, and the process continues. When not, a negative
 * OrderConfirmation is sent and the process stops. <br>
 * After that, the transportation has to be arranged or the transportation time has to be calculated. At the agreed delivery
 * time minus the transportation time, the order is picked immediately (or as soon as it is available), and sent as a Shipment
 * to the other actor. <br>
 * A invoice is sent out before, with, or after the shipment, and in some cases, the shipment has to wait for the payment to
 * arrive.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <O> The specific order type (if any) for which this handler applies
 */
public abstract class OrderHandler<O extends Order> extends ContentHandler<O, SellingRole>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** access to the owner's stocContentHandlerlability of products. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Inventory stock;

    /**
     * Construct a new OrderHandler. The OrderHandler is abstract, so this constructor can not be called directly.
     * @param id the id of the handler
     * @param owner the owner of the handler
     * @param stock the stock to use to handle the incoming order
     * @param messageClass the specific order message class
     */
    public OrderHandler(final String id, final SellingRole owner, final Inventory stock, final Class<O> messageClass)
    {
        super(id, owner, messageClass);
        this.stock = stock;
    }

    // ========================================
    // HELPER METHODS FOR DERIVED ORDERHANDLERS
    // ========================================

    /**
     * Pick and ship the goods.
     * @param order the order that should be handled
     */
    protected void ship(final Order order)
    {
        Product product = order.product();
        double amount = order.amount();
        try
        {
            if (this.stock.getActualAmount(product) < amount)
            {
                // try again in one day
                Serializable[] args = new Serializable[] {order};
                getSimulator().scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, "ship", args);
            }
            else
            {
                // tell the stock that we got the claimed amount
                this.stock.changeClaimedAmount(order.getProduct(), -order.getAmount());
                // available: make shipment and ship to customer
                Money unitPrice = this.stock.getUnitPrice(product);
                double actualAmount = this.stock.removeFromInventory(product, amount);
                Shipment shipment = new Shipment(getActor(), order.getSender(), order.groupingId(), order, product,
                        actualAmount, unitPrice.multiplyBy(actualAmount));
                shipment.setInTransit(true);

                Duration transportTime = order.getTransportOption().estimatedTotalTransportDuration(product.getSku());
                Logger.trace("OrderHandlerStock: transportation delay for order: {} is: {}", order, transportTime);
                sendContent(shipment, transportTime);

                // send a invoice when the shipment leaves...
                Invoice invoice = new Invoice(getActor(), order.getSender(), order.groupingId(), order,
                        getSimulator().getAbsSimulatorTime().plus(new Duration(14.0, DurationUnit.DAY)),
                        shipment.getTotalCargoValue(), "SALE");

                // ... by scheduling it based on the transportation delay
                Serializable[] args = new Serializable[] {invoice};
                getSimulator().scheduleEventRel(transportTime, this, "sendInvoice", args);
            }
        }
        catch (Exception e)
        {
            Logger.error(e, "ship");
            return;
        }
    }

    /**
     * Method sendInvoice.
     * @param invoice the invoice to send
     */
    protected void sendInvoice(final Invoice invoice)
    {
        // send after accepting the order.
        sendContent(invoice, new Duration(1.0, DurationUnit.MINUTE));
    }
}
