package nl.tudelft.simulation.supplychain.role.consuming;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * ConsumingActor is an interface to indicate that an Actor has a ConsumingRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface ConsumingActor extends Actor
{
    /**
     * Return the ConsumingRole for this actor.
     * @return the ConsumingRole for this actor
     */
    default ConsumingRole getConsumingRole()
    {
        return getRole(ConsumingRole.class);
    }

    /**
     * Set the ConsumingRole for this actor.
     * @param consumingRole the new ConsumingRole for this actor
     */
    default void setConsumingRole(final ConsumingRole consumingRole)
    {
        Throw.whenNull(consumingRole, "consumingRole cannot be null");
        registerRole(ConsumingRole.class, consumingRole);
    }

}
