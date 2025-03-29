package nl.tudelft.simulation.supplychain.message.store.trade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.message.trade.Bill;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.Order;
import nl.tudelft.simulation.supplychain.message.trade.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.message.trade.OrderConfirmation;
import nl.tudelft.simulation.supplychain.message.trade.OrderStandalone;
import nl.tudelft.simulation.supplychain.message.trade.Payment;
import nl.tudelft.simulation.supplychain.message.trade.ProductionOrder;
import nl.tudelft.simulation.supplychain.message.trade.Quote;
import nl.tudelft.simulation.supplychain.message.trade.RequestForQuote;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageRequest;

/**
 * The TradeMessageStore is taking care of storing messages for later use, for instance for matching purposes. It acts as a kind
 * of primitive database system. In this implementation, all the trade messages are linked to an InternalDemand, as this sets
 * off the whole chain of messages, no matter whether it is a purchase, internal production, or stock replenishment: in all
 * cases the InternalDemand triggers all the other messages. <br>
 * <br>
 * The MessageStore has a HashMap called internalDemandMap that maps the internal demand's internaldemandId onto the message
 * type map. This map has the TradeMessage's class as key, and maps that onto an ArrayList called 'messageList', which contains
 * all the messages sent or received in order of arrival or sending.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TradeMessageStore extends LocalEventProducer implements TradeMessageStoreInterface
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221203L;

    /** the received content. */
    private Map<Long, Map<Class<? extends TradeMessage>, List<? super TradeMessage>>> internalDemandMap =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /** the received content, latest state. */
    private Map<Class<? extends TradeMessage>, List<? super TradeMessage>> receivedStateMap =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /** the sent content, latest state. */
    private Map<Class<? extends TradeMessage>, List<? super TradeMessage>> sentStateMap =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /** the owner. */
    private Actor owner;

    @Override
    public void setOwner(final Actor owner)
    {
        Throw.whenNull(owner, "owner cannot be null");
        Throw.when(this.owner != null, RuntimeException.class,
                "MessageStore - setting owner for %s while it has been set before", owner.toString());
        this.owner = owner;
    }

    @Override
    public synchronized void addMessage(final TradeMessage message, final boolean sent)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        long internalDemandId = message.getInternalDemandId();
        Map<Class<? extends TradeMessage>, List<? super TradeMessage>> messageMap =
                this.internalDemandMap.get(internalDemandId);
        if (messageMap == null)
        {
            messageMap = new LinkedHashMap<Class<? extends TradeMessage>, List<? super TradeMessage>>();
            this.internalDemandMap.put(internalDemandId, messageMap);
        }
        List<? super TradeMessage> messageList = messageMap.get(message.getClass());
        if (messageList == null)
        {
            messageList = new ArrayList<TradeMessage>();
            messageMap.put(message.getClass(), messageList);
        }
        messageList.add(message);
        Class<? extends TradeMessage> messageType = foldExtendedMessageClass(message.getClass());
        Map<Class<? extends TradeMessage>, List<? super TradeMessage>> srMap = sent ? this.sentStateMap : this.receivedStateMap;
        List<? super TradeMessage> srList = srMap.get(messageType);
        if (srList == null)
        {
            srList = new ArrayList<TradeMessage>();
            srMap.put(messageType, srList);
        }
        srList.add(message);
        removeOldStateContent(message, sent, internalDemandId);
    }

    @Override
    public synchronized void removeMessage(final TradeMessage message, final boolean sent)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        long identifier = message.getInternalDemandId();
        Map<Class<? extends TradeMessage>, List<? super TradeMessage>> messageMap = this.internalDemandMap.get(identifier);
        if (messageMap != null)
        {
            List<? super TradeMessage> messageList = messageMap.get(message.getClass());
            if (messageList != null)
            {
                messageList.remove(message);
            }
        }
        this.removeSentReceivedMessage(message, sent);
    }

    @Override
    public synchronized void removeSentReceivedMessage(final TradeMessage message, final boolean sent)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        Class<? extends TradeMessage> contentClass = foldExtendedMessageClass(message.getClass());
        Map<Class<? extends TradeMessage>, List<? super TradeMessage>> srMap = sent ? this.sentStateMap : this.receivedStateMap;
        List<? super TradeMessage> srList = srMap.get(contentClass);
        if (srList != null)
        {
            srList.remove(message);
        }
    }

    @Override
    public void removeAllMessages(final long internalDemandId)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        Map<Class<? extends TradeMessage>, List<? super TradeMessage>> messageMap =
                this.internalDemandMap.get(internalDemandId);
        if (messageMap != null)
        {
            removeMessageList(messageMap, YellowPageRequest.class);
            removeMessageList(messageMap, YellowPageAnswer.class);
            removeMessageList(messageMap, RequestForQuote.class);
            removeMessageList(messageMap, Quote.class);
            removeMessageList(messageMap, Order.class);
            removeMessageList(messageMap, OrderStandalone.class);
            removeMessageList(messageMap, OrderBasedOnQuote.class);
            removeMessageList(messageMap, OrderConfirmation.class);
            removeMessageList(messageMap, Shipment.class);
            removeMessageList(messageMap, Bill.class);
            removeMessageList(messageMap, Payment.class);
            removeMessageList(messageMap, InternalDemand.class);
            removeMessageList(messageMap, ProductionOrder.class);
        }
        removeInternalDemand(internalDemandId);
    }

    /**
     * Private, local method to remove all the content from one of the lists in the internalDemandMap for a certain
     * internalDemandId for a certain message type.
     * @param messageMap Map; the Map for one internal demand ID to clean
     * @param messageType MessageType; the message type to search for
     */
    private synchronized void removeMessageList(final Map<Class<? extends TradeMessage>, List<? super TradeMessage>> messageMap,
            final Class<? extends TradeMessage> messageType)
    {
        List<? super TradeMessage> messageList = messageMap.get(messageType);
        if (messageList != null)
        {
            while (messageList.size() > 0)
            {
                TradeMessage message = (TradeMessage) messageList.remove(0);
                this.removeMessage(message, true);
                this.removeMessage(message, false);
            }
        }
    }

    /**
     * When we do not have a pointer to the InternalDemand object, deleting an InternalDemand object is carried out through its
     * internalDemandId.
     * @param internalDemandId the identifier of the internal demand
     */
    protected void removeInternalDemand(final long internalDemandId)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        Map<Class<? extends TradeMessage>, List<? super TradeMessage>> idMap = null;
        idMap = this.internalDemandMap.remove(internalDemandId);

        if (idMap != null)
        {
            List<? super TradeMessage> messageList = idMap.get(InternalDemand.class);
            if (messageList != null)
            {
                for (Object o : messageList)
                {
                    TradeMessage message = (TradeMessage) o;
                    this.removeMessage(message, true);
                    this.removeMessage(message, false);
                }
            }
        }
    }

    /**
     * Method getMessageList returns a list of Message objects of type messageClass based on the internalDemandId.
     * @param internalDemandId the identifier of the InternalDemand belonging to the message
     * @param messageClass the message class to look for
     * @return returns a list of messages of class messageClass based on the internalDemandId
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends TradeMessage> List<T> getMessageList(final long internalDemandId, final Class<T> messageClass)
    {
        List<T> messageList = new ArrayList<>();
        for (Object message : this.internalDemandMap.get(internalDemandId).get(messageClass))
        {
            messageList.add((T) message);
        }
        return messageList;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends TradeMessage> List<T> getMessageList(final long internalDemandId, final Class<T> messageClass,
            final boolean sent)
    {
        Class<? extends TradeMessage> type = foldExtendedMessageClass(messageClass);
        Map<Class<? extends TradeMessage>, List<? super TradeMessage>> messageMap =
                sent ? this.sentStateMap : this.receivedStateMap;
        List<? super TradeMessage> messageList = messageMap.get(type);
        List<T> result = new ArrayList<>();
        if (messageList != null)
        {
            for (Object o : messageList)
            {
                TradeMessage m = (TradeMessage) o;
                if (m.getInternalDemandId() == internalDemandId)
                {
                    result.add((T) m);
                }
            }
        }
        return result;
    }

    /**
     * @param message the message to remove
     * @param sent indicates whether the message is sent or received
     * @param internalDemandId the internal demand id
     */
    @SuppressWarnings("checkstyle:methodlength")
    private void removeOldStateContent(final TradeMessage message, final boolean sent, final long internalDemandId)
    {
        // remove "old" data
        if (!sent && message instanceof Quote)
        {
            List<RequestForQuote> rfqList = getMessageList(internalDemandId, RequestForQuote.class, true);
            if (rfqList.size() == 0)
            {
                Logger.warn(
                        "t=" + this.owner.getSimulatorTime() + " removeOldStateContent - could not find RFQ for quote uniqueId="
                                + message.getUniqueId() + ", IDid=" + message.getInternalDemandId() + " " + message.toString());
            }
            else
            {
                for (int i = 0; i < rfqList.size(); i++)
                {
                    RequestForQuote rfq = (RequestForQuote) rfqList.get(i);
                    removeSentReceivedMessage(rfq, true);
                }
            }
        }
        else if (sent && message instanceof OrderBasedOnQuote)
        {
            List<Quote> quoteList = getMessageList(internalDemandId, Quote.class, false);
            if (quoteList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find quote for order uniqueId=" + message.getUniqueId()
                        + ", IDid=" + message.getInternalDemandId() + " " + message.toString());
            }
            else
            {
                for (Quote quote : quoteList)
                {
                    removeSentReceivedMessage(quote, false);
                }
            }
        }
        else if (!sent && message instanceof OrderConfirmation)
        {
            List<Order> orderList = getMessageList(internalDemandId, Order.class, true);
            if (orderList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find order for order confirmation uniqueId="
                        + message.getUniqueId() + ", IDid=" + message.getInternalDemandId() + " " + message.toString());
            }
            else
            {
                for (Order order : orderList)
                {
                    removeSentReceivedMessage(order, true);
                }
            }
        }
        else if (!sent && message instanceof Shipment)
        {
            List<OrderConfirmation> orderConfirmationList = getMessageList(internalDemandId, OrderConfirmation.class, false);
            if (orderConfirmationList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find order confirmation for shipment uniqueId="
                        + message.getUniqueId() + ", IDid=" + message.getInternalDemandId() + " " + message.toString());
            }
            else
            {
                for (OrderConfirmation orderConfirmation : orderConfirmationList)
                {
                    removeSentReceivedMessage(orderConfirmation, false);
                }
            }
        }
        else if (!sent && message instanceof Bill)
        {
            // don't do anything when the bill arrives
            // wait for payment to possibly clear some data
        }
        else if (sent && message instanceof Payment)
        {
            // remove the bill
            List<Bill> billList = getMessageList(internalDemandId, Bill.class, false);
            if (billList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find bill for payment uniqueId=" + message.getUniqueId()
                        + ", IDid=" + message.getInternalDemandId() + " " + message.toString());
            }
            else
            {
                for (Bill bill : billList)
                {
                    removeSentReceivedMessage(bill, false);
                }
            }
        }

        // remove "old" data
        if (sent && message instanceof Quote)
        {
            List<RequestForQuote> rfqList = getMessageList(internalDemandId, RequestForQuote.class, false);
            if (rfqList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find RFQ for quote uniqueId=" + message.getUniqueId() + ", IDid="
                        + message.getInternalDemandId() + " " + message.toString());
            }
            else
            {
                for (RequestForQuote rfq : rfqList)
                {
                    removeSentReceivedMessage(rfq, false);
                }
            }
        }
        else if (!sent && message instanceof OrderBasedOnQuote)
        {
            List<Quote> quoteList = getMessageList(internalDemandId, Quote.class, true);
            if (quoteList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find quote for order uniqueId=" + message.getUniqueId()
                        + ", IDid=" + message.getInternalDemandId() + " " + message.toString());
            }
            else
            {
                for (Quote quote : quoteList)
                {
                    removeSentReceivedMessage(quote, true);
                }
            }
        }
        else if (sent && message instanceof OrderConfirmation)
        {
            List<Order> orderList = getMessageList(internalDemandId, Order.class, false);
            if (orderList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find order for order confirmation uniqueId="
                        + message.getUniqueId() + ", IDid=" + message.getInternalDemandId() + " " + message.toString());
            }
            else
            {
                for (Order order : orderList)
                {
                    removeSentReceivedMessage(order, false);
                }
            }
        }
        else if (sent && message instanceof Shipment)
        {
            List<OrderConfirmation> orderConfirmationList = getMessageList(internalDemandId, OrderConfirmation.class, true);
            if (orderConfirmationList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find order confirmation for shipment uniqueId="
                        + message.getUniqueId() + ", IDid=" + message.getInternalDemandId() + " " + message.toString());
            }
            else
            {
                for (OrderConfirmation orderConfirmation : orderConfirmationList)
                {
                    removeSentReceivedMessage(orderConfirmation, true);
                }
            }
        }
        else if (sent && message instanceof Bill)
        {
            // don't do anything with the bill yet
            // wait for payment
        }
        else if (!sent && message instanceof Payment)
        {
            // remove the bill
            List<Bill> billList = getMessageList(internalDemandId, Bill.class, true);
            if (billList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find bill for payment uniqueId=" + message.getUniqueId()
                        + ", IDid=" + message.getInternalDemandId() + " " + message.toString());
            }
            else
            {
                for (Bill bill : billList)
                {
                    removeSentReceivedMessage(bill, true);
                }
            }
        }

    }

    /**
     * This method folds back extended message classes onto their basic message class. Examples are OrderBasedOnQuote and
     * OrderStandAlone that are mapped back onto 'Order' to simplify the business logic, because the business logic now only has
     * to deal with an 'Order' in the MessageStore, and not with each of the separate extensions.
     * @param messageClass Class&lt;? extends TradeMessage&gt;; the message class to fold
     * @return Class&lt;? extends TradeMessage&gt;; the folded extended message class
     */
    protected Class<? extends TradeMessage> foldExtendedMessageClass(final Class<? extends TradeMessage> messageClass)
    {
        if (messageClass.equals(OrderBasedOnQuote.class) || messageClass.equals(OrderStandalone.class))
        {
            return Order.class;
        }
        return messageClass;
    }

    /**
     * @return the owner.
     */
    @Override
    public Actor getOwner()
    {
        return this.owner;
    }

}
