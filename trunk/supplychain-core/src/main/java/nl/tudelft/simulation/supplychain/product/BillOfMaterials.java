package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The BillOfMaterials is a list of products and amounts that are needed to make another product. The BillOfMaterials is used in
 * the simulation of manufacturing processes to be able to pick the right materials in the right quantities to make a new
 * product. The amounts in the BillOfMaterials are related to producing one unit of the product to which it belongs, so if the
 * Unit is a Unit.CONTAINER20FT, the amounts in the BillOfMaterials (in their own units) indicate the quantities needed to
 * produce one 20 ft container load of end products. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BillOfMaterials implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the Product for which this is the BOM */
    private Product product;

    /** the amount produced */
    private double amountProduced;

    /** the bill of materials (product, amount) */
    private Map<Product, Double> materials = new LinkedHashMap<Product, Double>();

    /**
     * Construct a new Bill of Materials for a product
     * @param product the product to which this BOM belongs
     */
    public BillOfMaterials(final Product product)
    {
        super();
        this.product = product;
    }

    /**
     * Adds one product with a certain amount in its own Units to the BOM of this product.
     * @param _product the product to add to the BOM
     * @param amount the amount of products needed in its own units
     */
    public void add(final Product _product, final double amount)
    {
        this.materials.put(_product,Double.valueOf(amount));
    }

    /**
     * Removes one product with its total amount from the BOM of this product.
     * @param _product the product
     */
    public void remove(final Product _product)
    {
        this.materials.remove(_product);
    }

    /**
     * @return Returns the map of materials and amounts.
     */
    public Map<Product, Double> getMaterials()
    {
        return this.materials;
    }

    /**
     * @return Returns the product.
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * @param product The product to set.
     */
    public void setProduct(final Product product)
    {
        this.product = product;
    }

    /**
     * @return Returns the amountProduced.
     */
    public double getAmountProduced()
    {
        return this.amountProduced;
    }

    /**
     * @param amountProduced The amountProduced to set.
     */
    public void setAmountProduced(final double amountProduced)
    {
        this.amountProduced = amountProduced;
    }
}
