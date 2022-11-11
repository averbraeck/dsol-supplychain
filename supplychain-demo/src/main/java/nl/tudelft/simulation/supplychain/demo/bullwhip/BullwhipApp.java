package nl.tudelft.simulation.supplychain.demo.bullwhip;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.Event;
import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.ReplicationMode;
import nl.tudelft.simulation.dsol.model.DSOLModel;
import nl.tudelft.simulation.dsol.simtime.SimTimeDoubleUnit;
import nl.tudelft.simulation.dsol.simulators.DEVSRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.animation.D2.AnimationPanel;
import nl.tudelft.simulation.dsol.swing.gui.DSOLApplication;
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.language.DSOLException;

/**
 * TestModelApp.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BullwhipApp extends DSOLApplication
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param title
     * @param panel
     */
    public BullwhipApp(String title, DSOLPanel<Time, Duration, SimTimeDoubleUnit> panel)
    {
        super(title, panel);
    }

    /**
     * @param args args
     * @throws RemoteException if error
     * @throws SimRuntimeException if error
     * @throws NamingException if error
     * @throws DSOLException on dsol error
     */
    public static void main(final String[] args) throws SimRuntimeException, NamingException, RemoteException, DSOLException
    {
        CategoryLogger.setAllLogLevel(Level.TRACE);
        CategoryLogger.setAllLogMessageFormat("{level} - {class_name}.{method}:{line}  {message}");

        DEVSRealTimeAnimator.TimeDoubleUnit animator = new DEVSRealTimeAnimator.TimeDoubleUnit("MTSMTO");
        DSOLModel<Duration, SCSimulatorInterface> model = new BullwhipModel(animator);
        Replication.TimeDoubleUnit replication = Replication.TimeDoubleUnit.create("rep1", Time.ZERO, Duration.ZERO,
                new Duration(3000.0, DurationUnit.HOUR), model);
        animator.setPauseOnError(true);
        animator.setAnimationDelay(20); // 50 Hz animation update
        replication.getStreams().put("default", new MersenneTwister(1L));
        animator.initialize(replication, ReplicationMode.TERMINATING);
        animator.setSpeedFactor(10000.0);

        DSOLPanel<Time, Duration, SimTimeDoubleUnit> panel = new DSOLPanel<Time, Duration, SimTimeDoubleUnit>(model, animator);

        Rectangle2D extent = new Rectangle2D.Double(-400, -300, 800, 600);
        Dimension size = new Dimension(1024, 768);
        AnimationPanel animationPanel = new AnimationPanel(extent, size, animator);
        panel.getTabbedPane().addTab(0, "animation", animationPanel);
        panel.getTabbedPane().setSelectedIndex(0);
        // tell the animation panel to update its statistics
        animationPanel.notify(new Event(SimulatorInterface.START_REPLICATION_EVENT, animator, null));

        new BullwhipApp("MTSMTO", panel);
    }

}
