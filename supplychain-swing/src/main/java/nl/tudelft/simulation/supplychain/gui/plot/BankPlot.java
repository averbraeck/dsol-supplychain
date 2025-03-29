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
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;

/**
 * StockPlot.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BankPlot extends XYChart
{
    /** */
    private static final long serialVersionUID = 20200211L;

    /** */
    private SimPersistent<Duration> balancePersistent;

    /**
     * @param model
     * @param title
     * @param bankAccount
     */
    public BankPlot(final SupplyChainModelInterface model, final String title, final BankAccount bankAccount)
    {
        super(model.getSimulator(), title);
        BalanceListener balanceListener = new BalanceListener(model.getSimulator(), bankAccount);
        try
        {
            this.balancePersistent = new SimPersistent<Duration>("balance " + title, model, balanceListener,
                    BalanceListener.BALANCE_CHANGE_EVENT);
            add("balance", this.balancePersistent, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
        }
        catch (RemoteException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * BalanceListener - delegate class to handle the bank account balance change subscription and event production for the
     * Persistent variables. <br>
     * <br>
     * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
     * The supply chain Java library uses a BSD-3 style license.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    private static class BalanceListener extends LocalEventProducer implements EventListener
    {
        /** */
        private static final long serialVersionUID = 20221201L;

        /** the simulator to get the time for the TimedEvent. */
        private final SupplyChainSimulatorInterface simulator;

        /** An event to indicate stock levels changed */
        static final EventType BALANCE_CHANGE_EVENT = new EventType("BALANCE_CHANGE_EVENT");

        /**
         * @param simulator
         * @param bankAccount
         */
        public BalanceListener(final SupplyChainSimulatorInterface simulator, final BankAccount bankAccount)
        {
            super();
            this.simulator = simulator;
            bankAccount.addListener(this, BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
        }

        /** {@inheritDoc} */
        @Override
        public void notify(final Event event) throws RemoteException
        {
            Money balance = (Money) event.getContent();
            fireEvent(new TimedEvent<Double>(BALANCE_CHANGE_EVENT, balance.getAmount(), this.simulator.getSimulatorTime().si));
        }

    }
}
