package nl.tudelft.simulation.supplychain.demo.reference;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.reference.FaxDevice;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.reference.WebApplication;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.handler.demand.DemandHandlerYP;
import nl.tudelft.simulation.supplychain.handler.invoice.InvoiceHandler;
import nl.tudelft.simulation.supplychain.handler.invoice.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.handler.order.OrderHandler;
import nl.tudelft.simulation.supplychain.handler.order.OrderHandlerMake;
import nl.tudelft.simulation.supplychain.handler.order.OrderHandlerStock;
import nl.tudelft.simulation.supplychain.handler.orderconfirmation.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.handler.payment.PaymentHandler;
import nl.tudelft.simulation.supplychain.handler.quote.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.handler.quote.QuoteHandler;
import nl.tudelft.simulation.supplychain.handler.quote.QuoteHandlerAll;
import nl.tudelft.simulation.supplychain.handler.rfq.RequestForQuoteHandler;
import nl.tudelft.simulation.supplychain.handler.search.SearchAnswerHandler;
import nl.tudelft.simulation.supplychain.handler.shipment.ShipmentHandler;
import nl.tudelft.simulation.supplychain.handler.shipment.ShipmentHandlerConsume;
import nl.tudelft.simulation.supplychain.message.store.trade.LeanTradeMessageStore;
import nl.tudelft.simulation.supplychain.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.supplychain.money.Bank;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Manufacturer;
import nl.tudelft.simulation.supplychain.reference.Search;
import nl.tudelft.simulation.supplychain.role.manufacturing.ManufacturingService;
import nl.tudelft.simulation.supplychain.role.manufacturing.ManufacturingServiceDelay;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRoleSearch;
import nl.tudelft.simulation.supplychain.role.searching.Topic;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;
import nl.tudelft.simulation.supplychain.role.warehousing.RestockingServiceSafety;
import nl.tudelft.simulation.supplychain.transport.TransportMode;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * MtsMtomarket.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemoManufacturer extends Manufacturer
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
     * @param ypCustomer
     * @param ypProduction
     * @param stream
     * @param mts true if MTS, false if MTO
     */
    public DemoManufacturer(final String name, final SupplyChainSimulatorInterface simulator, final OrientedPoint3d position,
            final Bank bank, final Money initialBankAccount, final Product product, final double initialStock,
            final Search ypCustomer, final Search ypProduction, final StreamInterface stream, final boolean mts)
    {
        super(name, simulator, position, bank, initialBankAccount, new LeanTradeMessageStore(simulator));

        // COMMUNICATION

        WebApplication www = new WebApplication("Web-" + name, this.simulator);
        super.addSendingDevice(www);
        ContentReceiver webSystem = new HandleAllMessages(this);
        super.addReceivingDevice(www, webSystem, new DistConstantDuration(new Duration(10.0, DurationUnit.SECOND)));

        FaxDevice fax = new FaxDevice("fax-" + name, this.simulator);
        super.addSendingDevice(fax);
        ContentReceiver faxChecker = new HandleAllMessages(this);
        super.addReceivingDevice(fax, faxChecker, new DistConstantDuration(new Duration(1.0, DurationUnit.HOUR)));

        // REGISTER IN YP

        ypProduction.register(this, Topic.DEFAULT);
        ypProduction.addSupplier(product, this);

        // STOCK, ALSO FOR BOM ENTRIES

        Inventory _stock = new Inventory(this);
        _stock.addToInventory(product, initialStock, product.getUnitMarketPrice().multiplyBy(initialStock));
        for (Product p : product.getBillOfMaterials().getMaterials().keySet())
        {
            double amount = initialStock * product.getBillOfMaterials().getMaterials().get(p);
            _stock.addToInventory(p, amount, p.getUnitMarketPrice().multiplyBy(amount));
        }
        super.setInitialStock(_stock);

        // BUYING HANDLERS

        DistContinuousDuration administrativeDelayDemand =
                new DistContinuousDuration(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        DemandHandlerYP demandHandler = new DemandHandlerYP(this, administrativeDelayDemand,
                ypProduction, new Length(1E6, LengthUnit.METER), 1000, super.inventory);

        DistContinuousDuration administrativeDelaySearchAnswer =
                new DistContinuousDuration(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        SearchAnswerHandler searchAnswerHandler = new SearchAnswerHandler(this, administrativeDelaySearchAnswer);

        DistContinuousDuration administrativeDelayQuote =
                new DistContinuousDuration(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        QuoteHandler quoteHandler =
                new QuoteHandlerAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE, administrativeDelayQuote, 0.4, 0);

        OrderConfirmationHandler orderConfirmationHandler = new OrderConfirmationHandler(this);

        ShipmentHandler shipmentHandler = new ShipmentHandlerConsume(this);

        DistContinuousDuration paymentDelay = new DistContinuousDuration(new DistConstant(stream, 0.0), DurationUnit.HOUR);
        InvoiceHandler billHandler = new InvoiceHandler(this, this.getBankAccount(), PaymentPolicyEnum.PAYMENT_ON_TIME, paymentDelay);

        PurchasingRoleSearch purchasingRole = new PurchasingRoleSearch(this, simulator, demandHandler, searchAnswerHandler, quoteHandler,
                orderConfirmationHandler, shipmentHandler, billHandler);
        this.setPurchasingRole(purchasingRole);

        // SELLING HANDLERS

        RequestForQuoteHandler rfqHandler = new RequestForQuoteHandler(this, super.inventory, 1.2,
                new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);

        OrderHandler orderHandler;
        if (mts)
            orderHandler = new OrderHandlerStock(this, super.inventory);
        else
            orderHandler = new OrderHandlerMake(this, super.inventory);

        PaymentHandler paymentHandler = new PaymentHandler(this, super.bankAccount);

        SellingRole sellingRole = new SellingRole(this, this.simulator, rfqHandler, orderHandler, paymentHandler);
        super.setSellingRole(sellingRole);

        // RESTOCKING

        Iterator<Product> stockIter = super.inventory.iterator();
        while (stockIter.hasNext())
        {
            Product stockProduct = stockIter.next();
            // the restocking handler will generate Demand, handled by the PurchasingRole
            new RestockingServiceSafety(super.inventory, stockProduct, new Duration(24.0, DurationUnit.HOUR), false, initialStock,
                    true, 2.0 * initialStock, new Duration(14.0, DurationUnit.DAY));
        }

        // MANUFACTURING

        ManufacturingService productionService = new ManufacturingServiceDelay(this, super.getStock(), product,
                new DistContinuousDuration(new DistUniform(stream, 5.0, 10.0), DurationUnit.DAY), true, true, 0.2);
        getProduction().addProductionService(productionService);

        // ANIMATION

        if (simulator instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable<>(this, simulator,
                        DemoManufacturer.class.getResource("/nl/tudelft/simulation/supplychain/images/Manufacturer.gif"));
            }
            catch (RemoteException | NamingException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public Bounds3d getBounds()
    {
        return new Bounds3d(25.0, 25.0, 1.0);
    }

}
