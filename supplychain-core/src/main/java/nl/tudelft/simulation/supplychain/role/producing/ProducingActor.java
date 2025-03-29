package nl.tudelft.simulation.supplychain.role.producing;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * ProducingActor is an interface to indicate that an Actor has a ProducingRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface ProducingActor extends Actor
{
    /**
     * Return the ProducingRole for this actor.
     * @return ProducingRole; the ProducingRole for this actor
     */
    ProducingRole getProducingRole();

    /**
     * Set the ProducingRole for this actor.
     * @param producingRole ProducingRole; the new ProducingRole for this actor
     */
    void setProducingRole(ProducingRole producingRole);

}
