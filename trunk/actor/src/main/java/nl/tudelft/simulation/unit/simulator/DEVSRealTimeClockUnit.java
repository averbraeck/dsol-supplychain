package nl.tudelft.simulation.unit.simulator;

import java.rmi.RemoteException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.ReplicationMode;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSRealTimeClock;

/**
 * DEVSRealTimeClockUnit - the RealTimeClock with Time and Duration. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DEVSRealTimeClockUnit extends DEVSRealTimeClock<Time, Duration, SimTimeUnit>
        implements DEVSSimulatorInterfaceUnit, AnimatorInterface
{
    /** */
    private static final long serialVersionUID = 20140909L;

    /**
     * Create a new DEVSRealTimeClockUnit.
     */
    public DEVSRealTimeClockUnit()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public final void initialize(final Replication<Time, Duration, SimTimeUnit> initReplication,
            final ReplicationMode replicationMode) throws SimRuntimeException
    {
        try
        {
            super.initialize(initReplication, replicationMode);
        }
        catch (RemoteException exception)
        {
            throw new SimRuntimeException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void runUpTo(final Time when) throws SimRuntimeException
    {
        super.runUpTo(when);
    }

    /** {@inheritDoc} */
    @Override
    protected final Duration relativeMillis(final double factor)
    {
        return new Duration(factor, DurationUnit.MILLISECOND);
    }

    /** {@inheritDoc} */
    @Override
    public final String toString()
    {
        return "DEVSRealTimeClockUnit [time=" + getSimulatorTime().getTime() + "]";
    }

}
