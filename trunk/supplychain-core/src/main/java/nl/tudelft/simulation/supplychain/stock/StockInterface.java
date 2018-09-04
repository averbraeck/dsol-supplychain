package nl.tudelft.simulation.supplychain.stock;

import java.io.Serializable;
import java.util.Iterator;

import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.event.EventProducerInterface;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.supplychain.actor.Trader;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The StockInterface describes the standard services that any object representing Stock in the supply chain project should
 * have. Methods are related to handling physical stock itself, and three types of information on the stock: the available
 * amount (really in the warehouse), the ordered amount (how many units did we order), and the claimed amount (how many units
 * are claimed for committed orders, as far as we know). <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface StockInterface extends Serializable, EventProducerInterface
{
    /** An event to indicate stock levels changed */
    EventType STOCK_CHANGE_EVENT = new EventType("STOCK_CHANGE_EVENT");

    /**
     * @return the trader who owns this stock
     */
    Trader getOwner();

    /**
     * Method addStock.
     * @param product the product
     * @param amount the amount
     * @param totalPrice the value of this amount of product
     */
    void addStock(Product product, double amount, Money totalPrice);

    /**
     * Method addStock.
     * @param shipment the shipment to add to the stock
     */
    void addStock(Shipment shipment);

    /**
     * Method removeStock.
     * @param product the product
     * @param amount the amount
     * @return double the actual amount of the product taken out of stock
     */
    public double removeStock(final Product product, final double amount);

    /**
     * Method getActualAmount.
     * @param product the product
     * @return double the actual amount
     */
    double getActualAmount(Product product);

    /**
     * Method getClaimedAmount.
     * @param product the product
     * @return double the claimed amount
     */
    double getClaimedAmount(Product product);

    /**
     * Method getOrderedAmount.
     * @param product the product
     * @return double the ordered amount
     */
    double getOrderedAmount(Product product);

    /**
     * Method changeClaimedAmount.
     * @param product the product
     * @param delta the delta (positive or negative)
     * @return boolean success or not
     */
    boolean changeClaimedAmount(Product product, double delta);

    /**
     * Method changeOrderedAmount.
     * @param product the product
     * @param delta the delta (positive or negative)
     * @return boolean success or not
     */
    boolean changeOrderedAmount(Product product, double delta);

    /**
     * Method getUnitPrice.
     * @param product the product
     * @return double the price per unit
     */
    Money getUnitPrice(Product product);

    /**
     * Method iterator.
     * @return the iterator
     */
    Iterator<Product> iterator();

    /**
     * give the number of product types in stock.
     * @return int number of products
     */
    int numberOfProducts();

    /**
     * fires an update event on the current status of the stock for the specific product
     * @param product the product to fire the update for
     */
    void sendStockUpdateEvent(Product product);
}
