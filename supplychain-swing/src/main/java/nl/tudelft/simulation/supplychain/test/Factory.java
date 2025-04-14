package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Bank;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;
import nl.tudelft.simulation.supplychain.role.financing.handler.InventoryReleaseHandler;
import nl.tudelft.simulation.supplychain.role.financing.handler.PaymentHandler;
import nl.tudelft.simulation.supplychain.role.receiving.ReceivingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingRoleRFQ;
import nl.tudelft.simulation.supplychain.role.selling.handler.InventoryQuoteHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.InventoryReservationHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.OrderHandlerStock;
import nl.tudelft.simulation.supplychain.role.selling.handler.RequestForQuoteHandler;
import nl.tudelft.simulation.supplychain.role.selling.handler.TransportQuoteHandler;
import nl.tudelft.simulation.supplychain.role.shipping.ShippingRole;
import nl.tudelft.simulation.supplychain.role.shipping.handler.ShippingOrderHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryQuoteRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryReleaseRequestHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.handler.InventoryReservationRequestHandler;

/**
 * The ComputerShop named Factory.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Factory extends Supplier
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

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
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     * @throws NamingException on animation error
     * @throws RemoteException on animation error
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Factory(final String id, final String name, final SupplyChainModelInterface model, final Geography geography,
            final Bank bank, final Money initialBalance, final ContentStoreInterface contentStore, final Product product,
            final double amount) throws ActorAlreadyDefinedException, RemoteException, NamingException
    {
        super(id, name, model, geography, contentStore);

        setFinancingRole(new FinancingRole(this, bank, initialBalance));
        setWarehousingRole(new WarehousingRole(this));
        setShippingRole(new ShippingRole(this));
        setReceivingRole(new ReceivingRole(this));
        setSellingRole(new SellingRoleRFQ(this));

        // give the factory some stock
        getInventory().addToInventory(product, amount, product.getUnitMarketPrice().multiplyBy(amount));
        // We initialize Factory
        this.init();
        // Let's give Factory its corresponding image
        if (getSimulator() instanceof AnimatorInterface)
        {
            new SingleImageRenderable<>(this, getSimulator(),
                    Factory.class.getResource("/nl/tudelft/simulation/supplychain/images/Manufacturer.gif"));
        }
    }

    /**
     * @throws RemoteException simulator error
     */
    public void init() throws RemoteException
    {
        // tell Factory to use the RFQHandler to handle RFQs
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
        // hopefully, the Factory will get payments in the end
        new PaymentHandler(getFinancingRole());

        //
        // CHARTS
        //
        if (getSimulator() instanceof AnimatorInterface)
        {
            XYChart bankChart = new XYChart(getSimulator(), "BankAccount " + getName());
            // TODO bankChart.add("bank account", getBankAccount(), BankAccount.BANK_ACCOUNT_CHANGED_EVENT);
        }
    }

    /**
     * @return inventory
     */
    public Inventory getInventory()
    {
        return getWarehousingRole().getInventory();
    }

    @Override
    public Bounds2d getBounds()
    {
        return new Bounds2d(25.0, 25.0);
    }
}
