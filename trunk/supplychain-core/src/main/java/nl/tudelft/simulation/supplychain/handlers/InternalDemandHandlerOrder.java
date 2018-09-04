package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.OrderStandAlone;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * The InternalDemandHandlerOrder is a simple implementation of the business logic to handle a request for new products through
 * direct ordering at a known supplier. When receiving the internal demand, it just creates an Order based on a table that maps
 * Products onto Actors, and sends it after a given time delay. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InternalDemandHandlerOrder extends InternalDemandHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** a table to map the products onto a unique supplier */
    private Map<Product, SupplierRecord> suppliers = new HashMap<Product, SupplierRecord>();

    /** the logger. */
    private static Logger logger = LogManager.getLogger(InternalDemandHandlerOrder.class);

    /**
     * Constructs a new InternalDemandHandlerOrder
     * @param owner the owner of the internal demand
     * @param handlingTime the handling time distribution
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandHandlerOrder(final SupplyChainActor owner, final DistContinuous handlingTime,
            final StockInterface stock)
    {
        super(owner, handlingTime, stock);
    }

    /**
     * Constructs a new InternalDemandHandlerOrder
     * @param owner the owner of the internal demand
     * @param handlingTime the constant handling time
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandHandlerOrder(final SupplyChainActor owner, final double handlingTime, final StockInterface stock)
    {
        this(owner, new DistConstant(new Java2Random(), handlingTime), stock);
    }

    /**
     * @param product the product that has a fixed supplier.
     * @param supplier the supplier for that product.
     * @param unitPrice the price per unit to ask for.
     */
    public void addSupplier(final Product product, final SupplyChainActor supplier, final double unitPrice)
    {
        this.suppliers.put(product, new SupplierRecord(supplier, unitPrice));
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface #handleContent(java.io.Serializable)
     */
    public boolean handleContent(final Serializable content)
    {
        InternalDemand internalDemand = (InternalDemand) checkContent(content);
        if (!isValidContent(internalDemand))
        {
            return false;
        }
        // resolve the suplier
        SupplierRecord supplierRecord = this.suppliers.get(internalDemand.getProduct());
        if (supplierRecord == null)
        {
            logger.warn("checkContent", "InternalDemand for actor " + getOwner() + " contains product "
                    + internalDemand.getProduct().toString() + " without a supplier");
            return false;
        }
        // create an immediate order
        if (super.stock != null)
        {
            super.stock.changeOrderedAmount(internalDemand.getProduct(), internalDemand.getAmount());
        }
        SupplyChainActor supplier = supplierRecord.getSupplier();
        double price = internalDemand.getAmount() * supplierRecord.getUnitPrice();
        Order order = new OrderStandAlone(getOwner(), supplier, internalDemand.getInternalDemandID(),
                internalDemand.getLatestDeliveryDate(), internalDemand.getProduct(), internalDemand.getAmount(), price);
        // and send it out after the handling time
        getOwner().sendContent(order, this.handlingTime.draw());
        return true;
    }

    /**
     * INNER CLASS FOR STORING RECORDS OF SUPPLIERS AND PRICE <br>
     * Copyright (c) 2003-2006 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl </a>. The source code
     * and binary code of this software is proprietary information of Delft University of Technology.
     * @version May 1, 2004 <br>
     * @author <a href="http://www.tbm.tudelft.nl/webstaf/alexandv/index.htm">Alexander Verbraeck </a>
     */
    protected class SupplierRecord
    {
        /** the supplier */
        private SupplyChainActor supplier;

        /** the agreed price to pay per unit of product */
        private double unitPrice;

        /**
         * Construct a new SupplierRecord
         * @param supplier the supplier
         * @param unitPrice the price per unit
         */
        public SupplierRecord(final SupplyChainActor supplier, final double unitPrice)
        {
            super();
            this.supplier = supplier;
            this.unitPrice = unitPrice;
        }

        /**
         * @return Returns the supplier.
         */
        public SupplyChainActor getSupplier()
        {
            return this.supplier;
        }

        /**
         * @return Returns the unitPrice.
         */
        public double getUnitPrice()
        {
            return this.unitPrice;
        }
    }
}
