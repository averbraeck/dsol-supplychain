package nl.tudelft.simulation.supplychain.reference;

import java.io.Serializable;

import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Message;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Bank;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;

/**
 * Reference implementation for a Retailer.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Retailer extends SupplyChainActor implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221206L;

    /** The role to buy. */
    private PurchasingRole purchasingRole = null;

    /** The role to sell. */
    private SellingRole sellingRole = null;

    /** the role to keep inventory. */
    private WarehousingRole inventoryRole = null;

    /**
     * @param id String, the unique id of the retaler
     * @param name the longer name of the retailer
     * @param model the model
     * @param location the location of the actor
     * @param locationDescription the location description of the actor (e.g., a city, country)
     * @param bank the bank for the BankAccount
     * @param initialBalance the initial balance for the actor
     * @param messageStore the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Retailer(final String id, final String name, final SupplyChainModelInterface model, final OrientedPoint2d location,
            final String locationDescription, final Bank bank, final Money initialBalance,
            final ContentStoreInterface messageStore) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription, bank, initialBalance, messageStore);
    }

    /**
     * Return the purchasing role.
     * @return the purchasing role
     */
    public PurchasingRole getPurchasingRole()
    {
        return this.purchasingRole;
    }

    /**
     * Set the purchasing role.
     * @param purchasingRole the new purchasing role
     */
    public void setPurchasingRole(final PurchasingRole purchasingRole)
    {
        Throw.whenNull(purchasingRole, "purchasingRole cannot be null");
        Throw.when(this.purchasingRole != null, IllegalStateException.class, "purchasingRole already initialized");
        addRole(purchasingRole);
        this.purchasingRole = purchasingRole;
    }

    /**
     * Return the selling role.
     * @return the selling role
     */
    public SellingRole getSellingRole()
    {
        return this.sellingRole;
    }

    /**
     * Set the selling role.
     * @param sellingRole the new selling role
     */
    public void setSellingRole(final SellingRole sellingRole)
    {
        Throw.whenNull(sellingRole, "sellingRole cannot be null");
        Throw.when(this.sellingRole != null, IllegalStateException.class, "sellingRole already initialized");
        addRole(sellingRole);
        this.sellingRole = sellingRole;
    }

    /**
     * Return the inventory role.
     * @return the inventory role
     */
    public WarehousingRole getInventoryRole()
    {
        return this.inventoryRole;
    }

    /**
     * Set the inventory role.
     * @param inventoryRole the new inventory role
     */
    public void setInventoryRole(final WarehousingRole inventoryRole)
    {
        Throw.whenNull(inventoryRole, "inventoryRole cannot be null");
        Throw.when(this.inventoryRole != null, IllegalStateException.class, "inventoryRole already initialized");
        addRole(inventoryRole);
        this.inventoryRole = inventoryRole;
    }

    @Override
    public void receiveMessage(final Message message)
    {
        Throw.whenNull(this.purchasingRole, "PurchasingRole not initialized for actor: " + this.getName());
        Throw.whenNull(this.sellingRole, "SellingRole not initialized for actor: " + this.getName());
        Throw.whenNull(this.inventoryRole, "InventoryRole not initialized for actor: " + this.getName());
        super.receiveContent(message);
    }
}
