package nl.tudelft.simulation.supplychain.handler.order;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.ProductionOrder;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;

/**
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderHandlerMake extends OrderHandler<Order>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new OrderHandler that makes the goods when ordered.
     * @param owner the owner of the handler
     * @param stock the stock to use to handle the incoming order
     */
    public OrderHandlerMake(final Role owner, final Inventory stock)
    {
        super("OrderHandlerMake", owner, stock, Order.class);
    }

    @Override
    public boolean handleContent(final Order order)
    {
        // send out the confirmation
        OrderConfirmation orderConfirmation =
                new OrderConfirmation(getRole().getActor(), order.sender(), order.groupingId(), order, true);
        sendContent(orderConfirmation, Duration.ZERO);

        Logger.trace("t={} - MTO ORDER CONFIRMATION of actor '{}': sent '{}'", getSimulator().getSimulatorTime(),
                getActor().getName(), orderConfirmation);

        // this is MTO, so we don't keep stock of this product. Therefore, produce it.
        ProductionOrder productionOrder = new ProductionOrder(getActor(), order.groupingId(), order.deliveryDate(),
                order.product(), order.amount());
        sendContent(productionOrder);

        // production should get an mto stock
        // tell the stock that we claimed some amount
        this.stock.changeClaimedAmount(order.product(), order.amount());

        // wait till the right time to start shipping
        try
        {
            Duration transportationDuration =
                    order.transportOption().estimatedTotalTransportDuration(order.product().getSku());
            Time proposedShippingDate = ((OrderBasedOnQuote) order).quote().proposedShippingDate();
            Time scheduledShippingTime = proposedShippingDate.minus(transportationDuration);

            // start shipping 8 hours from now at the earliest
            Time shippingTime = Time.max(getSimulator().getAbsSimulatorTime().plus(new Duration(8.0, DurationUnit.HOUR)),
                    scheduledShippingTime);
            Serializable[] args = new Serializable[] {order};
            getRole().getSimulator().scheduleEventAbs(shippingTime, this, "ship", args);

            Logger.trace("t={} - MTO SHIPPING from actor '{}': scheduled for t={}", getSimulator().getSimulatorTime(),
                    getActor().getName(), shippingTime);
        }
        catch (Exception e)
        {
            Logger.error(e, "handleContent");
            return false;
        }
        return true;
    }
}
