package nl.tudelft.simulation.unit.simulator;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.ReplicationMode;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * Typed extension of the SimulatorInterface without remote exceptions. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface SimulatorInterfaceUnit extends SimulatorInterface<Time, Duration, SimTimeUnit>
{
    /** {@inheritDoc} */
    @Override
    SimTimeUnit getSimulatorTime();

    /** {@inheritDoc} */
    @Override
    void initialize(Replication<Time, Duration, SimTimeUnit> replication, ReplicationMode replicationMode)
            throws SimRuntimeException;

    /** {@inheritDoc} */
    @Override
    boolean isRunning();

    /** {@inheritDoc} */
    @Override
    void start() throws SimRuntimeException;

    /** {@inheritDoc} */
    @Override
    void step() throws SimRuntimeException;

    /** {@inheritDoc} */
    @Override
    void stop() throws SimRuntimeException;

}
