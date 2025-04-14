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
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Bank;
import nl.tudelft.simulation.supplychain.reference.Customer;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.role.consuming.ConsumingRole;
import nl.tudelft.simulation.supplychain.role.consuming.process.DemandGeneratingProcess;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;
import nl.tudelft.simulation.supplychain.role.financing.handler.InvoiceHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.PaymentHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.role.financing.process.FixedCostProcess;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRoleRFQ;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.DemandHandlerRFQ;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteHandlerAll;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteNoHandler;
import nl.tudelft.simulation.supplychain.role.receiving.ReceivingRole;
import nl.tudelft.simulation.supplychain.role.receiving.handler.TransportDeliveryHandlerConsume;
import nl.tudelft.simulation.supplychain.role.shipping.ShippingRole;
import nl.tudelft.simulation.supplychain.role.transporting.TransportPreference;
import nl.tudelft.simulation.supplychain.role.transporting.TransportPreference.CostTimeImportance;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * Customer.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Client extends Customer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the product that Client wants to buy. */
    private Product product;

    /** the fixed retailer where Client buys. */
    private Retailer retailer;

    /**
     * @param id String, the unique id of the supplier
     * @param name the longer name of the supplier
     * @param model the model
     * @param geography the location of the actor
     * @param bank the bank for the BankAccount
     * @param initialBalance the initial balance for the actor
     * @param contentStore the message store for messages
     * @param product product to order
     * @param retailer fixed retailer to use
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     * @throws NamingException on animation error
     * @throws RemoteException on animation error
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Client(final String id, final String name, final SupplyChainModelInterface model, final Geography geography,
            final Bank bank, final Money initialBalance, final ContentStoreInterface contentStore, final Product product,
            final Retailer retailer) throws ActorAlreadyDefinedException, RemoteException, NamingException
    {
        super(id, name, model, geography, contentStore);
        this.product = product;
        this.retailer = retailer;
        setPurchasingRole(new PurchasingRoleRFQ(this));
        setConsumingRole(new ConsumingRole(this, new DistConstantDuration(Duration.ZERO)));
        setFinancingRole(new FinancingRole(this, bank, initialBalance));
        setReceivingRole(new ReceivingRole(this));

        makeHandlers();

        // Let's give Client its corresponding image
        if (getSimulator() instanceof AnimatorInterface)
        {
            new SingleImageRenderable<>(this, getSimulator(),
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Market.gif"));
        }
    }

    /**
     * Set the handlers.
     */
    public void makeHandlers()
    {
        StreamInterface stream = getSimulator().getModel().getStream("default");
        DurationUnit hours = DurationUnit.HOUR;
        DurationUnit days = DurationUnit.DAY;
        //
        // create the demand for PCs
        new DemandGeneratingProcess(getConsumingRole(), this.product,
                new DistContinuousDuration(new DistExponential(stream, 24.0), hours), 1.0, Duration.ZERO,
                new Duration(14.0, days));
        //
        // tell Client to use the DemandHandler
        DemandHandlerRFQ demandHandler = new DemandHandlerRFQ(getPurchasingRole(), new Duration(24.0, hours));
        TransportPreference transportPreference = new TransportPreference(new ArrayList<>(), CostTimeImportance.COST);
        demandHandler.addSupplier(this.product, this.retailer, transportPreference);
        //
        // tell Client to use the QuoteHandler to handle quotes
        new QuoteNoHandler((PurchasingRoleRFQ) getPurchasingRole());
        new QuoteHandlerAll(getPurchasingRole(), QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE, 0.4, 0.1);
        //
        // Client has the standard order confirmation Handler
        new OrderConfirmationHandler(getPurchasingRole());
        //
        // Client will get a bill in the end
        new InvoiceHandler(getFinancingRole(), PaymentPolicyEnum.PAYMENT_IMMEDIATE, new DistConstantDuration(Duration.ZERO));
        new FixedCostProcess(getFinancingRole(), "no fixed costs", new Duration(1, DurationUnit.WEEK),
                new Money(0.0, MoneyUnit.USD));
        //
        // hopefully, Client will get computer shipments
        new TransportDeliveryHandlerConsume(getReceivingRole());
        //
        // useless handlers
        new PaymentHandler(getFinancingRole());
        
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
