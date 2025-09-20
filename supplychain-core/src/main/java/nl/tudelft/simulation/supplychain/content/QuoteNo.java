package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;

/**
 * A Quote is an answer to a RequestForQuote (or RFQ) and indicates how many items of a certain product could be sold for a
 * certain price at a certain date. The QuoteNo indicates that the requested product is not available. Note that the default
 * option is not to send a negative response, but no response at all.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the information that the SellingActor does not quote on the RFQ
 * @param receiver the receiver of the information that the SellingActor does not quote on the RFQ
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param rfq the RFQ for which this is the quote denial
 */
public record QuoteNo(SellingActor sender, PurchasingActor receiver, Time timestamp, long uniqueId, long groupingId,
        RequestForQuote rfq) implements GroupedContent, ProductContent
{
    public QuoteNo(final RequestForQuote rfq)
    {
        this(rfq.receiver(), rfq.sender(), rfq.sender().getSimulatorTime(), rfq.sender().getModel().getUniqueContentId(),
                rfq.groupingId(), rfq);
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
