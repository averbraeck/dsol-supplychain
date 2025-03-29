package nl.tudelft.simulation.supplychain.policy.order;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.message.trade.Order;
import nl.tudelft.simulation.supplychain.message.trade.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.message.trade.OrderConfirmation;

/**
 * An OrderHandler that purchases the goods when receiving an order (no stock).
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderPolicyNoStock extends OrderPolicy<Order>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new OrderHandler that purchases the goods when ordered.
     * @param owner the owner of the policy
     * @param stock the stock to use to handle the incoming order
     */
    public OrderPolicyNoStock(final Role owner, final Inventory stock)
    {
        super("OrderPolicyNoStock", owner, stock, Order.class);
    }

    @Override
    public boolean handleContent(final Order order)
    {
        // send out the confirmation
        OrderConfirmation orderConfirmation = new OrderConfirmation(getActor(), order.getSender(), order.getInternalDemandId(),
                order, OrderConfirmation.CONFIRMED);
        sendMessage(orderConfirmation, Duration.ZERO);

        Logger.trace("t={} - NOSTOCK ORDER CONFIRMATION of actor '{}': sent '{}'", getSimulator().getSimulatorTime(),
                getActor().getName(), orderConfirmation);

        // production should get an mto stock
        // tell the stock that we claimed some amount
        this.stock.changeClaimedAmount(order.getProduct(), order.getAmount());

        // wait till the right time to start shipping
        try
        {
            Duration transportationDuration =
                    order.getTransportOption().estimatedTotalTransportDuration(order.getProduct().getSku());
            Time proposedShippingDate = ((OrderBasedOnQuote) order).getQuote().getProposedShippingDate();
            Time scheduledShippingTime = proposedShippingDate.minus(transportationDuration);

            // start shipping 8 hours from now at the earliest
            Time shippingTime = Time.max(getSimulator().getAbsSimulatorTime().plus(new Duration(8.0, DurationUnit.HOUR)),
                    scheduledShippingTime);
            Serializable[] args = new Serializable[] {order};
            getSimulator().scheduleEventAbs(shippingTime, this, "ship", args);

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
