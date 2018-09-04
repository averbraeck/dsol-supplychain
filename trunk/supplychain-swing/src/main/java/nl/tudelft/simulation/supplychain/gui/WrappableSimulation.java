package nl.tudelft.simulation.supplychain.gui;

import javax.naming.NamingException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.unit.simulator.SimulatorInterfaceUnit;

/**
 * Requirements for demonstration that can be shown in the SuperDemo.
 * <p>
 * Copyright (c) 2013-2017 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * <p>
 * $LastChangedDate: 2018-04-06 23:57:52 +0200 (Fri, 06 Apr 2018) $, @version $Revision: 3820 $, by $Author: wjschakel $,
 * initial version 17 dec. 2014 <br>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public interface WrappableSimulation
{
    /**
     * Build the simulation.
     * @param startTime Time; the start time of the simulation
     * @param warmupPeriod Duration; the warm up period of the simulation (use new Duration(0, SECOND) if you don't know what
     *            this is)
     * @param runLength Duration; the duration of the simulation
     * @return SimpleSimulation; the new simulation
     * @throws SimRuntimeException on ???
     * @throws NamingException when context for the animation cannot be created
     */
    SimulatorInterfaceUnit buildSimulator(Time startTime, Duration warmupPeriod, Duration runLength)
            throws SimRuntimeException, NamingException;

    /**
     * Return a very short description of the simulation.
     * @return String; short description of the simulation
     */
    String shortName();

    /**
     * Return a description of the simulation (HTML formatted).
     * @return String; HTML text describing the simulation
     */
    String description();

    /**
     * Set the number of the next spawned replication.
     * @param nextReplication Integer; the next replication number, or null to use the built-in auto-incrementing replication
     *            counter
     */
    void setNextReplication(Integer nextReplication);

}
