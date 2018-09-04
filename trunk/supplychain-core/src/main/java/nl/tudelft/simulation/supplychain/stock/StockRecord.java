package nl.tudelft.simulation.supplychain.stock;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.unit.TimeUnit;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.Trader;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * A StockRecord keeps the information about products, such as actual, ordered and claimed amounts of products. It assists the
 * Stock object and the restocking policies to assess the needed order amounts. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class StockRecord implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the owner */
    private Trader owner = null;

    /** the simulator to schedule the depriciation */
    private DEVSSimulatorInterfaceUnit simulator = null;

    /** the product for which to keep information */
    private Product product;

    /** the amount currently on stock */
    private double actualAmount;

    /** the amount that is claimed by orders, but not yet taken */
    private double claimedAmount;

    /** the amount that has been ordered, but not yet delivered */
    private double orderedAmount;

    /** the costprice of the total amount of these products in stock */
    private double costprice;

    /** the depreciation per day */
    private double dailyDepreciation = 0.0;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(StockRecord.class);

    /**
     * @param owner the trader
     * @param simulator the simulator
     * @param product the product
     */
    public StockRecord(final Trader owner, final DEVSSimulatorInterfaceUnit simulator, final Product product)
    {
        super();
        this.owner = owner;
        this.simulator = simulator;
        this.product = product;
        this.dailyDepreciation = product.getDepreciation();
        // start the depreciation process...
        try
        {
            this.simulator.scheduleEvent(0.0, this, this, "depreciate", null);
        }
        catch (Exception exception)
        {
            logger.fatal("<init>", exception);
        }
    }

    /**
     * Returns the actualAmount.
     * @return double
     */
    public double getActualAmount()
    {
        return this.actualAmount;
    }

    /**
     * Returns the claimedAmount.
     * @return double
     */
    public double getClaimedAmount()
    {
        return this.claimedAmount;
    }

    /**
     * Returns the orderedAmount.
     * @return double
     */
    public double getOrderedAmount()
    {
        return this.orderedAmount;
    }

    /**
     * Returns the product.
     * @return Product
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * Method setActualAmount.
     * @param actualAmount the actual amount
     * @param unitprice the unit price
     */
    public void setActualAmount(final double actualAmount, final double unitprice)
    {
        this.actualAmount = actualAmount;
        this.costprice = actualAmount * unitprice;
    }

    /**
     * Sets the claimedAmount.
     * @param claimedAmount The claimedAmount to set
     */
    public void setClaimedAmount(final double claimedAmount)
    {
        this.claimedAmount = claimedAmount;
    }

    /**
     * Sets the orderedAmount.
     * @param orderedAmount The orderedAmount to set
     */
    public void setOrderedAmount(final double orderedAmount)
    {
        this.orderedAmount = orderedAmount;
    }

    /**
     * Method addActualAmount.
     * @param delta The change in product; must be positive
     * @param unitprice The costprice of the products. Has to be positive
     */
    public void addActualAmount(final double delta, final double unitprice)
    {
        this.actualAmount += delta;
        this.costprice += delta * unitprice;
    }

    /**
     * Method remove an actualAmount.
     * @param delta The change in product; must be positive
     */
    public void removeActualAmount(final double delta)
    {
        this.costprice -= getUnitPrice() * delta;
        this.actualAmount -= delta;
    }

    /**
     * Changes the claimedAmount.
     * @param delta The claimedAmount to change
     */
    public void changeClaimedAmount(final double delta)
    {
        this.claimedAmount += delta;
    }

    /**
     * Changes the orderedAmount.
     * @param delta The orderedAmount to change
     */
    public void changeOrderedAmount(final double delta)
    {
        this.orderedAmount += delta;
    }

    /**
     * Returns the costprice.
     * @return double
     */
    public double getCostprice()
    {
        return this.costprice;
    }

    /**
     * Returns the costprice per product unit.
     * @return double Returns the costprice per unit
     */
    public double getUnitPrice()
    {
        if (this.actualAmount > 0.0)
        {
            return this.costprice / this.actualAmount;
        }
        return this.product.getUnitMarketPrice();
    }

    /**
     * @param dailyDepriciation the daily depreciation
     */
    public void setDailyDepreciation(final double dailyDepriciation)
    {
        this.dailyDepreciation = dailyDepriciation;
    }

    /**
     * decrease the value of the stock according to the current depreciation
     */
    protected void depreciate()
    {
        try
        {
            this.costprice = (1.0 - this.dailyDepreciation) * this.costprice;
            this.owner.getBankAccount().withdrawFromBalance(this.dailyDepreciation * this.costprice);
            this.simulator.scheduleEvent(TimeUnit.convert(1.0, TimeUnit.DAY, this.simulator), this, this, "depreciate", null);
        }
        catch (Exception exception)
        {
            logger.fatal("depreciate", exception);
        }
    }
}
