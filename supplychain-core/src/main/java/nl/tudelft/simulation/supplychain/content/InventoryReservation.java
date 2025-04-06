package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The InventoryReservation is the answer to a question to the warehouse to definitively reserve the inventory to fulfill the
 * demand for a certain amount of product at a certain date.
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
 * @param inventoryReservationRequest the InventoryReservationRequest from the selling role
 * @param reserved if false, the product cannot be released (anymore) at the requested date
 */
public record InventoryReservation(WarehousingActor sender, SellingActor receiver, Time timestamp, long uniqueId,
        long groupingId, InventoryReservationRequest inventoryReservationRequest, boolean reserved)
        implements GroupedContent, ProductContent
{
    public InventoryReservation(final InventoryReservationRequest inventoryReservationRequest, final boolean reserved)
    {
        this(inventoryReservationRequest.receiver(), inventoryReservationRequest.sender(),
                inventoryReservationRequest.sender().getSimulatorTime(),
                inventoryReservationRequest.sender().getModel().getUniqueContentId(), inventoryReservationRequest.groupingId(),
                inventoryReservationRequest, reserved);
    }

    @Override
    public Product product()
    {
        return inventoryReservationRequest().product();
    }

    @Override
    public double amount()
    {
        return inventoryReservationRequest().amount();
    }

}
