package nl.tudelft.simulation.supplychain.role.consuming;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * DemandGeneratingActor is an interface to indicate that an Actor has a DemandGenerationRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface DemandGeneratingActor extends Actor
{
    /**
     * Return the DemandGenerationRole for this actor.
     * @return the DemandGenerationRole for this actor
     */
    DemandGenerationRole getDemandGenerationRole();

    /**
     * Set the DemandGenerationRole for this actor.
     * @param demandGenerationRole the new DemandGenerationRole for this actor
     */
    void setDemandGenerationRole(DemandGenerationRole demandGenerationRole);

}
