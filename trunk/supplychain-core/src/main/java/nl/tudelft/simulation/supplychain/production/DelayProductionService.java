package nl.tudelft.simulation.supplychain.production;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.unit.TimeUnit;

import nl.tudelft.simulation.dsol.experiment.TimeUnitInterface;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.Trader;
import nl.tudelft.simulation.supplychain.content.ProductionOrder;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

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
    protected DistContinuous productionTime;

    /** fixed time, independent of order size; otherwise time is per unit */
    protected boolean fixedTime;

    /** if true, immediately start picking raw materials */
    protected boolean greedy;

    /** the fraction that is added to the cost of the materials */
    protected double profitMargin;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(DelayProductionService.class);

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
    public DelayProductionService(final Trader owner, final StockInterface stock, final Product product,
            final DistContinuous productionTime, final boolean fixedTime, final boolean greedy, final double profitMargin)
    {
        super(owner, stock, product);
        this.productionTime = productionTime;
        this.fixedTime = fixedTime;
        this.greedy = greedy;
        this.profitMargin = profitMargin;
    }

    /**
     * Accept the production order, and delay till the start of production (equals delivery time minus production time minus
     * transportation time) to get the raw materials to produce. Acquire the materials either greedy or all-at-once.
     * @see nl.tudelft.simulation.supplychain.production.ProductionServiceInterface#acceptProductionOrder(nl.tudelft.simulation.supplychain.content.ProductionOrder)
     */
    public void acceptProductionOrder(final ProductionOrder productionOrder)
    {
        System.out.println("DelayProductionOrder: acceptProductionOrder received: " + productionOrder);

        // calculate production time
        double ptime = this.productionTime.draw();
        if (!this.fixedTime)
        {
            ptime *= productionOrder.getAmount();
        }
        double startTime = productionOrder.getDateReady() - ptime;
        startTime = Math.max(getOwner().getSimulatorTime(), startTime);
        // determine the needed raw materials
        Product _product = productionOrder.getProduct();
        Map bom = _product.getBillOfMaterials().getMaterials();

        HashMap<Product, Double> availableMaterials = new HashMap<Product, Double>();
        Iterator bomIter = bom.keySet().iterator();
        while (bomIter.hasNext())
        {
            Product raw = (Product) (bomIter.next());
            double amount = ((Double) (bom.get(raw))).doubleValue();
            amount *= productionOrder.getAmount();
            availableMaterials.put(raw, new Double(amount));
        }
        // don't do anyting before production has to start
        Serializable[] args = new Serializable[] { productionOrder, new Double(ptime), availableMaterials };
        try
        {
            SimEvent simEvent = new SimEvent(startTime, this, this, "startProduction", args);
            System.out.println("DelayProduction: production started for product: " + productionOrder.getProduct());
            getOwner().getSimulator().scheduleEvent(simEvent);
        }
        catch (Exception e)
        {
            logger.fatal("acceptProductionOrder", e);
        }
    }

    /**
     * @see nl.tudelft.simulation.supplychain.production.ProductionService#getExpectedProductionTime(nl.tudelft.simulation.supplychain.content.ProductionOrder)
     */
    public double getExpectedProductionTime(final ProductionOrder productionOrder)
    {
        // calculate production time
        double ptime = this.productionTime.draw();
        if (!this.fixedTime)
        {
            ptime *= productionOrder.getAmount();
        }

        Product _product = productionOrder.getProduct();
        Map bom = _product.getBillOfMaterials().getMaterials();

        // check whether there is enough on stock for this order
        HashMap<Product, Double> availableMaterials = new HashMap<Product, Double>();
        Iterator bomIter = bom.keySet().iterator();
        while (bomIter.hasNext())
        {
            Product raw = (Product) (bomIter.next());
            double amount = ((Double) (bom.get(raw))).doubleValue();
            amount *= productionOrder.getAmount();
            availableMaterials.put(raw, new Double(amount));
        }

        boolean enoughOnStock = pickRawMaterials(productionOrder, availableMaterials, false);

        // restocking is arranged somewhere else
        // however we simply add some time to the expected production time
        // TODO make the expected production time more intelligent
        if (!enoughOnStock)
        {
            // for now we simply add one week to the expected production time
            try
            {
                ptime += TimeUnit.convert(1, TimeUnitInterface.WEEK, getOwner().getDEVSSimulator());
            }
            catch (RemoteException remoteException)
            {
                logger.fatal("getExpectedProductionTime", remoteException);
            }
        }

        return ptime;
    }

    /**
     * Start the production at the latest possible time. When raw materials are
     * @param productionOrder the production order.
     * @param ptime the production time.
     * @param availableMaterials the gathered raw materials.
     */
    protected void startProduction(final ProductionOrder productionOrder, final double ptime, final HashMap availableMaterials)
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
                SimEvent simEvent = new SimEvent(getOwner().getSimulatorTime() + ptime, this, this, "endProduction", args);
                getOwner().getSimulator().scheduleEvent(simEvent);
            }
            catch (Exception e)
            {
                logger.fatal("startProduction", e);
            }
        }
        else
        {
            if (this.greedy)
            {
                pickRawMaterials(productionOrder, availableMaterials, true);
            }
            // try again in one day
            Serializable[] args = new Serializable[] { productionOrder, new Double(ptime), availableMaterials };
            try
            {
                SimEvent simEvent = new SimEvent(
                        getOwner().getSimulatorTime() + TimeUnit.convert(1.0, TimeUnit.DAY, getOwner().getSimulator()), this,
                        this, "startProduction", args);
                getOwner().getSimulator().scheduleEvent(simEvent);
            }
            catch (Exception e)
            {
                logger.fatal("startProduction", e);
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
        double cost = productionOrder.getMaterialCost();
        super.stock.addStock(_product, amount, cost * this.profitMargin);
    }

    /**
     * @param productionOrder the order that has to be produced
     * @param availableMaterials the materials we already have picked
     * @param pick pick materials (true) or just check availability (false)
     * @return success meaning that all materials were available
     */
    private boolean pickRawMaterials(final ProductionOrder productionOrder, final HashMap availableMaterials,
            final boolean pick)
    {
        boolean ready = true;
        Iterator materialIter = availableMaterials.keySet().iterator();
        while (materialIter.hasNext())
        {
            Product rawProduct = (Product) (materialIter.next());
            double neededAmount = ((Double) (availableMaterials.get(rawProduct))).doubleValue();
            double pickAmount = Math.min(super.stock.getActualAmount(rawProduct), neededAmount);
            if (pickAmount == 0)
            {
                ready = false;
            }
            if (pick)
            {
                double actualAmount = super.stock.removeStock(rawProduct, pickAmount);
                productionOrder.addMaterialCost(actualAmount * super.stock.getUnitPrice(rawProduct));
                System.out.println("DelayProduction: products taken from stock: " + rawProduct + ", amount=" + actualAmount);
            }
        }
        return ready;
    }

}
