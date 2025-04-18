package nl.tudelft.simulation.supplychain.role.purchasing;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.tudelft.simulation.supplychain.content.Demand;

/**
 * The purchasing role with searchs is a role that organizes the purchasing based on a SearchRequest, and continues from there.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PurchasingRoleSearch extends PurchasingRoleRFQ
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /** The map of groupingId to Demand. */
    private Map<Long, Demand> groupingIdDemandMap = new LinkedHashMap<>();

    /**
     * Construct a new PurchasingRole for Demand - SearchAnswer - Quote - Confirmation - Shipment - Invoice.
     * @param owner the actor to which this role belongs
     */
    public PurchasingRoleSearch(final PurchasingActor owner)
    {
        super(owner);
    }

    /**
     * Store the demand in the search map.
     * @param demand the demand to store
     */
    public void storeDemand(final Demand demand)
    {
        this.groupingIdDemandMap.put(demand.groupingId(), demand);
    }

    /**
     * Return the demand with the given groupingId.
     * @param groupingId the groupingId to look up
     * @return the Demand belonging to the groupingId
     */
    public Demand getDemandWithGroupingId(final long groupingId)
    {
        return this.groupingIdDemandMap.get(groupingId);
    }
    
    /**
     * Remove the demand with the given groupingId.
     * @param groupingId the groupingId to remove
     */
    public void removeDemandWithGroupingId(final long groupingId)
    {
        this.groupingIdDemandMap.remove(groupingId);
    }
    
    @Override
    public String getId()
    {
        return getActor().getId() + "-BUYING(search)";
    }

}
