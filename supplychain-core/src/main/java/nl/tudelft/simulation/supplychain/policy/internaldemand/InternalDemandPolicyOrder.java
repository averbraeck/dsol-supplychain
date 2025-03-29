package nl.tudelft.simulation.supplychain.policy.internaldemand;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.Order;
import nl.tudelft.simulation.supplychain.message.trade.OrderStandalone;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.transport.TransportChoiceProvider;
import nl.tudelft.simulation.supplychain.transport.TransportOption;
import nl.tudelft.simulation.supplychain.transport.TransportOptionProvider;

/**
 * The InternalDemandPolicyOrder is a simple implementation of the business logic to handle a request for new products through
 * direct ordering at a known supplier. When receiving the internal demand, it just creates an Order based on a table that maps
 * Products onto Actors, and sends it after a given time delay.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InternalDemandPolicyOrder extends InternalDemandPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** a table to map the products onto a unique supplier. */
    private Map<Product, SupplierRecord> suppliers = new LinkedHashMap<Product, SupplierRecord>();

    /** the provider of transport options betwween two locations. */
    private final TransportOptionProvider transportOptionProvider;

    /** the provider to choose between transport options. */
    private final TransportChoiceProvider transportChoiceProvider;

    /**
     * Constructs a new InternalDemandPolicyOrder.
     * @param owner Role; the owner of the internal demand
     * @param transportOptionProvider TransportOptionProvider; the provider of transport options betwween two locations
     * @param transportChoiceProvider TransportChoiceProvider; the provider to choose between transport options
     * @param handlingTime the handling time distribution
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandPolicyOrder(final BuyingRole owner, final TransportOptionProvider transportOptionProvider,
            final TransportChoiceProvider transportChoiceProvider, final DistContinuousDuration handlingTime,
            final Inventory stock)
    {
        super("InternalDemandPolicyOrder", owner, handlingTime, stock);
        Throw.whenNull(transportOptionProvider, "transportOptionProvider cannot be null");
        Throw.whenNull(transportChoiceProvider, "transportChoiceProvider cannot be null");
        this.transportOptionProvider = transportOptionProvider;
        this.transportChoiceProvider = transportChoiceProvider;
    }

    /**
     * @param product Product; the product that has a fixed supplier.
     * @param supplier the supplier for that product.
     * @param unitPrice the price per unit to ask for.
     */
    public void addSupplier(final Product product, final Actor supplier, final Money unitPrice)
    {
        this.suppliers.put(product, new SupplierRecord(supplier, unitPrice));
    }

    @Override
    public boolean handleContent(final InternalDemand internalDemand)
    {
        if (!isValidMessage(internalDemand))
        {
            return false;
        }
        // resolve the suplier
        SupplierRecord supplierRecord = this.suppliers.get(internalDemand.getProduct());
        if (supplierRecord == null)
        {
            Logger.warn("checkContent", "InternalDemand for actor " + getRole() + " contains product "
                    + internalDemand.getProduct().toString() + " without a supplier");
            return false;
        }
        // create an immediate order
        if (super.inventory != null)
        {
            super.inventory.changeOrderedAmount(internalDemand.getProduct(), internalDemand.getAmount());
        }
        SellingActor supplier = supplierRecord.getSupplier();
        Money price = supplierRecord.getUnitPrice().multiplyBy(internalDemand.getAmount());
        Set<TransportOption> transportOptions = this.transportOptionProvider.provideTransportOptions(supplier, getActor());
        TransportOption transportOption =
                this.transportChoiceProvider.chooseTransportOptions(transportOptions, internalDemand.getProduct().getSku());
        Order order = new OrderStandalone(getRole().getActor(), supplier, internalDemand, internalDemand.getLatestDeliveryDate(),
                internalDemand.getProduct(), internalDemand.getAmount(), price, transportOption);
        // and send it out after the handling time
        sendMessage(order, this.handlingTime.draw());
        return true;
    }

    @Override
    public BuyingRole getRole()
    {
        return (BuyingRole) super.getRole();
    }


    /**
     * INNER CLASS FOR STORING RECORDS OF SUPPLIERS AND PRICE
     * <p>
     * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
     * The supply chain Java library uses a BSD-3 style license.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    protected class SupplierRecord
    {
        /** the supplier. */
        private Actor supplier;

        /** the agreed price to pay per unit of product. */
        private Money unitPrice;

        /**
         * Construct a new SupplierRecord.
         * @param supplier the supplier
         * @param unitPrice the price per unit
         */
        public SupplierRecord(final Actor supplier, final Money unitPrice)
        {
            super();
            this.supplier = supplier;
            this.unitPrice = unitPrice;
        }

        /**
         * @return the supplier.
         */
        public Actor getSupplier()
        {
            return this.supplier;
        }

        /**
         * @return the unitPrice.
         */
        public Money getUnitPrice()
        {
            return this.unitPrice;
        }
    }
}
