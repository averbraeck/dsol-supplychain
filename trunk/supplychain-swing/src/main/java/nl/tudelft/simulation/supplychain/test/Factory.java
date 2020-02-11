package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;

import javax.media.j3d.Bounds;
import javax.naming.NamingException;
import javax.vecmath.Point3d;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.actor.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.actor.messagehandlers.MessageHandlerInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.language.d3.BoundingBox;
import nl.tudelft.simulation.messaging.devices.reference.FaxDevice;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.handlers.OrderHandler;
import nl.tudelft.simulation.supplychain.handlers.OrderHandlerStock;
import nl.tudelft.simulation.supplychain.handlers.PaymentHandler;
import nl.tudelft.simulation.supplychain.handlers.RequestForQuoteHandler;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.roles.SellingRole;
import nl.tudelft.simulation.supplychain.stock.Stock;
import nl.tudelft.simulation.supplychain.transport.TransportMode;
import nl.tudelft.simulation.unit.dist.DistConstantDurationUnit;

/**
 * The ComputerShop named Factory. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Factory extends Supplier
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param product initial stock product
     * @param amount amount of initial stock
     * @param contentStore the contentStore to store the messages
     * @throws RemoteException remote simulator error
     * @throws NamingException
     */
    public Factory(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final Product product, final double amount, final ContentStoreInterface contentStore)
            throws RemoteException, NamingException
    {
        this(name, simulator, position, bank, new Money(0.0, MoneyUnit.USD), product, amount, contentStore);
    }

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     * @param product initial stock product
     * @param amount amount of initial stock
     * @param contentStore the contentStore to store the messages
     * @throws RemoteException remote simulator error
     * @throws NamingException
     */
    public Factory(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final Money initialBankAccount, final Product product, final double amount,
            final ContentStoreInterface contentStore) throws RemoteException, NamingException
    {
        super(name, simulator, position, bank, initialBankAccount, contentStore);
        // give the retailer some stock
        Stock _stock = new Stock(this);
        if (product != null)
        {
            _stock.addStock(product, amount, product.getUnitMarketPrice().multiplyBy(amount));
            super.setInitialStock(_stock);
        }
        // We initialize Factory
        this.init();
        // Let's give Factory its corresponding image
        if (simulator instanceof AnimatorInterface)
        {
            new SingleImageRenderable(this, simulator,
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Manufacturer.gif"));
        }
    }

    /**
     * @throws RemoteException simulator error
     */
    public void init() throws RemoteException
    {
        // give the actor a fax device which is checked every hour
        FaxDevice fax = new FaxDevice("FactoryFax", this.simulator);
        super.addSendingDevice(fax);
        MessageHandlerInterface secretary = new HandleAllMessages(this);
        super.addReceivingDevice(fax, secretary, new DistConstantDurationUnit(new Duration(1.0, DurationUnit.HOUR)));
        //
        // tell Factory to use the RFQhandler to handle RFQs
        RequestForQuoteHandler rfqHandler = new RequestForQuoteHandler(this, super.stock, 1.2,
                new DistConstantDurationUnit(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);
        //
        // create an order handler
        OrderHandler orderHandler = new OrderHandlerStock(this, super.stock);
        //
        // hopefully, Factory will get payments in the end
        PaymentHandler paymentHandler = new PaymentHandler(this, super.bankAccount);
        //
        // add the handlers to the SellingRole
        SellingRole sellingRole = new SellingRole(this, this.simulator, rfqHandler, orderHandler, paymentHandler);
        super.setSellingRole(sellingRole);
        //
        // CHARTS
        //
        if (this.simulator instanceof AnimatorInterface)
        {
            XYChart bankChart = new XYChart("BankAccount " + this.name);
            bankChart.add("bank account", this.bankAccount, BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Bounds getBounds()
    {
        return new BoundingBox(25.0, 25.0, 1.0);
    }
}
