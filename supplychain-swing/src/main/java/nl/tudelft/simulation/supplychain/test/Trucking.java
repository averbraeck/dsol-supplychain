package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;
import java.util.LinkedHashMap;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.reference.Bank;
import nl.tudelft.simulation.supplychain.reference.Transporter;
import nl.tudelft.simulation.supplychain.role.directing.DirectingRoleTransporting;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;
import nl.tudelft.simulation.supplychain.role.financing.handler.TransportConfirmationHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.TransportPaymentHandler;
import nl.tudelft.simulation.supplychain.role.financing.process.FixedCostProcess;
import nl.tudelft.simulation.supplychain.role.transporting.TransportMode;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingRole;
import nl.tudelft.simulation.supplychain.role.transporting.handler.TransportOrderHandler;
import nl.tudelft.simulation.supplychain.role.transporting.handler.TransportQuoteRequestHandler;

/**
 * Customer.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Trucking extends Transporter
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * @param id String, the unique id of the supplier
     * @param name the longer name of the supplier
     * @param model the model
     * @param location location on the map
     * @param locationDescription description of the location
     * @param landmass continent or island
     * @param bank the bank of this transporter
     * @param initialBalance the initial bank balance
     * @param contentStore the content store to use
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     * @throws NamingException on animation error
     * @throws RemoteException on animation error
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Trucking(final String id, final String name, final SupplyChainModelInterface model, final Point2d location,
            final String locationDescription, final String landmass, final Bank bank, final Money initialBalance,
            final ContentStoreInterface contentStore) throws ActorAlreadyDefinedException, RemoteException, NamingException
    {
        super(id, name, model, location, locationDescription, landmass);
        setTransportingRole(new TransportingRole(this));
        setFinancingRole(new FinancingRole(this, bank, initialBalance));
        var transportModeProfitMarginMap = new LinkedHashMap<TransportMode, Double>();
        transportModeProfitMarginMap.put(TransportMode.TRUCK, 0.2);
        setDirectingRole(new DirectingRoleTransporting(this, transportModeProfitMarginMap));

        makeHandlers();

        // Let's give Transporter its corresponding image
        if (getSimulator() instanceof AnimatorInterface)
        {
            new SingleImageRenderable<>(this, getSimulator(),
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/ActorTransport.gif"));
        }
    }

    /**
     * Set the handlers.
     */
    public void makeHandlers()
    {
        // Handle the transport messages
        new TransportQuoteRequestHandler(this);
        new TransportOrderHandler(this);
        new TransportConfirmationHandler(this);
        //
        // Transporter will get a payment in the end
        new TransportPaymentHandler(this);
        new FixedCostProcess(this, "no fixed costs", new Duration(1, DurationUnit.WEEK), new Money(0.0, MoneyUnit.USD));

        //
        // CHARTS
        //

        if (getSimulator() instanceof AnimatorInterface)
        {
            XYChart bankChart = new XYChart(getSimulator(), "BankAccount " + getName());
            // TODO: bankChart.add("bank account", getBankAccount(), BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
        }
    }

    @Override
    public Bounds2d getBounds()
    {
        return new Bounds2d(25.0, 25.0);
    }
}
