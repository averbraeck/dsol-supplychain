package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The InventoryQuote is the answer to a question to the warehouse to check whether there is inventory to fulfill the demand for
 * a certain amount of product at a certain date. Note that the warehouse can decide to look at buying this product on the
 * market, producing it on time, or just looking in the inventory.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the InventoryQuote
 * @param receiver the receiver of the InventoryQuote, as an answer to the InventoryQuoteRequest
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param inventoryQuoteRequest the InventoryQuoteRequest from the selling role
 * @param possible if false, the product cannot be released in time
 * @param priceWithoutProfit the price of the goods without a profit margin, or null if possible is false
 * @param earliestReleaseDate the earliest date that the products can be released, or null if possible is false
 */
public record InventoryQuote(WarehousingActor sender, SellingActor receiver, Time timestamp, long uniqueId, long groupingId,
        InventoryQuoteRequest inventoryQuoteRequest, boolean possible, Money priceWithoutProfit, Time earliestReleaseDate)
        implements GroupedContent, ProductContent
{
    public InventoryQuote(final InventoryQuoteRequest inventoryQuoteRequest, final boolean possible,
            final Money priceWithoutProfit, final Time proposedDeliveryDate)
    {
        this(inventoryQuoteRequest.receiver(), inventoryQuoteRequest.sender(),
                inventoryQuoteRequest.sender().getSimulatorTime(),
                inventoryQuoteRequest.sender().getModel().getUniqueContentId(), inventoryQuoteRequest.groupingId(),
                inventoryQuoteRequest, possible, priceWithoutProfit, proposedDeliveryDate);
    }

    @Override
    public Product product()
    {
        return inventoryQuoteRequest().product();
    }

    @Override
    public double amount()
    {
        return inventoryQuoteRequest().amount();
    }

}
