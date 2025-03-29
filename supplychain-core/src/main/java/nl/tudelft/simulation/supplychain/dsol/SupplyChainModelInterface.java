package nl.tudelft.simulation.supplychain.dsol;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.point.Point;

import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.ActorNotFoundException;

/**
 * SupplyChainModelInterface defines the specific methods of a supply chain model.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SupplyChainModelInterface extends DsolModel<Duration, SupplyChainSimulatorInterface>
{
    /**
     * Return a unique message id.
     * @return long; a unique message id
     */
    long getUniqueMessageId();

    /**
     * Calculate the distance between two points as a Length. The point could be in lat/lon, on an orthogonal grid, or based on
     * a GIS projection.
     * @param loc1 Point; first location
     * @param loc2 Point; second location
     * @return Length; distance between the locations
     */
    Length calculateDistance(Point<?> loc1, Point<?> loc2);

    /**
     * Calculate the distance between two points in km. The point could be in lat/lon, on an orthogonal grid, or based on a GIS
     * projection.
     * @param loc1 Point; first location
     * @param loc2 Point; second location
     * @return double; distance between the locations in km
     */
    default double calculateDistanceKm(final Point<?> loc1, final Point<?> loc2)
    {
        return calculateDistance(loc1, loc2).si / 1000.0;
    }

    /**
     * Retrieve an actor based on its id.
     * @param actor Actor; the actor to register
     * @throws ActorAlreadyDefinedException when the actor was already registered in the Actor map
     */
    void registerActor(Actor actor) throws ActorAlreadyDefinedException;

    /**
     * Retrieve an actor based on its id.
     * @param id String; the id to use to find the Actor
     * @return Actor; the actor based on its id
     * @throws ActorNotFoundException when the actor was not registered in the Actor map
     */
    Actor getActor(String id) throws ActorNotFoundException;
}
