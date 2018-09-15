package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.unit.dist.DistConstantDurationUnit;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * The InternalDemandHandlerRFQ is a simple implementation of the business logic to handle a request for new products through
 * sending out a number of RFQs to a list of preselected suppliers. When receiving the internal demand, it just creates a number
 * of RFQs based on a table that maps Products onto a list of Actors, and sends them out, all at the same time, after a given
 * time delay. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InternalDemandHandlerRFQ extends InternalDemandHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** a table to map the products onto a list of possible suppliers */
    private Map<Product, HashSet<SupplyChainActor>> suppliers = new HashMap<Product, HashSet<SupplyChainActor>>();

    /**
     * Constructs a new InternalDemandHandlerRFQ
     * @param owner the owner of the internal demand
     * @param handlingTime the handling time distribution delay to use
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandHandlerRFQ(final SupplyChainActor owner, final DistContinuousDurationUnit handlingTime,
            final StockInterface stock)
    {
        super(owner, handlingTime, stock);
    }

    /**
     * Constructs a new InternalDemandHandlerRFQ
     * @param owner the owner of the internal demand
     * @param handlingTime the constant handling time delay to use
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandHandlerRFQ(final SupplyChainActor owner, final Duration handlingTime, final StockInterface stock)
    {
        this(owner, new DistConstantDurationUnit(handlingTime), stock);
    }

    /**
     * Add a supplier to send an RFQ to for a certain product
     * @param product the product with a set of suppliers.
     * @param supplier a supplier for that product.
     */
    public void addSupplier(final Product product, final SupplyChainActor supplier)
    {
        HashSet<SupplyChainActor> supplierSet = this.suppliers.get(product);
        if (supplierSet == null)
        {
            supplierSet = new HashSet<SupplyChainActor>();
            this.suppliers.put(product, supplierSet);
        }
        supplierSet.add(supplier);
    }

    /**
     * Remove a supplier to send an RFQ to for a certain product
     * @param product the product.
     * @param supplier the supplier for that product to be removed.
     */
    public void removeSupplier(final Product product, final SupplyChainActor supplier)
    {
        HashSet<SupplyChainActor> supplierSet = this.suppliers.get(product);
        if (supplierSet != null)
        {
            supplierSet.remove(supplier);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (!isValidContent(content))
        {
            Logger.warn("handleContent",
                    "InternalDemand " + content.toString() + " for actor " + getOwner() + " not considered valid.");
            return false;
        }
        InternalDemand internalDemand = (InternalDemand) content;
        // resolve the suplier
        HashSet<SupplyChainActor> supplierSet = this.suppliers.get(internalDemand.getProduct());
        if (supplierSet == null)
        {
            Logger.warn("handleContent", "InternalDemand for actor " + getOwner() + " contains product "
                    + internalDemand.getProduct().toString() + " without any suppliers.");
            return false;
        }
        // create an RFQ for each of the suppliers
        if (super.stock != null)
        {
            super.stock.changeOrderedAmount(internalDemand.getProduct(), internalDemand.getAmount());
        }
        Duration delay = this.handlingTime.draw();
        Iterator<SupplyChainActor> supplierIterator = supplierSet.iterator();
        while (supplierIterator.hasNext())
        {
            SupplyChainActor supplier = supplierIterator.next();
            RequestForQuote rfq = new RequestForQuote(getOwner(), supplier, internalDemand, internalDemand.getProduct(),
                    internalDemand.getAmount(), internalDemand.getEarliestDeliveryDate(),
                    internalDemand.getLatestDeliveryDate());
            // and send it out after the handling time (same for each)
            getOwner().sendContent(rfq, delay);
        }
        return true;
    }
}
