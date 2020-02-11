package nl.tudelft.simulation.supplychain.production;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.content.ProductionOrder;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * The DelayProductionService starts production at the latest possible moment to meet the delivery date of the production order.
 * Two versions are available: one that waits till all the raw materials are available. If not, production is delayed until all
 * materials of the BoM are available in the right quantities. The other version is a greedy one, it takes all the materials it
 * needs from the moment production should start, and delays if necessary to get the missing materials. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DelayProductionService extends ProductionService
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the time distribution to produce products */
    protected DistContinuousDurationUnit productionTime;

    /** fixed time, independent of order size; otherwise time is per unit */
    protected boolean fixedTime;

    /** if true, immediately start picking raw materials */
    protected boolean greedy;

    /** the fraction that is added to the cost of the materials */
    protected double profitMargin;

    /**
     * Constructs a new production service for one product.
     * @param owner the actor that owns the production service.
     * @param stock the stock for getting and storing materials.
     * @param product the product of the production service.
     * @param productionTime the time distribution to produce products.
     * @param fixedTime fixed time, independent of order size; otherwise, the time is per unit.
     * @param greedy if true, immediately start picking raw materials when production has to start.
     * @param profitMargin the fraction that is added to the cost of the materials.
     */
    public DelayProductionService(final StockKeepingActor owner, final StockInterface stock, final Product product,
            final DistContinuousDurationUnit productionTime, final boolean fixedTime, final boolean greedy,
            final double profitMargin)
    {
        super(owner, stock, product);
        this.productionTime = productionTime;
        this.fixedTime = fixedTime;
        this.greedy = greedy;
        this.profitMargin = profitMargin;
    }

    /**
     * Accept the production order, and delay till the start of production (equals delivery time minus production time minus
     * transportation time) to get the raw materials to produce. Acquire the materials either greedy or all-at-once. <br>
     * {@inheritDoc}
     */
    @Override
    public void acceptProductionOrder(final ProductionOrder productionOrder)
    {
        System.out.println("DelayProductionOrder: acceptProductionOrder received: " + productionOrder);

        // calculate production time
        Duration ptime = this.productionTime.draw();
        if (!this.fixedTime)
        {
            ptime = ptime.times(productionOrder.getAmount());
        }
        Time startTime = productionOrder.getDateReady().minus(ptime);
        startTime = Time.max(this.owner.getSimulatorTime(), startTime);
        // determine the needed raw materials
        Product _product = productionOrder.getProduct();
        Map<Product, Double> bom = _product.getBillOfMaterials().getMaterials();

        HashMap<Product, Double> availableMaterials = new HashMap<>();
        Iterator<Product> bomIter = bom.keySet().iterator();
        while (bomIter.hasNext())
        {
            Product raw = bomIter.next();
            double amount = bom.get(raw).doubleValue();
            amount *= productionOrder.getAmount();
            availableMaterials.put(raw,Double.valueOf(amount));
        }
        // don't do anyting before production has to start
        Serializable[] args = new Serializable[] { productionOrder, ptime, availableMaterials };
        try
        {
            System.out.println("DelayProduction: production started for product: " + productionOrder.getProduct());
            this.owner.getSimulator().scheduleEventAbs(startTime, this, this, "startProduction", args);
        }
        catch (Exception e)
        {
            Logger.error(e, "acceptProductionOrder");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Duration getExpectedProductionDuration(final ProductionOrder productionOrder)
    {
        // calculate production time
        Duration ptime = this.productionTime.draw();
        if (!this.fixedTime)
        {
            ptime = ptime.times(productionOrder.getAmount());
        }

        Product _product = productionOrder.getProduct();
        Map<Product, Double> bom = _product.getBillOfMaterials().getMaterials();

        // check whether there is enough on stock for this order
        HashMap<Product, Double> availableMaterials = new HashMap<>();
        Iterator<Product> bomIter = bom.keySet().iterator();
        while (bomIter.hasNext())
        {
            Product raw = bomIter.next();
            double amount = bom.get(raw).doubleValue();
            amount *= productionOrder.getAmount();
            availableMaterials.put(raw,Double.valueOf(amount));
        }

        boolean enoughOnStock = pickRawMaterials(productionOrder, availableMaterials, false);

        // restocking is arranged somewhere else
        // however we simply add some time to the expected production time
        // TODO make the expected production time more intelligent
        if (!enoughOnStock)
        {
            // for now we simply add one week to the expected production time
            ptime = ptime.plus(new Duration(1.0, DurationUnit.WEEK));
        }

        return ptime;
    }

    /**
     * Start the production at the latest possible time. When raw materials are
     * @param productionOrder the production order.
     * @param prodctionDuration the production duration.
     * @param availableMaterials the gathered raw materials.
     */
    protected void startProduction(final ProductionOrder productionOrder, final Duration prodctionDuration,
            final HashMap<Product, Double> availableMaterials)
    {
        // implement production: look if raw materials available in stock
        boolean ready = pickRawMaterials(productionOrder, availableMaterials, false);
        if (ready)
        {
            pickRawMaterials(productionOrder, availableMaterials, true);
            // wait for the production time to put the final products together
            Serializable[] args = new Serializable[] { productionOrder };
            try
            {
                this.owner.getSimulator().scheduleEventRel(prodctionDuration, this, this, "endProduction", args);
            }
            catch (Exception e)
            {
                Logger.error(e, "startProduction");
            }
        }
        else
        {
            if (this.greedy)
            {
                pickRawMaterials(productionOrder, availableMaterials, true);
            }
            // try again in one day
            Serializable[] args = new Serializable[] { productionOrder, prodctionDuration, availableMaterials };
            try
            {
                this.owner.getSimulator().scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, this, "startProduction",
                        args);
            }
            catch (Exception e)
            {
                Logger.error(e, "startProduction");
            }
        }
    }

    /**
     * endProduction is scheduled after the production time, which starts when all raw materials are available. The task of this
     * scheduled method is to store the finished products in stock.
     * @param productionOrder the original production order
     */
    protected void endProduction(final ProductionOrder productionOrder)
    {
        Product _product = productionOrder.getProduct();
        double amount = productionOrder.getAmount();
        Money cost = productionOrder.getMaterialCost();
        super.stock.addStock(_product, amount, cost.multiplyBy(this.profitMargin));
    }

    /**
     * @param productionOrder the order that has to be produced
     * @param availableMaterials the materials we already have picked
     * @param pick pick materials (true) or just check availability (false)
     * @return success meaning that all materials were available
     */
    private boolean pickRawMaterials(final ProductionOrder productionOrder, final HashMap<Product, Double> availableMaterials,
            final boolean pick)
    {
        boolean ready = true;
        Iterator<Product> materialIter = availableMaterials.keySet().iterator();
        while (materialIter.hasNext())
        {
            Product rawProduct = materialIter.next();
            double neededAmount = availableMaterials.get(rawProduct).doubleValue();
            double pickAmount = Math.min(super.stock.getActualAmount(rawProduct), neededAmount);
            if (pickAmount == 0)
            {
                ready = false;
            }
            if (pick)
            {
                double actualAmount = super.stock.removeStock(rawProduct, pickAmount);
                productionOrder.addMaterialCost(super.stock.getUnitPrice(rawProduct).multiplyBy(actualAmount));
                System.out.println("DelayProduction: products taken from stock: " + rawProduct + ", amount=" + actualAmount);
            }
        }
        return ready;
    }

}
