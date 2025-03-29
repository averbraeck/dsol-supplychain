package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;
import java.util.Objects;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.finance.Money;

/**
 * CostPerSKU indicates a cost for, e.g., storing, transporting, (un)loading of one SKU, independent of the product.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class CostPerSku implements Serializable
{
    /** */
    private static final long serialVersionUID = 20221202L;

    /** the SKU, independent of the product. */
    private final Sku sku;

    /** the cost for, e.g., storing, transporting, (un)loading of one SKU. */
    private final Money cost;

    /**
     * Construct a new CostPerSKU object.
     * @param sku Sku; the SKU, independent of the product.
     * @param cost Money; the cost for, e.g., storing, transporting, (un)loading of one SKU.
     */
    public CostPerSku(final Sku sku, final Money cost)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(cost, "cost cannot be null");
        this.sku = sku;
        this.cost = cost;
    }

    /**
     * Return the SKU, independent of the product.
     * @return sku Sku; the SKU, independent of the product
     */
    public Sku getSku()
    {
        return this.sku;
    }

    /**
     * Return the cost for, e.g., storing, transporting, (un)loading of one SKU.
     * @return cost Money; the cost for, e.g., storing, transporting, (un)loading of one SKU
     */
    public Money getCost()
    {
        return this.cost;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.cost, this.sku);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CostPerSku other = (CostPerSku) obj;
        return Objects.equals(this.cost, other.cost) && Objects.equals(this.sku, other.sku);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "CostPerSKU [sku=" + this.sku + ", cost=" + this.cost + "]";
    }

}
