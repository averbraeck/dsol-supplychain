package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * The ContentStore is taking care of storing content for later use, for instance for matching purposes. It acts as a kind of
 * primitive database system. In this implementation, all the messages are linked to an InternalDemand, as this sets off the
 * whole chain of messages, no matter whether it is a purchase, internal production, or stock replenishment: in all cases the
 * InternalDemand triggers all the other messages. <br>
 * <br>
 * The ContentStore has a HashMap called internalDemandMap that maps the internal demand's uniqueID onto the so-called
 * contentClassMap. This map has the Content's class as key, and maps that onto an ArrayList called 'contentList', which
 * contains all the contents sent or received in order of arrival or sending. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ContentStore extends EventProducer implements ContentStoreInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the received content */
    private Map<Serializable, Map<Class<?>, List<Content>>> internalDemandMap =
            Collections.synchronizedMap(new HashMap<Serializable, Map<Class<?>, List<Content>>>());

    /** the received content, latest state */
    private Map<Class<?>, List<Content>> receivedStateMap = Collections.synchronizedMap(new HashMap<Class<?>, List<Content>>());

    /** the sent content, latest state */
    private Map<Class<?>, List<Content>> sentStateMap = Collections.synchronizedMap(new HashMap<Class<?>, List<Content>>());

    /** the owner */
    private SupplyChainActor owner;

    /** true for debug */
    private static final boolean DEBUG = false;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(ContentStore.class);

    /**
     * Constructs a new ContentStore
     * @param owner the owner
     */
    public ContentStore(final SupplyChainActor owner)
    {
        super();
        this.owner = owner;
    }

    /**
     * Method addContent stores a new Content object into the store.
     * @param content the content to add
     * @param sent sent or not
     */
    public synchronized void addContent(final Content content, final boolean sent)
    {
        if (ContentStore.DEBUG)
        {
            System.err.println("t=" + this.owner.getSimulatorTime() + " DEBUG -- CONTENTSTORE of actor " + this.owner
                    + " -- ADD uniqueId=" + content.getUniqueID() + ", IDid=" + content.getInternalDemandID() + " (" + sent
                    + "): " + content);
        }

        Serializable identifier = content.getInternalDemandID();
        // look if the internal demand already exists
        Map<Class<?>, List<Content>> contentMap = this.internalDemandMap.get(identifier);
        if (contentMap == null)
        {
            contentMap = new HashMap<Class<?>, List<Content>>();
            this.internalDemandMap.put(identifier, contentMap);
        }
        // look if the content class already exists in the contentMap
        List<Content> contentList = contentMap.get(content.getClass());
        if (contentList == null)
        {
            contentList = new ArrayList<Content>();
            contentMap.put(content.getClass(), contentList);
        }
        // add the new content to the end of the list
        contentList.add(content);
        //
        Class<?> contentClass = foldExtendedContentClass(content);
        // look if the content class already exists
        Map<Class<?>, List<Content>> srMap;
        if (sent)
        {
            srMap = this.sentStateMap;
        }
        else
        {
            srMap = this.receivedStateMap;
        }
        List<Content> srList = srMap.get(contentClass);
        if (srList == null)
        {
            srList = new ArrayList<Content>();
            srMap.put(contentClass, srList);
        }
        // add the new content to the end of the list
        srList.add(content);
        // old content...
        removeOldStateContent(content, sent, identifier);
    }

    /**
     * Method removeContent removes a Content object from the store.
     * @param content the content to remove
     * @param sent indicates whether the content was sent or received
     */
    public synchronized void removeContent(final Content content, final boolean sent)
    {
        if (ContentStore.DEBUG)
        {
            System.err.println("t=" + this.owner.getSimulatorTime() + " DEBUG -- CONTENTSTORE of actor " + this.owner
                    + " -- REMOVE uniqueId=" + content.getUniqueID() + ", IDid=" + content.getInternalDemandID() + " (" + sent
                    + "): " + content);
        }

        Serializable identifier = content.getInternalDemandID();
        // remove from InternalDemand map
        Map<Class<?>, List<Content>> contentMap = this.internalDemandMap.get(identifier);
        if (contentMap != null)
        {
            List<Content> contentList = contentMap.get(content.getClass());
            if (contentList != null)
            {
                contentList.remove(content);
            }
        }
        this.removeSentReceivedContent(content, sent);
    }

    /**
     * Method removeSentReceivedContent removes a Content object from the sent / received store.
     * @param content the content to remove
     * @param sent indicates whether the content was sent or received
     */
    public synchronized void removeSentReceivedContent(final Content content, final boolean sent)
    {
        Class<?> contentClass = foldExtendedContentClass(content);
        Map<Class<?>, List<Content>> srMap;
        if (sent)
        {
            srMap = this.sentStateMap;
        }
        else
        {
            srMap = this.receivedStateMap;
        }
        List<?> srList = srMap.get(contentClass);
        if (srList != null)
        {
            srList.remove(content);
        }
    }

    /**
     * Method removeAllContent removes an exisiting Content object from the store. No error message is given when the content
     * was not there; this is just ignored.
     * @param internalDemandID the identifier of the internal demand
     */
    public void removeAllContent(final Serializable internalDemandID)
    {
        Map<?, ?> contentMap = this.internalDemandMap.get(internalDemandID);
        if (contentMap != null)
        {
            removeContentList(contentMap, YellowPageRequest.class);
            removeContentList(contentMap, YellowPageAnswer.class);
            removeContentList(contentMap, RequestForQuote.class);
            removeContentList(contentMap, Quote.class);
            removeContentList(contentMap, Order.class);
            removeContentList(contentMap, OrderStandAlone.class);
            removeContentList(contentMap, OrderBasedOnQuote.class);
            removeContentList(contentMap, OrderConfirmation.class);
            removeContentList(contentMap, Shipment.class);
            removeContentList(contentMap, Bill.class);
            removeContentList(contentMap, Payment.class);
            removeContentList(contentMap, InternalDemand.class);
        }
        removeInternalDemand(internalDemandID);
    }

    /**
     * Private, local method to remove all the content from one of the lists in the internalDemandMap for a certain
     * internalDemandID for a certain class.
     * @param contentMap the Map for one internal demand ID to clean
     * @param clazz the class to search for
     */
    private synchronized void removeContentList(final Map<?, ?> contentMap, final Class<?> clazz)
    {
        List<?> contentList = (List<?>) contentMap.get(clazz);
        if (contentList != null)
        {
            int oldSize = contentList.size();
            while (contentList.size() > 0)
            {
                Content content = (Content) contentList.remove(0);
                this.removeContent(content, true);
                this.removeContent(content, false);
                if (oldSize == contentList.size())
                {
                    logger.fatal("removeAllContent", "object not removed from list for " + clazz);
                    break;
                }
                oldSize = contentList.size();
            }
        }
    }

    /**
     * As we seldomly have a pointer to the InternalDemand object, deleting an InternalDemand object is carried out through its
     * ID.
     * @param internalDemandID the identifier of the internal demand
     */
    protected void removeInternalDemand(final Serializable internalDemandID)
    {
        Map<?, ?> idMap = null;
        idMap = this.internalDemandMap.remove(internalDemandID);

        if (idMap != null)
        {
            List<?> contentList = (List<?>) idMap.get(InternalDemand.class);
            if (contentList != null)
            {
                for (int i = 0; i < contentList.size(); i++)
                {
                    Content content = (Content) contentList.get(i);
                    this.removeContent(content, true);
                    this.removeContent(content, false);
                }
            }
        }
    }

    /**
     * Method getContentList returns a list of Content objects of type clazz based on the internalDemandID.
     * @param internalDemandID the identifier of the content
     * @param clazz the content class to look for
     * @return returns a list of content of type class based on the internalDemandID
     */
    @SuppressWarnings("unchecked")
    public <C extends Content> List<C> getContentList(final Serializable internalDemandID, final Class<C> clazz)
    {
        List<C> contentList = new ArrayList<>();
        for (Content content : this.internalDemandMap.get(internalDemandID).get(clazz))
        {
            contentList.add((C) content);
        }
        return contentList;
    }

    /**
     * Method getContentList returns the Content object of type clazz based on the internalDemandID, for either sent or received
     * items.
     * @param internalDemandID the identifier of the content
     * @param clazz the content class to look for
     * @param sent indicates whether the content was sent or received
     * @return returns a list of content of type class based on the internalDemandID
     */
    @SuppressWarnings("unchecked")
    public <C extends Content> List<C> getContentList(final Serializable internalDemandID, final Class<C> clazz,
            final boolean sent)
    {
        Class<?> contentClass = clazz;
        if (clazz.equals(OrderBasedOnQuote.class) || clazz.equals(OrderStandAlone.class))
        {
            contentClass = Order.class;
        }

        Map<Class<?>, List<Content>> contentMap;
        if (sent)
        {
            contentMap = this.sentStateMap;
        }
        else
        {
            contentMap = this.receivedStateMap;
        }
        List<Content> contentList = contentMap.get(contentClass);
        List<C> result = new ArrayList<>();
        if (contentList != null)
        {
            Iterator<Content> it = contentList.iterator();
            while (it.hasNext())
            {
                Content itContent = it.next();
                if (itContent.getInternalDemandID().equals(internalDemandID))
                {
                    result.add((C) itContent);
                }
            }
        }
        return result;
    }

    /**
     * @param content the content to remove
     * @param sent indicates wheter the content is sent or received
     * @param internalDemandId the internal demand id
     */
    private void removeOldStateContent(final Content content, final boolean sent, final Serializable internalDemandId)
    {
        // remove "old" data
        if (!sent && content instanceof Quote)
        {
            List<?> rfqList = getContentList(internalDemandId, RequestForQuote.class, true);
            if (rfqList.size() == 0)
            {
                // TODO is this needed?
                if (ContentStore.DEBUG)
                {
                    // only do this when debugging, otherwise during
                    // testing the error files grow extremely large
                    logger.warn("t=" + this.owner.getSimulatorTime() + " removeOldStateContent",
                            "could not find RFQ for quote uniqueId=" + content.getUniqueID() + ", IDid="
                                    + content.getInternalDemandID() + " " + content.toString());
                }
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
            List<?> quoteList = getContentList(internalDemandId, Quote.class, false);
            if (quoteList.size() == 0)
            {
                logger.warn("t=" + this.owner.getSimulatorTime() + " removeOldStateContent",
                        "could not find quote for order uniqueId=" + content.getUniqueID() + ", IDid="
                                + content.getInternalDemandID() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < quoteList.size(); i++)
                {
                    Quote quote = (Quote) quoteList.get(i);
                    removeSentReceivedContent(quote, false);
                }
            }
        }
        else if (!sent && content instanceof OrderConfirmation)
        {
            List<?> orderList = getContentList(internalDemandId, Order.class, true);
            if (orderList.size() == 0)
            {
                logger.warn("t=" + this.owner.getSimulatorTime() + " removeOldStateContent",
                        "could not find order for order confirmation uniqueId=" + content.getUniqueID() + ", IDid="
                                + content.getInternalDemandID() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < orderList.size(); i++)
                {
                    Order order = (Order) orderList.get(i);
                    removeSentReceivedContent(order, true);
                }
            }
        }
        else if (!sent && content instanceof Shipment)
        {
            List<?> orderConfirmationList = getContentList(internalDemandId, OrderConfirmation.class, false);
            if (orderConfirmationList.size() == 0)
            {
                logger.warn("t=" + this.owner.getSimulatorTime() + " removeOldStateContent",
                        "could not find order confirmation for shipment uniqueId=" + content.getUniqueID() + ", IDid="
                                + content.getInternalDemandID() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < orderConfirmationList.size(); i++)
                {
                    OrderConfirmation orderConfirmation = (OrderConfirmation) orderConfirmationList.get(i);
                    removeSentReceivedContent(orderConfirmation, false);
                }
            }
        }
        else if (!sent && content instanceof Bill)
        {
            // don't do anything when the bill arrives
            // wait for payment to possibly clear some data
        }
        else if (sent && content instanceof Payment)
        {
            // remove the bill
            List<?> billList = getContentList(internalDemandId, Bill.class, false);
            if (billList.size() == 0)
            {
                logger.warn("t=" + this.owner.getSimulatorTime() + " removeOldStateContent",
                        "could not find bill for payment uniqueId=" + content.getUniqueID() + ", IDid="
                                + content.getInternalDemandID() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < billList.size(); i++)
                {
                    Bill bill = (Bill) billList.get(i);
                    removeSentReceivedContent(bill, false);
                }
            }
        }

        // remove "old" data
        if (sent && content instanceof Quote)
        {
            List<?> rfqList = getContentList(internalDemandId, RequestForQuote.class, false);
            if (rfqList.size() == 0)
            {
                if (ContentStore.DEBUG)
                {
                    // only do this when debugging, otherwise during
                    // testing the error files grow extremely large

                    logger.warn("t=" + this.owner.getSimulatorTime() + " removeOldStateContent2",
                            "could not find RFQ for quote uniqueId=" + content.getUniqueID() + ", IDid="
                                    + content.getInternalDemandID() + " " + content.toString());
                }
            }
            else
            {
                for (int i = 0; i < rfqList.size(); i++)
                {
                    RequestForQuote rfq = (RequestForQuote) rfqList.get(i);
                    removeSentReceivedContent(rfq, false);
                }
            }
        }
        else if (!sent && content instanceof OrderBasedOnQuote)
        {
            List<?> quoteList = getContentList(internalDemandId, Quote.class, true);
            if (quoteList.size() == 0)
            {
                logger.warn("t=" + this.owner.getSimulatorTime() + " removeOldStateContent2",
                        "could not find quote for order uniqueId=" + content.getUniqueID() + ", IDid="
                                + content.getInternalDemandID() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < quoteList.size(); i++)
                {
                    Quote quote = (Quote) quoteList.get(i);
                    removeSentReceivedContent(quote, true);
                }
            }
        }
        else if (sent && content instanceof OrderConfirmation)
        {
            List<?> orderList = getContentList(internalDemandId, Order.class, false);
            if (orderList.size() == 0)
            {
                logger.warn("t=" + this.owner.getSimulatorTime() + " removeOldStateContent2",
                        "could not find order for order confirmation uniqueId=" + content.getUniqueID() + ", IDid="
                                + content.getInternalDemandID() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < orderList.size(); i++)
                {
                    Order order = (Order) orderList.get(i);
                    removeSentReceivedContent(order, false);
                }
            }
        }
        else if (sent && content instanceof Shipment)
        {
            List<?> orderConfirmationList = getContentList(internalDemandId, OrderConfirmation.class, true);
            if (orderConfirmationList.size() == 0)
            {
                logger.warn("t=" + this.owner.getSimulatorTime() + " removeOldStateContent2",
                        "could not find order confirmation for shipment uniqueId=" + content.getUniqueID() + ", IDid="
                                + content.getInternalDemandID() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < orderConfirmationList.size(); i++)
                {
                    OrderConfirmation orderConfirmation = (OrderConfirmation) orderConfirmationList.get(i);
                    removeSentReceivedContent(orderConfirmation, true);
                }
            }
        }
        else if (sent && content instanceof Bill)
        {
            // don't do anything with the bill yet
            // wait for payment
        }
        else if (!sent && content instanceof Payment)
        {
            // remove the bill
            List<?> billList = getContentList(internalDemandId, Bill.class, true);
            if (billList.size() == 0)
            {
                logger.warn("t=" + this.owner.getSimulatorTime() + " removeOldStateContent",
                        "could not find bill for payment uniqueId=" + content.getUniqueID() + ", IDid="
                                + content.getInternalDemandID() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < billList.size(); i++)
                {
                    Bill bill = (Bill) billList.get(i);
                    removeSentReceivedContent(bill, true);
                }
            }
        }

    }

    /**
     * This method folds back extended content classes onto their basic class. Examples are OrderBasedOnQuote and
     * OrderStandAlone that are mapped back onto 'Order' to simplify the business logic, acause the business logic now only has
     * to deal with an 'Order' in the ContentStore, and not with each of the separate extensions.
     * @param content the content of which to fold the class
     * @return returns the class of the fold extended content class
     */
    protected Class<?> foldExtendedContentClass(final Content content)
    {
        Class<?> contentClass = content.getClass();
        if (contentClass.equals(OrderBasedOnQuote.class) || contentClass.equals(OrderStandAlone.class))
        {
            contentClass = Order.class;
        }
        return contentClass;
    }

    /**
     * @return Returns the owner.
     */
    public SupplyChainActor getOwner()
    {
        return this.owner;
    }
}
