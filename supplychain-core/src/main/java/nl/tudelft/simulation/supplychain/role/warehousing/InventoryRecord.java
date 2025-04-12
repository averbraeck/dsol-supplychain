package nl.tudelft.simulation.supplychain.role.warehousing;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * A InventoryRecord keeps the information about products, such as actual, ordered and reserved amounts of products. It assists
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
    private WarehousingActor owner = null;

    /** the simulator to schedule the depreciation. */
    private SupplyChainSimulatorInterface simulator = null;

    /** the product for which to keep information. */
    private Product product;

    /** the amount currently on inventory. */
    private double actualAmount;

    /** the amount that is reserved by orders, but not yet taken. */
    private double reservedAmount;

    /** the amount that has been ordered, but not yet delivered. */
    private double orderedAmount;

    /** the total monetary value of the amount of these products in inventory. */
    private Money totalMonetaryValue = new Money(0.0, MoneyUnit.USD);

    /** the depreciation factor per day. */
    private double dailyDepreciation = 0.0;

    /**
     * @param owner the trader
     * @param simulator the simulator
     * @param product the product
     */
    public InventoryRecord(final WarehousingActor owner, final SupplyChainSimulatorInterface simulator, final Product product)
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
     * Return the reservedAmount.
     * @return double
     */
    public double getReservedAmount()
    {
        return this.reservedAmount;
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
     * Change the actual amount of product with a delta (positive or negative).
     * @param actualDelta the amount that will be added to the total actual amount
     * @param unitprice The unit price of the products; has to be positive
     */
    public void addActualAmount(final double actualDelta, final Money unitprice)
    {
        this.actualAmount += actualDelta;
        this.totalMonetaryValue = this.totalMonetaryValue.plus(unitprice.multiplyBy(actualDelta));
    }

    /**
     * Reserve a certain amount of product.
     * @param reservedDelta the reserved amount that will be added to the total reserved amount
     */
    public void reserveAmount(final double reservedDelta)
    {
        this.reservedAmount += reservedDelta;
    }

    /**
     * Release a certain amount of reserved product.
     * @param releasedDelta the reserved amount that will be added to the total reserved amount
     */
    public void releaseReservedAmount(final double releasedDelta)
    {
        this.reservedAmount -= releasedDelta;
        this.actualAmount -= releasedDelta;
        this.totalMonetaryValue = this.totalMonetaryValue.minus(getUnitMonetaryValue().multiplyBy(releasedDelta));
    }

    /**
     * Indicate that a certain amount of product has been ordered.
     * @param orderedDelta the ordered amount that will be added to the total ordered amount
     */
    public void orderAmount(final double orderedDelta)
    {
        this.orderedAmount += orderedDelta;
    }

    /**
     * Indicate that a certain amount of ordered product has been delivered.
     * @param enteredDelta the amount that will be added the actual amount and subtracted from the ordered amount
     * @param unitprice The unit price of the products; has to be positive
     */
    public void enterOrderedAmount(final double enteredDelta, final Money unitprice)
    {
        this.orderedAmount -= enteredDelta;
        addActualAmount(enteredDelta, unitprice);
    }

    /**
     * Return the total monetary value of the products in the inventory.
     * @return the total monetary value of the products in the inventory
     */
    public Money getTotalMonetaryValue()
    {
        return this.totalMonetaryValue;
    }

    /**
     * Return the monetary value per product unit.
     * @return the monetary value per product unit
     */
    public Money getUnitMonetaryValue()
    {
        if (this.actualAmount > 0.0)
        {
            return this.totalMonetaryValue.divideBy(this.actualAmount);
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
            this.totalMonetaryValue = this.totalMonetaryValue.multiplyBy(1.0 - this.dailyDepreciation);
            this.owner.getFinancingRole().getBank().withdrawFromBalance(this.owner,
                    this.totalMonetaryValue.multiplyBy(this.dailyDepreciation));
            this.simulator.scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, "depreciate", null);
        }
        catch (Exception exception)
        {
            Logger.error(exception, "depreciate");
        }
    }
}
