package nl.tudelft.simulation.supplychain.role.warehousing;

import java.io.Serializable;

/**
 * An update to the inventory.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param productName the product name
 * @param actualAmount the actual amount
 * @param claimedAmount the claimed amount
 * @param orderedAmount the ordered amount
 */
public record InventoryUpdateData(String productName, double actualAmount, double claimedAmount, double orderedAmount)
        implements Serializable
{
}
