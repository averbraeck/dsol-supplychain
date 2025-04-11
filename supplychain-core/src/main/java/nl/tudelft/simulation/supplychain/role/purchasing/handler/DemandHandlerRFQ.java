package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.transporting.TransportPreference;

/**
 * The DemandHandlerRFQ is a simple implementation of the business logic to handle a request for new products through sending
 * out a number of RFQs to a list of preselected suppliers. When receiving the demand, it just creates a number of RFQs based on
 * a table that maps Products onto a list of Actors, and sends them out, all at the same time, after a given time delay.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemandHandlerRFQ extends DemandHandler
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** a table to map the products onto a list of possible suppliers with associated transport. */
    private Map<Product, Set<SupplierAndTransport>> suppliers = new LinkedHashMap<>();

    /** the maximum time after which the RFQ will stop collecting quotes. */
    private final Duration cutoffDuration;

    /**
     * Constructs a new DemandHandlerRFQ.
     * @param owner the owner of the demand
     * @param cutoffDuration the maximum time after which the RFQ will stop collecting quotes
     */
    public DemandHandlerRFQ(final PurchasingRole owner, final Duration cutoffDuration)
    {
        super("DemandHandlerRFQ", owner);
        Throw.whenNull(cutoffDuration, "cutoffDuration cannot be null");
        this.cutoffDuration = cutoffDuration;
    }

    /**
     * Add a supplier to send an RFQ to for a certain product, with a transport preference to get the goods from the supplier to
     * the purchaser.
     * @param product the product with a set of suppliers.
     * @param supplier a supplier for that product.
     * @param transportPreference the transport preference to get the goods from the supplier to the purchaser
     */
    public void addSupplier(final Product product, final SellingActor supplier, final TransportPreference transportPreference)
    {
        var supplierSet = this.suppliers.get(product);
        if (supplierSet == null)
        {
            supplierSet = new LinkedHashSet<SupplierAndTransport>();
            this.suppliers.put(product, supplierSet);
        }
        supplierSet.add(new SupplierAndTransport(supplier, transportPreference));
    }

    /**
     * Remove a supplier to send an RFQ to for a certain product.
     * @param product the product.
     * @param supplier the supplier for that product to be removed.
     */
    public void removeSupplier(final Product product, final SellingActor supplier)
    {
        var supplierSet = this.suppliers.get(product);
        if (supplierSet != null)
        {
            for (var st : supplierSet)
            {
                if (st.supplier().equals(supplier))
                {
                    supplierSet.remove(st);
                }
            }
        }
    }

    @Override
    public boolean handleContent(final Demand demand)
    {
        if (!isValidContent(demand))
        {
            Logger.warn("handleContent", "Demand " + demand.toString() + " for actor " + getRole() + " not considered valid.");
            return false;
        }
        // resolve the suplier
        var supplierSet = this.suppliers.get(demand.product());
        if (supplierSet == null)
        {
            Logger.warn("handleContent", "Demand for actor " + getRole() + " contains product " + demand.product().toString()
                    + " without any suppliers.");
            return false;
        }
        // create an RFQ for each of the suppliers
        Duration delay = getHandlingTime().draw();
        for (var st : supplierSet)
        {
            RequestForQuote rfq = new RequestForQuote(getRole().getActor(), st.supplier, demand, st.transportPreference(),
                    getSimulatorTime().plus(this.cutoffDuration));
            sendContent(rfq, delay);
        }
        return true;
    }

    /**
     * The SupplierAndTransport record contains the combination of a supplier and a transport preference.
     * @param supplier the actor who sells the goods
     * @param transportPreference the preferred transport option to transport the goods from this supplier
     */
    record SupplierAndTransport(SellingActor supplier, TransportPreference transportPreference)
    {
    }
}
