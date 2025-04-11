package nl.tudelft.simulation.supplychain.role.directing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.tudelft.simulation.supplychain.role.transporting.TransportMode;

/**
 * DirectingRoleSelling contains the most important variables for sales for the organization. What profit margins do we use?
 * What products do we sell? In which markets?.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DirectingRoleTransporting extends DirectingRole
{
    /** */
    private static final long serialVersionUID = 1L;

    /** a map of the transport modes we use, each with their profit margin. */
    private final Map<TransportMode, Double> transportModeProfitMarginMap;

    /** an indication whether we transport from/to all landmasses. */
    private boolean transportOnAllLandmasses = true;

    /** the landmasses on which we operate. Empty means all. */
    private final List<String> landmassesForTransport = new ArrayList<>();

    /**
     * Create a new Directing role for sales.
     * @param owner the actor that owns the Directing role
     * @param transportModeProfitMarginMap a map of the transport modes we use, each with their profit margin
     */
    public DirectingRoleTransporting(final DirectingActorSelling owner,
            final Map<TransportMode, Double> transportModeProfitMarginMap)
    {
        super(owner);
        this.transportModeProfitMarginMap = transportModeProfitMarginMap;
    }

    /**
     * Get the profit margin for a transport mode, or NaN when the transport mode is not in the map.
     * @param transportMode the transport mode for which to look up the profit margin
     * @return the profit margin for a transport mode, or NaN when not found
     */
    public double getProfitMargin(final TransportMode transportMode)
    {
        return this.transportModeProfitMarginMap.containsKey(transportMode)
                ? this.transportModeProfitMarginMap.get(transportMode) : Double.NaN;
    }

    /**
     * Set the profit margin for a transport mode.
     * @param transportMode the transport mode for which to set a (new) profit margin
     * @param profitMargin the (new) profit margin for the transport mode
     */
    public void setProfitMargin(final TransportMode transportMode, final double profitMargin)
    {
        this.transportModeProfitMarginMap.put(transportMode, profitMargin);
    }

    /**
     * Remove the profit margin for a transport mode. No error will be given if the transport mode was not present.
     * @param transportMode the transport mode for which the profit margin will be removed
     */
    public void removeProfitMargin(final TransportMode transportMode)
    {
        this.transportModeProfitMarginMap.remove(transportMode);
    }

    /**
     * Return whether we transport on this landmass.
     * @param landmass the landmass to look up
     * @return whether we transport on this landmass
     */
    public boolean isTransportOnLandmass(final String landmass)
    {
        return this.transportOnAllLandmasses ? true : this.landmassesForTransport.contains(landmass);
    }

    /**
     * Add this landmass for transport business. We turn transportOnAllLandmasses automatically to false when data is entered
     * into the set of landmasses where we do transport.
     * @param landmass the landmass to add for transport
     */
    public void addTransportOnLandmass(final String landmass)
    {
        this.landmassesForTransport.add(landmass);
        this.transportOnAllLandmasses = false;
    }

    /**
     * Remove this landmass for transport. No error will be given if the landmass was not present. We do NOT turn
     * transportOnAllLandmasses automatically to true in case the set is empty; one might want to stop transporting for a while.
     * @param landmass the landmass to remove for transport
     */
    public void removeTransportOnLandmass(final String landmass)
    {
        this.landmassesForTransport.remove(landmass);
    }

    /**
     * Return whether we transport on all landmasses.
     * @return whether we transport on all landmasses
     */
    public boolean isTransportOnAllLandmasses()
    {
        return this.transportOnAllLandmasses;
    }

    /**
     * Set whether we transport on all landmasses.
     * @param transportOnAllLandmasses true when we transport on all landmasses
     */
    public void setTransportOnAllLandmasses(final boolean transportOnAllLandmasses)
    {
        this.transportOnAllLandmasses = transportOnAllLandmasses;
    }

}
