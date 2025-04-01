package nl.tudelft.simulation.supplychain.policy;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.Message;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * SupplyChainPolicy is the Role specific MessagePolicy class. It has a Role as owner, making it
 * unnecessary to cast the Role all the time to a Role. <br>
 * The abstract SupplyChainPolicy has the methods to check whether the content is of the right type, and methods to do basic
 * filtering on product and on the partner with whom the owner is dealing. This makes it very easy to have different policies
 * for e.g. production orders and for purchase orders; it can be done on the basis of the message sender (in case of production
 * orders the owner itself), or on the basis of the product type.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> The type of TradeMessage for which this policy applies
 */
public abstract class SupplyChainPolicy<T extends TradeMessage> extends ContentHandler<T>
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the products for which this policy is valid; if empty, all products are valid. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<Product> validProducts = new LinkedHashSet<Product>();

    /** the partner actors for which this policy is valid; if empty, all partners are valid. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<Actor> validPartners = new LinkedHashSet<>();

    /**
     * @param id the id of the policy
     * @param owner the owner of this policy
     * @param messageClass the message type that this policy can process
     */
    public SupplyChainPolicy(final String id, final Role owner, final Class<T> messageClass)
    {
        super(id, owner, messageClass);
    }

    /**
     * Check Message in terms of class and owner.
     * @param message the message to check
     * @return returns whether the message is okay, and we are the one supposed to handle it
     */
    protected boolean checkMessage(final TradeMessage message)
    {
        if (!getContentClass().equals(message.getClass()))
        {
            Logger.warn("checkContent - Wrong content type for actor " + getRole() + ", policy " + this.getClass() + ": "
                    + message.getClass());
            return false;
        }
        if (!message.getReceiver().equals(getActor()))
        {
            Logger.warn("checkContent - Wrong receiver for content " + message.toString() + " sent to actor " + getRole());
            return false;
        }
        return true;
    }

    /**
     * Add a valid product to the list of products to handle with this policy.
     * @param product a new valid product to add to the valid product set for this policy
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
     * Check whether the product is of the right type for this policy. If the set is empty, all products are valid.
     * @param message the content to check
     * @return whether type is right or not
     */
    private boolean checkValidProduct(final TradeMessage message)
    {
        if (this.validProducts.size() == 0)
        {
            return true;
        }
        if (message instanceof InternalDemand)
        {
            return (this.validProducts.contains(((InternalDemand) (message)).getProduct()));
        }
        long id = message.getInternalDemandId();
        // get the internal demand to retrieve the product
        List<InternalDemand> storedIDs = getRole().getActor().getContentStore().getMessageList(id, InternalDemand.class);
        if (storedIDs.size() == 0)
        {
            return false;
        }
        InternalDemand internalDemand = storedIDs.get(0);
        return (this.validProducts.contains(internalDemand.getProduct()));
    }

    @Override
    public Role getRole()
    {
        return (Role) super.getRole();
    }

    /**
     * Convenience method: return the Actor that owns this policy.
     * @return the Actor that owns this policy
     */
    public Actor getActor()
    {
        return getRole().getActor();
    }
    
    /**
     * Convenience method: return the Model.
     * @return the model
     */
    public SupplyChainModelInterface getModel()
    {
        return getRole().getActor().getModel();
    }

    /**
     * Convenience method: return the Simulator.
     * @return the simulator of this model
     */
    public SupplyChainSimulatorInterface getSimulator()
    {
        return getRole().getActor().getSimulator();
    }
    
    /**
     * Convenience method: send a message to another actor with a delay.
     * @param message the message to send
     * @param delay the time it takes between sending and receiving
     */
    protected void sendMessage(final Message message, final Duration delay)
    {
        getActor().sendContent(message, delay);
    }

    /**
     * Convenience method: send a message to another actor without a delay.
     * @param message the message to send
     */
    protected void sendMessage(final Message message)
    {
        getActor().sendContent(message);
    }
    
    /**
     * Add a valid partner to the list of supply chain partners to handle with this policy.
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
     * Check whether the partner actor is one that this policy can handle.
     * @param content the content to check
     * @return whether partner is right or not
     */
    private boolean checkValidPartner(final TradeMessage content)
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
     * Check partner and content for validity for this policy.
     * @param message the messageto check
     * @return indicating whether the content can be handled by this policy
     */
    protected boolean isValidMessage(final TradeMessage message)
    {
        return checkMessage(message) && checkValidProduct(message) && checkValidPartner(message);
    }

}
