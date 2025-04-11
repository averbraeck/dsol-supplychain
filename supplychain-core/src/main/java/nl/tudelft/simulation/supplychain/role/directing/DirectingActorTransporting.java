package nl.tudelft.simulation.supplychain.role.directing;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * DirectingActor is an interface to indicate that an Actor has a DirectingRole. The Transporting version focuses on transport.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface DirectingActorTransporting extends Actor
{
    /**
     * Return the DirectingRole for this actor.
     * @return the DirectingRole for this actor
     */
    DirectingRoleTransporting getDirectingRoleTransporting();

    /**
     * Set the DirectingRole for this actor.
     * @param directingRoleTransporting the new DirectingRole for this actor
     */
    void setDirectingRole(DirectingRoleTransporting directingRoleTransporting);

}
