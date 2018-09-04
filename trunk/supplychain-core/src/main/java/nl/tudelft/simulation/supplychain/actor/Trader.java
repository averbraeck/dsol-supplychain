package nl.tudelft.simulation.supplychain.actor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point3d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.roles.Role;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * A Trader is a SupplyChainActor that maintains a Stock of products. The stock is not implemented with the Role, because
 * several roles might want to share the same stock, e.g. a product might be bought and then sold again from the same stock.
 * <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class Trader extends SupplyChainActor
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(Trader.class);

    /**
     * the stock of the trader
     */
    protected StockInterface stock = null;

    /**
     * Constructs a new Trader
     * @param name the name to display for this supply chain actor
     * @param simulator the simulator on which to schedule
     * @param position the location for transportation calculations, which can also be used for animation purposes
     * @param roles the roles of the supply chain actor, might be null
     * @param bank the bank
     */
    public Trader(final String name, final DEVSSimulatorInterfaceUnit simulator, final Point3d position,
            final Role[] roles, final Bank bank)
    {
        super(name, simulator, position, roles, bank);
    }

    /**
     * Constructs a new Trader with a certain bank balance
     * @param name the name to display for this supply chain actor
     * @param simulator the simulator on which to schedule
     * @param position the location for transportation calculations, which can also be used for animation purposes
     * @param roles the roles of the supply chain actor, might be null
     * @param bank the bank
     * @param initialBankBalance the initial bank balance
     */
    public Trader(final String name, final DEVSSimulatorInterfaceUnit simulator, final Point3d position,
            final Role[] roles, final Bank bank, final double initialBankBalance)
    {
        super(name, simulator, position, roles, bank, initialBankBalance);
    }

    /**
     * Give the Trader some initial stock. Note: no clone, so make sure different stocks for different actors are truly
     * different. The method ONLY works when the stock object is still null
     * @param stock the initial stock to set
     */
    public void setInitialStock(final StockInterface stock)
    {
        if (this.stock == null)
        {
            this.stock = stock;
        }
        else
        {
            logger.warn("setInitialStock", "Stock initialized when not empty, initialization ignored");
        }
    }

    /**
     * Implement to check whether the stock is below some level, might trigger ordering of extra amount of the product
     * @param product the product to check the stock for.
     */
    public abstract void checkStock(final Product product);

    /**
     * @return the raw materials
     */
    public List<Product> getProductsOnStock()
    {
        List<Product> products = new ArrayList<Product>();
        Iterator<Product> it = this.stock.iterator();
        while (it.hasNext())
        {
            Product product = it.next();
            products.add(product);
        }
        return products;
    }

    /**
     * @return Returns the stock.
     */
    public StockInterface getStock()
    {
        return this.stock;
    }
}
