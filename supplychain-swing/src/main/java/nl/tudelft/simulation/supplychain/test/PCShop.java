package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint2d;

import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.handler.demand.InternalDemandPolicyRFQ;
import nl.tudelft.simulation.supplychain.handler.payment.PaymentPolicy;
import nl.tudelft.simulation.supplychain.handler.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.money.Bank;
import nl.tudelft.simulation.supplychain.money.BankAccount;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicy;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicyStock;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicyAll;
import nl.tudelft.simulation.supplychain.policy.rfq.RequestForQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicyStock;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRoleYP;
import nl.tudelft.simulation.supplychain.role.inventory.Inventory;
import nl.tudelft.simulation.supplychain.role.inventory.RestockingServiceSafety;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingRoleRFQ;
import nl.tudelft.simulation.supplychain.transport.TransportMode;
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
    private Supplier manufacturer;

    /**
     * @param id String, the unique id of the supplier
     * @param name the longer name of the supplier
     * @param model the model
     * @param location the location of the actor
     * @param locationDescription the location description of the actor (e.g., a city, country)
     * @param bank the bank for the BankAccount
     * @param initialBalance the initial balance for the actor
     * @param messageStore the message store for messages
     * @param product initial stock product
     * @param amount amount of initial stock
     * @param manufacturer fixed manufacturer to use
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     * @throws NamingException on animation error
     * @throws RemoteException on animation error
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public PCShop(final String id, final String name, final SupplyChainModelInterface model, final OrientedPoint2d location,
            final String locationDescription, final Bank bank, final Money initialBalance,
            final TradeMessageStoreInterface messageStore, final Product product, final double amount,
            final Supplier manufacturer) throws ActorAlreadyDefinedException, RemoteException, NamingException
    {
        super(id, name, model, location, locationDescription, bank, initialBalance, messageStore);
        this.manufacturer = manufacturer;
        // give the retailer some stock
        getInventory().addToInventory(product, amount, product.getUnitMarketPrice().multiplyBy(amount));
        init();
        if (getSimulator() instanceof AnimatorInterface)
        {
            new SingleImageRenderable<>(this, getSimulator(),
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Retailer.gif"));
        }
    }

    /**
     * @throws RemoteException remote simulator error
     */
    public void init() throws RemoteException
    {
        // tell PCshop to use the RFQPolicy to handle RFQs
        RequestForQuotePolicy rfqPolicy = new RequestForQuotePolicy(this, getInventory(), 1.2,
                new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);
        //
        // create an order Policy
        OrderPolicy orderPolicy = new OrderPolicyStock(this, getInventory());
        //
        // hopefully, the PCShop will get payments in the end
        PaymentPolicy paymentPolicy = new PaymentPolicy(this, getBankAccount());
        //
        // add the Policys to the buying role for PCShop
        SellingRole sellingRole = new SellingRoleRFQ(this, getSimulator(), rfqPolicy, orderPolicy, paymentPolicy);
        super.setSellingRole(sellingRole);
        //
        // After a while, the PC Shop needs to restock and order
        // do this for every product we have initially in stock
        for (Product product : getInventory().getProducts())
        {
            new RestockingServiceSafety(getInventory(), product, new Duration(24.0, DurationUnit.HOUR), false, 5.0, true, 10.0,
                    new Duration(14.0, DurationUnit.DAY));
            // order 100 PCs when actual+claimed < 100
            // policy will schedule itself
        }
        
        //
        // BUY PRODUCTS WHEN THERE IS INTERNAL DEMAND
        //
        
        // tell PCShop to use the InternalDemandPolicy for all products
        InternalDemandPolicyRFQ demandPolicy =
                new InternalDemandPolicyRFQ(this, new Duration(1.0, DurationUnit.HOUR), getInventory());
        for (Product product : getInventory().getProducts())
        {
            demandPolicy.addSupplier(product, this.manufacturer);
        }
        //
        // tell PCShop to use the QuotePolicy to handle quotes
        QuotePolicy quotePolicy = new QuotePolicyAll(this, QuoteComparatorEnum.SORT_DATE_PRICE_DISTANCE,
                new Duration(1.0, DurationUnit.HOUR), 0.4, 0.1);
        //
        // PCShop has the standard order confirmation Policy
        OrderConfirmationPolicy confirmationPolicy = new OrderConfirmationPolicy(this);
        //
        // PCShop will get a bill in the end
        BillPolicy billPolicy = new BillPolicy(this, getBankAccount(), PaymentPolicyEnum.PAYMENT_IMMEDIATE,
                new DistConstantDuration(Duration.ZERO));
        //
        // hopefully, PCShop will get laptop shipments, put them in stock
        ShipmentPolicy shipmentPolicy = new ShipmentPolicyStock(this, getInventory());
        //
        // add the Policys to the buying role for PCShop
        BuyingRoleYP buyingRole = new BuyingRoleYP(this, getSimulator(), demandPolicy, quotePolicy,
                confirmationPolicy, shipmentPolicy, billPolicy);
        super.setBuyingRole(buyingRole);
        //
        // CHARTS
        //
        if (getSimulator() instanceof AnimatorInterface)
        {
            XYChart bankChart = new XYChart(getSimulator(), "BankAccount " + getName());
            bankChart.add("bank account", getBankAccount(), BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
        }
    }

    @Override
    public Bounds3d getBounds()
    {
        return new Bounds3d(25.0, 25.0, 1.0);
    }
    
    public Inventory getInventory()
    {
        return getInventoryRole().getInventory();
    }
}
