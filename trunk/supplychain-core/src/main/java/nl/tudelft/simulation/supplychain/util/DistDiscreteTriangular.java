package nl.tudelft.simulation.supplychain.util;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * DistDiscreteTriangular draws rounded integer values from a Triangular distribution.
 * <p>
 * Copyright (c) 2019-2019 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistDiscreteTriangular extends DistDiscrete
{
    /** */
    private static final long serialVersionUID = 20200214L;

    /** the embedded continuous triangular distribution. */
    private final DistContinuous distTriangular;

    /**
     * Create a triangular distribution from which rounded integer values will be drawn.
     * @param stream the random stream
     * @param min the minimum value
     * @param mode the mode
     * @param max the maximum value
     */
    public DistDiscreteTriangular(final StreamInterface stream, final double min, final double mode, final double max)
    {
        super(stream);
        this.distTriangular = new DistTriangular(stream, min, mode, max);
    }

    /** {@inheritDoc} */
    @Override
    public long draw()
    {
        return Math.round(this.distTriangular.draw());
    }

    /** {@inheritDoc} */
    @Override
    public double probability(final int observation)
    {
        return this.distTriangular.probDensity(observation + 0.5) - this.distTriangular.probDensity(observation - 0.5);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "DistDiscreteTriangular [distTriangular=" + this.distTriangular + "]";
    }

}
