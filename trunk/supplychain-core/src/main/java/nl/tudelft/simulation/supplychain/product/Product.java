package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;

/**
 * Product represents a certain type of product. It might have a billOfMaterials of other products and amounts, indicating from
 * which products this product can be put together in a production process. When a product does not have a billOfMaterials, the
 * product can not be manufactured in the simulation, in other words, it is a raw material that has always to be acquired from a
 * supplier. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
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
    protected double unitMarketPrice = 0.0;

    /** the average weight per unit */
    protected double averageUnitWeight = 0.0;

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
    public Product(final String name, final Unit unit, final double initialUnitMarketPrice, final double averageUnitWeight,
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
    public double getAverageUnitWeight()
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
    public double getUnitMarketPrice()
    {
        return this.unitMarketPrice;
    }

    /**
     * @param unitMarketPrice The unitMarketPrice to set.
     */
    public void setUnitMarketPrice(final double unitMarketPrice)
    {
        this.unitMarketPrice = unitMarketPrice;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.name;
    }
}
