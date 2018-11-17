package nl.tudelft.simulation.supplychain.gui.plot;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.simtime.SimTimeDoubleUnit;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.Persistent;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventListenerInterface;
import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.event.TimedEvent;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.supplychain.stock.StockUpdateData;

/**
 * StockPlot.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class StockPlot extends XYChart
{
    /** */
    private Persistent<Time, Duration, SimTimeDoubleUnit> actualPersistent;

    /** */
    private Persistent<Time, Duration, SimTimeDoubleUnit> claimedPersistent;

    /** */
    private Persistent<Time, Duration, SimTimeDoubleUnit> orderedPersistent;

    /**
     * @param simulator
     * @param title
     * @param stock
     * @param product
     */
    @SuppressWarnings("static-access")
    public StockPlot(DEVSSimulatorInterface.TimeDoubleUnit simulator, String title, StockInterface stock, Product product)
    {
        super(simulator, title);
        StockListener stockListener = new StockListener(simulator, stock, product);
        try
        {
            this.actualPersistent =
                    new Persistent<>("actual stock", simulator, stockListener, StockListener.STOCK_ACTUAL_CHANGE_EVENT);
            this.claimedPersistent =
                    new Persistent<>("claimed stock", simulator, stockListener, StockListener.STOCK_CLAIMED_CHANGE_EVENT);
            this.orderedPersistent =
                    new Persistent<>("ordered stock", simulator, stockListener, StockListener.STOCK_ORDERED_CHANGE_EVENT);
            add("actual stock", this.actualPersistent, Persistent.VALUE_EVENT);
            add("claimed stock", this.claimedPersistent, Persistent.VALUE_EVENT);
            add("ordered stock", this.orderedPersistent, Persistent.VALUE_EVENT);
        }
        catch (RemoteException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * StockListener - delegate class to handle the stock change subscription, filtering for the right product, and event
     * production for the Persistent variables. <br>
     * <br>
     * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>.
     * The source code and binary code of this software is proprietary information of Delft University of Technology.
     * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
     */
    private static class StockListener extends EventProducer implements EventListenerInterface
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** the product to filter the updates for. */
        private final Product product;

        /** the simulator to get the time for the TimedEvent. */
        private final DEVSSimulatorInterface.TimeDoubleUnit simulator;

        /** An event to indicate stock levels changed */
        static final EventType STOCK_ACTUAL_CHANGE_EVENT = new EventType("STOCK_ACTUAL_CHANGE_EVENT");

        /** An event to indicate stock levels changed */
        static final EventType STOCK_CLAIMED_CHANGE_EVENT = new EventType("STOCK_CLAIMED_CHANGE_EVENT");

        /** An event to indicate stock levels changed */
        static final EventType STOCK_ORDERED_CHANGE_EVENT = new EventType("STOCK_ORDERED_CHANGE_EVENT");

        /**
         * @param simulator
         * @param stock
         * @param product
         */
        public StockListener(DEVSSimulatorInterface.TimeDoubleUnit simulator, StockInterface stock, Product product)
        {
            super();
            this.product = product;
            this.simulator = simulator;
            try
            {
                stock.addListener(this, StockInterface.STOCK_CHANGE_EVENT);
            }
            catch (RemoteException exception)
            {
                exception.printStackTrace();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void notify(EventInterface event) throws RemoteException
        {
            StockUpdateData data = (StockUpdateData) event.getContent();
            if (!data.getProductName().equals(this.product.getName()))
                return;
            fireEvent(new TimedEvent<Double>(STOCK_ACTUAL_CHANGE_EVENT, this, data.getActualAmount(),
                    this.simulator.getSimulatorTime().si));
            fireEvent(new TimedEvent<Double>(STOCK_CLAIMED_CHANGE_EVENT, this, data.getClaimedAmount(),
                    this.simulator.getSimulatorTime().si));
            fireEvent(new TimedEvent<Double>(STOCK_ORDERED_CHANGE_EVENT, this, data.getOrderedAmount(),
                    this.simulator.getSimulatorTime().si));
        }

    }
}
