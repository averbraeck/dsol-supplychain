package nl.tudelft.simulation.supplychain.role.warehousing.handler;

import nl.tudelft.simulation.supplychain.content.InventoryReservation;
import nl.tudelft.simulation.supplychain.content.InventoryReservationRequest;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;

/**
 * The InventoryReservationRequestHandler implements the business logic for a warehouse that receives an
 * InventoryReservationRequest. It reserves inventory for later release.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InventoryReservationRequestHandler extends ContentHandler<InventoryReservationRequest, WarehousingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new InventoryReservationRequest handler.
     * @param owner the role belonging to this handler
     */
    public InventoryReservationRequestHandler(final WarehousingRole owner)
    {
        super("InventoryReservationRequestHandler", owner, InventoryReservationRequest.class);
    }

    @Override
    public boolean handleContent(final InventoryReservationRequest irr)
    {
        if (!isValidContent(irr))
        {
            return false;
        }
        // Reserve the inventory, even when it is not (yet) available
        getRole().getInventory().reserveAmount(irr.product(), irr.amount());
        var inventoryReservation = new InventoryReservation(irr, true);
        sendContent(inventoryReservation, getHandlingTime().draw());
        return true;
    }

}
