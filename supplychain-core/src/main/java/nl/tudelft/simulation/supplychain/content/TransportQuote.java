package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.transporting.TransportOption;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;

/**
 * The InventoryQuote is the answer to a question to the warehouse to check whether there is inventory to fulfill the demand for
 * a certain amount of product at a certain date. Note that the warehouse can decide to look at buying this product on the
 * market, producing it on time, or just looking in the inventory.
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
 * @param transportQuoteRequest the InventoryQuoteRequest from the selling role
 * @param transportOption a single transport option that matches the transport request best
 */
public record TransportQuote(TransportingActor sender, SellingActor receiver, Time timestamp, long uniqueId, long groupingId,
        TransportQuoteRequest transportQuoteRequest, TransportOption transportOption) implements GroupedContent, ProductContent
{
    public TransportQuote(final TransportingActor sender, final SellingActor receiver,
            final TransportQuoteRequest transportQuoteRequest, final TransportOption transportOption)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(),
                transportQuoteRequest.groupingId(), transportQuoteRequest, transportOption);
    }

    @Override
    public Product product()
    {
        return transportQuoteRequest().product();
    }

    @Override
    public double amount()
    {
        return transportQuoteRequest().amount();
    }

}
