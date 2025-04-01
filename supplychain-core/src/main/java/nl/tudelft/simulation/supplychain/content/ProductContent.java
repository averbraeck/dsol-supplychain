package nl.tudelft.simulation.supplychain.content;

import nl.tudelft.simulation.supplychain.product.Product;

/**
 * Content with a product and an amount.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface ProductContent extends Content
{
    /**
     * Return the product of the content.
     * @return the product of the content
     */
    Product product();

    /**
     * Return the amount of product.
     * @return the amount of product
     */
    double amount();

}
