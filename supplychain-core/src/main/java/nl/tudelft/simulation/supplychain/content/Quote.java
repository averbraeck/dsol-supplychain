package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.transport.TransportOption;

/**
 * A Quote is an answer to a RequestForQuote (or RFQ) and indicates how many items of a certain product could be sold for a
 * certain price at a certain date. The Quote might have a limited validity.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the quote
 * @param receiver the receiver of the quote
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param requestForQuote the RFQ for which this is the quote
 * @param product the product of the quote, couldbe a replacement product
 * @param amount the amount of products, could be less than the requested amount in the RFQ
 * @param price the quotation price
 * @param proposedShippingDate the intended shipping date of the products
 * @param transportOption the transport option offered
 * @param validityTime the time on the simulator clock until which the quote is valid
 */
public record Quote(SellingActor sender, PurchasingActor receiver, Time timestamp, long uniqueId, long groupingId,
        RequestForQuote requestForQuote, Product product, double amount, Money price, Time proposedShippingDate,
        TransportOption transportOption, Time validityTime) implements GroupedContent, ProductContent
{
    @SuppressWarnings("checkstyle:parameternumber")
    public Quote(final SellingActor sender, final PurchasingActor receiver, final RequestForQuote requestForQuote,
            final Product product, final double amount, final Money price, final Time proposedShippingDate,
            final TransportOption transportOption, final Time validityTime)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), requestForQuote.groupingId(),
                requestForQuote, product, amount, price, proposedShippingDate, transportOption, validityTime);
    }

}
