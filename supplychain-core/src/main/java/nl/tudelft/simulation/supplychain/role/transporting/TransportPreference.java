package nl.tudelft.simulation.supplychain.role.transporting;

import java.util.List;

/**
 * TransportPreferences indicates what modes of transport should be preferred (if possible) and whether cost or time is the most
 * important, or neither.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param preferredTransportModes A sequence of preferred transport modes
 * @param importance the relative importance of cost or time
 */
public record TransportPreference(List<TransportMode> preferredTransportModes, CostTimeImportance importance)
{
    /** The importance of cost versus speed. */
    public enum CostTimeImportance
    {
        /** Cost. */
        COST,

        /** Time. */
        TIME,

        /** Time. */
        DISTANCE,

        /** None. */
        NONE;
    }
}
