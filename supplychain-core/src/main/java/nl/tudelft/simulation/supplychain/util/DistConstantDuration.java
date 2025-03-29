package nl.tudelft.simulation.supplychain.util;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.Java2Random;

/**
 * DistConstantDuration.java.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistConstantDuration extends DistContinuousDuration
{
    /** */
    private static final long serialVersionUID = 20221203L;

    /**
     * Create a constant Duration distribution, that can be used in places where a DistConstantDuration is needed.
     * @param constantDuration the constant duration to draw each time
     */
    public DistConstantDuration(final Duration constantDuration)
    {
        super(new DistConstant(new Java2Random(1), constantDuration.getInUnit()), constantDuration.getDisplayUnit());
    }

}
