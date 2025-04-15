package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleLogger;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleOutput;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationApplication;
import nl.tudelft.simulation.dsol.swing.gui.control.DevsControlPanel;
import nl.tudelft.simulation.language.DsolException;
import nl.tudelft.simulation.supplychain.actor.ActorNotFoundException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainAnimator;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
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
public class TestModelSwing extends DsolAnimationApplication
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the model. */
    private final TestModel model;

    /**
     * @param title
     * @param panel
     * @param model
     * @throws DsolException
     * @throws IllegalArgumentException
     * @throws RemoteException
     * @throws ActorNotFoundException
     */
    public TestModelSwing(final String title, final DsolPanel panel, final TestModel model)
            throws RemoteException, IllegalArgumentException, DsolException, ActorNotFoundException
    {
        super(panel, title, new Bounds2d(-100, 300, 50, 250));
        this.model = model;
        panel.enableSimulationControlButtons();
        addTabs();
        panel.getTabbedPane().setSelectedIndex(0);
    }

    private void addTabs() throws ActorNotFoundException
    {
        TablePanel charts = new TablePanel(3, 2);
        getDsolPanel().addTab("statistics", charts);
        getDsolPanel().getTabbedPane().setSelectedIndex(1);
        SupplyChainSimulatorInterface devsSimulator = this.model.getSimulator();

        BankPlot fb = new BankPlot(this.model, "Factory Bank balance", this.model.getFactory());
        charts.setCell(fb.getSwingPanel(), 0, 0);

        BankPlot pb = new BankPlot(this.model, "PCShop Bank balance", this.model.getPcShop());
        charts.setCell(pb.getSwingPanel(), 1, 0);

        BankPlot cb = new BankPlot(this.model, "Client Bank balance", this.model.getClient());
        charts.setCell(cb.getSwingPanel(), 2, 0);

        StockPlot fs = new StockPlot(this.model, "Factory stock Laptop",
                this.model.getFactory().getWarehousingRole().getInventory(), this.model.getLaptop());
        charts.setCell(fs.getSwingPanel(), 0, 1);

        StockPlot ps = new StockPlot(this.model, "PCShop stock Laptop",
                this.model.getPcShop().getWarehousingRole().getInventory(), this.model.getLaptop());
        charts.setCell(ps.getSwingPanel(), 1, 1);
    }

    /**
     * @param args not used
     * @throws Exception on dsol error
     */
    public static void main(final String[] args) throws Exception
    {
        CategoryLogger.setAllLogLevel(Level.INFO);
        CategoryLogger.setAllLogMessageFormat("{level} - {class_name}.{method}:{line}  {message}");

        SupplyChainAnimator animator = new SupplyChainAnimator("MTSMTO", Time.ZERO);
        animator.setSpeedFactor(3600.0);
        TestModel model = new TestModel(animator);
        Replication<Duration> replication =
                new SingleReplication<Duration>("rep1", Duration.ZERO, Duration.ZERO, new Duration(1800.0, DurationUnit.HOUR));
        animator.initialize(model, replication);
        DsolPanel panel = new DsolPanel(new DevsControlPanel.TimeDoubleUnit(model, animator));
        panel.addTab("logger", new ConsoleLogger(Level.INFO));
        panel.addTab("console", new ConsoleOutput());
        new TestModelSwing("TestModelApp", panel, model);
    }

}
