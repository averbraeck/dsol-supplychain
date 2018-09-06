package nl.tudelft.simulation.supplychain.test;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.animation.D2.AnimationPanel;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.ReplicationMode;
import nl.tudelft.simulation.dsol.gui.swing.DSOLApplication;
import nl.tudelft.simulation.dsol.gui.swing.DSOLPanel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.event.Event;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.unit.simulator.DEVSAnimatorUnit;
import nl.tudelft.simulation.unit.simulator.DEVSRealTimeClockUnit;
import nl.tudelft.simulation.unit.simulator.ModelInterfaceUnit;
import nl.tudelft.simulation.unit.simulator.SimTimeUnit;

/**
 * TestModelApp.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class TestModelApp extends DSOLApplication
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param title
     * @param panel
     */
    public TestModelApp(String title, DSOLPanel<Time, Duration, SimTimeUnit> panel)
    {
        super(title, panel);
    }

    /**
     * @param args args
     * @throws RemoteException if error
     * @throws SimRuntimeException if error
     * @throws NamingException if error
     */
    public static void main(final String[] args) throws SimRuntimeException, NamingException, RemoteException
    {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig rootLoggerConfig = config.getLoggers().get("");
        rootLoggerConfig.setLevel(Level.WARN);
        ctx.updateLoggers();
        
        ModelInterfaceUnit model = new TestModel();
        DEVSAnimatorUnit animator = new DEVSAnimatorUnit();
        // DEVSRealTimeClockUnit animator = new DEVSRealTimeClockUnit();
        Replication<Time, Duration, SimTimeUnit> replication = new Replication<>("rep1", new SimTimeUnit(Time.ZERO),
                Duration.ZERO, new Duration(1800.0, DurationUnit.HOUR), model);
        animator.setPauseOnError(true);
        animator.setAnimationDelay(20); // 50 Hz animation update
        replication.getStreams().put("default", new MersenneTwister(1L));
        animator.initialize(replication, ReplicationMode.TERMINATING);
        // animator.setSpeedFactor(10000.0);
        
        DSOLPanel<Time, Duration, SimTimeUnit> panel = new DSOLPanel<Time, Duration, SimTimeUnit>(model, animator);

        Rectangle2D extent = new Rectangle2D.Double(-50, -50, 300, 100);
        Dimension size = new Dimension(1024, 768);
        AnimationPanel animationPanel = new AnimationPanel(extent, size, animator);
        panel.getTabbedPane().addTab(0, "animation", animationPanel);
        panel.getTabbedPane().setSelectedIndex(0);
        // tell the animation panel to update its statistics
        animationPanel.notify(new Event(SimulatorInterface.START_REPLICATION_EVENT, animator, null));

        new TestModelApp("TestModelApp", panel);
    }

}
