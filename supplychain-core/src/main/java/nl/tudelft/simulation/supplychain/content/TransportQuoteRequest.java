package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;

/**
 * The TransportQuoteRequest is a question to one or more transport actors to provide a quote to transport a certain amount of
 * goods. It will be answered with a TransportQuote.
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
 * @param rfq the RequestForQuote from the purchaser
 * @param cutoffTime the time before which the transport quote needs to be sent
 */
public record TransportQuoteRequest(SellingActor sender, TransportingActor receiver, Time timestamp, long uniqueId,
        long groupingId, RequestForQuote rfq, Time cutoffTime) implements GroupedContent, ProductContent
{
    public TransportQuoteRequest(final SellingActor sender, final TransportingActor receiver, final RequestForQuote rfq,
            final Time cutoffTime)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), rfq.groupingId(), rfq,
                cutoffTime);
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
