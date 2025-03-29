package nl.tudelft.simulation.supplychain.product;

import java.util.Objects;

import org.djunits.value.vdouble.scalar.Mass;
import org.djutils.exceptions.Throw;

/**
 * The WeightUnit is a Unit that has a certain weight as limiting / determining factor. Examples are mining materials and
 * agricultural products.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class WeightSku extends Sku
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221129L;

    /** weight of the unit in kg. */
    private double weightKg;

    /**
     * Instantiate a Stock Keeping Unit based on weight in kg.
     * @param name String; the name of the unit
     * @param weightKg double; the weight of the unit in kg
     */
    public WeightSku(final String name, final double weightKg)
    {
        super(name);
        Throw.when(weightKg <= 0, IllegalArgumentException.class, "SKU weight cannot be <= 0");
        this.weightKg = weightKg;
    }

    /**
     * Instantiate a Stock Keeping Unit based on weight provided as a Mass.
     * @param name String; the name of the unit
     * @param weight Mass; the weight of the unit
     */
    public WeightSku(final String name, final Mass weight)
    {
        super(name);
        Throw.whenNull(weight, "weight cannot be null");
        Throw.when(weight.si <= 0, IllegalArgumentException.class, "SKU weight cannot be <= 0");
        this.weightKg = weight.si;
    }

    /**
     * Return the weight in kg.
     * @return double; the weight in kg.
     */
    public double getWeightKg()
    {
        return this.weightKg;
    }

    /**
     * Return the weight as a Mass.
     * @return Mass; the weight of one SKU
     */
    public Mass getWeight()
    {
        return Mass.instantiateSI(this.weightKg);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(this.weightKg);
        return result;
    }

    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WeightSku other = (WeightSku) obj;
        return Double.doubleToLongBits(this.weightKg) == Double.doubleToLongBits(other.weightKg);
    }

}
