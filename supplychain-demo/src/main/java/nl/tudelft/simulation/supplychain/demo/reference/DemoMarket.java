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
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreEmpty;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.reference.Bank;
import nl.tudelft.simulation.supplychain.reference.Customer;
import nl.tudelft.simulation.supplychain.reference.Directory;
import nl.tudelft.simulation.supplychain.role.consuming.ConsumingRole;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;
import nl.tudelft.simulation.supplychain.role.financing.handler.FulfillmentHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.InvoiceHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.role.financing.handler.TransportInvoiceHandler;
import nl.tudelft.simulation.supplychain.role.financing.process.FixedCostProcess;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRoleSearch;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.DemandHandlerSearch;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteHandlerAll;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.QuoteNoHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.handler.SearchAnswerHandler;
import nl.tudelft.simulation.supplychain.role.receiving.ReceivingRole;
import nl.tudelft.simulation.supplychain.role.receiving.handler.TransportDeliveryHandlerConsume;
import nl.tudelft.simulation.supplychain.role.transporting.TransportPreference;
import nl.tudelft.simulation.supplychain.role.transporting.TransportPreference.CostTimeImportance;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * Customer.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemoMarket extends Customer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the fixed Directory actor. */
    private Directory directory;

    /**
     * Make a market for the model. The market does not yet have demand generators -- these have to be added in a model-specific
     * way.
     * @param id String, the unique id of the supplier
     * @param model the model
     * @param geography the location of the actor
     * @param bank the bank for the BankAccount
     * @param initialBalance the initial balance for the actor
     * @param directory fixed directory to use
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     * @throws NamingException on animation error
     * @throws RemoteException on animation error
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public DemoMarket(final String id, final SupplyChainModelInterface model, final Geography geography, final Bank bank,
            final Money initialBalance, final Directory directory)
            throws ActorAlreadyDefinedException, RemoteException, NamingException
    {
        super(id, id, model, geography, new ContentStoreEmpty());
        bank.getBankingRole().addToBalance(this, initialBalance);
        this.directory = directory;
        setPurchasingRole(new PurchasingRoleSearch(this));
        setConsumingRole(new ConsumingRole(this, new DistConstantDuration(Duration.ZERO)));
        setFinancingRole(new FinancingRole(this, bank, initialBalance));
        setReceivingRole(new ReceivingRole(this));

        makeHandlers();

        // Let's give Client its corresponding image
        if (getSimulator() instanceof AnimatorInterface)
        {
            new SingleImageRenderable<>(this, getSimulator(),
                    DemoMarket.class.getResource("/nl/tudelft/simulation/supplychain/images/ActorMarket.gif"));
        }
    }

    /**
     * Set the handlers.
     */
    public void makeHandlers()
    {
        //
        // tell Client to use the DemandHandler
        new DemandHandlerSearch(this, this.directory, new Length(1000.0, LengthUnit.KILOMETER), 100);
        TransportPreference transportPreference = new TransportPreference(new ArrayList<>(), CostTimeImportance.COST);
        new SearchAnswerHandler(this, new Duration(24.0, DurationUnit.HOUR), transportPreference);
        //
        // tell Client to use the QuoteHandler to handle quotes
        new QuoteNoHandler(this);
        new QuoteHandlerAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE, 0.5, 0.0);
        //
        // Client has the standard order confirmation Handler
        new OrderConfirmationHandler(this);
        //
        // Client will get a bill in the end
        new InvoiceHandler(this, PaymentPolicyEnum.PAYMENT_ON_TIME, new DistConstantDuration(Duration.ZERO));
        new TransportInvoiceHandler(this, PaymentPolicyEnum.PAYMENT_ON_TIME, new DistConstantDuration(Duration.ZERO));
        new FixedCostProcess(this, "no fixed costs", new Duration(1, DurationUnit.WEEK), new Money(0.0, MoneyUnit.USD));
        //
        // hopefully, Client will get computer shipments
        new TransportDeliveryHandlerConsume(this);
        new FulfillmentHandler(this);

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
