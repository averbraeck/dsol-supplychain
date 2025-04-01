package nl.tudelft.simulation.supplychain.role.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.ProductAmount;

/**
 * Simple implementation of Inventory for a Trader. The information on inventoryed amounts is stored in a HashTable of
 * InventoryRecords. Events on inventory changes are fired by Inventory, so subscribers who are interested in the inventory
 * amounts can see what is going on in the Inventory.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Inventory extends LocalEventProducer implements Serializable, EventProducer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221210L;

    /** An event to indicate inventory levels changed. */
    public static final EventType INVENTORY_CHANGE_EVENT = new EventType("INVENTORY_CHANGE_EVENT");

    /** An event to indicate that there is a new inventory forecast. */
    public static final EventType STOCK_FORECAST_UPDATE_EVENT = new EventType("STOCK_FORECAST_UPDATE_EVENT");

    /** the actow that owns the inventory. */
    private final Actor owner;

    /** the InventoryRole of the owner. */
    private final InventoryRole inventoryRole;

    /** record keeping of the inventory. */
    private Map<Product, InventoryRecord> inventoryRecords = new LinkedHashMap<Product, InventoryRecord>();

    /** Map of Product to Map of time to ArrayList of values for time moment: future changes. */
    private Map<Product, TreeMap<Time, ArrayList<Double>>> futureChanges = new LinkedHashMap<>();

    /**
     * Create a new Inventory for an actor.
     * @param inventoryRole the Role that physically handles the inventory.
     */
    public Inventory(final InventoryRole inventoryRole)
    {
        Throw.whenNull(inventoryRole, "inventoryRole cannot be null");
        this.owner = inventoryRole.getActor();
        this.inventoryRole = inventoryRole;
    }

    /**
     * Create a new Inventory for an actor, with an initial amount of products.
     * @param inventoryRole the Role that physically handles the inventory.
     * @param initialInventory the initial inventory
     */
    public Inventory(final InventoryRole inventoryRole, final List<ProductAmount> initialInventory)
    {
        this(inventoryRole);
        Throw.whenNull(initialInventory, "initialInventory cannot be null");
        for (ProductAmount productAmount : initialInventory)
        {
            Product product = productAmount.getProduct();
            addToInventory(product, productAmount.getAmount(), product.getUnitMarketPrice());
            sendInventoryUpdateEvent(product);
        }
    }

    /**
     * Return the actor who owns this inventory.
     * @return the actor who owns this inventory
     */
    public Actor getOwner()
    {
        return this.owner;
    }

    /**
     * Return an overview of the products that we have in inventory.
     * @return an overview of the products that we have in inventory
     */
    public Set<Product> getProducts()
    {
        return this.inventoryRecords.keySet();
    }

    /**
     * Add products to the inventory.
     * @param product the product
     * @param amount the amount
     * @param totalPrice the value of this amount of product
     */
    public void addToInventory(final Product product, final double amount, final Money totalPrice)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord == null)
        {
            inventoryRecord = new InventoryRecord(this.owner, this.owner.getSimulator(), product);
            this.inventoryRecords.put(product, inventoryRecord);
        }
        try
        {
            if (amount == 0.0)
            {
                throw new Exception("Amount is 0.0; leading to a divide by zero.");
            }
            inventoryRecord.addActualAmount(amount, totalPrice.divideBy(amount));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            Logger.error(exception, "addInventory");
        }
        this.sendInventoryUpdateEvent(inventoryRecord);
    }

    /**
     * Add products to the inventory, based on a received Shipment.
     * @param shipment the shipment to add to the inventory
     */
    public void addToInventory(final Shipment shipment)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(shipment.getProduct());
        if (inventoryRecord == null)
        {
            inventoryRecord = new InventoryRecord(this.owner, this.owner.getSimulator(), shipment.getProduct());
            this.inventoryRecords.put(shipment.getProduct(), inventoryRecord);
        }
        inventoryRecord.addActualAmount(shipment.getAmount(), shipment.getTotalCargoValue().divideBy(shipment.getAmount()));
        this.sendInventoryUpdateEvent(inventoryRecord);
    }

    /**
     * Remove products from the inventory.
     * @param product the product
     * @param amount the amount
     * @return double the actual amount of the product taken out of inventory
     */
    public double removeFromInventory(final Product product, final double amount)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        double actualAmount = 0.0;
        if (inventoryRecord != null)
        {
            actualAmount = Math.min(amount, inventoryRecord.getActualAmount());
        }
        // double unitprice = inventoryRecord.getUnitPrice();
        inventoryRecord.removeActualAmount(actualAmount);
        this.inventoryRole.checkInventory(product);
        this.sendInventoryUpdateEvent(inventoryRecord);
        return actualAmount;
    }

    /**
     * Get the actual amount of a certain product in inventory.
     * @param product the product
     * @return double the actual amount
     */
    public double getActualAmount(final Product product)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord == null)
        {
            return 0.0;
        }
        return inventoryRecord.getActualAmount();
    }

    /**
     * Get the claimed amount of a certain product in inventory.
     * @param product the product
     * @return double the claimed amount
     */
    public double getClaimedAmount(final Product product)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord == null)
        {
            return 0.0;
        }
        return inventoryRecord.getClaimedAmount();
    }

    /**
     * Get the ordered amount of a certain product in inventory.
     * @param product the product
     * @return double the ordered amount
     */
    public double getOrderedAmount(final Product product)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord == null)
        {
            return 0.0;
        }
        return inventoryRecord.getOrderedAmount();
    }

    /**
     * Update the claimed amount of a certain product in inventory.
     * @param product the product
     * @param delta the delta (positive or negative)
     * @return boolean success or not
     */
    public boolean changeClaimedAmount(final Product product, final double delta)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord == null)
        {
            return false;
        }
        inventoryRecord.changeClaimedAmount(delta);
        this.inventoryRole.checkInventory(product);
        this.sendInventoryUpdateEvent(inventoryRecord);
        return true;
    }

    /**
     * Method changeFutureClaimedAmount.
     * @param product the product
     * @param delta the delta (positive or negative)
     * @param time the time the change is scheduled to take place
     * @return boolean success or not
     */
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
        // we consider a future claimed amount as a negative change for our inventory value
        this.futureChanges.get(product).get(time).add(-delta);
        // this.sendForecastUpdateEvent(product);
        return true;
    }

    /**
     * Update the ordered amount of a certain product in inventory.
     * @param product the product
     * @param delta the delta (positive or negative)
     * @return boolean success or not
     */
    public boolean changeOrderedAmount(final Product product, final double delta)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord == null)
        {
            return false;
        }
        inventoryRecord.changeOrderedAmount(delta);
        this.inventoryRole.checkInventory(product);
        this.sendInventoryUpdateEvent(inventoryRecord);
        return true;
    }

    /**
     * Method changeFutureOrderedAmount.
     * @param product the product
     * @param delta the delta (positive or negative)
     * @param time the time the change is scheduled to take place
     * @return boolean success or not
     */
    public boolean changeFutureOrderedAmount(final Product product, final double delta, final Time time)
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
        // this.sendForecastUpdateEvent(product);
        return true;
    }

    /**
     * Return the unit price of a product (based on its SKU).
     * @param product the product
     * @return double the price per unit
     */
    public Money getUnitPrice(final Product product)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord == null)
        {
            return product.getUnitMarketPrice();
        }
        return inventoryRecord.getUnitPrice();
    }

    /**
     * Return the number of product types in inventory.
     * @return int number of products
     */
    public int numberOfProducts()
    {
        return this.inventoryRecords.keySet().size();
    }

    /**
     * Method sendInventoryUpdateEvent.
     * @param inventoryRecord the inventory record that is updated
     */
    public void sendInventoryUpdateEvent(final InventoryRecord inventoryRecord)
    {
        InventoryUpdateData data = new InventoryUpdateData(inventoryRecord.getProduct().getName(),
                inventoryRecord.getActualAmount(), inventoryRecord.getClaimedAmount(), inventoryRecord.getOrderedAmount());

        this.fireEvent(new TimedEvent<Time>(INVENTORY_CHANGE_EVENT, data, this.owner.getSimulatorTime()));
    }

    /**
     * Method sendInventoryUpdateEvent.
     * @param product the product for which the inventory is updated
     */
    public void sendInventoryUpdateEvent(final Product product)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord != null)
        {
            this.sendInventoryUpdateEvent(inventoryRecord);
        }
    }

    @Override
    public String toString()
    {
        return this.owner.toString() + "_inventory";
    }

}
