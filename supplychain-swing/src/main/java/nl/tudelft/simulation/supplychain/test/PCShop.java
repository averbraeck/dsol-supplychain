package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Bank;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.role.directing.DirectingRoleSelling;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;
import nl.tudelft.simulation.supplychain.role.financing.handler.FulfillmentHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.InventoryReleaseHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.InvoiceHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.PaymentHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.role.financing.handler.TransportInvoiceHandler;
import nl.tudelft.simulation.supplychain.role.financing.process.FixedCostProcess;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRoleRFQ;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.DemandHandlerRFQ;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteHandlerAll;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteNoHandler;
import nl.tudelft.simulation.supplychain.role.receiving.ReceivingRole;
import nl.tudelft.simulation.supplychain.role.receiving.handler.TransportDeliveryHandlerStock;
import nl.tudelft.simulation.supplychain.role.selling.SellingRoleRFQ;
import nl.tudelft.simulation.supplychain.role.selling.handler.InventoryQuoteHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.InventoryReservationHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.OrderHandlerStock;
import nl.tudelft.simulation.supplychain.role.selling.handler.RequestForQuoteHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.TransportQuoteHandler;
import nl.tudelft.simulation.supplychain.role.shipping.ShippingRole;
import nl.tudelft.simulation.supplychain.role.shipping.handler.ShippingOrderHandler;
import nl.tudelft.simulation.supplychain.role.transporting.TransportPreference;
import nl.tudelft.simulation.supplychain.role.transporting.TransportPreference.CostTimeImportance;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryEntryHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryQuoteRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryReleaseRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryReservationRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.process.RestockingProcessSafety;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * Retailer.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PCShop extends Retailer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the manufacturer where the PCShop buys. */
    private Supplier supplier;

    /**
     * @param id String, the unique id of the supplier
     * @param name the longer name of the supplier
     * @param model the model
     * @param geography the location of the actor
     * @param bank the bank for the BankAccount
     * @param initialBalance the initial balance for the actor
     * @param contentStore the message store for messages
     * @param product initial stock product
     * @param amount amount of initial stock
     * @param manufacturer fixed manufacturer to use
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     * @throws NamingException on animation error
     * @throws RemoteException on animation error
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public PCShop(final String id, final String name, final SupplyChainModelInterface model, final Geography geography,
            final Bank bank, final Money initialBalance, final ContentStoreInterface contentStore, final Product product,
            final double amount, final Supplier manufacturer)
            throws ActorAlreadyDefinedException, RemoteException, NamingException
    {
        super(id, name, model, geography, contentStore);
        this.supplier = manufacturer;

        setPurchasingRole(new PurchasingRoleRFQ(this));
        setFinancingRole(new FinancingRole(this, bank, initialBalance));
        setWarehousingRole(new WarehousingRole(this));
        setShippingRole(new ShippingRole(this));
        setReceivingRole(new ReceivingRole(this));
        var sellingRole = new SellingRoleRFQ(this);
        setSellingRole(sellingRole);
        sellingRole.addTransporters(((TestModel) model).getTrucking()); // XXX: use argument or later setter.
        setDirectingRole(new DirectingRoleSelling(this));

        // give the retailer some stock
        getInventory().addToInventory(product, amount, product.getUnitMarketPrice().multiplyBy(amount));

        init();

        if (getSimulator() instanceof AnimatorInterface)
        {
            new SingleImageRenderable<>(this, getSimulator(),
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/ActorRetailer.gif"));
        }
    }

    /**
     * @throws RemoteException remote simulator error
     */
    public void init() throws RemoteException
    {
        DurationUnit hours = DurationUnit.HOUR;
        DurationUnit days = DurationUnit.DAY;

        // tell PCshop to use the RFQHandler to handle RFQs
        new RequestForQuoteHandler((SellingRoleRFQ) getSellingRole());
        new InventoryQuoteRequestHandler(getWarehousingRole());
        new InventoryQuoteHandler((SellingRoleRFQ) getSellingRole());
        new TransportQuoteHandler((SellingRoleRFQ) getSellingRole());
        //
        // create an order Handler
        new OrderHandlerStock(getSellingRole());
        new InventoryReservationRequestHandler(getWarehousingRole());
        new InventoryReservationHandler(getSellingRole());
        //
        // Release the inventory and ship it
        new InventoryReleaseRequestHandler(getWarehousingRole());
        new InventoryReleaseHandler(getFinancingRole());
        new ShippingOrderHandler(getShippingRole());
        //
        // hopefully, the PCShop will get payments in the end
        new TransportInvoiceHandler(getFinancingRole(), PaymentPolicyEnum.PAYMENT_IMMEDIATE,
                new DistConstantDuration(Duration.ZERO));
        new PaymentHandler(getFinancingRole());
        new FixedCostProcess(getFinancingRole(), "no fixed costs", new Duration(1, DurationUnit.WEEK),
                new Money(0.0, MoneyUnit.USD));
        //
        // After a while, the PC Shop needs to restock and order
        // do this for every product we have initially in stock
        for (Product product : getInventory().getProducts())
        {
            new RestockingProcessSafety(getWarehousingRole(), getInventory(), product, new Duration(24.0, DurationUnit.HOUR),
                    false, 5.0, true, 10.0, new Duration(14.0, days));
            // order 100 PCs when actual+reserved < 100
        }

        //
        // BUY PRODUCTS WHEN THERE IS INTERNAL DEMAND
        //

        // tell PCShop to use the DemandHandler for all products
        DemandHandlerRFQ demandHandler = new DemandHandlerRFQ(getPurchasingRole(), new Duration(1.0, hours));
        TransportPreference transportPreference = new TransportPreference(new ArrayList<>(), CostTimeImportance.COST);
        for (Product product : getInventory().getProducts())
        {
            demandHandler.addSupplier(product, this.supplier, transportPreference);
        }
        //
        // tell PCShop to use the QuoteHandler to handle quotes
        new QuoteNoHandler((PurchasingRoleRFQ) getPurchasingRole());
        new QuoteHandlerAll(getPurchasingRole(), QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE, 0.4, 0.1);
        //
        // PCShop has the standard order confirmation Handler
        new OrderConfirmationHandler(getPurchasingRole());
        //
        // PCShop will get a bill in the end
        new InvoiceHandler(getFinancingRole(), PaymentPolicyEnum.PAYMENT_IMMEDIATE, new DistConstantDuration(Duration.ZERO));
        //
        // hopefully, PCShop will get computer shipments
        new TransportDeliveryHandlerStock(getReceivingRole());
        new InventoryEntryHandler(getWarehousingRole());
        new FulfillmentHandler(getFinancingRole());

        //
        // CHARTS
        //

        if (getSimulator() instanceof AnimatorInterface)
        {
            XYChart bankChart = new XYChart(getSimulator(), "BankAccount " + getName());
            // TODO bankChart.add("bank account", getBankAccount(), BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
        }
    }

    @Override
    public Bounds2d getBounds()
    {
        return new Bounds2d(25.0, 25.0);
    }

    /**
     * @return the inventory
     */
    public Inventory getInventory()
    {
        return getWarehousingRole().getInventory();
    }
}
