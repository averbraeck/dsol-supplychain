package nl.tudelft.simulation.supplychain.role.yellowpage;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * YellowPageActor is an interface to indicate that an Actor has a YellowPageRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface YellowPageActor extends Actor
{
    /**
     * Return the YellowPageRole for this actor.
     * @return the YellowPageRole for this actor
     */
    YellowPageRole getYellowPageRole();

    /**
     * Set the YellowPageRole for this actor.
     * @param yellowPageRole the new YellowPageRole for this actor
     */
    void setYellowPageRole(YellowPageRole yellowPageRole);

}
