package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Mass;

import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;

/**
 * Product represents a certain type of product. It might have a billOfMaterials of other products and amounts, indicating from
 * which products this product can be put together in a production process. When a product does not have a billOfMaterials, the
 * product can not be manufactured in the simulation, in other words, it is a raw material that has always to be acquired from a
 * supplier.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Product implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the bill of materials (product, amount) */
    protected BillOfMaterials billOfMaterials;

    /** the unit in which this product is shipped (boxes, pallets, etc.) */
    protected Unit unit;

    /** the descriptive name of the product. */
    protected String name;

    /** the current world market price of the product per unit */
    protected Money unitMarketPrice = new Money(0.0, MoneyUnit.USD);

    /** the average weight per unit */
    protected Mass averageUnitWeight = Mass.ZERO;

    /** depreciation the depreciation as a percentage per day */
    protected double depreciation = 0.0;

    /**
     * Construct a new product.
     * @param name the descriptive name of the product.
     * @param unit the unit in which this product is shipped (boxes, pallets)
     * @param initialUnitMarketPrice the initial world market price of the product per unit
     * @param averageUnitWeight the average weight per unit
     * @param depreciation the depreciation as a percentage per day for the product
     */
    public Product(final String name, final Unit unit, final Money initialUnitMarketPrice, final Mass averageUnitWeight,
            final double depreciation)
    {
        super();
        this.name = name;
        this.unit = unit;
        this.unitMarketPrice = initialUnitMarketPrice;
        this.billOfMaterials = new BillOfMaterials(this);
        this.averageUnitWeight = averageUnitWeight;
        this.depreciation = depreciation;
    }

    /**
     * Method getBillOfMaterials. Returns the entire BOM as one Map.
     * @return Map
     */
    public BillOfMaterials getBillOfMaterials()
    {
        return this.billOfMaterials;
    }

    /**
     * Method setBillOfMaterials. Sets the BOM as one Map. The copy of the BOM is a shallow copy, which only copies the pointer
     * to the map, so changing the parameter of the setBillOfMaterials method may change the BOM of this product!
     * @param billOfMaterials the Bill of Materials
     */
    public void setBillOfMaterials(final BillOfMaterials billOfMaterials)
    {
        this.billOfMaterials = billOfMaterials;
    }

    /**
     * Method getUnit. Returns the standard unit of this product.
     * @return Unit
     */
    public Unit getUnit()
    {
        return this.unit;
    }

    /**
     * Returns the name.
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return Returns the averageUnitWeight.
     */
    public Mass getAverageUnitWeight()
    {
        return this.averageUnitWeight;
    }

    /**
     * @return Returns the depreciation
     */
    public double getDepreciation()
    {
        return this.depreciation;
    }

    /**
     * @return Returns the unitMarketPrice.
     */
    public Money getUnitMarketPrice()
    {
        return this.unitMarketPrice;
    }

    /**
     * @param unitMarketPrice The unitMarketPrice to set.
     */
    public void setUnitMarketPrice(final Money unitMarketPrice)
    {
        this.unitMarketPrice = unitMarketPrice;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.name;
    }
}
