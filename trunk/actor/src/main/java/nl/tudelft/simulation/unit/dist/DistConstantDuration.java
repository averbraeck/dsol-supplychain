package nl.tudelft.simulation.unit.dist;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.Java2Random;

/**
 * DistConstantDurationUnit.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistConstantDuration extends DistContinuousDuration
{
    /** */
    private static final long serialVersionUID = 20180904;

    /** the duration unit. */
    private final Duration duration;

    /** static stream. */
    private static final DistConstant unusedDist = new DistConstant(new Java2Random(), 0.0);

    /**
     * constructs a new continuous distribution.
     * @param duration the constant duration distribution parameter
     */
    public DistConstantDuration(final Duration duration)
    {
        super(unusedDist, DurationUnit.SI);
        this.duration = duration;
    }

    /**
     * draws the next stream value according to the probability of this this distribution.
     * @return the next double value drawn.
     */
    public Duration draw()
    {
        return this.duration;
    }

}
