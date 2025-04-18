package nl.tudelft.simulation.supplychain.role.manufacturing;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * ManufacturingActor is an interface to indicate that an Actor has a ManufacturingRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface ManufacturingActor extends Actor
{
    /**
     * Return the ManufacturingRole for this actor.
     * @return the ManufacturingRole for this actor
     */
    default ManufacturingRole getManufacturingRole()
    {
        return getRole(ManufacturingRole.class);
    }

    /**
     * Set the ManufacturingRole for this actor.
     * @param manufacturingRole the new ManufacturingRole for this actor
     */
    default void setManufacturingRole(final ManufacturingRole manufacturingRole)
    {
        Throw.whenNull(manufacturingRole, "manufacturingRole cannot be null");
        registerRole(ManufacturingRole.class, manufacturingRole);
    }

}
