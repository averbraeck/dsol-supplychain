package nl.tudelft.simulation.supplychain.test;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.gui.swing.DSOLPanel;
import nl.tudelft.simulation.dsol.gui.swing.TablePanel;
import nl.tudelft.simulation.dsol.simtime.SimTimeDoubleUnit;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.supplychain.gui.plot.BankPlot;
import nl.tudelft.simulation.supplychain.gui.plot.StockPlot;

/**
 * TestModelPanel.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class TestModelPanel extends DSOLPanel<Time, Duration, SimTimeDoubleUnit>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param model
     * @param simulator
     */
    public TestModelPanel(TestModel model, DEVSSimulatorInterface.TimeDoubleUnit simulator)
    {
        super(model, simulator);
        addTabs(model);
    }

    /**
     * add a number of charts for the demo.
     * @param model the model from which to take the statistics
     */
    protected final void addTabs(final TestModel model)
    {
        TablePanel charts = new TablePanel(3, 2);
        super.tabbedPane.addTab("statistics", charts);
        super.tabbedPane.setSelectedIndex(1);

        DEVSSimulatorInterface.TimeDoubleUnit devsSimulator = (DEVSSimulatorInterface.TimeDoubleUnit) this.simulator;

        BankPlot fb = new BankPlot(devsSimulator, "Factory Bank balance", model.factory.getBankAccount());
        charts.setCell(fb.getSwingPanel(), 0, 0);
        
        BankPlot pb = new BankPlot(devsSimulator, "PCShop Bank balance", model.pcShop.getBankAccount());
        charts.setCell(pb.getSwingPanel(), 1, 0);
        
        BankPlot cb = new BankPlot(devsSimulator, "Client Bank balance", model.client.getBankAccount());
        charts.setCell(cb.getSwingPanel(), 2, 0);
        
        StockPlot fs = new StockPlot(devsSimulator, "Factory stock Laptop", model.factory.getStock(), model.laptop);
        charts.setCell(fs.getSwingPanel(), 0, 1);

        StockPlot ps = new StockPlot(devsSimulator, "PCShop stock Laptop", model.pcShop.getStock(), model.laptop);
        charts.setCell(ps.getSwingPanel(), 1, 1);
    }

}
