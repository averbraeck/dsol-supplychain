package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The InternalDemand record represents content for an internal demand of a supply chain actor. The InternalDemand triggers
 * buying or manufacturing of products, and is usually the first in a long chain of messages that are exchanged between actors.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the internal demand
 * @param receiver the receiver of the internal demand (same actor)
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the internalDemandId or the orderId
 * @param product the product which is demanded
 * @param amount the amount of the product in the product's SKU
 * @param earliestDeliveryDate the earliest delivery date
 * @param latestDeliveryDate the latest delivery date
 */
public record InternalDemand(Actor sender, Actor receiver, Time timestamp, long uniqueId, long groupingId, Product product,
        double amount, Time earliestDeliveryDate, Time latestDeliveryDate) implements Content
{
    public InternalDemand(final Actor sender, final Product product, final double amount, final Time earliestDeliveryDate,
            final Time latestDeliveryDate)
    {
        this(sender, sender, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(),
                sender.getModel().getUniqueMessageId(), product, amount, earliestDeliveryDate, latestDeliveryDate);
    }
}
