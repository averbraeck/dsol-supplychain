package nl.tudelft.simulation.supplychain.production;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.content.ProductionOrder;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * Production is a basic production unit for a producing Trader. It accepts a ProductionOrder, and searches for the right
 * ProductionService that can produce the ordered product. It only works for a Trader, because there needs to be stock to take
 * the raw materials from, and there needs to be stock to put the final products in. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Production implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the owner of production */
    protected StockKeepingActor owner = null;

    /** the handlers for this role */
    protected Map<Product, ProductionService> productionServices = new HashMap<>();

    /**
     * Constructor for a production department of a Trader.
     * @param owner the owner of production.
     */
    public Production(final StockKeepingActor owner)
    {
        super();
        this.owner = owner;
    }

    /**
     * Add a production service
     * @param productionService the service to add
     */
    public void addProductionService(final ProductionService productionService)
    {
        this.productionServices.put(productionService.getProduct(), productionService);
    }

    /**
     * Method acceptProductionOrder gets the correct production service and starts producing
     * @param productionOrder the production order
     * @return returns false if no production service could be found
     */
    public boolean acceptProductionOrder(final ProductionOrder productionOrder)
    {
        if (this.productionServices.containsKey(productionOrder.getProduct()))
        {
            Logger.trace("Production for actor '{}': acceptProductionOrder: production service found for product: {}",
                    this.owner.getName(), productionOrder.getProduct().getName());
            this.productionServices.get(productionOrder.getProduct()).acceptProductionOrder(productionOrder);
            return true;
        }
        Logger.trace("Production for actor '{}': acceptProductionOrder: could not find production service for product: {}",
                this.owner.getName(), productionOrder.getProduct().getName());
        return false;
    }

    /**
     * Method getProductionServices
     * @return returns the production services
     */
    public Map<Product, ProductionService> getProductionServices()
    {
        return this.productionServices;
    }
}
