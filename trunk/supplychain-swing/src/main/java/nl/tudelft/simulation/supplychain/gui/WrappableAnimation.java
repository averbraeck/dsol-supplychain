package nl.tudelft.simulation.supplychain.gui;

import java.awt.Rectangle;

import javax.naming.NamingException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.unit.simulator.DEVSAnimatorUnit;

/**
 * Requirements for demonstration that can be shown in the SuperDemo.
 * <p>
 * Copyright (c) 2013-2017 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * <p>
 * $LastChangedDate: 2015-08-23 12:51:29 +0200 (Sun, 23 Aug 2015) $, @version $Revision: 1293 $, by $Author: averbraeck $,
 * initial version 17 dec. 2014 <br>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public interface WrappableAnimation
{
    /**
     * Build the animation.
     * @param startTime Time; the start time of the simulation
     * @param warmupPeriod Duration; the warm up period of the simulation (use new Duration(0, SECOND) if you don't know what
     *            this is)
     * @param runLength Duration; the duration of the simulation
     * @param rect the x, y, width and height for the window to rebuild. Use null for maximized screen.
     * @param exitOnClose Use EXIT_ON_CLOSE when true, DISPOSE_ON_CLOSE when false on closing of the window.
     * @return SimpleSimulation; the new simulation
     * @throws SimRuntimeException on ???
     * @throws NamingException when context for the animation cannot be created
     */
    DEVSAnimatorUnit buildAnimator(Time startTime, Duration warmupPeriod, Duration runLength, Rectangle rect,
            boolean exitOnClose) throws SimRuntimeException, NamingException;

    /**
     * Restart (rebuild) the simulation.
     * @param rect the x, y, width and height for the window to rebuild. Use null for maximized screen.
     * @return SimpleSimulation; the new simulation
     * @throws SimRuntimeException on ???
     * @throws NamingException when context for the animation cannot be created
     */
    DEVSAnimatorUnit rebuildSimulator(Rectangle rect) throws SimRuntimeException, NamingException;

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
     * Stop the timers and threads that are connected when disposing of this wrappable simulation.
     */
    void stopTimersThreads();

    /**
     * Set the number of the next spawned replication.
     * @param nextReplication Integer; the next replication number, or null to use the built-in auto-incrementing replication
     *            counter
     */
    void setNextReplication(Integer nextReplication);

}
