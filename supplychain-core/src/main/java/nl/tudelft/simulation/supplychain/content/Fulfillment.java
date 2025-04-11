package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The Fulfillment indicates that product delivery for a Demand has taken place.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the demand. It is not specified further, since it can be, e.g., a ConsumingActor or
 *            WaterhousingActor that sends the request.
 * @param receiver the receiver of the demand (same actor). This is also not specified since the sender is not specified. Yet,
 *            it will be typically handled by the PurchasingActor role.
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param demand the demand for which this is the fulfillment
 */
public record Fulfillment(Actor sender, Actor receiver, Time timestamp, long uniqueId, long groupingId, Demand demand)
        implements GroupedContent, ProductContent
{
    public Fulfillment(final Demand demand)
    {
        this(demand.receiver(), demand.sender(), demand.sender().getSimulatorTime(),
                demand.sender().getModel().getUniqueContentId(), demand.groupingId(), demand);
    }

    @Override
    public Product product()
    {
        return demand().product();
    }

    @Override
    public double amount()
    {
        return demand().amount();
    }
}
