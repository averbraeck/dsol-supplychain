package nl.tudelft.simulation.supplychain.transport;

import java.util.Set;

import org.djutils.draw.point.Point;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * TransportOptionProvider gives options for transport on the basis of an origin and a destination.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface TransportOptionProvider
{
    /**
     * Give the transport options for a transport from a sender actor to a receiver actor.
     * @param sender the sender actor
     * @param receiver the receiver actor
     * @return a set of transport options from sender to receiver
     */
    default Set<TransportOption> provideTransportOptions(final Actor sender, final Actor receiver)
    {
        return provideTransportOptions(sender.getLocation(), receiver.getLocation());
    }

    /**
     * Give the transport options for a transport between two locations.
     * @param sender the origin location
     * @param receiver the destination location
     * @return a set of transport options between two locations
     */
    Set<TransportOption> provideTransportOptions(Point<?> sender, Point<?> receiver);

}
