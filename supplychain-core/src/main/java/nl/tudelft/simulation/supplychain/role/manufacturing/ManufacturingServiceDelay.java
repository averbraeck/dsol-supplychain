package nl.tudelft.simulation.supplychain.role.manufacturing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.immutablecollections.ImmutableMap;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.ProductionOrder;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;

/**
 * The DelayProductionService starts production at the latest possible moment to meet the delivery date of the production order.
 * Two versions are available: one that waits till all the raw materials are available. If not, production is delayed until all
 * materials of the BoM are available in the right quantities. The other version is a greedy one, it takes all the materials it
 * needs from the moment production should start, and delays if necessary to get the missing materials.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ManufacturingServiceDelay extends ManufacturingService
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the time distribution to produce products. */
    private DistContinuousDuration productionTime;

    /** fixed time, independent of order size; otherwise time is per unit. */
    private boolean fixedTime;

    /** if true, immediately start picking raw materials. */
    private boolean greedy;

    /** the fraction that is added to the cost of the materials. */
    private double profitMargin;

    /**
     * Constructs a new production service for one product.
     * @param owner the role that owns the production service.
     * @param product the product of the production service.
     * @param productionTime the time distribution to produce products.
     * @param fixedTime otherwise, the time is per unit.
     * @param greedy if true, immediately start picking raw materials when production has to start.
     * @param profitMargin the fraction that is added to the cost of the materials.
     */
    public ManufacturingServiceDelay(final WarehousingRole owner, final Product product, final DistContinuousDuration productionTime,
            final boolean fixedTime, final boolean greedy, final double profitMargin)
    {
        super(owner, product);
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
        startTime = Time.max(getOwner().getActor().getSimulatorTime(), startTime);
        // determine the needed raw materials
        Product product = productionOrder.getProduct();
        ImmutableMap<Product, Double> bom = product.getBillOfMaterials().getMaterials();

        HashMap<Product, Double> availableMaterials = new LinkedHashMap<>();
        Iterator<Product> bomIter = bom.keySet().iterator();
        while (bomIter.hasNext())
        {
            Product raw = bomIter.next();
            double amount = bom.get(raw).doubleValue();
            amount *= productionOrder.getAmount();
            availableMaterials.put(raw, Double.valueOf(amount));
        }
        // don't do anyting before production has to start
        Serializable[] args = new Serializable[] {productionOrder, ptime, availableMaterials};
        try
        {
            System.out.println("DelayProduction: production started for product: " + productionOrder.getProduct());
            getOwner().getSimulator().scheduleEventAbs(startTime, this, "startProduction", args);
        }
        catch (Exception e)
        {
            Logger.error(e, "acceptProductionOrder");
        }
    }

    @Override
    public Duration getExpectedProductionDuration(final ProductionOrder productionOrder)
    {
        // calculate production time
        Duration ptime = this.productionTime.draw();
        if (!this.fixedTime)
        {
            ptime = ptime.times(productionOrder.getAmount());
        }

        Product product = productionOrder.getProduct();
        ImmutableMap<Product, Double> bom = product.getBillOfMaterials().getMaterials();

        // check whether there is enough on stock for this order
        HashMap<Product, Double> availableMaterials = new LinkedHashMap<>();
        Iterator<Product> bomIter = bom.keySet().iterator();
        while (bomIter.hasNext())
        {
            Product raw = bomIter.next();
            double amount = bom.get(raw).doubleValue();
            amount *= productionOrder.getAmount();
            availableMaterials.put(raw, Double.valueOf(amount));
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
            Serializable[] args = new Serializable[] {productionOrder};
            try
            {
                getOwner().getSimulator().scheduleEventRel(prodctionDuration, this, "endProduction", args);
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
            Serializable[] args = new Serializable[] {productionOrder, prodctionDuration, availableMaterials};
            try
            {
                getOwner().getSimulator().scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, "startProduction", args);
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
        Product product = productionOrder.getProduct();
        double amount = productionOrder.getAmount();
        Money cost = productionOrder.getMaterialCost();
        getInventory().addToInventory(product, amount, cost.multiplyBy(this.profitMargin));
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
            double pickAmount = Math.min(getInventory().getActualAmount(rawProduct), neededAmount);
            if (pickAmount == 0)
            {
                ready = false;
            }
            if (pick)
            {
                double actualAmount = getInventory().removeFromInventory(rawProduct, pickAmount);
                productionOrder.addMaterialCost(getInventory().getUnitPrice(rawProduct).multiplyBy(actualAmount));
                System.out.println("DelayProduction: products taken from stock: " + rawProduct + ", amount=" + actualAmount);
            }
        }
        return ready;
    }

}
