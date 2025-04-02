package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.yellowpage.YellowPageActor;

/**
 * The YellowPageRequest is a request to a YellowPageActor to provide a list, based on some contraints, of actors who could
 * provide a certain service or sell a certain product.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the yellow page request
 * @param receiver the receiver of the yellow page request
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param demand demand that triggered the process
 * @param maximumDistance the maximum distance around the 'sender' to search for suppliers
 * @param maximumNumber the maximum number of answers to return
 * @param product the product we are interested in
 */
public record YellowPageRequest(Actor sender, YellowPageActor receiver, Time timestamp, long uniqueId, long groupingId,
        Demand demand, Length maximumDistance, int maximumNumber, Product product) implements GroupedContent
{
    public YellowPageRequest(final Actor sender, final YellowPageActor receiver, final Demand demand,
            final Length maximumDistance, final int maximumNumber, final Product product)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(), demand.groupingId(),
                demand, maximumDistance, maximumNumber, product);
    }

    public YellowPageRequest(final Actor sender, final YellowPageActor receiver, final Demand demand,
            final Length maximumDistance, final Product product)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(), demand.groupingId(),
                demand, maximumDistance, Integer.MAX_VALUE, product);
    }

    public YellowPageRequest(final Actor sender, final YellowPageActor receiver, final Demand demand,
            final int maximumNumber, final Product product)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(), demand.groupingId(),
                demand, Length.POS_MAXVALUE, maximumNumber, product);
    }

    public YellowPageRequest(final Actor sender, final YellowPageActor receiver, final Demand demand,
            final Product product)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(), demand.groupingId(),
                demand, Length.POS_MAXVALUE, Integer.MAX_VALUE, product);
    }

}
