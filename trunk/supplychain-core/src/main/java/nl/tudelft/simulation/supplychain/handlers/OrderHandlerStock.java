package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.simtime.TimeUnit;
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

    /** the logger. */
    private static Logger logger = LogManager.getLogger(OrderHandlerStock.class);

    /**
     * Construct a new OrderHandler that takes the goods from stock when ordered.
     * @param owner the owner of the handler
     * @param stock the stock to use to handle the incoming order
     */
    public OrderHandlerStock(final SupplyChainActor owner, final StockInterface stock)
    {
        super(owner, stock);
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface#handleContent(java.io.Serializable)
     */
    public boolean handleContent(final Serializable content)
    {
        // get the order
        Order order = (Order) content;
        // send out the confirmation
        OrderConfirmation orderConfirmation = new OrderConfirmation(getOwner(), order.getSender(), order.getInternalDemandID(),
                order, OrderConfirmation.CONFIRMED);
        getOwner().sendContent(orderConfirmation, 0.00000);

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
            double hour = TimeUnit.convert(1.0, TimeUnit.HOUR, getOwner().getSimulator());
            // TODO is this handler used in the game? if so --> remove and use
            // the GameTransportMode

            // for now we use the TransportMode.PLANE
            // TODO get the transportation mode, currently using
            // TransportMode.PLANE
            double transportationTimeInHours = TransportMode.PLANE.transportTime(order.getSender(), order.getReceiver());

            double proposedShippingDate = ((OrderBasedOnQuote) order).getQuote().getProposedShippingDate();

            double scheduledShippingTime = proposedShippingDate
                    - TimeUnit.convert(transportationTimeInHours, TimeUnit.HOUR, getOwner().getSimulator());

            // start shipping 8 hours from now at the earliest
            double shippingTime = Math.max(getOwner().getSimulatorTime() + 8.0 * hour, scheduledShippingTime);
            Serializable[] args = new Serializable[] { order };
            SimEventInterface simEvent = new SimEvent(shippingTime, this, this, "ship", args);

            if (OrderHandlerStock.DEBUG)
            {
                System.err.println("t=" + getOwner().getSimulatorTime() + " DEBUG -- SHIPPING from actor " + getOwner()
                        + " scheduled for t=" + shippingTime);
            }

            getOwner().getSimulator().scheduleEvent(simEvent);
        }
        catch (Exception e)
        {
            logger.fatal("handleContent", e);
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
            double day = TimeUnit.convert(1.0, TimeUnit.DAY, getOwner().getSimulator());
            if (this.stock.getActualAmount(product) < amount)
            {
                // try again in one day
                Serializable[] args = new Serializable[] { order };
                SimEventInterface simEvent = new SimEvent(getOwner().getSimulatorTime() + day, this, this, "ship", args);
                getOwner().getSimulator().scheduleEvent(simEvent);
            }
            else
            {
                // tell the stock that we got the claimed amount
                this.stock.changeClaimedAmount(order.getProduct(), -order.getAmount());
                // available: make shipment and ship to customer
                double unitPrice = this.stock.getUnitPrice(product);
                double actualAmount = this.stock.removeStock(product, amount);
                Shipment shipment = new Shipment(getOwner(), order.getSender(), order.getInternalDemandID(), order, product,
                        actualAmount, actualAmount * unitPrice);
                shipment.setInTransit(true);

                if (OrderHandlerStock.DEBUG)
                {
                    System.out.println("DEBUG -- OrderHandlerStock: transportation delay for order: " + order + " is: "
                            + TimeUnit.convert(TransportMode.PLANE.transportTime(shipment.getSender(), shipment.getReceiver()),
                                    TimeUnit.HOUR, getOwner().getSimulator())
                            + " expressed in: "
                            + getOwner().getSimulator().getReplication().getRunControl().getTreatment().getTimeUnit());
                }

                // TODO get the transportation mode from the shipment?
                getOwner().sendContent(shipment,
                        TransportMode.PLANE.transportTime(shipment.getSender(), shipment.getReceiver()));

                // send a bill when the shipment leaves...
                Bill bill = new Bill(getOwner(), order.getSender(), order.getInternalDemandID(), order,
                        getOwner().getSimulatorTime() + (14.0 * day), shipment.getValue(), "SALE");

                // .... by scheduling it based on the transportation delay
                Serializable[] args = new Serializable[] { bill };
                SimEventInterface simEvent = new SimEvent(getOwner().getSimulatorTime()
                        + TimeUnit.convert(TransportMode.PLANE.transportTime(shipment.getSender(), shipment.getReceiver()),
                                TimeUnit.HOUR, getOwner().getSimulator()),
                        this, this, "sendBill", args);
                getOwner().getSimulator().scheduleEvent(simEvent);
            }
        }
        catch (Exception e)
        {
            logger.fatal("ship", e);
            return;
        }
    }

    /**
     * Method sendBill
     * @param bill the bill to send
     */
    protected void sendBill(final Bill bill)
    {
        getOwner().sendContent(bill, 0.002);
    }
}
