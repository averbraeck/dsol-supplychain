package nl.tudelft.simulation.supplychain.role.demand;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * DemandGeneratingActor is an interface to indicate that an Actor has a DemandGenerationRole.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface DemandGeneratingActor extends Actor
{
    /**
     * Return the DemandGenerationRole for this actor.
     * @return DemandGenerationRole; the DemandGenerationRole for this actor
     */
    DemandGenerationRole getBuyingRole();

    /**
     * Set the DemandGenerationRole for this actor.
     * @param demandGenerationRole DemandGenerationRole; the new DemandGenerationRole for this actor
     */
    void setBuyingRole(DemandGenerationRole demandGenerationRole);

}
