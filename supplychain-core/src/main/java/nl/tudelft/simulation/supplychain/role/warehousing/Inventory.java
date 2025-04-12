package nl.tudelft.simulation.supplychain.role.warehousing;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.ProductAmount;
import nl.tudelft.simulation.supplychain.product.Shipment;

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
    public static final EventType INVENTORY_CHANGE_EVENT = new EventType("INVENTORY_CHANGE_EVENT", new MetaData("stock_update",
            "stock update", new ObjectDescriptor("stock update", "stock update", InventoryUpdateData.class)));

    /** the InventoryRole of the owner. */
    private final WarehousingRole warehousingRole;

    /** record keeping of the inventory. */
    private Map<Product, InventoryRecord> inventoryRecords = new LinkedHashMap<Product, InventoryRecord>();

    /**
     * Create a new Inventory for an actor.
     * @param warehousingRole the Role that physically handles the inventory.
     */
    public Inventory(final WarehousingRole warehousingRole)
    {
        Throw.whenNull(warehousingRole, "inventoryRole cannot be null");
        this.warehousingRole = warehousingRole;
    }

    /**
     * Create a new Inventory for an actor, with an initial amount of products.
     * @param warehousingRole the Role that physically handles the inventory.
     * @param initialInventory the initial inventory
     */
    public Inventory(final WarehousingRole warehousingRole, final List<ProductAmount> initialInventory)
    {
        this(warehousingRole);
        Throw.whenNull(initialInventory, "initialInventory cannot be null");
        for (ProductAmount productAmount : initialInventory)
        {
            Product product = productAmount.getProduct();
            addToInventory(product, productAmount.getAmount(), product.getUnitMarketPrice());
            sendInventoryUpdateEvent(product);
        }
    }

    /**
     * Check if a record for the product is there, and make it if not.
     * @param product the product to check
     * @return the new or existing inventory record
     */
    protected InventoryRecord retrieveInventoryRecord(final Product product)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord == null)
        {
            inventoryRecord = new InventoryRecord(getActor(), this.warehousingRole.getSimulator(), product);
            this.inventoryRecords.put(product, inventoryRecord);
        }
        return inventoryRecord;
    }
    
    /**
     * Add products to the inventory.
     * @param product the product
     * @param amount the amount
     * @param totalPrice the value of this amount of product
     */
    public void addToInventory(final Product product, final double amount, final Money totalPrice)
    {
        var inventoryRecord = retrieveInventoryRecord(product);
        inventoryRecord.addActualAmount(amount, totalPrice.divideBy(amount));
        this.sendInventoryUpdateEvent(inventoryRecord);
    }

    /**
     * Add products to the inventory, based on a received Shipment.
     * @param shipment the shipment to add to the inventory
     */
    public void addToInventory(final Shipment shipment)
    {
        var inventoryRecord = retrieveInventoryRecord(shipment.getProduct());
        inventoryRecord.addActualAmount(shipment.getAmount(), shipment.getTotalCargoValue().divideBy(shipment.getAmount()));
        this.sendInventoryUpdateEvent(inventoryRecord);
    }
    
    /**
     * Reserve a certain amount of product in inventory.
     * @param product the product
     * @param reservedDelta the reserved amount that will be added to the total reserved amount
     */
    public void reserveAmount(final Product product, final double reservedDelta)
    {
        var inventoryRecord = retrieveInventoryRecord(product);
        inventoryRecord.reserveAmount(reservedDelta);
        this.warehousingRole.checkInventory(product);
        this.sendInventoryUpdateEvent(inventoryRecord);
    }

    /**
     * Release a certain amount of reserved product in inventory.
     * @param product the product
     * @param releasedDelta the released amount of previously reserved product
     */
    public void releaseReservedAmount(final Product product, final double releasedDelta)
    {
        var inventoryRecord = retrieveInventoryRecord(product);
        inventoryRecord.releaseReservedAmount(releasedDelta);
        this.sendInventoryUpdateEvent(inventoryRecord);
    }

    /**
     * Indicate that a certain amount of product has been ordered.
     * @param product the product
     * @param orderedDelta the ordered amount that will be added to the total ordered amount
     */
    public void orderedAmount(final Product product, final double orderedDelta)
    {
        var inventoryRecord = retrieveInventoryRecord(product);
        inventoryRecord.orderAmount(orderedDelta);
        this.warehousingRole.checkInventory(product);
        this.sendInventoryUpdateEvent(inventoryRecord);
    }

    /**
     * Indicate that a certain amount of ordered product has been delivered.
     * @param product the product
     * @param enteredDelta the ordered amount that will be added to the total ordered amount
     * @param unitPrice The unit price of the products; has to be positive
     */
    public void enterOrderedAmount(final Product product, final double enteredDelta, final Money unitPrice)
    {
        var inventoryRecord = retrieveInventoryRecord(product);
        inventoryRecord.enterOrderedAmount(enteredDelta, unitPrice);
        this.warehousingRole.checkInventory(product);
        this.sendInventoryUpdateEvent(inventoryRecord);
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
     * Get the reserved amount of a certain product in inventory.
     * @param product the product
     * @return double the reserved amount
     */
    public double getReservedAmount(final Product product)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord == null)
        {
            return 0.0;
        }
        return inventoryRecord.getReservedAmount();
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
     * Get the virtual amount of a certain product in inventory, which is available + ordered - reserved.
     * @param product the product
     * @return double the virtual amount
     */
    public double getVirtualAmount(final Product product)
    {
        InventoryRecord inventoryRecord = this.inventoryRecords.get(product);
        if (inventoryRecord == null)
        {
            return 0.0;
        }
        return inventoryRecord.getActualAmount() + inventoryRecord.getOrderedAmount() - inventoryRecord.getReservedAmount();
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
        return inventoryRecord.getUnitMonetaryValue();
    }

    /**
     * Method sendInventoryUpdateEvent.
     * @param inventoryRecord the inventory record that is updated
     */
    public void sendInventoryUpdateEvent(final InventoryRecord inventoryRecord)
    {
        InventoryUpdateData data = new InventoryUpdateData(inventoryRecord.getProduct().getName(),
                inventoryRecord.getActualAmount(), inventoryRecord.getReservedAmount(), inventoryRecord.getOrderedAmount());

        this.fireEvent(new TimedEvent<Time>(INVENTORY_CHANGE_EVENT, data, this.warehousingRole.getSimulatorTime()));
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

    /**
     * Return the number of product types in inventory.
     * @return int number of products
     */
    public int numberOfProducts()
    {
        return this.inventoryRecords.keySet().size();
    }

    /**
     * Return the actor who owns this inventory.
     * @return the actor who owns this inventory
     */
    public WarehousingActor getActor()
    {
        return this.warehousingRole.getActor();
    }

    /**
     * Return an overview of the products that we have in inventory.
     * @return an overview of the products that we have in inventory
     */
    public Set<Product> getProducts()
    {
        return this.inventoryRecords.keySet();
    }

    @Override
    public String toString()
    {
        return getActor().toString() + "_inventory";
    }

}
