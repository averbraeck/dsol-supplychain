package nl.tudelft.simulation.supplychain.role.transporting;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.role.directing.DirectingActorTransporting;

/**
 * TransportingActor is an interface to indicate that an Actor has a TransportingRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface TransportingActor extends DirectingActorTransporting
{
    /**
     * Return the TransportingRole for this actor.
     * @return the TransportingRole for this actor
     */
    default TransportingRole getTransportingRole()
    {
        return getRole(TransportingRole.class);
    }

    /**
     * Set the TransportingRole for this actor.
     * @param transportingRole the new TransportingRole for this actor
     */
    default void setTransportingRole(final TransportingRole transportingRole)
    {
        Throw.whenNull(transportingRole, "transportingRole cannot be null");
        registerRole(TransportingRole.class, transportingRole);
    }

}
