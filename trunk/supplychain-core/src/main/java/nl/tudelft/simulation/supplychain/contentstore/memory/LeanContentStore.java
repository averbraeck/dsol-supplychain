package nl.tudelft.simulation.supplychain.contentstore.memory;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.OrderStandAlone;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.content.ProductionOrder;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.content.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class LeanContentStore extends ContentStore
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the simulator to schedule time-out events */
    protected DEVSSimulatorInterface.TimeDoubleUnit simulator;

    /** the map of unanswered content */
    private Map<Serializable, Content> unansweredContentMap = Collections.synchronizedMap(new LinkedHashMap<Serializable, Content>());

    /**
     * @param simulator the simulator
     */
    public LeanContentStore(final DEVSSimulatorInterface.TimeDoubleUnit simulator)
    {
        super();
        this.simulator = simulator;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void addContent(final Content content, final boolean sent)
    {
        super.addContent(content, sent);
        Class<?> contentClass = content.getClass();
        try
        {
            // schedule the removal after the 'lifetime' of the content is unanswered
            if (InternalDemand.class.isAssignableFrom(contentClass))
            {
                InternalDemand internalDemand = (InternalDemand) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                Time date = Time.max(this.simulator.getSimulatorTime(), internalDemand.getLatestDeliveryDate());
                this.simulator.scheduleEventAbs(date, this, this, "internalDemandTimeout",
                        new Serializable[] { internalDemand, Boolean.valueOf(sent) });
            }
            else if (RequestForQuote.class.isAssignableFrom(contentClass) && !sent)
            {
                RequestForQuote rfq = (RequestForQuote) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                this.unansweredContentMap.remove(rfq.getInternalDemandID());
                Time date = Time.max(this.simulator.getSimulatorTime(), rfq.getCutoffDate());
                this.simulator.scheduleEventAbs(date, this, this, "requestForQuoteTimeout",
                        new Serializable[] { rfq, Boolean.valueOf(sent) });
            }
            else if (RequestForQuote.class.isAssignableFrom(contentClass) && sent)
            {
                RequestForQuote rfq = (RequestForQuote) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                this.unansweredContentMap.remove(rfq.getInternalDemandID());
                Time date = Time.max(this.simulator.getSimulatorTime(),
                        rfq.getCutoffDate().plus(new Duration(1.0, DurationUnit.DAY)));
                this.simulator.scheduleEventAbs(date, this, this, "requestForQuoteTimeout",
                        new Serializable[] { rfq, Boolean.valueOf(sent) });
            }
            else if (Quote.class.isAssignableFrom(contentClass))
            {
                Quote quote = (Quote) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                this.unansweredContentMap.remove(quote.getRequestForQuote().getUniqueID());
                Time date = Time.max(quote.getProposedDeliveryDate(),
                        quote.getRequestForQuote().getCutoffDate().plus(new Duration(1.0, DurationUnit.DAY)));
                date = Time.max(date, quote.getRequestForQuote().getLatestDeliveryDate());
                date = Time.max(this.simulator.getSimulatorTime(), date);
                this.simulator.scheduleEventAbs(date, this, this, "quoteTimeout",
                        new Serializable[] { quote, Boolean.valueOf(sent) });
            }
            else if (OrderBasedOnQuote.class.isAssignableFrom(contentClass))
            {
                OrderBasedOnQuote order = (OrderBasedOnQuote) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                this.unansweredContentMap.remove(order.getQuote().getUniqueID());
                Time date = Time.max(order.getDeliveryDate(), order.getQuote().getProposedDeliveryDate());
                date = Time.max(date, order.getQuote().getRequestForQuote().getLatestDeliveryDate());
                date = Time.max(this.simulator.getSimulatorTime(), date);
                this.simulator.scheduleEventAbs(date, this, this, "orderBasedOnQuoteTimeout",
                        new Serializable[] { order, Boolean.valueOf(sent) });
            }
            else if (OrderStandAlone.class.isAssignableFrom(contentClass))
            {
                OrderStandAlone order = (OrderStandAlone) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                this.unansweredContentMap.remove(order.getInternalDemandID());
                Time date = Time.max(this.simulator.getSimulatorTime(), order.getDeliveryDate());
                this.simulator.scheduleEventAbs(date, this, this, "orderStandAloneTimeout",
                        new Serializable[] { order, Boolean.valueOf(sent) });
            }
            else if (OrderConfirmation.class.isAssignableFrom(contentClass))
            {
                OrderConfirmation orderConfirmation = (OrderConfirmation) content;
                this.unansweredContentMap.remove(orderConfirmation.getOrder().getUniqueID());
            }
            else if (ProductionOrder.class.isAssignableFrom(contentClass) || Shipment.class.isAssignableFrom(contentClass)
                    || Bill.class.isAssignableFrom(contentClass) || Payment.class.isAssignableFrom(contentClass)
                    || YellowPageRequest.class.isAssignableFrom(contentClass)
                    || YellowPageAnswer.class.isAssignableFrom(contentClass))
            {
                // nothing to do
            }
            else
            {
                Logger.warn("addContent - could not find content class {}", contentClass);
            }
        }
        catch (Exception e)
        {
            Logger.warn(e, "addContent");
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeContent(final Content content, final boolean sent)
    {
        super.removeContent(content, sent);
        this.unansweredContentMap.remove(content.getUniqueID());
    }

    /**
     * @param internalDemand the internal demand
     * @param sent boolean sent
     */
    protected void internalDemandTimeout(final InternalDemand internalDemand, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(internalDemand.getUniqueID()))
        {
            this.removeContent(internalDemand, sent);
        }
    }

    /**
     * @param rfq the request for quote content
     * @param sent sent or received
     */
    protected void requestForQuoteTimeout(final RequestForQuote rfq, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(rfq.getUniqueID()))
        {
            this.removeContent(rfq, sent);
            super.removeInternalDemand(rfq.getInternalDemandID());
        }
    }

    /**
     * @param quote the quote content
     * @param sent sent or received
     */
    protected void quoteTimeout(final Quote quote, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(quote.getUniqueID()))
        {
            this.removeContent(quote, sent);
            this.removeContent(quote.getRequestForQuote(), !sent);
            super.removeInternalDemand(quote.getInternalDemandID());
        }
    }

    /**
     * @param order the quote based order content
     * @param sent sent or received
     */
    protected void orderBasedOnQuoteTimeout(final OrderBasedOnQuote order, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(order.getUniqueID()))
        {
            this.removeContent(order, sent);
            this.removeContent(order.getQuote(), !sent);
            this.removeContent(order.getQuote().getRequestForQuote(), sent);
            super.removeInternalDemand(order.getInternalDemandID());
        }
    }

    /**
     * @param order the stand alone order content
     * @param sent sent or received
     */
    protected void orderStandAloneTimeout(final OrderStandAlone order, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(order.getUniqueID()))
        {
            this.removeContent(order, sent);
            super.removeInternalDemand(order.getInternalDemandID());
        }
    }
}
