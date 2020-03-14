package nl.tudelft.simulation.supplychain.policy.order;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.capabilities.ProducerInterface;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.ProductionOrder;
import nl.tudelft.simulation.supplychain.production.Production;
import nl.tudelft.simulation.supplychain.production.ProductionService;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class OrderPolicyMake extends OrderPolicy
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Construct a new OrderHandler that makes the goods when ordered.
     * @param owner the owner of the handler
     * @param stock the stock to use to handle the incoming order
     */
    public OrderPolicyMake(final SupplyChainActor owner, final StockInterface stock)
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

        Logger.trace("t={} - MTO ORDER CONFIRMATION of actor '{}': sent '{}'", getOwner().getSimulatorTime(),
                getOwner().getName(), orderConfirmation);

        // this is MTO, so we don't keep stock of this product. Therefore, produce it.
        if (!(getOwner() instanceof ProducerInterface))
        {
            Logger.error("OrderHandlerMake: Actor '{}' not a Producer", getOwner().getName());
            return false;
        }

        Production production = ((ProducerInterface) getOwner()).getProduction();
        if (production == null)
        {
            Logger.error("OrderHandlerMake: Production for Actor '{}' not found", getOwner().getName());
            return false;
        }
        ProductionService productionService = production.getProductionServices().get(order.getProduct());
        if (productionService == null)
        {
            Logger.error("OrderHandlerMake: ProductionService for Actor '{}' not found for product {}", getOwner().getName(),
                    order.getProduct());
            return false;
        }
        ProductionOrder productionOrder = new ProductionOrder(((StockKeepingActor) getOwner()), order.getInternalDemandID(),
                order.getDeliveryDate(), order.getProduct(), order.getAmount());
        productionService.acceptProductionOrder(productionOrder);

        // production should get an mto stock
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

            Logger.trace("t={} - MTO SHIPPING from actor '{}': scheduled for t={}", getOwner().getSimulatorTime(),
                    getOwner().getName(), shippingTime);
        }
        catch (Exception e)
        {
            Logger.error(e, "handleContent");
            return false;
        }
        return true;
    }
}
