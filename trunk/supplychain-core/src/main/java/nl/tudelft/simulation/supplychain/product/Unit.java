package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;

/**
 * Unit is a class that helps to define SKUs (Stock Keeping Units). At this moment, it just is a placeholder for a unit name,
 * providing some standard static Unit types. We could provide extensions of this class allowing to calculate weight or volume
 * of products, based on the density of the product. It could also provide some basic measures to translate the number of units
 * from one Unit class to the other. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Unit implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** units without weight or volume : PIECE */
    public static final Unit PIECE = new Unit("piece");

    /** units without weight or volume : PALLET */
    public static final Unit PALLET = new Unit("pallet");

    /** units without weight or volume : BOX */
    public static final Unit BOX = new Unit("box");

    /** units with volume : 20 FT CONTAINER */
    public static final Unit CONTAINER20FT = new VolumeUnit("container 20ft", 33.2);

    /** units with volume : 40 FT CONTAINER */
    public static final Unit CONTAINER40FT = new VolumeUnit("container 40ft", 67.6);

    /** units with volume : M3 or cubic meter */
    public static final Unit M3 = new VolumeUnit("m3", 1.0);

    /** units with weight : KG */
    public static final Unit KG = new WeightUnit("kg", 1.0);

    /** the name of the unit for printing purposes */
    protected String name;

    /**
     * Constructor for Unit.
     * @param name the name of the unit for printing purposes.
     */
    public Unit(final String name)
    {
        this.name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return this.name;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return this.name;
    }
}
