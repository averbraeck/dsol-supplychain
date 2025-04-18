package nl.tudelft.simulation.supplychain.demo.reference;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreEmpty;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Bank;
import nl.tudelft.simulation.supplychain.reference.Directory;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.role.directing.DirectingRoleSelling;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;
import nl.tudelft.simulation.supplychain.role.financing.handler.InventoryReleaseHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.PaymentHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.role.financing.handler.TransportInvoiceHandler;
import nl.tudelft.simulation.supplychain.role.financing.process.FixedCostProcess;
import nl.tudelft.simulation.supplychain.role.receiving.ReceivingRole;
import nl.tudelft.simulation.supplychain.role.searching.Topic;
import nl.tudelft.simulation.supplychain.role.selling.SellingActorRFQ;
import nl.tudelft.simulation.supplychain.role.selling.SellingRoleRFQ;
import nl.tudelft.simulation.supplychain.role.selling.handler.InventoryQuoteHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.InventoryReservationHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.OrderHandlerStock;
import nl.tudelft.simulation.supplychain.role.selling.handler.RequestForQuoteHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.TransportQuoteHandler;
import nl.tudelft.simulation.supplychain.role.shipping.ShippingRole;
import nl.tudelft.simulation.supplychain.role.shipping.handler.ShippingOrderHandler;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryQuoteRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryReleaseRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryReservationRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.process.RestockingProcessSafety;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * DemoSupplier.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemoSupplier extends Supplier implements SellingActorRFQ
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /**
     * Make a supplier for the model. The supplier makes a single product available.
     * @param id String, the unique id of the supplier
     * @param model the model
     * @param geography the location of the actor
     * @param bank the bank for the BankAccount
     * @param initialBalance the initial balance for the actor
     * @param product the product that this supplier makes available
     * @param initialStock the initial stock of the product
     * @param ypProduction fixed directory to use
     * @param stream the random stream to use
     * @param transporters the transporting companies that the supplier can use
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public DemoSupplier(final String id, final SupplyChainModelInterface model, final Geography geography, final Bank bank,
            final Money initialBalance, final Product product, final double initialStock, final Directory ypProduction,
            final StreamInterface stream, final TransportingActor[] transporters)
    {
        super(id, id, model, geography, new ContentStoreEmpty());
        bank.getBankingRole().addToBalance(this, initialBalance);

        // GIVE ROLES

        setFinancingRole(new FinancingRole(this, bank, initialBalance));
        setWarehousingRole(new WarehousingRole(this));
        setShippingRole(new ShippingRole(this));
        setReceivingRole(new ReceivingRole(this));
        var sellingRole = new SellingRoleRFQ(this);
        setSellingRole(sellingRole);
        sellingRole.addTransporters(transporters);
        setDirectingRole(new DirectingRoleSelling(this));

        // REGISTER IN YP

        ypProduction.getSearchingRole().register(this, Topic.DEFAULT);

        // STOCK

        getWarehousingRole().getInventory().addToInventory(product, initialStock,
                product.getUnitMarketPrice().multiplyBy(initialStock));

        // SELLING HANDLERS

        // tell supplier to use the RFQHandler to handle RFQs
        new RequestForQuoteHandler(this);
        new InventoryQuoteRequestHandler(this);
        new InventoryQuoteHandler(this);
        new TransportQuoteHandler(this);
        //
        // create an order Handler
        new OrderHandlerStock(this);
        new InventoryReservationRequestHandler(this);
        new InventoryReservationHandler(this);
        //
        // Release the inventory and ship it
        new InventoryReleaseRequestHandler(this);
        new InventoryReleaseHandler(this);
        new ShippingOrderHandler(this);
        //
        // hopefully, the supplier will get payments in the end
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

        // ANIMATION

        if (getSimulator() instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable<>(this, getSimulator(),
                        DemoSupplier.class.getResource("/nl/tudelft/simulation/supplychain/images/ActorSupplier.gif"));
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
