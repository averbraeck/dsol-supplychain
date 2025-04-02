package nl.tudelft.simulation.supplychain.content.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.GroupedContent;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.content.Order;
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

/**
 * FullContentStore.java.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FullContentStore implements ContentStoreInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the received content. */
    private Map<Long, Map<Class<? extends Content>, List<? super Content>>> groupingContentMap =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /** the received content, latest state. */
    private Map<Class<? extends Content>, List<? super Content>> receivedContentMap =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /** the sent content, latest state. */
    private Map<Class<? extends Content>, List<? super Content>> sentContentMap =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /** the owner. */
    private Actor owner;

    @Override
    public void setOwner(final Actor owner)
    {
        Throw.whenNull(owner, "owner cannot be null");
        Throw.when(this.owner != null, RuntimeException.class,
                "ContentStore - setting owner for %s while it has been set before", owner.toString());
        this.owner = owner;
    }

    @Override
    public synchronized void addContent(final Content content, final boolean sent)
    {
        Throw.whenNull(this.owner, "ContentStore - owner has not been initialized");
        if (content instanceof GroupedContent groupedContent)
        {
            long groupingId = groupedContent.groupingId();
            Map<Class<? extends Content>, List<? super Content>> contentMap = this.groupingContentMap.get(groupingId);
            if (contentMap == null)
            {
                contentMap = new LinkedHashMap<Class<? extends Content>, List<? super Content>>();
                this.groupingContentMap.put(groupingId, contentMap);
            }
            List<? super Content> contentList = contentMap.get(content.getClass());
            if (contentList == null)
            {
                contentList = new ArrayList<Content>();
                contentMap.put(content.getClass(), contentList);
            }
            contentList.add(content);
        }

        Class<? extends Content> contentType = content.getClass();
        Map<Class<? extends Content>, List<? super Content>> srMap = sent ? this.sentContentMap : this.receivedContentMap;
        List<? super Content> srList = srMap.get(contentType);
        if (srList == null)
        {
            srList = new ArrayList<Content>();
            srMap.put(contentType, srList);
        }
        srList.add(content);
        if (content instanceof GroupedContent groupedContent)
        {
            removeOldStateContent(groupedContent, sent);
        }
    }

    @Override
    public synchronized void removeContent(final Content content, final boolean sent)
    {
        Throw.whenNull(this.owner, "ContentStore - owner has not been initialized");
        if (content instanceof GroupedContent groupedContent)
        {
            long groupingId = groupedContent.groupingId();
            Map<Class<? extends Content>, List<? super Content>> contentMap = this.groupingContentMap.get(groupingId);
            if (contentMap != null)
            {
                List<? super Content> contentList = contentMap.get(content.getClass());
                if (contentList != null)
                {
                    contentList.remove(content);
                }
            }
        }
        this.removeSentReceivedContent(content, sent);
    }

    @Override
    public synchronized void removeSentReceivedContent(final Content content, final boolean sent)
    {
        Throw.whenNull(this.owner, "ContentStore - owner has not been initialized");
        Class<? extends Content> contentClass = content.getClass();
        Map<Class<? extends Content>, List<? super Content>> srMap = sent ? this.sentContentMap : this.receivedContentMap;
        List<? super Content> srList = srMap.get(contentClass);
        if (srList != null)
        {
            srList.remove(content);
        }
    }

    @Override
    public void removeAllContents(final long groupingId)
    {
        Throw.whenNull(this.owner, "ContentStore - owner has not been initialized");
        Map<Class<? extends Content>, List<? super Content>> contentMap = this.groupingContentMap.get(groupingId);
        if (contentMap != null)
        {
            removeContentList(contentMap, YellowPageRequest.class);
            removeContentList(contentMap, YellowPageAnswer.class);
            removeContentList(contentMap, RequestForQuote.class);
            removeContentList(contentMap, Quote.class);
            removeContentList(contentMap, Order.class);
            removeContentList(contentMap, OrderStandalone.class);
            removeContentList(contentMap, OrderBasedOnQuote.class);
            removeContentList(contentMap, OrderConfirmation.class);
            removeContentList(contentMap, Shipment.class);
            removeContentList(contentMap, Bill.class);
            removeContentList(contentMap, Payment.class);
            removeContentList(contentMap, Demand.class);
            removeContentList(contentMap, ProductionOrder.class);
        }
    }

    /**
     * Private, local method to remove all the content from one of the lists in the demandMap for a certain groupingId
     * for a certain content type.
     * @param contentMap the Map for one demand ID to clean
     * @param contentType the content type to search for
     */
    private synchronized void removeContentList(final Map<Class<? extends Content>, List<? super Content>> contentMap,
            final Class<? extends Content> contentType)
    {
        List<? super Content> contentList = contentMap.get(contentType);
        if (contentList != null)
        {
            while (contentList.size() > 0)
            {
                Content content = (Content) contentList.remove(0);
                this.removeContent(content, true);
                this.removeContent(content, false);
            }
        }
    }

    /**
     * Method getContentList returns a list of Content objects of type contentClass based on the groupingId.
     * @param groupingId the identifier of the Demand belonging to the content
     * @param contentClass the content class to look for
     * @return returns a list of content items of class contentClass based on the groupingId
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Content> List<T> getContentList(final long groupingId, final Class<T> contentClass)
    {
        List<T> contentList = new ArrayList<>();
        for (Object content : this.groupingContentMap.get(groupingId).get(contentClass))
        {
            contentList.add((T) content);
        }
        return contentList;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Content> List<T> getContentList(final long groupingId, final Class<T> contentClass, final boolean sent)
    {
        Map<Class<? extends Content>, List<? super Content>> contentMap = sent ? this.sentContentMap : this.receivedContentMap;
        List<? super Content> contentList = contentMap.get(contentClass);
        List<T> result = new ArrayList<>();
        if (contentList != null)
        {
            for (Object o : contentList)
            {
                if (o instanceof GroupedContent m)
                {
                    if (m.groupingId() == groupingId)
                    {
                        result.add((T) m);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean contains(final Content content)
    {
        var sentList = this.sentContentMap.get(content.getClass());
        if (sentList != null && sentList.contains(content))
        {
            return true;
        }
        var recdList = this.receivedContentMap.get(content.getClass());
        if (recdList != null && recdList.contains(content))
        {
            return true;
        }
        return false;
    }

    /**
     * @param content the content to remove
     * @param sent indicates whether the content is sent or received
     */
    @SuppressWarnings("checkstyle:methodlength")
    private void removeOldStateContent(final GroupedContent content, final boolean sent)
    {
        long groupingId = content.groupingId();

        // remove "old" data
        if (!sent && content instanceof Quote)
        {
            List<RequestForQuote> rfqList = getContentList(groupingId, RequestForQuote.class, true);
            if (rfqList.size() == 0)
            {
                Logger.warn(
                        "t=" + this.owner.getSimulatorTime() + " removeOldStateContent - could not find RFQ for quote uniqueId="
                                + content.uniqueId() + ", IDid=" + content.groupingId() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < rfqList.size(); i++)
                {
                    RequestForQuote rfq = (RequestForQuote) rfqList.get(i);
                    removeSentReceivedContent(rfq, true);
                }
            }
        }
        else if (sent && content instanceof OrderBasedOnQuote)
        {
            List<Quote> quoteList = getContentList(groupingId, Quote.class, false);
            if (quoteList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find quote for order uniqueId=" + content.uniqueId() + ", IDid="
                        + content.groupingId() + " " + content.toString());
            }
            else
            {
                for (Quote quote : quoteList)
                {
                    removeSentReceivedContent(quote, false);
                }
            }
        }
        else if (!sent && content instanceof OrderConfirmation)
        {
            List<Order> orderList = getContentList(groupingId, Order.class, true);
            if (orderList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find order for order confirmation uniqueId=" + content.uniqueId()
                        + ", IDid=" + content.groupingId() + " " + content.toString());
            }
            else
            {
                for (Order order : orderList)
                {
                    removeSentReceivedContent(order, true);
                }
            }
        }
        else if (!sent && content instanceof Shipment)
        {
            List<OrderConfirmation> orderConfirmationList = getContentList(groupingId, OrderConfirmation.class, false);
            if (orderConfirmationList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find order confirmation for shipment uniqueId="
                        + content.uniqueId() + ", IDid=" + content.groupingId() + " " + content.toString());
            }
            else
            {
                for (OrderConfirmation orderConfirmation : orderConfirmationList)
                {
                    removeSentReceivedContent(orderConfirmation, false);
                }
            }
        }
        // if (!sent && content instanceof Bill): // don't do anything when the bill arrives, wait for payment
        else if (sent && content instanceof Payment)
        {
            // remove the bill
            List<Bill> billList = getContentList(groupingId, Bill.class, false);
            if (billList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find bill for payment uniqueId=" + content.uniqueId() + ", IDid="
                        + content.groupingId() + " " + content.toString());
            }
            else
            {
                for (Bill bill : billList)
                {
                    removeSentReceivedContent(bill, false);
                }
            }
        }

        // remove "old" data
        if (sent && content instanceof Quote)
        {
            List<RequestForQuote> rfqList = getContentList(groupingId, RequestForQuote.class, false);
            if (rfqList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find RFQ for quote uniqueId=" + content.uniqueId() + ", IDid="
                        + content.groupingId() + " " + content.toString());
            }
            else
            {
                for (RequestForQuote rfq : rfqList)
                {
                    removeSentReceivedContent(rfq, false);
                }
            }
        }
        else if (!sent && content instanceof OrderBasedOnQuote)
        {
            List<Quote> quoteList = getContentList(groupingId, Quote.class, true);
            if (quoteList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find quote for order uniqueId=" + content.uniqueId() + ", IDid="
                        + content.groupingId() + " " + content.toString());
            }
            else
            {
                for (Quote quote : quoteList)
                {
                    removeSentReceivedContent(quote, true);
                }
            }
        }
        else if (sent && content instanceof OrderConfirmation)
        {
            List<Order> orderList = getContentList(groupingId, Order.class, false);
            if (orderList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find order for order confirmation uniqueId=" + content.uniqueId()
                        + ", IDid=" + content.groupingId() + " " + content.toString());
            }
            else
            {
                for (Order order : orderList)
                {
                    removeSentReceivedContent(order, false);
                }
            }
        }
        else if (sent && content instanceof Shipment)
        {
            List<OrderConfirmation> orderConfirmationList = getContentList(groupingId, OrderConfirmation.class, true);
            if (orderConfirmationList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find order confirmation for shipment uniqueId="
                        + content.uniqueId() + ", IDid=" + content.groupingId() + " " + content.toString());
            }
            else
            {
                for (OrderConfirmation orderConfirmation : orderConfirmationList)
                {
                    removeSentReceivedContent(orderConfirmation, true);
                }
            }
        }
        // if (sent && content instanceof Bill) // don't do anything with the bill yet, wait for payment
        else if (!sent && content instanceof Payment)
        {
            // remove the bill
            List<Bill> billList = getContentList(groupingId, Bill.class, true);
            if (billList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find bill for payment uniqueId=" + content.uniqueId() + ", IDid="
                        + content.groupingId() + " " + content.toString());
            }
            else
            {
                for (Bill bill : billList)
                {
                    removeSentReceivedContent(bill, true);
                }
            }
        }
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
