package nl.tudelft.simulation.supplychain.demo.reference;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreEmpty;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Bank;
import nl.tudelft.simulation.supplychain.reference.Directory;
import nl.tudelft.simulation.supplychain.reference.Manufacturer;
import nl.tudelft.simulation.supplychain.role.directing.DirectingRoleSelling;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;
import nl.tudelft.simulation.supplychain.role.financing.handler.FulfillmentHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.InventoryReleaseHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.InvoiceHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.PaymentHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.role.financing.handler.TransportInvoiceHandler;
import nl.tudelft.simulation.supplychain.role.financing.process.FixedCostProcess;
import nl.tudelft.simulation.supplychain.role.manufacturing.ManufacturingRole;
import nl.tudelft.simulation.supplychain.role.manufacturing.ManufacturingService;
import nl.tudelft.simulation.supplychain.role.manufacturing.ManufacturingServiceDelay;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRoleRFQ;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.DemandHandlerSearch;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteHandlerAll;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteNoHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.SearchAnswerHandler;
import nl.tudelft.simulation.supplychain.role.receiving.ReceivingRole;
import nl.tudelft.simulation.supplychain.role.receiving.handler.TransportDeliveryHandlerStock;
import nl.tudelft.simulation.supplychain.role.searching.Topic;
import nl.tudelft.simulation.supplychain.role.selling.SellingActorRFQ;
import nl.tudelft.simulation.supplychain.role.selling.SellingRoleRFQ;
import nl.tudelft.simulation.supplychain.role.selling.handler.InventoryQuoteHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.InventoryReservationHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.OrderHandlerMake;
import nl.tudelft.simulation.supplychain.role.selling.handler.OrderHandlerStock;
import nl.tudelft.simulation.supplychain.role.selling.handler.RequestForQuoteHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.TransportQuoteHandler;
import nl.tudelft.simulation.supplychain.role.shipping.ShippingRole;
import nl.tudelft.simulation.supplychain.role.shipping.handler.ShippingOrderHandler;
import nl.tudelft.simulation.supplychain.role.transporting.TransportPreference;
import nl.tudelft.simulation.supplychain.role.transporting.TransportPreference.CostTimeImportance;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryEntryHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryQuoteRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryReleaseRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryReservationRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.process.RestockingProcessSafety;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * DemoManufacturer.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemoManufacturer extends Manufacturer implements SellingActorRFQ
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /**
     * Make a retailer for the model. The retailer makes a single product available.
     * @param id String, the unique id of the supplier
     * @param model the model
     * @param geography the location of the actor
     * @param bank the bank for the BankAccount
     * @param initialBalance the initial balance for the actor
     * @param product the product that this supplier makes available
     * @param initialStock the initial stock of the product
     * @param ypCustomer fixed directory to use for the customers
     * @param ypProduction fixed directory to use for the manufacturers
     * @param stream the random stream to use
     * @param mts true if MTS, false if MTO
     * @param transporters the transporting companies that the supplier can use
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public DemoManufacturer(final String id, final SupplyChainModelInterface model, final Geography geography, final Bank bank,
            final Money initialBalance, final Product product, final double initialStock, final Directory ypCustomer,
            final Directory ypProduction, final StreamInterface stream, final boolean mts,
            final TransportingActor[] transporters)
    {
        super(id, id, model, geography, new ContentStoreEmpty());
        bank.getBankingRole().addToBalance(this, initialBalance);

        setPurchasingRole(new PurchasingRoleRFQ(this));
        setFinancingRole(new FinancingRole(this, bank, initialBalance));
        setWarehousingRole(new WarehousingRole(this));
        setShippingRole(new ShippingRole(this));
        setReceivingRole(new ReceivingRole(this));
        var sellingRole = new SellingRoleRFQ(this);
        setSellingRole(sellingRole);
        sellingRole.addTransporters(transporters);
        setDirectingRole(new DirectingRoleSelling(this));
        setManufacturingRole(new ManufacturingRole(this));

        // REGISTER IN YP

        ypProduction.getSearchingRole().register(this, Topic.DEFAULT);
        ypProduction.getSearchingRole().addSupplier(product, this);

        // STOCK, ALSO FOR BOM ENTRIES

        getWarehousingRole().getInventory().addToInventory(product, initialStock,
                product.getUnitMarketPrice().multiplyBy(initialStock));
        for (Product p : product.getBillOfMaterials().getMaterials().keySet())
        {
            double amount = initialStock * product.getBillOfMaterials().getMaterials().get(p);
            getWarehousingRole().getInventory().addToInventory(p, amount, p.getUnitMarketPrice().multiplyBy(amount));
        }

        // BUYING HANDLERS

        // tell Manufacturer to use the DemandHandler for all products
        new DemandHandlerSearch(this, ypProduction, new Length(1000.0, LengthUnit.KILOMETER), 10);
        TransportPreference transportPreference = new TransportPreference(new ArrayList<>(), CostTimeImportance.COST);
        new SearchAnswerHandler(this, new Duration(1.0, DurationUnit.DAY), transportPreference);
        //
        // tell Manufacturer to use the QuoteHandler to handle quotes
        new QuoteNoHandler(this);
        new QuoteHandlerAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE, 0.4, 0.1);
        //
        // Manufacturer has the standard order confirmation Handler
        new OrderConfirmationHandler(this);
        //
        // Manufacturer will get a bill in the end
        new InvoiceHandler(this, PaymentPolicyEnum.PAYMENT_IMMEDIATE, new DistConstantDuration(Duration.ZERO));
        //
        // hopefully, Manufacturer will get computer shipments
        new TransportDeliveryHandlerStock(this);
        new InventoryEntryHandler(this);
        new FulfillmentHandler(this);

        // SELLING HANDLERS

        // tell Retailer to use the RFQHandler to handle RFQs
        new RequestForQuoteHandler(this);
        new InventoryQuoteRequestHandler(this);
        new InventoryQuoteHandler(this);
        new TransportQuoteHandler(this);
        //
        // create an order Handler
        if (mts)
            new OrderHandlerStock(this);
        else
            new OrderHandlerMake(this);
        new InventoryReservationRequestHandler(this);
        new InventoryReservationHandler(this);
        //
        // Release the inventory and ship it
        new InventoryReleaseRequestHandler(this);
        new InventoryReleaseHandler(this);
        new ShippingOrderHandler(this);
        //
        // hopefully, the Manufacturer will get payments in the end
        new TransportInvoiceHandler(this, PaymentPolicyEnum.PAYMENT_IMMEDIATE, new DistConstantDuration(Duration.ZERO));
        new PaymentHandler(this);
        new FixedCostProcess(this, "no fixed costs", new Duration(1, DurationUnit.WEEK), new Money(0.0, MoneyUnit.USD));

        // RESTOCKING

        for (Product stockProduct : getWarehousingRole().getProductsInInventory())
        {
            // the restocking handler will generate Demand, handled by the PurchasingRole
            new RestockingProcessSafety(this, getWarehousingRole().getInventory(), stockProduct,
                    new Duration(24.0, DurationUnit.HOUR), false, initialStock, true, 2.0 * initialStock,
                    new Duration(14.0, DurationUnit.DAY));
        }

        // MANUFACTURING

        ManufacturingService productionService = new ManufacturingServiceDelay(getWarehousingRole(), product,
                new DistContinuousDuration(new DistUniform(stream, 5.0, 10.0), DurationUnit.DAY), true, true, 0.2);
        getManufacturingRole().addProductionService(productionService);

        // ANIMATION

        if (getSimulator() instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable<>(this, getSimulator(),
                        DemoManufacturer.class.getResource("/nl/tudelft/simulation/supplychain/images/ActorManufacturer.gif"));
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
