package nl.tudelft.simulation.supplychain.message.store.trade;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.OrderStandalone;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.content.ProductionOrder;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.content.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;

/**
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class LeanTradeMessageStore extends TradeMessageStore
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the simulator to schedule time-out events. */
    protected SupplyChainSimulatorInterface simulator;

    /** the map of unanswered content. */
    private Map<Serializable, TradeMessage> unansweredContentMap =
            Collections.synchronizedMap(new LinkedHashMap<Serializable, TradeMessage>());

    /**
     * @param simulator the simulator
     */
    public LeanTradeMessageStore(final SupplyChainSimulatorInterface simulator)
    {
        Throw.whenNull(simulator, "simulator cannot be null");
        this.simulator = simulator;
    }

    @Override
    public synchronized void addMessage(final TradeMessage message, final boolean sent)
    {
        super.addMessage(message, sent);
        Class<? extends TradeMessage> messageClass = message.getClass();
        try
        {
            // schedule the removal after the 'lifetime' of the content is unanswered
            if (messageClass.equals(InternalDemand.class))
            {
                InternalDemand internalDemand = (InternalDemand) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                Time date = Time.max(this.simulator.getAbsSimulatorTime(), internalDemand.getLatestDeliveryDate());
                this.simulator.scheduleEventAbs(date, this, "internalDemandTimeout",
                        new Serializable[] {internalDemand, Boolean.valueOf(sent)});
            }
            else if (messageClass.equals(RequestForQuote.class) && !sent)
            {
                RequestForQuote rfq = (RequestForQuote) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                this.unansweredContentMap.remove(rfq.getInternalDemandId());
                Time date = Time.max(this.simulator.getAbsSimulatorTime(), rfq.getCutoffDate());
                this.simulator.scheduleEventAbs(date, this, "requestForQuoteTimeout",
                        new Serializable[] {rfq, Boolean.valueOf(sent)});
            }
            else if (messageClass.equals(RequestForQuote.class) && sent)
            {
                RequestForQuote rfq = (RequestForQuote) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                this.unansweredContentMap.remove(rfq.getInternalDemandId());
                Time date = Time.max(this.simulator.getAbsSimulatorTime(),
                        rfq.getCutoffDate().plus(new Duration(1.0, DurationUnit.DAY)));
                this.simulator.scheduleEventAbs(date, this, "requestForQuoteTimeout",
                        new Serializable[] {rfq, Boolean.valueOf(sent)});
            }
            else if (messageClass.equals(Quote.class))
            {
                Quote quote = (Quote) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                this.unansweredContentMap.remove(quote.getRequestForQuote().getUniqueId());
                Time date = Time.max(quote.getProposedDeliveryDate(),
                        quote.getRequestForQuote().getCutoffDate().plus(new Duration(1.0, DurationUnit.DAY)));
                date = Time.max(date, quote.getRequestForQuote().getLatestDeliveryDate());
                date = Time.max(this.simulator.getAbsSimulatorTime(), date);
                this.simulator.scheduleEventAbs(date, this, "quoteTimeout", new Serializable[] {quote, Boolean.valueOf(sent)});
            }
            else if (messageClass.equals(OrderBasedOnQuote.class))
            {
                OrderBasedOnQuote order = (OrderBasedOnQuote) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                this.unansweredContentMap.remove(order.getQuote().getUniqueId());
                Time date = Time.max(order.getDeliveryDate(), order.getQuote().getProposedDeliveryDate());
                date = Time.max(date, order.getQuote().getRequestForQuote().getLatestDeliveryDate());
                date = Time.max(this.simulator.getAbsSimulatorTime(), date);
                this.simulator.scheduleEventAbs(date, this, "orderBasedOnQuoteTimeout",
                        new Serializable[] {order, Boolean.valueOf(sent)});
            }
            else if (messageClass.equals(OrderStandalone.class))
            {
                OrderStandalone order = (OrderStandalone) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                this.unansweredContentMap.remove(order.getInternalDemandId());
                Time date = Time.max(this.simulator.getAbsSimulatorTime(), order.getDeliveryDate());
                this.simulator.scheduleEventAbs(date, this, "orderStandAloneTimeout",
                        new Serializable[] {order, Boolean.valueOf(sent)});
            }
            else if (messageClass.equals(OrderConfirmation.class))
            {
                OrderConfirmation orderConfirmation = (OrderConfirmation) message;
                this.unansweredContentMap.remove(orderConfirmation.getOrder().getUniqueId());
            }
            else if (messageClass.equals(ProductionOrder.class) || messageClass.equals(Shipment.class)
                    || messageClass.equals(Bill.class) || messageClass.equals(Payment.class)
                    || messageClass.equals(YellowPageRequest.class) || messageClass.equals(YellowPageAnswer.class))
            {
                // nothing to do
            }
            else
            {
                Logger.warn("addContent - could not find content class {}", messageClass);
            }
        }
        catch (Exception e)
        {
            Logger.warn(e, "addContent");
        }
    }

    @Override
    public synchronized void removeMessage(final TradeMessage message, final boolean sent)
    {
        super.removeMessage(message, sent);
        this.unansweredContentMap.remove(message.getUniqueId());
    }

    /**
     * @param internalDemand the internal demand
     * @param sent boolean sent
     */
    protected void internalDemandTimeout(final InternalDemand internalDemand, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(internalDemand.getUniqueId()))
        {
            this.removeMessage(internalDemand, sent);
        }
    }

    /**
     * @param rfq the request for quote content
     * @param sent sent or received
     */
    protected void requestForQuoteTimeout(final RequestForQuote rfq, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(rfq.getUniqueId()))
        {
            this.removeMessage(rfq, sent);
            super.removeInternalDemand(rfq.getInternalDemandId());
        }
    }

    /**
     * @param quote the quote content
     * @param sent sent or received
     */
    protected void quoteTimeout(final Quote quote, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(quote.getUniqueId()))
        {
            this.removeMessage(quote, sent);
            this.removeMessage(quote.getRequestForQuote(), !sent);
            super.removeInternalDemand(quote.getInternalDemandId());
        }
    }

    /**
     * @param order the quote based order content
     * @param sent sent or received
     */
    protected void orderBasedOnQuoteTimeout(final OrderBasedOnQuote order, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(order.getUniqueId()))
        {
            this.removeMessage(order, sent);
            this.removeMessage(order.getQuote(), !sent);
            this.removeMessage(order.getQuote().getRequestForQuote(), sent);
            super.removeInternalDemand(order.getInternalDemandId());
        }
    }

    /**
     * @param order the stand alone order content
     * @param sent sent or received
     */
    protected void orderStandAloneTimeout(final OrderStandalone order, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(order.getUniqueId()))
        {
            this.removeMessage(order, sent);
            super.removeInternalDemand(order.getInternalDemandId());
        }
    }
}
