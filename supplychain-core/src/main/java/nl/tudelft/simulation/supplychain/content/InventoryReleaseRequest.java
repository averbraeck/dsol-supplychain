package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The InventoryReleaseRequest is the order to the warehouse to release the inventory.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the RFW
 * @param receiver the receiver of the RFQ
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param inventoryReservation the inventory reservation that was made earlier
 */
public record InventoryReleaseRequest(SellingActor sender, WarehousingActor receiver, Time timestamp, long uniqueId,
        long groupingId, InventoryReservation inventoryReservation) implements GroupedContent, ProductContent
{
    public InventoryReleaseRequest(final InventoryReservation inventoryReservation)
    {
        this(inventoryReservation.receiver(), inventoryReservation.sender(), inventoryReservation.sender().getSimulatorTime(),
                inventoryReservation.sender().getModel().getUniqueContentId(), inventoryReservation.groupingId(),
                inventoryReservation);
    }

    @Override
    public Product product()
    {
        return inventoryReservation().product();
    }

    @Override
    public double amount()
    {
        return inventoryReservation().amount();
    }

}
