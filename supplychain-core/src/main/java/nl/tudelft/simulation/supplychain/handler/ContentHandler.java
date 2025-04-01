package nl.tudelft.simulation.supplychain.handler;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.ProductContent;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * ContentHandlers work on behalf of a Role and take care of processing incoming content (messages, news, shipments).
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <C> the content class for which this handler applies
 */
public abstract class ContentHandler<C extends Content> implements Identifiable, Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221126L;

    /** the id of the handler. */
    private final String id;

    /** the role that owns this handler. */
    private final Role<?> role;

    /** the content class for which this handler applies. */
    private final Class<C> contentClass;

    /** the products for which this handler is valid; if empty, all products are valid. */
    private Set<Product> validProducts = new LinkedHashSet<>();

    /** the partner actors for which this handler is valid; if empty, all partners are valid. */
    private Set<Actor> validPartners = new LinkedHashSet<>();

    /**
     * constructs a new content handler.
     * @param id the id of the handler
     * @param role the role that owns this handler
     * @param contentClass the content type that this handler can process
     */
    public ContentHandler(final String id, final Role<?> role, final Class<C> contentClass)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(role, "role cannot be null");
        Throw.whenNull(contentClass, "contentClass cannot be null");
        this.id = id;
        this.role = role;
        this.contentClass = contentClass;
    }

    /**
     * Handle the content.
     * @param content the content to be handled
     * @return true or false
     */
    public abstract boolean handleContent(C content);

    /**
     * Check Content in terms of class and owner.
     * @param content the content to check
     * @return returns whether the content is okay, and we are the one supposed to handle it
     */
    protected boolean checkContent(final Content content)
    {
        if (!getContentClass().equals(content.getClass()))
        {
            Logger.warn("checkContent - Wrong content type for actor " + getRole() + ", handler " + this.getClass() + ": "
                    + content.getClass());
            return false;
        }
        if (!content.receiver().equals(getActor()))
        {
            Logger.warn("checkContent - Wrong receiver for content " + content.toString() + " sent to actor " + getRole());
            return false;
        }
        return true;
    }

    /**
     * Add a valid product to the list of products to handle with this handler.
     * @param product a new valid product to add to the valid product set for this handler
     */
    public void addValidProduct(final Product product)
    {
        this.validProducts.add(product);
    }

    /**
     * @return the valid products.
     */
    public Set<Product> getValidProducts()
    {
        return this.validProducts;
    }

    /**
     * Replace the current set of valid products. If you want to ADD a set, use addValidProduct per product instead.
     * @param validProducts a new set of valid products
     */
    public void setValidProducts(final Set<Product> validProducts)
    {
        this.validProducts = validProducts;
    }

    /**
     * Check whether the product is of the right type for this handler. If the set is empty, all products are valid.
     * @param content the content to check
     * @return whether product type is right or not
     */
    private boolean checkValidProduct(final Content content)
    {
        if (this.validProducts.size() == 0)
        {
            return true;
        }
        if (content instanceof ProductContent productContent)
        {
            return (this.validProducts.contains(productContent.product()));
        }
        return true;
    }

    /**
     * Add a valid partner to the list of supply chain partners to handle with this handler.
     * @param partner a new valid partner to use
     */
    public void addValidPartner(final Actor partner)
    {
        this.validPartners.add(partner);
    }

    /**
     * @return the valid partners.
     */
    public Set<Actor> getValidPartners()
    {
        return this.validPartners;
    }

    /**
     * Replace the current set of valid partners. If you want to ADD a set, use addValidPartner per partner instead.
     * @param validPartners A new set of valid partners.
     */
    public void setValidPartners(final Set<Actor> validPartners)
    {
        this.validPartners = validPartners;
    }

    /**
     * Check whether the partner actor is one that this handler can handle.
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
        return (this.validPartners.contains(content.sender()));
    }

    /**
     * Check partner and content for validity for this handler.
     * @param content the contentto check
     * @return indicating whether the content can be handled by this handler
     */
    protected boolean isValidContent(final Content content)
    {
        return checkContent(content) && checkValidProduct(content) && checkValidPartner(content);
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the role to which this handler belongs.
     * @return the role to which this handler belongs
     */
    public Role<?> getRole()
    {
        return this.role;
    }

    /**
     * Convenience method: return the Actor that owns this handler.
     * @return the Actor that owns this handler
     */
    public Actor getActor()
    {
        return getRole().getActor();
    }

    /**
     * Return the content class for which this handler applies.
     * @return the content class for which this handler applies
     */
    public Class<C> getContentClass()
    {
        return this.contentClass;
    }

}
