package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.media.j3d.Bounds;
import javax.naming.NamingException;
import javax.vecmath.Point3d;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.MoneyUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.actor.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.actor.messagehandlers.MessageHandlerInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.charts.xy.XYChart;
import nl.tudelft.simulation.language.d3.BoundingBox;
import nl.tudelft.simulation.messaging.devices.reference.FaxDevice;
import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.handlers.BillHandler;
import nl.tudelft.simulation.supplychain.handlers.InternalDemandHandlerRFQ;
import nl.tudelft.simulation.supplychain.handlers.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.handlers.OrderHandler;
import nl.tudelft.simulation.supplychain.handlers.OrderHandlerStock;
import nl.tudelft.simulation.supplychain.handlers.PaymentHandler;
import nl.tudelft.simulation.supplychain.handlers.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.handlers.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.handlers.QuoteHandler;
import nl.tudelft.simulation.supplychain.handlers.QuoteHandlerAll;
import nl.tudelft.simulation.supplychain.handlers.RequestForQuoteHandler;
import nl.tudelft.simulation.supplychain.handlers.ShipmentHandler;
import nl.tudelft.simulation.supplychain.handlers.ShipmentHandlerStock;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.roles.BuyingRole;
import nl.tudelft.simulation.supplychain.roles.SellingRole;
import nl.tudelft.simulation.supplychain.stock.Stock;
import nl.tudelft.simulation.supplychain.stock.policies.RestockingPolicySafety;
import nl.tudelft.simulation.supplychain.transport.TransportMode;
import nl.tudelft.simulation.unit.dist.DistConstantDurationUnit;

/**
 * Retailer. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class PCShop extends Retailer
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the manufacturer where the PCShop buys */
    private StockKeepingActor manufacturer;

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param product initial stock product
     * @param amount amount of initial stock
     * @param manufacturer fixed manufacturer to use
     * @param contentStore the contentStore to store the messages
     * @throws RemoteException remote simulator error
     * @throws NamingException
     */
    public PCShop(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final Product product, final double amount, final StockKeepingActor manufacturer,
            final ContentStoreInterface contentStore) throws RemoteException, NamingException
    {
        this(name, simulator, position, bank, new Money(0.0, MoneyUnit.USD), product, amount, manufacturer, contentStore);
    }

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     * @param product initial stock product
     * @param amount amount of initial stock
     * @param manufacturer fixed manufacturer to use
     * @param contentStore the contentStore to store the messages
     * @throws RemoteException remote simulator error
     * @throws NamingException
     */
    public PCShop(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final Money initialBankAccount, final Product product, final double amount,
            final StockKeepingActor manufacturer, final ContentStoreInterface contentStore) throws RemoteException, NamingException
    {
        super(name, simulator, position, bank, initialBankAccount, contentStore);
        this.manufacturer = manufacturer;
        // give the retailer some stock
        Stock _stock = new Stock(this);
        if (product != null)
        {
            _stock.addStock(product, amount, product.getUnitMarketPrice().multiplyBy(amount));
            super.setInitialStock(_stock);
        }
        init();
        if (simulator instanceof AnimatorInterface)
        {
            new SingleImageRenderable(this, simulator,
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Retailer.gif"));
        }
    }

    /**
     * @throws RemoteException remote simulator error
     */
    public void init() throws RemoteException
    {
        // give the actor a fax device which is checked every hour
        FaxDevice fax = new FaxDevice("PCShopFAx", this.simulator);
        super.addSendingDevice(fax);
        MessageHandlerInterface secretary = new HandleAllMessages(this);
        super.addReceivingDevice(fax, secretary, new DistConstantDurationUnit(new Duration(1.0, DurationUnit.HOUR)));
        //
        // tell PCshop to use the RFQhandler to handle RFQs
        RequestForQuoteHandler rfqHandler = new RequestForQuoteHandler(this, super.stock, 1.2,
                new DistConstantDurationUnit(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);
        //
        // create an order handler
        OrderHandler orderHandler = new OrderHandlerStock(this, super.stock);
        //
        // hopefully, the PCShop will get payments in the end
        PaymentHandler paymentHandler = new PaymentHandler(this, super.bankAccount);
        //
        // add the handlers to the buying role for PCShop
        SellingRole sellingRole = new SellingRole(this, this.simulator, rfqHandler, orderHandler, paymentHandler);
        super.setSellingRole(sellingRole);
        //
        // After a while, the PC Shop needs to restock and order
        // do this for every product we have initially in stock
        Iterator<Product> stockIter = super.stock.iterator();
        while (stockIter.hasNext())
        {
            Product product = stockIter.next();
            new RestockingPolicySafety(super.stock, product, new Duration(24.0, DurationUnit.HOUR), false, 5.0, true, 10.0,
                    new Duration(14.0, DurationUnit.DAY));
            // order 100 PCs when actual+claimed < 100
            // policy will schedule itself
        }
        //
        // BUY PRODUCTS WHEN THERE IS INTERNAL DEMAND
        //
        // tell PCShop to use the InternalDemandHandler for all products
        InternalDemandHandlerRFQ internalDemandHandler =
                new InternalDemandHandlerRFQ(this, new Duration(1.0, DurationUnit.HOUR), super.stock);
        Iterator<Product> productIter = super.stock.iterator();
        while (productIter.hasNext())
        {
            Product product = productIter.next();
            internalDemandHandler.addSupplier(product, this.manufacturer);
        }
        //
        // tell PCShop to use the Quotehandler to handle quotes
        QuoteHandler quoteHandler = new QuoteHandlerAll(this, QuoteComparatorEnum.SORT_DATE_PRICE_DISTANCE,
                new Duration(1.0, DurationUnit.HOUR), 0.4, 0.1);
        //
        // PCShop has the standard order confirmation handler
        OrderConfirmationHandler confirmationHandler = new OrderConfirmationHandler(this);
        //
        // PCShop will get a bill in the end
        BillHandler billHandler = new BillHandler(this, super.bankAccount, PaymentPolicyEnum.PAYMENT_IMMEDIATE,
                new DistConstantDurationUnit(Duration.ZERO));
        //
        // hopefully, PCShop will get laptop shipments, put them in stock
        ShipmentHandler shipmentHandler = new ShipmentHandlerStock(this, super.stock);
        //
        // add the handlers to the buying role for PCShop
        BuyingRole buyingRole = new BuyingRole(this, this.simulator, internalDemandHandler, quoteHandler, confirmationHandler,
                shipmentHandler, billHandler);
        super.setBuyingRole(buyingRole);
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
