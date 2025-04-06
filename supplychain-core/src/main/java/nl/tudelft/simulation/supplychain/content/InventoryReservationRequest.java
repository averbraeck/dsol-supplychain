package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The InventoryReservationRequest is a question to the warehouse to definitively reserve the inventory to fulfill the demand
 * for a certain amount of product at a certain date. It will be answered with an InventoryReservation.
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
 * @param order the Order from the purchaser
 */
public record InventoryReservationRequest(SellingActor sender, WarehousingActor receiver, Time timestamp, long uniqueId,
        long groupingId, Order order) implements GroupedContent, ProductContent
{
    public InventoryReservationRequest(final SellingActor sender, final WarehousingActor receiver, final Order order)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), order.groupingId(), order);
    }

    @Override
    public Product product()
    {
        return order().product();
    }

    @Override
    public double amount()
    {
        return order().amount();
    }

}
