package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.content.AbstractHandler;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * SupplyChainHandler is the SupplyChainActor specific abstract Handler class. It has a SupplyChainActor as owner, making it
 * unnecessary to cast the Actor all the time to a SupplyChainActor. <br>
 * The generic SupplyChainHandler already has the methods to check whether the content is of the right type, and methods to do
 * basic filtering on product and on the partner with whom the owner is dealing. This makes it very easy to have different
 * handlers for e.g. production orders and for purchase orders; it can be done on the basis of the message sender (in case of
 * production orders the owner itself), or on the basis of the product type. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class SupplyChainHandler extends AbstractHandler
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the products for which this handler is valid */
    protected Set<Product> validProducts = new HashSet<Product>();

    /** the partner actors for which this handler is valid */
    protected Set<SupplyChainActor> validPartners = new HashSet<SupplyChainActor>();

    /**
     * @param owner the a that 'owns' the handler
     */
    public SupplyChainHandler(final SupplyChainActor owner)
    {
        super(owner);
    }

    /**
     * Get the content class that this handler is able to handle.
     * @return the content class that this
     */
    public abstract Class<? extends Content> getContentClass();

    /**
     * Check Content in terms of class and owner.
     * @param content the content to check
     * @return returns whether the content is okay, and we are the one supposed to handle it
     */
    protected boolean checkContent(final Content content)
    {
        // e.g., PaymentHandler is assignable from PaymentFineHandler
        if (!getContentClass().isAssignableFrom(content.getClass()))
        {
            Logger.warn("checkContent - Wrong content type for actor " + getOwner() + ", handler " + this.getClass() + ": "
                    + content.getClass());
            return false;
        }
        if (!content.getReceiver().equals(getOwner()))
        {
            Logger.warn("checkContent - Wrong receiver for content " + content.toString() + " sent to actor " + getOwner());
            return false;
        }
        return true;
    }

    /**
     * Add a valid product to the list of products to handle with this handler.
     * @param product a new valid product to use
     */
    public void addValidProduct(final Product product)
    {
        this.validProducts.add(product);
    }

    /**
     * @return Returns the valid products.
     */
    public Set<Product> getValidProducts()
    {
        return this.validProducts;
    }

    /**
     * Replace the current set of valid products. If you want to ADD a set, use addValidProduct per product instead.
     * @param validProducts A new set of valid products
     */
    public void setValidProducts(final Set<Product> validProducts)
    {
        this.validProducts = validProducts;
    }

    /**
     * Check whether the product is of the right type for this handler
     * @param content the content to check
     * @return whether type is right or not
     */
    private boolean checkValidProduct(final Content content)
    {
        if (this.validProducts == null)
        {
            return true;
        }
        if (this.validProducts.size() == 0)
        {
            return true;
        }
        if (content instanceof InternalDemand)
        {
            return (this.validProducts.contains(((InternalDemand) (content)).getProduct()));
        }
        Serializable id = content.getInternalDemandID();
        // get the internal demand to retrieve the product
        List<InternalDemand> storedIDs = getOwner().getContentStore().getContentList(id, InternalDemand.class);
        if (storedIDs.size() == 0)
        {
            return false;
        }
        InternalDemand internalDemand = storedIDs.get(0);
        return (this.validProducts.contains(internalDemand.getProduct()));
    }

    /** {@inheritDoc} */
    @Override
    public SupplyChainActor getOwner()
    {
        return (SupplyChainActor) super.getOwner();
    }

    /**
     * Add a valid partner to the list of supply chain partners to handle with this handler.
     * @param partner a new valid partner to use
     */
    public void addValidPartner(final SupplyChainActor partner)
    {
        this.validPartners.add(partner);
    }

    /**
     * @return Returns the valid partners.
     */
    public Set<SupplyChainActor> getValidPartners()
    {
        return this.validPartners;
    }

    /**
     * Replace the current set of valid partners. If you want to ADD a set, use addValidPartner per partner instead.
     * @param validPartners A new set of valid partners.
     */
    public void setValidPartners(final Set<SupplyChainActor> validPartners)
    {
        this.validPartners = validPartners;
    }

    /**
     * Check whether the partner actor is one that this handler can handle
     * @param content the content to check
     * @return whether partner is right or not
     */
    private boolean checkValidPartner(final Content content)
    {
        if (this.validPartners == null)
        {
            return true;
        }
        if (this.validPartners.size() == 0)
        {
            return true;
        }
        return (this.validPartners.contains(content.getSender()));
    }

    /**
     * Check partner and content for validity for this handler.
     * @param serContent the content to check
     * @return boolean indicating whether the content can be handled by this handler
     */
    protected boolean isValidContent(final Serializable serContent)
    {
        if (serContent == null || !(serContent instanceof Content))
        {
            Logger.warn("isValidContent", "Serializable content = null, or not of type Content");
            return false;
        }
        Content content = (Content) serContent;
        return checkContent(content) && checkValidProduct(content) && checkValidPartner(content);
    }
}
