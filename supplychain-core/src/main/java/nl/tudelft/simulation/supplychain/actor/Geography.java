package nl.tudelft.simulation.supplychain.actor;

import java.util.List;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;

import nl.tudelft.simulation.supplychain.role.transporting.TransportMode;

/**
 * Geography contains the access to transfer locations of different modes of transport.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param landmass the name of the landmass (continent or island) for contiguously connected trucking
 * @param truckSpeed the average speed of a truck on the given landmass
 * @param transferLocations List with accessible transfer locations by truck
 */
public record Geography(String landmass, Speed truckSpeed, List<TransferLocation> transferLocations)
{
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
