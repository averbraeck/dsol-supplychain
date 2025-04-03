package nl.tudelft.simulation.supplychain.role.warehousing;

import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * InventoryActor is an interface to indicate that an Actor has a InventoryRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface WarehousingActor extends FinancingActor
{
    /**
     * Return the InventoryRole for this actor.
     * @return the InventoryRole for this actor
     */
    WarehousingRole getInventoryRole();

    /**
     * Set the InventoryRole for this actor.
     * @param inventoryRole the new InventoryRole for this actor
     */
    void setInventoryRole(WarehousingRole inventoryRole);

}
