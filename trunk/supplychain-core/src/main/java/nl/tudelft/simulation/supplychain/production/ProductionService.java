package nl.tudelft.simulation.supplychain.production;

import nl.tudelft.simulation.supplychain.actor.Trader;
import nl.tudelft.simulation.supplychain.content.ProductionOrder;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * The abstract class ProductionService implements the ProductionServiceInterface and is a simple starting point for the
 * production of goods. The bill of materials of the product determines the required raw materials to use. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class ProductionService implements ProductionServiceInterface
{
    /** The actor that owns the production service */
    protected Trader owner;

    /** the stock for getting and storing materials */
    protected StockInterface stock;

    /** The product of the production service */
    protected Product product;

    /**
     * Constructs a new production service for one product.
     * @param owner the actor that owns the production service.
     * @param stock the stock for getting and storing materials.
     * @param product the product of the production service.
     */
    public ProductionService(final Trader owner, final StockInterface stock, final Product product)
    {
        super();
        this.owner = owner;
        this.stock = stock;
        this.product = product;
    }

    /**
     * @see nl.tudelft.simulation.supplychain.production.ProductionServiceInterface#acceptProductionOrder(nl.tudelft.simulation.supplychain.content.ProductionOrder)
     */
    public abstract void acceptProductionOrder(final ProductionOrder productionOrder);

    /**
     * @see nl.tudelft.simulation.supplychain.production.ProductionServiceInterface#getExpectedProductionTime(nl.tudelft.simulation.supplychain.content.ProductionOrder)
     */
    public abstract double getExpectedProductionTime(final ProductionOrder productionOrder);

    /**
     * @return Returns the product.
     */
    public Product getProduct()
    {
        return this.product;
    }
}
