package nl.tudelft.simulation.unit.simulator;

import javax.naming.NamingException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.experiment.Experiment;
import nl.tudelft.simulation.dsol.experiment.Replication;

/**
 * Replication with DJUNITS Unit. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ReplicationUnit extends Replication<Time, Duration, SimTimeUnit>
{
    /**
     * @param experiment Experiment
     * @throws NamingException when the context for the replication cannot be created
     */
    public ReplicationUnit(final Experiment<Time, Duration, SimTimeUnit> experiment) throws NamingException
    {
        super(experiment);
    }

    /**
     * Create a new ReplicationUnit.
     * @param id String; id of the new ReplicationUnit
     * @param startTime SimTimeUnit; the start time of the new ReplicationUnit
     * @param warmupPeriod Duration; the warmup period of the new ReplicationUnit
     * @param runLength DoubleScalarRel&lt;TimeUnit&gt;; the run length of the new ReplicationUnit
     * @param model ModelInterfaceUnit; the model
     * @throws NamingException when the context for the replication cannot be created
     */
    public ReplicationUnit(final String id, final SimTimeUnit startTime, final Duration warmupPeriod, final Duration runLength,
            final ModelInterfaceUnit model) throws NamingException
    {
        super(id, startTime, warmupPeriod, runLength, model);
    }

    /** */
    private static final long serialVersionUID = 20140815L;

    /** {@inheritDoc} */
    @Override
    public final String toString()
    {
        return "ReplicationUnit []";
    }
}
