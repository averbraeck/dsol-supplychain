package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.transporting.TransportOption;

/**
 * A Quote is an answer to a RequestForQuote (or RFQ) and indicates how many items of a certain product could be sold for a
 * certain price at a certain date. The Quote might have a limited validity.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>q
 * @param sender the sender of the quote
 * @param receiver the receiver of the quote
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param rfq the RFQ for which this is the quote
 * @param price the quotation price, including transport costs and a profit margin
 * @param proposedDeliveryDate the intended delivery date of the products
 * @param transportOption the transport option offered
 * @param validityTime the time on the simulator clock until which the quote is valid
 */
public record Quote(SellingActor sender, PurchasingActor receiver, Time timestamp, long uniqueId, long groupingId,
        RequestForQuote rfq, Money price, Time proposedDeliveryDate, TransportOption transportOption, Time validityTime)
        implements GroupedContent, ProductContent
{
    public Quote(final RequestForQuote rfq, final Money price, final Time proposedDeliveryDate,
            final TransportOption transportOption, final Time validityTime)
    {
        this(rfq.receiver(), rfq.sender(), rfq.sender().getSimulatorTime(), rfq.sender().getModel().getUniqueContentId(),
                rfq.groupingId(), rfq, price, proposedDeliveryDate, transportOption, validityTime);
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
