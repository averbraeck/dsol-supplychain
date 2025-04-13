package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The InventoryRelease is the statement to finance that products are about to be sent to a purchaser, and that an invoice needs
 * to be sent.
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
 * @param inventoryReleaseRequest the inventory reservation that was made earlier
 */
public record InventoryRelease(WarehousingActor sender, FinancingActor receiver, Time timestamp, long uniqueId, long groupingId,
        InventoryReleaseRequest inventoryReleaseRequest) implements GroupedContent, ProductContent
{
    public InventoryRelease(final WarehousingActor sender, final FinancingActor receiver,
            final InventoryReleaseRequest inventoryReleaseRequest)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(),
                inventoryReleaseRequest.groupingId(), inventoryReleaseRequest);
    }

    @Override
    public Product product()
    {
        return inventoryReleaseRequest().product();
    }

    @Override
    public double amount()
    {
        return inventoryReleaseRequest().amount();
    }

}
