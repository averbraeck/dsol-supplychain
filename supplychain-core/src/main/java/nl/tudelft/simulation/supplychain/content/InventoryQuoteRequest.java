package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The InventoryQuoteRequest is a question to the warehouse to check whether there is inventory to fulfill the demand for a
 * certain amount of product at a certain date. It will be answered with an InventoryQuote. Note that the warehouse can decide
 * to look at buying this produt on the market, producing it on time, or just looking in the inventory.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the SellingActor that sends the InventoryQuoteRequest
 * @param receiver the WarehousingActor that receives the message
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param rfq the RequestForQuote from the purchaser, containing information about product, amount and due date
 */
public record InventoryQuoteRequest(SellingActor sender, WarehousingActor receiver, Time timestamp, long uniqueId,
        long groupingId, RequestForQuote rfq) implements GroupedContent, ProductContent
{
    public InventoryQuoteRequest(final SellingActor sender, final WarehousingActor receiver, final RequestForQuote rfq)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), rfq.groupingId(), rfq);
    }

    @Override
    public Product product()
    {
        return rfq().product();
    }

    @Override
    public double amount()
    {
        return rfq().amount();
    }

}
