package nl.tudelft.simulation.unit.simulator;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class SimTimeUnit extends SimTime<Time, Duration, SimTimeUnit> implements Serializable
{
    /** */
    private static final long serialVersionUID = 20180904;

    /** The time. */
    private Time time;

    /**
     * @param time DoubleSclaar.Abs&lt;TimeUnit&gt;
     */
    public SimTimeUnit(final Time time)
    {
        super(time);
    }

    /** {@inheritDoc} */
    @Override
    public final void add(final Duration simTime)
    {
        this.time = this.time.plus(simTime);
    }

    /** {@inheritDoc} */
    @Override
    public final void subtract(final Duration simTime)
    {
        this.time = this.time.minus(simTime);
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final SimTimeUnit simTime)
    {
        return this.time.compareTo(simTime.get());
    }

    /** {@inheritDoc} */
    @Override
    public final SimTimeUnit setZero()
    {
        this.time = Time.ZERO;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public final SimTimeUnit copy()
    {
        if (this.time.getUnit().equals(this.time.getUnit().getStandardUnit()))
        {
            return new SimTimeUnit(this.time);
        }
        return new SimTimeUnit(new Time(this.time.getInUnit(), this.time.getUnit()));
    }

    /** {@inheritDoc} */
    @Override
    public final void set(final Time value)
    {
        this.time = value;
    }

    /** {@inheritDoc} */
    @Override
    public final Time get()
    {
        return this.time;
    }

    /**
     * @return the time as a strongly typed time.
     */
    public final Time getTime()
    {
        return this.time;
    }

    /** {@inheritDoc} */
    @Override
    public final Duration minus(final SimTimeUnit absoluteTime)
    {
        Duration rel = this.time.minus(absoluteTime.get());
        return rel;
    }

    /** {@inheritDoc} */
    @Override
    public final String toString()
    {
        return "SimTimeUnit [time=" + this.time + "]";
    }
}
