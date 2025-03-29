package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;
import java.util.Objects;

import org.djutils.exceptions.Throw;

/**
 * SKU stands for Stock Keeping Unit. At this moment, it just is a placeholder for a unit name, providing some standard static
 * Unit types. We could provide extensions of this class allowing to calculate weight or volume of products, based on the
 * density of the product. It could also provide some basic measures to translate the number of units from one Unit class to the
 * other.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Sku implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221129L;

    /** units without weight or volume : PIECE. */
    public static final Sku PIECE = new Sku("piece");

    /** units without weight or volume : PALLET. */
    public static final Sku PALLET = new Sku("pallet");

    /** units without weight or volume : BOX. */
    public static final Sku BOX = new Sku("box");

    /** units with volume : 20 FT CONTAINER. */
    public static final Sku CONTAINER20FT = new VolumeSku("20ft container", 33.2);

    /** units with volume : 40 FT CONTAINER. */
    public static final Sku CONTAINER40FT = new VolumeSku("40ft container", 67.6);

    /** units with volume : 45 FT (high-cube) CONTAINER. */
    public static final Sku CONTAINER45FT = new VolumeSku("45ft container", 86.06);

    /** units with volume : 53 FT CONTAINER. */
    public static final Sku CONTAINER53FT = new VolumeSku("53ft container", 108.5);

    /** units with volume : M3 or cubic meter. */
    public static final Sku M3 = new VolumeSku("m3", 1.0);

    /** units with weight : KG. */
    public static final Sku KG = new WeightSku("kg", 1.0);

    /** units with weight : (metric) TON. */
    public static final Sku TON = new WeightSku("ton", 1000.0);

    /** the name of the unit for printing and identification purposes. */
    private String name;

    /**
     * Constructor for Unit.
     * @param name the name of the unit for printing purposes.
     */
    public Sku(final String name)
    {
        Throw.whenNull(name, "name cannot be null");
        this.name = name;
    }

    /**
     * Return the name of the SKU.
     * @return the name of the SKU.
     */
    public String getName()
    {
        return this.name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.name);
    }

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
        Sku other = (Sku) obj;
        return Objects.equals(this.name, other.name);
    }

}
