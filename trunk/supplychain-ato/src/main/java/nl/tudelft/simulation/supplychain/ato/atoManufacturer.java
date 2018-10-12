package nl.tudelft.simulation.supplychain.ato;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.media.j3d.Bounds;
import javax.naming.NamingException;
import javax.vecmath.Point3d;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.actor.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.actor.messagehandlers.MessageHandlerInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface.TimeDoubleUnit;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.BoundingBox;
import nl.tudelft.simulation.messaging.devices.reference.FaxDevice;
import nl.tudelft.simulation.messaging.devices.reference.WebApplication;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.memory.LeanContentStore;

import nl.tudelft.simulation.supplychain.handlers.BillHandler;
import nl.tudelft.simulation.supplychain.handlers.InternalDemandHandlerYP;
import nl.tudelft.simulation.supplychain.handlers.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.handlers.OrderHandler;
import nl.tudelft.simulation.supplychain.handlers.OrderHandlerMake;
import nl.tudelft.simulation.supplychain.handlers.OrderHandlerStock;
import nl.tudelft.simulation.supplychain.handlers.PaymentHandler;
import nl.tudelft.simulation.supplychain.handlers.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.handlers.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.handlers.QuoteHandler;
import nl.tudelft.simulation.supplychain.handlers.QuoteHandlerAll;
import nl.tudelft.simulation.supplychain.handlers.RequestForQuoteHandler;
import nl.tudelft.simulation.supplychain.handlers.ShipmentHandler;
import nl.tudelft.simulation.supplychain.handlers.ShipmentHandlerConsume;
import nl.tudelft.simulation.supplychain.handlers.YellowPageAnswerHandler;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.production.DelayProductionService;
import nl.tudelft.simulation.supplychain.production.ProductionService;
import nl.tudelft.simulation.supplychain.reference.Manufacturer;
import nl.tudelft.simulation.supplychain.reference.YellowPage;
import nl.tudelft.simulation.supplychain.roles.BuyingRole;
import nl.tudelft.simulation.supplychain.roles.SellingRole;
import nl.tudelft.simulation.supplychain.stock.Stock;
import nl.tudelft.simulation.supplychain.stock.policies.RestockingPolicySafety;
import nl.tudelft.simulation.supplychain.transport.TransportMode;
import nl.tudelft.simulation.unit.dist.DistConstantDurationUnit;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;
import nl.tudelft.simulation.yellowpage.Category;

/**
 * <p>
 * Copyright (c) 2016 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * </p>
 * $LastChangedDate: 2015-07-24 02:58:59 +0200 (Fri, 24 Jul 2015) $, @version $Revision: 1147 $, by $Author: averbraeck $,
 * initial version Oct 12, 2018 <br>
 * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a> 
 * @author <a href="http://https://www.tudelft.nl/tbm/over-de-faculteit/afdelingen/multi-actor-systems/people/phd-candidates/b-bahareh-zohoori/">Bahareh Zohoori</a> 
 */
public class atoManufacturer extends Manufacturer
{
    /** */
    private static final long serialVersionUID = 1L;

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
    public atoManufacturer(String name, TimeDoubleUnit simulator, Point3d position, Bank bank, Money initialBankAccount,
            Product product, double initialStock, YellowPage ypCustomer, YellowPage ypProduction, StreamInterface stream,
            boolean mts)
    {
        super(name, simulator, position, bank, initialBankAccount, new LeanContentStore(simulator));

        // COMMUNICATION

        WebApplication www = new WebApplication("Web-" + name, this.simulator);
        super.addSendingDevice(www);
        MessageHandlerInterface webSystem = new HandleAllMessages(this);
        super.addReceivingDevice(www, webSystem, new DistConstantDurationUnit(new Duration(10.0, DurationUnit.SECOND)));

        FaxDevice fax = new FaxDevice("fax-" + name, this.simulator);
        super.addSendingDevice(fax);
        MessageHandlerInterface faxChecker = new HandleAllMessages(this);
        super.addReceivingDevice(fax, faxChecker, new DistConstantDurationUnit(new Duration(1.0, DurationUnit.HOUR)));

        // REGISTER IN YP

        ypProduction.register(this, Category.DEFAULT);
        ypProduction.addSupplier(product, this);

        // STOCK, ALSO FOR BOM ENTRIES

        Stock _stock = new Stock(this);
        _stock.addStock(product, initialStock, product.getUnitMarketPrice().multiplyBy(initialStock));
        for (Product p : product.getBillOfMaterials().getMaterials().keySet())
        {
            double amount = initialStock * product.getBillOfMaterials().getMaterials().get(p);
            _stock.addStock(p, amount, p.getUnitMarketPrice().multiplyBy(amount));
        }
        super.setInitialStock(_stock);

        // BUYING HANDLERS

        DistContinuousDurationUnit administrativeDelayInternalDemand =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        InternalDemandHandlerYP internalDemandHandler = new InternalDemandHandlerYP(this, administrativeDelayInternalDemand,
                ypProduction, new Length(1E6, LengthUnit.METER), 1000, super.stock);

        DistContinuousDurationUnit administrativeDelayYellowPageAnswer =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        YellowPageAnswerHandler ypAnswerHandler = new YellowPageAnswerHandler(this, administrativeDelayYellowPageAnswer);

        DistContinuousDurationUnit administrativeDelayQuote =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        QuoteHandler quoteHandler =
                new QuoteHandlerAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE, administrativeDelayQuote, 0.4, 0);

        OrderConfirmationHandler orderConfirmationHandler = new OrderConfirmationHandler(this);

        ShipmentHandler shipmentHandler = new ShipmentHandlerConsume(this);

        DistContinuousDurationUnit paymentDelay =
                new DistContinuousDurationUnit(new DistConstant(stream, 0.0), DurationUnit.HOUR);
        BillHandler billHandler = new BillHandler(this, this.getBankAccount(), PaymentPolicyEnum.PAYMENT_ON_TIME, paymentDelay);

        BuyingRole buyingRole = new BuyingRole(this, simulator, internalDemandHandler, ypAnswerHandler, quoteHandler,
                orderConfirmationHandler, shipmentHandler, billHandler);
        this.setBuyingRole(buyingRole);

        // SELLING HANDLERS

        RequestForQuoteHandler rfqHandler = new RequestForQuoteHandler(this, super.stock, 1.2,
                new DistConstantDurationUnit(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);

        OrderHandler orderHandler;
        if (mts)
            orderHandler = new OrderHandlerStock(this, super.stock);
        else
            orderHandler = new OrderHandlerMake(this, super.stock);

        PaymentHandler paymentHandler = new PaymentHandler(this, super.bankAccount);

        SellingRole sellingRole = new SellingRole(this, this.simulator, rfqHandler, orderHandler, paymentHandler);
        super.setSellingRole(sellingRole);

        // RESTOCKING

        Iterator<Product> stockIter = super.stock.iterator();
        while (stockIter.hasNext())
        {
            Product stockProduct = stockIter.next();
            // the restocking policy will generate InternalDemand, handled by the BuyingRole
            new RestockingPolicySafety(super.stock, stockProduct, new Duration(24.0, DurationUnit.HOUR), false, initialStock,
                    true, 2.0 * initialStock, new Duration(14.0, DurationUnit.DAY));
        }

        // MANUFACTURING

        ProductionService productionService = new DelayProductionService(this, super.getStock(), product,
                new DistContinuousDurationUnit(new DistUniform(stream, 5.0, 10.0), DurationUnit.DAY), true, true, 0.2);
        getProduction().addProductionService(productionService);

        // ANIMATION

        if (simulator instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable(this, simulator,
                        atoManufacturer.class.getResource("/nl/tudelft/simulation/supplychain/images/Manufacturer.gif"));
            }
            catch (RemoteException | NamingException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Bounds getBounds()
    {
        return new BoundingBox(25.0, 25.0, 1.0);
    }


}

