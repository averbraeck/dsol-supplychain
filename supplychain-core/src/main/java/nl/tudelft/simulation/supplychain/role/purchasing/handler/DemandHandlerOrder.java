package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;

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

    /**
     * Constructs a new DemandHandlerOrder.
     * @param owner the owner of the handler
     */
    public DemandHandlerOrder(final PurchasingActor owner)
    {
        super("DemandHandlerOrder", owner);
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
        SupplierRecord supplierRecord = this.suppliers.get(demand.product());
        if (supplierRecord == null)
        {
            Logger.warn("checkContent", "Demand for actor " + getRole() + " contains product " + demand.product().toString()
                    + " without a supplier");
            return false;
        }
        SellingActor supplier = (SellingActor) supplierRecord.getSupplier();
        // send out a transportation quote request
        // TODO: CHANGE TQR: TransportQuoteRequest tqr = new TransportQuoteRequest(sender, receiver, rfq, cutoffTime)
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
