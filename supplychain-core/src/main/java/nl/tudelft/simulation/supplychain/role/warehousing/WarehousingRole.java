package nl.tudelft.simulation.supplychain.role.warehousing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.ProductAmount;
import nl.tudelft.simulation.supplychain.role.warehousing.process.RestockingProcess;

/**
 * The inventory role is a role that handles the storage of products, which can be raw materials for production or finished
 * goods. The InventoyRole can trigger production and purchasing to replenish the inventory.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class WarehousingRole extends Role<WarehousingRole>
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /** the inventory with products. */
    private final Inventory inventory;

    /** TODO: integrate the restocking processes per product as autonomous processes. */
    private final Map<Product, RestockingProcess> restockingProcesses = new LinkedHashMap<>();

    /**
     * Create an InventoryRole object for an actor, with an empty inventory.
     * @param owner the owner of this role
     */
    public WarehousingRole(final WarehousingActor owner)
    {
        super("inventory", owner, new ContentReceiverDirect());
        this.inventory = new Inventory(this);
    }

    /**
     * Create an InventoryRole object for an actor.
     * @param owner the owner of this role
     * @param initialInventory the Inventory to use within this role
     */
    public WarehousingRole(final WarehousingActor owner, final List<ProductAmount> initialInventory)
    {
        super("inventory", owner, new ContentReceiverDirect());
        Throw.whenNull(initialInventory, "initialInventory cannot be null");
        this.inventory = new Inventory(this, initialInventory);
    }

    /**
     * Add a restocking process to this role.
     * @param restockingProcess the restocking process to add to this role
     */
    public void addRestockingService(final RestockingProcess restockingProcess)
    {
        Throw.whenNull(restockingProcess, "restockingService cannot be null");
        Throw.when(!restockingProcess.getInventory().equals(this.inventory), IllegalArgumentException.class,
                "Inventory of the restocking process does not belong to Actor of InventoryRole");
        this.restockingProcesses.put(restockingProcess.getProduct(), restockingProcess);
    }

    /**
     * Implement to check whether the inventory is below some level, might trigger ordering of extra amount of the product.
     * @param product the product to check the inventory for.
     */
    public void checkInventory(final Product product)
    {
        // TODO decide what to do here
    }

    /**
     * @return the raw materials
     */
    public List<Product> getProductsInInventory()
    {
        List<Product> products = new ArrayList<Product>();
        for (Product product : this.inventory.getProducts())
        {
            products.add(product);
        }
        return products;
    }

    /**
     * Return the inventory of this Role.
     * @return the inventory of this Role
     */
    public Inventory getInventory()
    {
        return this.inventory;
    }

    @Override
    public WarehousingActor getActor()
    {
        return (WarehousingActor) super.getActor();
    }

}
