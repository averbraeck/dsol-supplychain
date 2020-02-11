/*
 * @(#)Stock.java Mar 3, 2004
 * 
 * Copyright (c) 2003-2006 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved.
 * 
 * See for project information <a href="http://www.simulation.tudelft.nl/">
 * www.simulation.tudelft.nl </a>.
 * 
 * The source code and binary code of this software is proprietary information
 * of Delft University of Technology.
 */

package nl.tudelft.simulation.supplychain.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.EventProducer;
import org.djutils.event.TimedEvent;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * Simple implementation of Stock for a Trader. The information on stocked amounts is stored in a HashTable of StockRecords.
 * Events on stock changes are fired by Stock, so subscribers who are interested in the stock amounts can see what is going on
 * in the Stock. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Stock extends EventProducer implements StockInterface, StockForecastInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the actow that owns of the stock */
    protected StockKeepingActor owner;

    /** record keeping of the stock */
    protected Hashtable<Product, StockRecord> stockRecords = new Hashtable<Product, StockRecord>();

    /**
     * <Product, <time moment, ArrayList<values for time moment>>> future changes
     */
    protected Map<Product, TreeMap<Time, ArrayList<Double>>> futureChanges = new HashMap<>();

    /**
     * Create a new Stock for an actor.
     * @param owner the Trader that physically owns the stock.
     */
    public Stock(final StockKeepingActor owner)
    {
        super();
        this.owner = owner;
    }

    /**
     * Create a new Stock for an actor, with an initial amount of products.
     * @param owner the trader for which this is the stock
     * @param initialStock the initial stock
     */
    public Stock(final StockKeepingActor owner, final Stock initialStock)
    {
        this(owner);
        for (Iterator<Product> productIterator = initialStock.iterator(); productIterator.hasNext();)
        {
            Product product = productIterator.next();
            addStock(product, initialStock.getActualAmount(product), product.getUnitMarketPrice());
            this.changeClaimedAmount(product, initialStock.getClaimedAmount(product));
            this.changeFutureClaimedAmount(product, initialStock.getClaimedAmount(product), owner.getSimulatorTime());
            this.changeOrderedAmount(product, initialStock.getOrderedAmount(product));
            this.changeFutureOrderedAmount(product, initialStock.getOrderedAmount(product), owner.getSimulatorTime());
            sendStockUpdateEvent(product);
        }
    }

    /** {@inheritDoc} */
    @Override
    public StockKeepingActor getOwner()
    {
        return this.owner;
    }

    /** {@inheritDoc} */
    @Override
    public void addStock(final Product product, final double amount, final Money totalPrice)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            stockRecord = new StockRecord(this.owner, this.owner.getSimulator(), product);
            this.stockRecords.put(product, stockRecord);
        }
        try
        {
            if (amount == 0.0)
            {
                throw new Exception("Amount is 0.0; leading to a divide by zero.");
            }
            stockRecord.addActualAmount(amount, totalPrice.divideBy(amount));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            Logger.error(exception, "addStock");
        }
        this.sendStockUpdateEvent(stockRecord);
    }

    /** {@inheritDoc} */
    @Override
    public void addStock(final Shipment shipment)
    {
        StockRecord stockRecord = this.stockRecords.get(shipment.getProduct());
        if (stockRecord == null)
        {
            stockRecord = new StockRecord(this.owner, this.owner.getSimulator(), shipment.getProduct());
            this.stockRecords.put(shipment.getProduct(), stockRecord);
        }
        stockRecord.addActualAmount(shipment.getAmount(), shipment.getTotalCargoValue().divideBy(shipment.getAmount()));
        this.sendStockUpdateEvent(stockRecord);
    }

    /** {@inheritDoc} */
    @Override
    public double removeStock(final Product product, final double amount)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        double actualAmount = 0.0;
        if (stockRecord != null)
        {
            actualAmount = Math.min(amount, stockRecord.getActualAmount());
        }
        // double unitprice = stockRecord.getUnitPrice();
        stockRecord.removeActualAmount(actualAmount);
        this.owner.checkStock(product);
        this.sendStockUpdateEvent(stockRecord);
        return actualAmount;
    }

    /** {@inheritDoc} */
    @Override
    public double getActualAmount(final Product product)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            return 0.0;
        }
        return stockRecord.getActualAmount();
    }

    /** {@inheritDoc} */
    @Override
    public double getClaimedAmount(final Product product)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            return 0.0;
        }
        return stockRecord.getClaimedAmount();
    }

    /** {@inheritDoc} */
    @Override
    public double getOrderedAmount(final Product product)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            return 0.0;
        }
        return stockRecord.getOrderedAmount();
    }

    /** {@inheritDoc} */
    @Override
    public boolean changeClaimedAmount(final Product product, final double delta)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            return false;
        }
        stockRecord.changeClaimedAmount(delta);
        this.owner.checkStock(product);
        this.sendStockUpdateEvent(stockRecord);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean changeFutureClaimedAmount(final Product product, final double delta, final Time time)
    {
        if (time.lt(this.owner.getSimulatorTime()))
        {
            Logger.error("changeFutureClaimedAmount - Time for the change is smaller than current simulator time (" + time + "<"
                    + this.owner.getSimulatorTime() + ").");
            return false;
        }

        if (delta < 0)
        {
            Logger.error("changeFutureOrderedAmount - The delta may not be smaller than 0 (" + delta + "<" + 0 + ").");
            return false;
        }

        if (!this.futureChanges.containsKey(product))
        {
            this.futureChanges.put(product, new TreeMap<Time, ArrayList<Double>>());
        }
        if (!this.futureChanges.get(product).containsKey(time))
        {
            this.futureChanges.get(product).put(time, new ArrayList<Double>());
        }
        // we consider a future claimed amount as a negative change for our
        // stock
        // value
        this.futureChanges.get(product).get(time).add(-delta);
        this.sendForecastUpdateEvent(product);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean changeOrderedAmount(final Product product, final double delta)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            return false;
        }
        stockRecord.changeOrderedAmount(delta);
        this.owner.checkStock(product);
        this.sendStockUpdateEvent(stockRecord);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean changeFutureOrderedAmount(Product product, double delta, Time time)
    {
        if (time.lt(this.owner.getSimulatorTime()))
        {
            Logger.error("changeFutureOrderedAmount - Time for the change is smaller than current simulator time (" + time + "<"
                    + this.owner.getSimulatorTime() + ").");
            return false;
        }
        if (delta < 0)
        {
            Logger.error("changeFutureOrderedAmount - The delta may not be smaller than 0 (" + delta + "<" + 0 + ").");
            return false;
        }

        if (!this.futureChanges.containsKey(product))
        {
            this.futureChanges.put(product, new TreeMap<Time, ArrayList<Double>>());
        }
        if (!this.futureChanges.get(product).containsKey(time))
        {
            this.futureChanges.get(product).put(time, new ArrayList<Double>());
        }
        this.futureChanges.get(product).get(time).add(delta);
        this.sendForecastUpdateEvent(product);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Money getUnitPrice(final Product product)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            return product.getUnitMarketPrice();
        }
        return stockRecord.getUnitPrice();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Product> iterator()
    {
        return this.stockRecords.keySet().iterator();
    }

    /** {@inheritDoc} */
    @Override
    public int numberOfProducts()
    {
        return this.stockRecords.keySet().size();
    }

    /**
     * Method sendStockUpdateEvent.
     * @param stockRecord the stock record that is updated
     */
    public void sendStockUpdateEvent(final StockRecord stockRecord)
    {
        StockUpdateData data = new StockUpdateData(stockRecord.getProduct().getName(), stockRecord.getActualAmount(),
                stockRecord.getClaimedAmount(), stockRecord.getOrderedAmount());

        this.fireEvent(new TimedEvent<Time>(StockInterface.STOCK_CHANGE_EVENT, this, data, this.owner.getSimulatorTime()));
    }

    // TODO: schedule the method below on a regular interval instead of invoking
    // it for every stock change?
    /**
     * Method sendForecastUpdateEvent
     * @param product
     */
    public void sendForecastUpdateEvent(final Product product)
    {
        // if (this.owner.getName().equalsIgnoreCase("tucker"))
        // {
        // TreeMap<Double, ArrayList<Double>> futureChangesPerProduct = this.futureChanges
        // .get(product);
        //
        // // make a list with the values on a per moment of time basis
        // SortedMap<Double, Double> futureValues = new TreeMap<Double, Double>();
        //
        // // get the current value of the stock
        // double futureStockValue = this.getActualAmount(product);
        //
        // // store this value
        // futureValues.put(this.owner.getSimulatorTime(), futureStockValue);
        //
        // for (Double time : futureChangesPerProduct.keySet())
        // {
        // // we only consider the 'future' changes
        // if (time >= this.owner.getSimulatorTime())
        // {
        // ArrayList<Double> changeValues = futureChangesPerProduct
        // .get(time);
        // for (Double changeValue : changeValues)
        // {
        // futureStockValue += changeValue;
        // }
        // // store the changed futureStockValue
        // futureValues.put(time, futureStockValue);
        // }
        // }
        //
        // this.fireEvent(new Event(
        // StockForecastInterface.STOCK_FORECAST_UPDATE_EVENT, this,
        // futureValues));
        //
        // System.out.println("Future values for product: "
        // + product.getName());
        // for (double time : futureValues.keySet())
        // {
        // System.out.println(time + " value: " + futureValues.get(time));
        // }
        // }
    }

    /**
     * Method sendStockUpdateEvent.
     * @param product the product for which the stock is updated
     */
    public void sendStockUpdateEvent(final Product product)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord != null)
        {
            this.sendStockUpdateEvent(stockRecord);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.owner.toString() + "_stock";
    }
}
