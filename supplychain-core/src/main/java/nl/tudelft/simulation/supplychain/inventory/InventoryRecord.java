package nl.tudelft.simulation.supplychain.inventory;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * A InventoryRecord keeps the information about products, such as actual, ordered and claimed amounts of products. It assists
 * the Inventory object and the restocking policies to assess the needed order amounts.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InventoryRecord implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221209L;

    /** the owner. */
    private Actor owner = null;

    /** the simulator to schedule the depreciation. */
    private SupplyChainSimulatorInterface simulator = null;

    /** the product for which to keep information. */
    private Product product;

    /** the amount currently on inventory. */
    private double actualAmount;

    /** the amount that is claimed by orders, but not yet taken. */
    private double claimedAmount;

    /** the amount that has been ordered, but not yet delivered. */
    private double orderedAmount;

    /** the costprice of the total amount of these products in inventory. */
    private Money costprice = new Money(0.0, MoneyUnit.USD);

    /** the depreciation factor per day. */
    private double dailyDepreciation = 0.0;

    /**
     * @param owner the trader
     * @param simulator the simulator
     * @param product Product; the product
     */
    public InventoryRecord(final Actor owner, final SupplyChainSimulatorInterface simulator, final Product product)
    {
        this.owner = owner;
        this.simulator = simulator;
        this.product = product;
        this.dailyDepreciation = product.getDepreciation();
        // start the depreciation process...
        try
        {
            this.simulator.scheduleEventNow(this, "depreciate", null);
        }
        catch (Exception exception)
        {
            Logger.error(exception, "<init>");
        }
    }

    /**
     * Return the actualAmount.
     * @return double
     */
    public double getActualAmount()
    {
        return this.actualAmount;
    }

    /**
     * Return the claimedAmount.
     * @return double
     */
    public double getClaimedAmount()
    {
        return this.claimedAmount;
    }

    /**
     * Return the orderedAmount.
     * @return double
     */
    public double getOrderedAmount()
    {
        return this.orderedAmount;
    }

    /**
     * Return the product.
     * @return Product
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * Method setActualAmount.
     * @param newActualAmount the actual amount
     * @param unitprice the unit price
     */
    public void setActualAmount(final double newActualAmount, final Money unitprice)
    {
        this.actualAmount = newActualAmount;
        this.costprice = unitprice.multiplyBy(newActualAmount);
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
    public void addActualAmount(final double delta, final Money unitprice)
    {
        this.actualAmount += delta;
        this.costprice = this.costprice.plus(unitprice.multiplyBy(delta));
    }

    /**
     * Method remove an actualAmount.
     * @param delta The change in product; must be positive
     */
    public void removeActualAmount(final double delta)
    {
        this.costprice = this.costprice.minus(getUnitPrice().multiplyBy(delta));
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
     * Return the costprice.
     * @return double
     */
    public Money getCostprice()
    {
        return this.costprice;
    }

    /**
     * Return the costprice per product unit.
     * @return double Returns the costprice per unit
     */
    public Money getUnitPrice()
    {
        if (this.actualAmount > 0.0)
        {
            return this.costprice.divideBy(this.actualAmount);
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
     * decrease the value of the inventory according to the current depreciation.
     */
    protected void depreciate()
    {
        try
        {
            this.costprice = this.costprice.multiplyBy(1.0 - this.dailyDepreciation);
            this.owner.getBankAccount().withdrawFromBalance(this.costprice.multiplyBy(this.dailyDepreciation));
            this.simulator.scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, "depreciate", null);
        }
        catch (Exception exception)
        {
            Logger.error(exception, "depreciate");
        }
    }
}
