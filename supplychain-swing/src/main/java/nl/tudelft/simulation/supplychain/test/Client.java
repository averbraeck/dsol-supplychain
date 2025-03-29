package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyRFQ;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicyAll;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicyConsume;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Customer;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRoleYP;
import nl.tudelft.simulation.supplychain.role.demand.Demand;
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
     * @param name String; the longer name of the supplier
     * @param model SupplyChainModelInterface; the model
     * @param location OrientedPoint2d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     * @param bank Bank; the bank for the BankAccount
     * @param initialBalance Money; the initial balance for the actor
     * @param messageStore TradeMessageStoreInterface; the message store for messages
     * @param product product to order
     * @param retailer fixed retailer to use
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     * @throws NamingException on animation error
     * @throws RemoteException on animation error
    */
    @SuppressWarnings("checkstyle:parameternumber")
    public Client(final String id, final String name, final SupplyChainModelInterface model, final OrientedPoint2d location,
            final String locationDescription, final Bank bank, final Money initialBalance,
            final TradeMessageStoreInterface messageStore, final Product product, final Retailer retailer)
            throws ActorAlreadyDefinedException, RemoteException, NamingException
    {
        super(id, name, model, location, locationDescription, bank, initialBalance, messageStore);
        this.product = product;
        this.retailer = retailer;
        this.init();
        // Let's give Client its corresponding image
        if (getSimulator() instanceof AnimatorInterface)
        {
            new SingleImageRenderable<>(this, getSimulator(),
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Market.gif"));
        }
    }

    /**
     * @throws RemoteException remote simulator error
     */
    public void init() throws RemoteException
    {
        StreamInterface stream = getSimulator().getModel().getStream("default");
        Duration hour = new Duration(1.0, DurationUnit.HOUR);
        //
        // create the internal demand for PCs
        Demand demand = new Demand(this.product,
                new DistContinuousDuration(new DistExponential(stream, 24.0), DurationUnit.HOUR), new DistConstant(stream, 1.0),
                new DistConstantDuration(Duration.ZERO), new DistConstantDuration(new Duration(14.0, DurationUnit.DAY)));
        DemandGenerationRolePeriodic dg = new DemandGenerationRolePeriodic(this,
                new DistContinuousDuration(new DistExponential(stream, 2.0), DurationUnit.MINUTE));
        dg.addDemandGenerator(this.product, demand);
        super.setDemandGeneration(dg);
        //
        // tell Client to use the InternalDemandPolicy
        InternalDemandPolicyRFQ internalDemandPolicy =
                new InternalDemandPolicyRFQ(this, new Duration(24.0, DurationUnit.HOUR), null); // XXX: Why does it need stock?
        internalDemandPolicy.addSupplier(this.product, this.retailer);
        //
        // tell Client to use the QuotePolicy to handle quotes
        QuotePolicy quotePolicy = new QuotePolicyAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE,
                new DistConstantDuration(new Duration(2.0, DurationUnit.HOUR)), 0.4, 0.1);
        //
        // Client has the standard order confirmation Policy
        OrderConfirmationPolicy confirmationPolicy = new OrderConfirmationPolicy(this);
        //
        // Client will get a bill in the end
        BillPolicy billPolicy = new BillPolicy(this, getBankAccount(), PaymentPolicyEnum.PAYMENT_IMMEDIATE,
                new DistConstantDuration(Duration.ZERO));
        //
        // hopefully, Client will get laptop shipments
        ShipmentPolicy shipmentPolicy = new ShipmentPolicyConsume(this);
        //
        // add the Policys to the buying role for Client
        BuyingRoleYP buyingRole = new BuyingRoleYP(this, super.simulator, internalDemandPolicy, quotePolicy,
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

    /** {@inheritDoc} */
    @Override
    public Bounds3d getBounds()
    {
        return new Bounds3d(25.0, 25.0, 1.0);
    }
}
