package nl.tudelft.simulation.supplychain.role.warehousing.handler;

import nl.tudelft.simulation.supplychain.content.InventoryEntry;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;

/**
 * The InventoryEntryHandler implements the business logic for a warehouse that receives an InventoryEntry. It reserves
 * inventory for later release.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InventoryEntryHandler extends ContentHandler<InventoryEntry, WarehousingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new InventoryEntry handler.
     * @param owner the actor belonging to this handler
     */
    public InventoryEntryHandler(final WarehousingActor owner)
    {
        super("InventoryEntryHandler", owner.getWarehousingRole(), InventoryEntry.class);
    }

    @Override
    public boolean handleContent(final InventoryEntry inventoryEntry)
    {
        if (!isValidContent(inventoryEntry))
        {
            return false;
        }
        // Reserve the inventory, even when it is not (yet) available
        getRole().getInventory().enterOrderedAmount(inventoryEntry.product(), inventoryEntry.amount(),
                inventoryEntry.shipment().getTotalCargoValue().divideBy(inventoryEntry.shipment().getAmount()));
        // TODO: fulfillment
        // var fulfillment = new Fulfillment(inventoryEntry.);
        // sendContent(fulfillment, getHandlingTime().draw());
        return true;
    }

}
