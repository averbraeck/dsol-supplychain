package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Money;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * The most simple form of an OrderHandler that takes the orders from stock is one that sends out an OrderConfirmation right
 * away, and waits till the delivery date (should be minus the expected transportation time), picks the order, and ships it out
 * as a Shipment. When the order is not available: wait one day and try again till it is available. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class OrderHandlerStock extends OrderHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** for debugging */
    private static final boolean DEBUG = false;

    /**
     * Construct a new OrderHandler that takes the goods from stock when ordered.
     * @param owner the owner of the handler
     * @param stock the stock to use to handle the incoming order
     */
    public OrderHandlerStock(final SupplyChainActor owner, final StockInterface stock)
    {
        super(owner, stock);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        // get the order
        Order order = (Order) content;
        // send out the confirmation
        OrderConfirmation orderConfirmation = new OrderConfirmation(getOwner(), order.getSender(), order.getInternalDemandID(),
                order, OrderConfirmation.CONFIRMED);
        getOwner().sendContent(orderConfirmation, Duration.ZERO);

        if (OrderHandlerStock.DEBUG)
        {
            System.err.println("t=" + getOwner().getSimulatorTime() + " DEBUG -- ORDER CONFIRMATION of actor " + getOwner()
                    + orderConfirmation + " sent.");
        }

        // tell the stock that we claimed some amount
        this.stock.changeClaimedAmount(order.getProduct(), order.getAmount());
        // wait till the right time to start shipping
        try
        {
            // for now we use the TransportMode.PLANE
            // TODO get the transportation mode, currently using TransportMode.PLANE
            Duration transportationDuration = TransportMode.PLANE.transportTime(order.getSender(), order.getReceiver());

            Time proposedShippingDate = ((OrderBasedOnQuote) order).getQuote().getProposedShippingDate();

            Time scheduledShippingTime = proposedShippingDate.minus(transportationDuration);

            // start shipping 8 hours from now at the earliest
            Time shippingTime =
                    Time.max(getOwner().getSimulatorTime().plus(new Duration(8.0, DurationUnit.HOUR)), scheduledShippingTime);
            Serializable[] args = new Serializable[] { order };
            getOwner().getSimulator().scheduleEventAbs(shippingTime, this, this, "ship", args);

            if (OrderHandlerStock.DEBUG)
            {
                System.err.println("t=" + getOwner().getSimulatorTime() + " DEBUG -- SHIPPING from actor " + getOwner()
                        + " scheduled for t=" + shippingTime);
            }
        }
        catch (Exception e)
        {
            Logger.error(e, "handleContent");
            return false;
        }
        return true;
    }

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

                if (OrderHandlerStock.DEBUG)
                {
                    System.out.println("DEBUG -- OrderHandlerStock: transportation delay for order: " + order + " is: "
                            + TransportMode.PLANE.transportTime(shipment.getSender(), shipment.getReceiver()));
                }

                // TODO: get the transportation mode from the shipment?
                getOwner().sendContent(shipment,
                        TransportMode.PLANE.transportTime(shipment.getSender(), shipment.getReceiver()));

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
