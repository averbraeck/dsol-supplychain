package nl.tudelft.simulation.supplychain.demo.bullwhip;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleLogger;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleOutput;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationApplication;
import nl.tudelft.simulation.language.DsolException;
import nl.tudelft.simulation.supplychain.demo.mtsmto.MTSMTOModel;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainAnimator;
import nl.tudelft.simulation.supplychain.test.dsol.SCControlPanel;

/**
 * BullwhipApp.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BullwhipApp extends DsolAnimationApplication
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /**
     * @param title
     * @param panel
     * @throws RemoteException
     * @throws IllegalArgumentException
     * @throws DsolException
     */
    public BullwhipApp(final String title, final DsolPanel panel)
            throws RemoteException, IllegalArgumentException, DsolException
    {
        super(panel, title, new Bounds2d(-400, 400, -300, 300));
        panel.enableSimulationControlButtons();
        panel.getTabbedPane().setSelectedIndex(0);
    }

    /**
     * @param args args
     * @throws RemoteException if error
     * @throws SimRuntimeException if error
     * @throws NamingException if error
     * @throws DsolException on dsol error
     */
    public static void main(final String[] args) throws SimRuntimeException, NamingException, RemoteException, DsolException
    {
        CategoryLogger.setAllLogLevel(Level.TRACE);
        CategoryLogger.setAllLogMessageFormat("{level} - {class_name}.{method}:{line}  {message}");

        SupplyChainAnimator animator = new SupplyChainAnimator("Bullwhip", Time.ZERO);
        animator.setSpeedFactor(3600.0);
        MTSMTOModel model = new MTSMTOModel(animator);
        Replication<Duration> replication =
                new SingleReplication<Duration>("rep1", Duration.ZERO, Duration.ZERO, new Duration(3000.0, DurationUnit.HOUR));
        animator.initialize(model, replication);
        DsolPanel panel = new DsolPanel(new SCControlPanel.TimeDoubleUnit(model, animator));
        panel.addTab("logger", new ConsoleLogger(Level.INFO));
        panel.addTab("console", new ConsoleOutput());
        new BullwhipApp("Bullwhip", panel);
    }

}
