package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * The OrderHandler contains the business logic for handling an incoming Order. It will send out a positive or negative
 * confirmation, based on the conditions at the firm at the moment when the order is received. In its most basic form, the Order
 * will put a claim on finished goods in the store of the owner, and schedule the release of these goods. The sending of the
 * bill has also to be decided here. All in all, the OrderHandler is one of the most complex handlers, because it involves a
 * number of different content types and a lot of possible parameters. <br>
 * In general, when an Order comes in, it puts a claim on the stock of that product (make to stock), or it is specifically
 * fabricated for that order (make to order). Both are implemented as a separate subclass as can differ considerably. When the
 * fabrication or reservation is successful, an OrderConfirmation is sent, and the process continues. When not, a negative
 * OrderConfirmation is sent and the process stops. <br>
 * After that, the transportation has to be arranged or the transportation time has to be calculated. At the agreed delivery
 * time minus the transportation time, the order is picked immediately (or as soon as it is available), and sent as a Shipment
 * to the other actor. <br>
 * A bill is sent out before, with, or after the shipment, and in some cases, the shipment has to wait for the payment to
 * arrive. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class OrderHandler extends SupplyChainHandler
{
    /** */
    private static final long serialVersionUID = 1L;

    /** access to the owner's stock to look at availability of products */
    protected StockInterface stock;

    /**
     * Construct a new OrderHandler. The OrderHandler is abstract, so this constructor can not be called directly.
     * @param owner the owner of the handler
     * @param stock the stock to use to handle the incoming order
     */
    public OrderHandler(final SupplyChainActor owner, final StockInterface stock)
    {
        super(owner);
        this.stock = stock;
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Content> getContentClass()
    {
        return Order.class;
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
        Product product = order.getProduct();
        double amount = order.getAmount();
        try
        {
            if (this.stock.getActualAmount(product) < amount)
            {
                // try again in one day
                Serializable[] args = new Serializable[] { order };
                getOwner().getSimulator().scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, this, "ship", args);
            }
            else
            {
                // tell the stock that we got the claimed amount
                this.stock.changeClaimedAmount(order.getProduct(), -order.getAmount());
                // available: make shipment and ship to customer
                Money unitPrice = this.stock.getUnitPrice(product);
                double actualAmount = this.stock.removeStock(product, amount);
                Shipment shipment = new Shipment(getOwner(), order.getSender(), order.getInternalDemandID(), order, product,
                        actualAmount, unitPrice.multiplyBy(actualAmount));
                shipment.setInTransit(true);

                // TODO: get the transportation mode from the shipment?
                Duration transportTime = TransportMode.PLANE.transportTime(shipment.getSender(), shipment.getReceiver());
                Logger.trace("OrderHandlerStock: transportation delay for order: {} is: {}", order, transportTime);
                getOwner().sendContent(shipment, transportTime);

                // send a bill when the shipment leaves...
                Bill bill = new Bill(getOwner(), order.getSender(), order.getInternalDemandID(), order,
                        getOwner().getSimulatorTime().plus(new Duration(14.0, DurationUnit.DAY)), shipment.getTotalCargoValue(),
                        "SALE");

                // .... by scheduling it based on the transportation delay
                Serializable[] args = new Serializable[] { bill };
                getOwner().getSimulator().scheduleEventRel(
                        TransportMode.PLANE.transportTime(shipment.getSender(), shipment.getReceiver()), this, this, "sendBill",
                        args);
            }
        }
        catch (Exception e)
        {
            Logger.error(e, "ship");
            return;
        }
    }

    /**
     * Method sendBill
     * @param bill the bill to send
     */
    protected void sendBill(final Bill bill)
    {
        // send after accepting the order.
        getOwner().sendContent(bill, new Duration(1.0, DurationUnit.MINUTE));
    }
}
