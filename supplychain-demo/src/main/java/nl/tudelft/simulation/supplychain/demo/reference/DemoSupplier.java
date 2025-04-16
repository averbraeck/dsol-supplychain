package nl.tudelft.simulation.supplychain.demo.reference;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.reference.FaxDevice;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.handler.order.OrderHandler;
import nl.tudelft.simulation.supplychain.handler.order.OrderHandlerStock;
import nl.tudelft.simulation.supplychain.handler.payment.PaymentHandler;
import nl.tudelft.simulation.supplychain.message.store.trade.LeanContentStore;
import nl.tudelft.simulation.supplychain.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.supplychain.money.Bank;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Search;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.role.searching.Topic;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.selling.handler.RequestForQuoteHandler;
import nl.tudelft.simulation.supplychain.role.transporting.TransportMode;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;
import nl.tudelft.simulation.supplychain.role.warehousing.process.RestockingProcessSafety;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * MtsMtomarket.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemoSupplier extends Supplier
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param initialBankAccount
     * @param product
     * @param initialStock
     * @param ypProduction
     * @param stream
     */
    public DemoSupplier(final String name, final SupplyChainSimulatorInterface simulator, final Point2d position,
            final Bank bank, final Money initialBankAccount, final Product product, final double initialStock,
            final Search ypProduction, final StreamInterface stream)
    {
        super(name, simulator, position, bank, initialBankAccount, new LeanContentStore(simulator));

        // COMMUNICATION

        FaxDevice fax = new FaxDevice("fax-" + name, this.simulator);
        super.addSendingDevice(fax);
        ContentReceiver faxChecker = new HandleAllMessages(this);
        super.addReceivingDevice(fax, faxChecker, new DistConstantDuration(new Duration(1.0, DurationUnit.HOUR)));

        // REGISTER IN YP

        ypProduction.register(this, Topic.DEFAULT);

        // STOCK

        Inventory _stock = new Inventory(this);
        _stock.addToInventory(product, initialStock, product.getUnitMarketPrice().multiplyBy(initialStock));
        super.setInitialStock(_stock);

        // SELLING HANDLERS

        RequestForQuoteHandler rfqHandler = new RequestForQuoteHandler(this, super.inventory, 1.2,
                new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);

        OrderHandler orderHandler = new OrderHandlerStock(this, super.inventory);

        PaymentHandler paymentHandler = new PaymentHandler(this, super.bankAccount);

        SellingRole sellingRole = new SellingRole(this, this.simulator, rfqHandler, orderHandler, paymentHandler);
        super.setSellingRole(sellingRole);

        // RESTOCKING

        Iterator<Product> stockIter = super.inventory.iterator();
        while (stockIter.hasNext())
        {
            Product stockProduct = stockIter.next();
            // the restocking handler will generate Demand, handled by the PurchasingRole
            new RestockingProcessSafety(super.inventory, stockProduct, new Duration(24.0, DurationUnit.HOUR), false, initialStock,
                    true, 2.0 * initialStock, new Duration(14.0, DurationUnit.DAY));
        }

        // ANIMATION

        if (simulator instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable<>(this, simulator,
                        DemoSupplier.class.getResource("/nl/tudelft/simulation/supplychain/images/Supplier.gif"));
            }
            catch (RemoteException | NamingException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public Bounds2d getBounds()
    {
        return new Bounds2d(25.0, 25.0);
    }

}
