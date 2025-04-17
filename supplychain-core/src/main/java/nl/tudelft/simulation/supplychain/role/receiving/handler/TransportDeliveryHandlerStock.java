package nl.tudelft.simulation.supplychain.role.receiving.handler;

import nl.tudelft.simulation.supplychain.content.InventoryEntry;
import nl.tudelft.simulation.supplychain.content.TransportDelivery;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.receiving.ReceivingRole;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The TransportDeliveryHandler handles the shipment that comes in. This version sends the shipment to the local warehouse.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportDeliveryHandlerStock extends ContentHandler<TransportDelivery, ReceivingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Constructs a new TransportDeliveryHandler.
     * @param owner the owner of the handler.
     */
    public TransportDeliveryHandlerStock(final WarehousingActor owner)
    {
        super("TransportDeliveryHandlerStock", owner.getReceivingRole(), TransportDelivery.class);
    }

    @Override
    public boolean handleContent(final TransportDelivery transportDelivery)
    {
        if (!isValidContent(transportDelivery))
        {
            return false;
        }

        InventoryEntry inventoryEntry = new InventoryEntry(getRole().getActor(), getRole().getActor(),
                transportDelivery.order(), transportDelivery.shipment());
        sendContent(inventoryEntry, getHandlingTime().draw());
        return true;
    }

}
