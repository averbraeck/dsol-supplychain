package nl.tudelft.simulation.unit.dist;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.Dist;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousDurationUnit - wraps the DistContinuous to draw Durations. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousDurationUnit extends Dist
{
    /** */
    private static final long serialVersionUID = 20180904;

    /** the wrapped distribution. */
    private final DistContinuous wrappedDistribution;

    /** the duration unit. */
    private final DurationUnit unit;

    /**
     * constructs a new continuous distribution.
     * @param wrappedDistribution the wrapped continuous distribution
     * @param unit the unit for the parameters (and drawn values) of the wrapped distribution
     */
    public DistContinuousDurationUnit(final DistContinuous wrappedDistribution, final DurationUnit unit)
    {
        super(wrappedDistribution.getStream());
        this.wrappedDistribution = wrappedDistribution;
        this.unit = unit;
    }

    /**
     * draws the next stream value according to the probability of this this distribution.
     * @return the next double value drawn.
     */
    public Duration draw()
    {
        return new Duration(this.wrappedDistribution.draw(), this.unit);
    }

    /**
     * returns the probability density value of an observation.
     * @param observation the observation.
     * @return double the probability density.
     */
    public final double probDensity(final double observation)
    {
        return this.wrappedDistribution.probDensity(observation);
    }
}
