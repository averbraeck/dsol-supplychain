package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.content.SearchRequest;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRoleSearch;
import nl.tudelft.simulation.supplychain.role.searching.SearchingActor;

/**
 * The DemandHandlerSearch is a simple implementation of the business logic to handle a request for new products through a
 * yellow page request. When receiving the demand, it just creates an Search request, without a given time delay.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemandHandlerSearch extends DemandHandler
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the search actor to use. */
    private SearchingActor searchingActor;

    /** maximum distance to use in the search. */
    private Length maximumDistance;

    /** maximum number of actors to return. */
    private int maximumNumber;

    /**
     * Constructs a new DemandHandlerSearch.
     * @param owner the owner of the handler
     * @param searchingActor the Actor that provides the searching service
     * @param maximumDistance the search distance to use for all products
     * @param maximumNumber the max number of suppliers to return
     */
    public DemandHandlerSearch(final PurchasingActor owner, final SearchingActor searchingActor, final Length maximumDistance,
            final int maximumNumber)
    {
        super("DemandHandlerSearch", owner);
        this.searchingActor = searchingActor;
        this.maximumDistance = maximumDistance;
        this.maximumNumber = maximumNumber;
    }

    @Override
    public boolean handleContent(final Demand demand)
    {
        if (!isValidContent(demand))
        {
            return false;
        }
        // store the Demand
        getRole().storeDemand(demand);
        // create a SearchRequest
        SearchRequest searchRequest = new SearchRequest(getActor(), this.searchingActor, demand.groupingId(),
                this.maximumDistance, this.maximumNumber, demand.product());
        // and send it out immediately
        sendContent(searchRequest, getHandlingTime().draw());
        return true;
    }

    @Override
    public PurchasingRoleSearch getRole()
    {
        return (PurchasingRoleSearch) super.getRole();
    }
}
