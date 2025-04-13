package nl.tudelft.simulation.supplychain.role.warehousing.handler;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.content.InventoryRelease;
import nl.tudelft.simulation.supplychain.content.InventoryReleaseRequest;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;

/**
 * The InventoryReleaseRequestHandler implements the business logic for a warehouse that receives an
 * InventoryReleaseRequest. It reserves inventory for later release.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InventoryReleaseRequestHandler extends ContentHandler<InventoryReleaseRequest, WarehousingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new InventoryReleaseRequest handler.
     * @param owner the role belonging to this handler
     */
    public InventoryReleaseRequestHandler(final WarehousingRole owner)
    {
        super("InventoryReleaseRequestHandler", owner, InventoryReleaseRequest.class);
    }

    @Override
    public boolean handleContent(final InventoryReleaseRequest irr)
    {
        if (!isValidContent(irr))
        {
            return false;
        }

        // Check if the inventory is available. If yes, release. If no, schedule this method again in one day.
        if (getRole().getInventory().getActualAmount(irr.product()) < irr.amount())
        {
            getSimulator().scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, "handleContent", new Object[] {irr});
            return true;
        }
        
        getRole().getInventory().reserveAmount(irr.product(), irr.amount());
        var inventoryRelease = new InventoryRelease(irr.receiver(), irr.sender().getFinancingRole().getActor(), irr);
        sendContent(inventoryRelease, getHandlingTime().draw());
        return true;
    }

}
