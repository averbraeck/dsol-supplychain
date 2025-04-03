package nl.tudelft.simulation.supplychain.handler.demand;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.content.OrderStandalone;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;
import nl.tudelft.simulation.supplychain.transport.TransportChoiceProvider;
import nl.tudelft.simulation.supplychain.transport.TransportOption;
import nl.tudelft.simulation.supplychain.transport.TransportOptionProvider;

/**
 * The DemandHandlerOrder is a simple implementation of the business logic to handle a request for new products through direct
 * ordering at a known supplier. When receiving the demand, it just creates an Order based on a table that maps Products onto
 * Actors, and sends it after a given time delay.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemandHandlerOrder extends DemandHandler
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
     * Constructs a new DemandHandlerOrder.
     * @param owner the owner of the demand
     * @param transportOptionProvider the provider of transport options betwween two locations
     * @param transportChoiceProvider the provider to choose between transport options
     * @param handlingTime the handling time distribution
     */
    public DemandHandlerOrder(final PurchasingRole owner, final TransportOptionProvider transportOptionProvider,
            final TransportChoiceProvider transportChoiceProvider, final DistContinuousDuration handlingTime)
    {
        super("DemandHandlerOrder", owner, handlingTime);
        Throw.whenNull(transportOptionProvider, "transportOptionProvider cannot be null");
        Throw.whenNull(transportChoiceProvider, "transportChoiceProvider cannot be null");
        this.transportOptionProvider = transportOptionProvider;
        this.transportChoiceProvider = transportChoiceProvider;
    }

    /**
     * @param product the product that has a fixed supplier.
     * @param supplier the supplier for that product.
     * @param unitPrice the price per unit to ask for.
     */
    public void addSupplier(final Product product, final Actor supplier, final Money unitPrice)
    {
        this.suppliers.put(product, new SupplierRecord(supplier, unitPrice));
    }

    @Override
    public boolean handleContent(final Demand demand)
    {
        if (!isValidContent(demand))
        {
            return false;
        }
        // resolve the suplier
        SupplierRecord supplierRecord = this.suppliers.get(demand.getProduct());
        if (supplierRecord == null)
        {
            Logger.warn("checkContent", "Demand for actor " + getRole() + " contains product " + demand.getProduct().toString()
                    + " without a supplier");
            return false;
        }
        SellingActor supplier = supplierRecord.getSupplier();
        Money price = supplierRecord.getUnitPrice().multiplyBy(demand.getAmount());
        Set<TransportOption> transportOptions = this.transportOptionProvider.provideTransportOptions(supplier, getActor());
        TransportOption transportOption =
                this.transportChoiceProvider.chooseTransportOptions(transportOptions, demand.getProduct().getSku());
        var order = new OrderStandalone(getRole().getActor(), supplier, demand, demand.getLatestDeliveryDate(),
                demand.getProduct(), demand.getAmount(), price, transportOption);
        // and send it out after the handling time
        sendContent(order, this.handlingTime.draw());
        return true;
    }

    @Override
    public PurchasingRole getRole()
    {
        return (PurchasingRole) super.getRole();
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
