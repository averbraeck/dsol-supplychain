package nl.tudelft.simulation.supplychain.content;

import java.util.List;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.role.yellowpage.YellowPageActor;

/**
 * The YellowPageAnswer is the answer from a Yellow Page actor to a YellowPageRequest. It contains a list of actors that might
 * sell a product or service that was asked for in the YellowPageRequest.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the yellow page answer
 * @param receiver the receiver of the yellow page answer
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param ypRequest the request that triggered this YP answer
 * @param actorList the suppliers of the requested product or service
 */
public record YellowPageAnswer(YellowPageActor sender, Actor receiver, Time timestamp, long uniqueId, long groupingId,
        YellowPageRequest ypRequest, List<Actor> actorList) implements GroupedContent
{
    public YellowPageAnswer(final YellowPageActor sender, final Actor receiver, final YellowPageRequest ypRequest,
            final List<Actor> actorList)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(), ypRequest.groupingId(),
                ypRequest, actorList);
    }
}
