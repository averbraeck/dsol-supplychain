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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.event.TimedEvent;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.Trader;
import nl.tudelft.simulation.supplychain.content.Shipment;
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
    protected Trader owner;

    /** record keeping of the stock */
    protected Hashtable<Product, StockRecord> stockRecords = new Hashtable<Product, StockRecord>();

    /** the logger. */
    private static Logger logger = LogManager.getLogger(Stock.class);

    /**
     * <Product, <time moment, ArrayList<values for time moment>>> future changes
     */
    protected Map<Product, TreeMap<Double, ArrayList<Double>>> futureChanges =
            new HashMap<Product, TreeMap<Double, ArrayList<Double>>>();

    /**
     * Create a new Stock for an actor.
     * @param owner the Trader that physically owns the stock.
     */
    public Stock(final Trader owner)
    {
        super();
        this.owner = owner;
    }

    /**
     * Create a new Stock for an actor, with an initial amount of products.
     * @param owner the trader for which this is the stock
     * @param initialStock the initial stock
     */
    public Stock(final Trader owner, final Stock initialStock)
    {
        this(owner);
        for (Iterator i = initialStock.iterator(); i.hasNext();)
        {
            Product product = (Product) i.next();
            addStock(product, initialStock.getActualAmount(product), product.getUnitMarketPrice());
            this.changeClaimedAmount(product, initialStock.getClaimedAmount(product));
            this.changeFutureClaimedAmount(product, initialStock.getClaimedAmount(product), owner.getSimulatorTime());
            this.changeOrderedAmount(product, initialStock.getOrderedAmount(product));
            this.changeFutureOrderedAmount(product, initialStock.getOrderedAmount(product), owner.getSimulatorTime());
        }
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#getOwner()
     */
    public Trader getOwner()
    {
        return this.owner;
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#addStock(nl.tudelft.simulation.supplychain.product.Product,
     *      double, double)
     */
    public void addStock(final Product product, final double amount, final double totalPrice)
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
            stockRecord.addActualAmount(amount, totalPrice / amount);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            logger.fatal("addStock", exception);
        }
        this.sendStockUpdateEvent(stockRecord);
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#addStock(nl.tudelft.simulation.supplychain.content.Shipment)
     */
    public void addStock(final Shipment shipment)
    {
        StockRecord stockRecord = this.stockRecords.get(shipment.getProduct());
        if (stockRecord == null)
        {
            stockRecord = new StockRecord(this.owner, this.owner.getSimulator(), shipment.getProduct());
            this.stockRecords.put(shipment.getProduct(), stockRecord);
        }
        stockRecord.addActualAmount(shipment.getAmount(), shipment.getValue() / shipment.getAmount());
        this.sendStockUpdateEvent(stockRecord);
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#removeStock(nl.tudelft.simulation.supplychain.product.Product,
     *      double)
     */
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

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#getActualAmount(nl.tudelft.simulation.supplychain.product.Product)
     */
    public double getActualAmount(final Product product)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            return 0.0;
        }
        return stockRecord.getActualAmount();
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#getClaimedAmount(nl.tudelft.simulation.supplychain.product.Product)
     */
    public double getClaimedAmount(final Product product)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            return 0.0;
        }
        return stockRecord.getClaimedAmount();
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#getOrderedAmount(nl.tudelft.simulation.supplychain.product.Product)
     */
    public double getOrderedAmount(final Product product)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            return 0.0;
        }
        return stockRecord.getOrderedAmount();
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#changeClaimedAmount(nl.tudelft.simulation.supplychain.product.Product,
     *      double)
     */
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

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockForecastInterface#changeFutureClaimedAmount(nl.tudelft.simulation.supplychain.product.Product,
     *      double, double)
     */
    public boolean changeFutureClaimedAmount(final Product product, final double delta, final double time)
    {
        // if (time < this.owner.getSimulatorTime())
        // {
        // logger.fatal("changeFutureClaimedAmount",
        // new IllegalArgumentException(
        // "Time for the change is smaller than current simulator time ("
        // + time + "<"
        // + this.owner.getSimulatorTime() + ")."));
        // return false;
        // }
        //
        // if (delta < 0)
        // {
        // logger.fatal("changeFutureOrderedAmount",
        // new IllegalArgumentException(
        // "The delta may not be smaller than 0 (" + delta
        // + "<" + 0 + ")."));
        // return false;
        // }
        //
        // if (!this.futureChanges.containsKey(product))
        // {
        // this.futureChanges.put(product,
        // new TreeMap<Double, ArrayList<Double>>());
        // }
        // if (!this.futureChanges.get(product).containsKey(time))
        // {
        // this.futureChanges.get(product).put(time, new ArrayList<Double>());
        // }
        // // we consider a future claimed amount as a negative change for our
        // // stock
        // // value
        // this.futureChanges.get(product).get(time).add(-delta);
        // this.sendForecastUpdateEvent(product);
        return true;
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#changeOrderedAmount(nl.tudelft.simulation.supplychain.product.Product,
     *      double)
     */
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

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockForecastInterface#changeFutureOrderedAmount(nl.tudelft.simulation.supplychain.product.Product,
     *      double, double)
     */
    public boolean changeFutureOrderedAmount(Product product, double delta, double time)
    {
        // if (time < this.owner.getSimulatorTime())
        // {
        // logger.fatal("changeFutureOrderedAmount",
        // new IllegalArgumentException(
        // "Time for the change is smaller than current simulator time ("
        // + time + "<"
        // + this.owner.getSimulatorTime() + ")."));
        // return false;
        // }
        // if (delta < 0)
        // {
        // logger.fatal("changeFutureOrderedAmount",
        // new IllegalArgumentException(
        // "The delta may not be smaller than 0 (" + delta
        // + "<" + 0 + ")."));
        // return false;
        // }
        //
        // if (!this.futureChanges.containsKey(product))
        // {
        // this.futureChanges.put(product,
        // new TreeMap<Double, ArrayList<Double>>());
        // }
        // if (!this.futureChanges.get(product).containsKey(time))
        // {
        // this.futureChanges.get(product).put(time, new ArrayList<Double>());
        // }
        // this.futureChanges.get(product).get(time).add(delta);
        // this.sendForecastUpdateEvent(product);
        return true;
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#getUnitPrice(nl.tudelft.simulation.supplychain.product.Product)
     */
    public double getUnitPrice(final Product product)
    {
        StockRecord stockRecord = this.stockRecords.get(product);
        if (stockRecord == null)
        {
            return product.getUnitMarketPrice();
        }
        return stockRecord.getUnitPrice();
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#iterator()
     */
    public Iterator<Product> iterator()
    {
        return this.stockRecords.keySet().iterator();
    }

    /**
     * @see nl.tudelft.simulation.supplychain.stock.StockInterface#numberOfProducts()
     */
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
        try
        {
            StockUpdateData data = new StockUpdateData(stockRecord.getProduct().getName(), stockRecord.getActualAmount(),
                    stockRecord.getClaimedAmount(), stockRecord.getOrderedAmount());

            this.fireEvent(new TimedEvent(StockInterface.STOCK_CHANGE_EVENT, this, data,
                    this.owner.getSimulator().getSimulatorTime()));
        }
        catch (RemoteException exception)
        {
            logger.warn("sendStockUpdateEvent", exception);
        }
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

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.owner.toString() + "_stock";
    }
}
