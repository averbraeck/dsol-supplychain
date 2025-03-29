package nl.tudelft.simulation.supplychain.product;

/**
 * ProductAmount models an amount of products, expressed in the SKU.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ProductAmount
{
    /** the product. */
    private final Product product;

    /** the amount in the product's SKU. */
    private final double amount;

    /**
     * Create an amount of products, expressed in the SKU.
     * @param product Product; the product
     * @param amount double; the amount in the product's SKU
     */
    public ProductAmount(final Product product, final double amount)
    {
        this.product = product;
        this.amount = amount;
    }

    /**
     * @return product
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * @return amount
     */
    public double getAmount()
    {
        return this.amount;
    }

}
