package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.ImmutableLinkedHashMap;
import org.djutils.immutablecollections.ImmutableMap;

/**
 * The BillOfMaterials is a list of products and amounts that are needed to make one SKU of a given product. The BillOfMaterials
 * is used in the simulation of manufacturing processes to be able to pick the right materials in the right quantities to make a
 * new product. The amounts in the BillOfMaterials are related to producing one SKU of the product to which it belongs, so if
 * the Unit is a Unit.CONTAINER20FT, the amounts in the BillOfMaterials (in their own SKUs) indicate the quantities needed to
 * produce one 20 ft container load of end product.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BillOfMaterials implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221129L;

    /** the Product for which this is the BOM. */
    private final Product product;

    /** the bill of materials as a map from product to the amount in the product's SKU. */
    private ImmutableMap<Product, Double> materials = new ImmutableLinkedHashMap<>(new LinkedHashMap<>());

    /**
     * Construct a new Bill of Materials for a product.
     * @param product the product to which this BOM belongs
     */
    public BillOfMaterials(final Product product)
    {
        Throw.whenNull(product, "product cannot be null");
        this.product = product;
    }

    /**
     * Adds one ingredient with a certain amount of he ingredient's SKUs in its own Units to the BOM of this product.
     * @param ingredient the product to add to the BOM
     * @param amount the amount of products needed in its own SKU
     */
    public void add(final Product ingredient, final double amount)
    {
        Throw.whenNull(ingredient, "ingredient cannot be null");
        Throw.when(amount <= 0, IllegalArgumentException.class, "amount of ingredient for a BOM cannot be <= 0");
        Map<Product, Double> newMaterials = this.materials.toMap();
        newMaterials.put(ingredient, amount);
        this.materials = new ImmutableLinkedHashMap<>(newMaterials);
    }

    /**
     * Return the bill of materials as a map from product to the amount in the product's SKU.
     * @return the map of raw materials and amounts in SKUs.
     */
    public ImmutableMap<Product, Double> getMaterials()
    {
        return this.materials;
    }

    /**
     * Return the product of which one unit is produced by this BOM.
     * @return the product that is the result of this BOM
     */
    public Product getProduct()
    {
        return this.product;
    }

}
