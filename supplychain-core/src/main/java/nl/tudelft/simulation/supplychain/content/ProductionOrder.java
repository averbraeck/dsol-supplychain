package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * A ProductionOrder indicates: I want to produce a certain amount of products on a certain date. The attributes "product",
 * "amount", and "date" make up the production order.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the production order
 * @param receiver the receiver of the production order
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param demand the demand that triggers this production order
 * @param product the ordered product
 * @param amount the amount of the product, in units for that product
 * @param dateReady the intended date when the products should be ready
 */
public record ProductionOrder(Actor sender, Actor receiver, Time timestamp, long uniqueId, long groupingId, Demand demand,
        Product product, double amount, Time dateReady) implements GroupedContent, ProductContent
{
    public ProductionOrder(final Actor sender, final Actor receiver, final Demand demand, final Time dateReady)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(), demand.groupingId(), demand,
                demand.product(), demand.amount(), dateReady);
    }

}
