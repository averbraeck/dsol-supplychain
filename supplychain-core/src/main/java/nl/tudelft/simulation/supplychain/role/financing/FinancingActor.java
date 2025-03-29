package nl.tudelft.simulation.supplychain.role.financing;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * FinancingActor is an interface to indicate that an Actor has a FinancingRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface FinancingActor extends Actor
{
    /**
     * Return the FinancingRole for this actor.
     * @return the FinancingRole for this actor
     */
    FinancingRole getFinancingRole();

    /**
     * Set the FinancingRole for this actor.
     * @param financingRole the new FinancingRole for this actor
     */
    void setFinancingRole(FinancingRole financingRole);

}
