package nl.tudelft.simulation.supplychain.product;

/**
 * The WeightUnit is a Unit that has a certain weight as limiting / determining factor. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class WeightUnit extends Unit
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** weight of the unit in kg */
    private double weight;

    /**
     * @param name the name of the unit
     * @param weight the weight of the unit
     */
    public WeightUnit(final String name, final double weight)
    {
        super(name);
        this.weight = weight;
    }

    /**
     * @return Returns the weight in kg.
     */
    public double getWeight()
    {
        return this.weight;
    }
}
