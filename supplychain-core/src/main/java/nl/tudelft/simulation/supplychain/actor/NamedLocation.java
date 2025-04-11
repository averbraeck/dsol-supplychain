package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;

import org.djutils.base.Identifiable;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.Locatable;

/**
 * NamedLocation is the super-interface for an Actor, but also used in transport intermediate locations such as ports, airports
 * and rail terminals.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface NamedLocation extends Locatable, Identifiable, Serializable
{
    /**
     * Return the longer name of the actor.
     * @return the longer name of the actor
     */
    String getName();

    /**
     * Return the location description of the actor (e.g., a city, country).
     * @return the location description of the actor
     */
    String getLocationDescription();

    @Override
    Point2d getLocation();

    /**
     * Return the z-value of the location, or 0.0 when the location is in 2 dimensions, avoiding the RemoteException.
     * @return the z-value of the location, or 0.0 when the location is in 2 dimensions, or when getLocation() returns null
     */
    @Override
    default double getZ()
    {
        return 0.0;
    }

    /**
     * Return the z-direction of the location in radians, or 0.0 when the location has no direction, , avoiding the
     * RemoteException.
     * @return the z-direction of the location in radians, or 0.0 when the location has no direction, or when getLocation()
     *         returns null
     */
    @Override
    default double getDirZ()
    {
        return 0.0;
    }

    @Override
    Bounds2d getBounds();

    /**
     * @param bounds
     */
    void setBounds(Bounds2d bounds);

}
