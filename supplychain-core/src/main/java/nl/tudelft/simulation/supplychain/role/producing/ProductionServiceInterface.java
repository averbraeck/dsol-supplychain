package nl.tudelft.simulation.supplychain.role.producing;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.inventory.InventoryInterface;
import nl.tudelft.simulation.supplychain.message.trade.ProductionOrder;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.inventory.InventoryActorInterface;

/**
 * The ProductionServiceInterface represents a production service. An internal production order can be handed to a class that
 * implements the ProductionServiceInterface, and after some time, depending on the internal logic of the production class, the
 * end products will end up in the stock of the company.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface ProductionServiceInterface extends Serializable
{
    /**
     * @param productionOrder the order to produce
     */
    void acceptProductionOrder(ProductionOrder productionOrder);

    /**
     * Method getExpectedProductionDuration.
     * @param productionOrder the production order
     * @return returns the expected production time for an order in simulator time units
     */
    Duration getExpectedProductionDuration(ProductionOrder productionOrder);
    
    /**
     * Return the product for which the ProductionService applies.
     * @return Product; the product for which the ProductionService applies
     */
    Product getProduct();

    /**
     * Return the actor with inventory.
     * @return InventoryActorInterface; the actor with inventory
     */
    InventoryActorInterface getOwner();

    /**
     * Return the inventory.
     * @return InventoryInterface; the inevntory.
     */
    default InventoryInterface getInventory()
    {
        return getOwner().getInventoryRole().getInventory();
    }
    
}
