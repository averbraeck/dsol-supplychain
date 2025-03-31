package nl.tudelft.simulation.supplychain.role.producing;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.message.trade.ProductionOrder;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.inventory.Inventory;
import nl.tudelft.simulation.supplychain.role.inventory.InventoryRole;

/**
 * The abstract class ProductionService implements the ProductionService and is a simple starting point for the
 * production of goods. The bill of materials of the product determines the required raw materials to use.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class ProductionService implements Serializable
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** The actor that owns the production service. */
    private InventoryRole owner;

    /** The product of the production service. */
    private Product product;

    /**
     * Constructs a new production service for one product.
     * @param owner the role that owns the production service.
     * @param product the product of the production service.
     */
    public ProductionService(final InventoryRole owner, final Product product)
    {
        Throw.whenNull(owner, "owner cannot be null");
        Throw.whenNull(product, "product cannot be null");
        this.owner = owner;
        this.product = product;
    }

    /**
     * @param productionOrder the order to produce
     */
    public abstract void acceptProductionOrder(ProductionOrder productionOrder);

    /**
     * Method getExpectedProductionDuration.
     * @param productionOrder the production order
     * @return returns the expected production time for an order in simulator time units
     */
    public abstract Duration getExpectedProductionDuration(ProductionOrder productionOrder);

    /**
     * Return the product for which the ProductionService applies.
     * @return the product for which the ProductionService applies
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * Return the role with inventory.
     * @return the actor with inventory
     */
    public InventoryRole getOwner()
    {
        return this.owner;
    }

    /**
     * Return the inventory.
     * @return the inevntory.
     */
    public Inventory getInventory()
    {
        return getOwner().getInventory();
    }

}
