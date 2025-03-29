package nl.tudelft.simulation.supplychain.role.inventory;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * Generic restocking service as the parent of different implementations. It contains the product, inventory, and interval for
 * checking the inventory levels or ordering.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface RestockingServiceInterface extends Serializable
{
    /**
     * Return the product for which this is the restocking service.
     * @return the product for which this is the restocking service
     */
    Product getProduct();

    /**
     * Return the inventory that needs to be checked for restocking.
     * @return the inventory that needs to be checked for restocking
     */
    Inventory getInventory();
}
