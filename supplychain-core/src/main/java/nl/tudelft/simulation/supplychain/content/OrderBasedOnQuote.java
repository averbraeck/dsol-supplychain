package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;

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
 * @param deliveryDate the delivery date as ordered
 */
public record OrderBasedOnQuote(PurchasingActor sender, SellingActor receiver, Time timestamp, long uniqueId, long groupingId,
        Quote quote, Time deliveryDate) implements Order
{
    public OrderBasedOnQuote(final Quote quote, final Time deliveryDate)
    {
        this(quote.receiver(), quote.sender(), quote.sender().getSimulatorTime(),
                quote.sender().getModel().getUniqueContentId(), quote.groupingId(), quote, deliveryDate);
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

    @Override
    public TransportQuote transportQuote()
    {
        return this.quote.transportQuote();
    }

    @Override
    public Money price()
    {
        return this.quote.price();
    }

}
