package nl.tudelft.simulation.supplychain.product;

import java.util.Objects;

import org.djunits.value.vdouble.scalar.Volume;
import org.djutils.exceptions.Throw;

/**
 * The VolumeUnit is a Unit that has a certain volume as limiting / determining factor. Examples are fluids and gases.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class VolumeSku extends Sku
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221129L;

    /** volume of the unit in m3. */
    private double volumeM3;

    /**
     * @param name the name of the unit
     * @param volumeM3 determining volume of the unit, in m3
     */
    public VolumeSku(final String name, final double volumeM3)
    {
        super(name);
        Throw.when(volumeM3 <= 0, IllegalArgumentException.class, "SKU volume cannot be <= 0");
        this.volumeM3 = volumeM3;
    }

    /**
     * @param name the name of the unit
     * @param volume determining volume of the unit
     */
    public VolumeSku(final String name, final Volume volume)
    {
        super(name);
        Throw.whenNull(volume, "volume cannot be null");
        Throw.when(volume.si <= 0, IllegalArgumentException.class, "SKU volume cannot be <= 0");
        this.volumeM3 = volume.si;
    }

    /**
     * Return the volume in m3.
     * @return the volume of the SKU in m3.
     */
    public double getVolumeM3()
    {
        return this.volumeM3;
    }

    /**
     * Return the volume as a Volume.
     * @return the volume of the SKU.
     */
    public Volume getVolume()
    {
        return Volume.instantiateSI(this.volumeM3);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(this.volumeM3);
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
        VolumeSku other = (VolumeSku) obj;
        return Double.doubleToLongBits(this.volumeM3) == Double.doubleToLongBits(other.volumeM3);
    }

}
