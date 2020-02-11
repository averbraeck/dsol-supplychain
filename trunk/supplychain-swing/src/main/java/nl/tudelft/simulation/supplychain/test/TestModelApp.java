package nl.tudelft.simulation.supplychain.test;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.Event;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.ReplicationMode;
import nl.tudelft.simulation.dsol.simtime.SimTimeDoubleUnit;
import nl.tudelft.simulation.dsol.simulators.DEVSRealTimeClock;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.animation.D2.AnimationPanel;
import nl.tudelft.simulation.dsol.swing.gui.DSOLApplication;
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.logger.ConsoleLogger;

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
    public TestModelApp(String title, DSOLPanel<Time, Duration, SimTimeDoubleUnit> panel)
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
        ConsoleLogger.create();
        ConsoleLogger.setLevel(Level.INFO);

        TestModel model = new TestModel();
        // DEVSAnimator.TimeDoubleUnit animator = new DEVSAnimator.TimeDoubleUnit();
        DEVSRealTimeClock.TimeDoubleUnit animator = new DEVSRealTimeClock.TimeDoubleUnit();
        Replication.TimeDoubleUnit replication = new Replication.TimeDoubleUnit("rep1", Time.ZERO, Duration.ZERO,
                new Duration(1800.0, DurationUnit.HOUR), model);
        animator.setPauseOnError(true);
        animator.setAnimationDelay(20); // 50 Hz animation update
        replication.getStreams().put("default", new MersenneTwister(1L));
        animator.initialize(replication, ReplicationMode.TERMINATING);
        animator.setSpeedFactor(10000.0);

        TestModelPanel panel = new TestModelPanel(model, animator);

        Rectangle2D extent = new Rectangle2D.Double(-100, 50, 400, 200);
        Dimension size = new Dimension(1024, 768);
        AnimationPanel animationPanel = new AnimationPanel(extent, size, animator);
        panel.getTabbedPane().addTab(0, "animation", animationPanel);
        panel.getTabbedPane().setSelectedIndex(0);
        // tell the animation panel to update its statistics
        animationPanel.notify(new Event(SimulatorInterface.START_REPLICATION_EVENT, animator, null));

        new TestModelApp("TestModelApp", panel);
    }

}
