package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.transport.TransportOption;

/**
 * This implementation of an Order contains a link to a Quote on which the order is based. The Order contains a link to the
 * Quote on which it was based.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the quote-based order
 * @param receiver the receiver of the quote-based order
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param quote the quote on which the order is based
 * @param deliveryDate the intended delivery date of the products
 * @param transportOption the accepted transport option
 */
public record OrderBasedOnQuote(PurchasingActor sender, SellingActor receiver, Time timestamp, long uniqueId, long groupingId,
        Quote quote, Time deliveryDate, TransportOption transportOption) implements Order
{
    public OrderBasedOnQuote(final PurchasingActor sender, final SellingActor receiver, final Time deliveryDate,
            final Quote quote, final TransportOption transportOption)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), quote.groupingId(), quote,
                deliveryDate, transportOption);
    }

    @Override
    public Product product()
    {
        return this.quote.product();
    }

    @Override
    public double amount()
    {
        return this.quote.amount();
    }
}
