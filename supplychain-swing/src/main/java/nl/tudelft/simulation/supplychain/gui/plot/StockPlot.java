package nl.tudelft.simulation.supplychain.gui.plot;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.TimedEvent;

import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;
import nl.tudelft.simulation.supplychain.role.warehousing.InventoryUpdateData;

/**
 * StockPlot.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class StockPlot extends XYChart
{
    /** */
    private static final long serialVersionUID = 20200211L;

    /** */
    private SimPersistent<Duration> actualPersistent;

    /** */
    private SimPersistent<Duration> claimedPersistent;

    /** */
    private SimPersistent<Duration> orderedPersistent;

    /**
     * @param model
     * @param title
     * @param stock
     * @param product
     */
    public StockPlot(final SupplyChainModelInterface model, final String title, final Inventory stock, final Product product)
    {
        super(model.getSimulator(), title);
        StockListener stockListener = new StockListener(model.getSimulator(), stock, product);
        try
        {
            this.actualPersistent =
                    new SimPersistent<>("actual stock " + title, model, stockListener, StockListener.STOCK_ACTUAL_CHANGE_EVENT);
            this.claimedPersistent = new SimPersistent<>("claimed stock " + title, model, stockListener,
                    StockListener.STOCK_CLAIMED_CHANGE_EVENT);
            this.orderedPersistent = new SimPersistent<>("ordered stock " + title, model, stockListener,
                    StockListener.STOCK_ORDERED_CHANGE_EVENT);
            add("actual stock", this.actualPersistent, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
            add("claimed stock", this.claimedPersistent, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
            add("ordered stock", this.orderedPersistent, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
        }
        catch (RemoteException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * StockListener - delegate class to handle the stock change subscription, filtering for the right product, and event
     * production for the SimPersistent variables.
     * <p>
     * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
     * The supply chain Java library uses a BSD-3 style license.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    private static class StockListener extends LocalEventProducer implements EventListener
    {
        /** */
        private static final long serialVersionUID = 20221201L;

        /** the product to filter the updates for. */
        private final Product product;

        /** the simulator to get the time for the TimedEvent. */
        private final SupplyChainSimulatorInterface simulator;

        /** An event to indicate stock levels changed. */
        static final EventType STOCK_ACTUAL_CHANGE_EVENT = new EventType("STOCK_ACTUAL_CHANGE_EVENT");

        /** An event to indicate stock levels changed. */
        static final EventType STOCK_CLAIMED_CHANGE_EVENT = new EventType("STOCK_CLAIMED_CHANGE_EVENT");

        /** An event to indicate stock levels changed. */
        static final EventType STOCK_ORDERED_CHANGE_EVENT = new EventType("STOCK_ORDERED_CHANGE_EVENT");

        /**
         * @param simulator
         * @param stock
         * @param product
         */
        public StockListener(final SupplyChainSimulatorInterface simulator, final Inventory stock, final Product product)
        {
            super();
            this.product = product;
            this.simulator = simulator;
            stock.addListener(this, Inventory.INVENTORY_CHANGE_EVENT);
        }

        @Override
        public void notify(final Event event) throws RemoteException
        {
            InventoryUpdateData data = (InventoryUpdateData) event.getContent();
            if (!data.productName().equals(this.product.getName()))
                return;
            fireEvent(new TimedEvent<Double>(STOCK_ACTUAL_CHANGE_EVENT, data.actualAmount(),
                    this.simulator.getSimulatorTime().si));
            fireEvent(new TimedEvent<Double>(STOCK_CLAIMED_CHANGE_EVENT, data.reservedAmount(),
                    this.simulator.getSimulatorTime().si));
            fireEvent(new TimedEvent<Double>(STOCK_ORDERED_CHANGE_EVENT, data.orderedAmount(),
                    this.simulator.getSimulatorTime().si));
        }

    }
}
