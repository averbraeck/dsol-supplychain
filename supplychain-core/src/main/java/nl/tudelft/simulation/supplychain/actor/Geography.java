package nl.tudelft.simulation.supplychain.actor;

import java.util.ArrayList;
import java.util.List;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.supplychain.role.transporting.TransportMode;

/**
 * Geography contains the access to transfer locations of different modes of transport.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param location the location of the actor
 * @param locationDescription the location description of the actor (e.g., a city, country)
 * @param landmass the name of the landmass (continent or island) for contiguously connected trucking
 * @param truckSpeed the average speed of a truck on the given landmass
 * @param transferLocations List with accessible transfer locations by truck
 */
public record Geography(Point2d location, String locationDescription, String landmass, Speed truckSpeed,
        List<TransferLocation> transferLocations)
{
    /**
     * A Geography without any transport details, e.g., for a Bank.
     * @param location the location of the actor
     * @param locationDescription the location description of the actor (e.g., a city, country)
     * @param landmass the landmass where the actor is located
     */
    public Geography(final Point2d location, final String locationDescription, final String landmass)
    {
        this(location, locationDescription, landmass, Speed.ZERO, new ArrayList<>());
    }

    /**
     * The access parameters to a transfer location.
     * <p>
     * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
     * The supply chain Java library uses a BSD-3 style license.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     * @param namedLocation the details of the transfer location (port, airport, terminal)
     * @param mode the mode of transport accessible at the transfer location
     * @param distance the distance to the transfer location
     * @param travelTime the travel time to the transfer location
     */
    public record TransferLocation(NamedLocation namedLocation, TransportMode mode, Length distance, Duration travelTime)
    {
    }
}
