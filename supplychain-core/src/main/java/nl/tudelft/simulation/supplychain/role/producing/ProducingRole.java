package nl.tudelft.simulation.supplychain.role.producing;

import java.util.LinkedHashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainRole;
import nl.tudelft.simulation.supplychain.message.trade.ProductionOrder;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.inventory.InventoryActorInterface;

/**
 * The producing role is a role that handles the production of products from parts, based on a Bill of Materials (BOM). Parts
 * are sourced from the Inventory, and ready products are added to teh Inventory (Make-to-Stock, MTS) or shipped directly to
 * customers (Make-to-Order, MTO).
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class ProducingRole extends SupplyChainRole
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /** the production services per product for this role. */
    private Map<Product, ProductionServiceInterface> productionServices = new LinkedHashMap<>();

    /**
     * Create a ProducingRole object for an actor.
     * @param owner SupplyChainActorInterface; the owner of this role
     */
    public ProducingRole(final InventoryActorInterface owner)
    {
        super(owner);
    }

    /**
     * Add a production service.
     * @param productionService the service to add
     */
    public void addProductionService(final ProductionService productionService)
    {
        this.productionServices.put(productionService.getProduct(), productionService);
    }

    /**
     * Method acceptProductionOrder gets the correct production service and starts producing.
     * @param productionOrder the production order.
     * @return returns false if no production service could be found
     */
    public boolean acceptProductionOrder(final ProductionOrder productionOrder)
    {
        if (this.productionServices.containsKey(productionOrder.getProduct()))
        {
            Logger.trace("Production for actor '{}': acceptProductionOrder: production service found for product: {}",
                    getOwner().getName(), productionOrder.getProduct().getName());
            this.productionServices.get(productionOrder.getProduct()).acceptProductionOrder(productionOrder);
            return true;
        }
        Logger.trace("Production for actor '{}': acceptProductionOrder: could not find production service for product: {}",
                getOwner().getName(), productionOrder.getProduct().getName());
        return false;
    }

    /**
     * Method getProductionServices.
     * @return returns the production services
     */
    public Map<Product, ProductionServiceInterface> getProductionServices()
    {
        return this.productionServices;
    }
}
