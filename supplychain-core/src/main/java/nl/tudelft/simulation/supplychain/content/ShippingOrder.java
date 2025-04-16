package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The ShippingOrder is the request from the WarehousingRole to the ShippingRole to ready the goods from the warehouse for
 * transport.
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
 * @param inventoryRelease the inventory release on which the shipment will be based
 */
public record ShippingOrder(WarehousingActor sender, WarehousingActor receiver, Time timestamp, long uniqueId, long groupingId,
        InventoryRelease inventoryRelease) implements GroupedContent, ProductContent
{
    public ShippingOrder(final WarehousingActor sender, final WarehousingActor receiver,
            final InventoryRelease inventoryRelease)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), inventoryRelease.groupingId(),
                inventoryRelease);
    }

    @Override
    public Product product()
    {
        return inventoryRelease().product();
    }

    @Override
    public double amount()
    {
        return inventoryRelease().amount();
    }

    public Order order()
    {
        return inventoryRelease().inventoryReleaseRequest().inventoryReservation().inventoryReservationRequest().order();
    }

}
