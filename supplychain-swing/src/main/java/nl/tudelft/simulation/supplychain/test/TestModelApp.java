package nl.tudelft.simulation.supplychain.test;

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
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.DSOLAnimationApplication;
import nl.tudelft.simulation.language.DSOLException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainAnimator;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.gui.SCControlPanel;
import nl.tudelft.simulation.supplychain.gui.plot.BankPlot;
import nl.tudelft.simulation.supplychain.gui.plot.StockPlot;

/**
 * TestModelApp.java.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TestModelApp extends DSOLAnimationApplication
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the model. */
    private final TestModel model;

    /**
     * @param title
     * @param panel
     * @param model
     * @throws DSOLException
     * @throws IllegalArgumentException
     * @throws RemoteException
     */
    public TestModelApp(final String title, final DSOLPanel panel, final TestModel model)
            throws RemoteException, IllegalArgumentException, DSOLException
    {
        super(panel, title, new Bounds2d(-100, 300, 50, 250));
        this.model = model;
        panel.enableSimulationControlButtons();
        addTabs();
        panel.getTabbedPane().setSelectedIndex(0);
    }

    private void addTabs()
    {
        TablePanel charts = new TablePanel(3, 2);
        getDSOLPanel().addTab("statistics", charts);
        getDSOLPanel().getTabbedPane().setSelectedIndex(1);
        SupplyChainSimulatorInterface devsSimulator = this.model.getSimulator();

        BankPlot fb = new BankPlot(this.model, "Factory Bank balance", this.model.factory.getBankAccount());
        charts.setCell(fb.getSwingPanel(), 0, 0);

        BankPlot pb = new BankPlot(this.model, "PCShop Bank balance", this.model.pcShop.getBankAccount());
        charts.setCell(pb.getSwingPanel(), 1, 0);

        BankPlot cb = new BankPlot(this.model, "Client Bank balance", this.model.client.getBankAccount());
        charts.setCell(cb.getSwingPanel(), 2, 0);

        StockPlot fs = new StockPlot(this.model, "Factory stock Laptop", this.model.factory.getInventory(), this.model.laptop);
        charts.setCell(fs.getSwingPanel(), 0, 1);

        StockPlot ps = new StockPlot(this.model, "PCShop stock Laptop", this.model.pcShop.getInventory(), this.model.laptop);
        charts.setCell(ps.getSwingPanel(), 1, 1);
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
        CategoryLogger.setAllLogLevel(Level.INFO);
        CategoryLogger.setAllLogMessageFormat("{level} - {class_name}.{method}:{line}  {message}");

        SupplyChainAnimator animator = new SupplyChainAnimator("MTSMTO", Time.ZERO);
        animator.setSpeedFactor(3600.0);
        TestModel model = new TestModel(animator);
        Replication<Duration> replication =
                new SingleReplication<Duration>("rep1", Duration.ZERO, Duration.ZERO, new Duration(1800.0, DurationUnit.HOUR));
        animator.initialize(model, replication);
        DSOLPanel panel = new DSOLPanel(new SCControlPanel(model, animator));
        panel.addTab("logger", new ConsoleLogger(Level.INFO));
        panel.addTab("console", new ConsoleOutput());
        new TestModelApp("TestModelApp", panel, model);
    }

}
