package nl.tudelft.simulation.supplychain.handler.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.point.Point;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.SearchAnswer;
import nl.tudelft.simulation.supplychain.content.SearchRequest;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;
import nl.tudelft.simulation.supplychain.role.searching.SearchingRole;

/**
 * The SearchRequestHandler implements the business logic for a yellow page actor who receives a SearchRequest and has
 * to look up supply chain actors within the boundaries of the request For the moment, these are max number, max distance, and
 * product.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SearchRequestHandler extends ContentHandler<SearchRequest>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the handling time of the policy in simulation time units. */
    private DistContinuousDuration handlingTime;

    /**
     * Constructs a new SearchRequestHandler.
     * @param owner the owner of the policy
     * @param handlingTime the distribution of the time to react on the YP request
     */
    public SearchRequestPolicy(final SearchingRole owner, final DistContinuousDuration handlingTime)
    {
        super("SearchRequestPolicy", owner, SearchRequest.class);
        this.handlingTime = handlingTime;
    }

    @Override
    public boolean handleContent(final SearchRequest searchRequest)
    {
        if (!isValidContent(searchRequest))
        {
            return false;
        }
        Set<Actor> supplierSet = ((SearchingRole) getRole()).getSuppliers(searchRequest.getProduct());
        if (supplierSet == null)
        {
            Logger.warn("Search '{}' has no supplier map for product {}", getActor().getName(),
                    searchRequest.getProduct().getName());
            return false;
        }
        SortedMap<Length, Actor> suppliers =
                pruneDistance(supplierSet, searchRequest.getMaximumDistance(), searchRequest.getSender().getLocation());
        pruneNumber(suppliers, searchRequest.getMaximumNumber());
        List<Actor> potentialSuppliers = new ArrayList<>(suppliers.values());
        SearchAnswer searchAnswer =
                new SearchAnswer(getActor(), searchRequest.getSender(), searchRequest.getDemandId(), potentialSuppliers, searchRequest);
        sendContent(searchAnswer, this.handlingTime.draw());
        return true;
    }

    /**
     * Prune the list of suppliers based on the maximum distance.
     * @param supplierSet the set of suppliers
     * @param maxDistance the maximum distance tgo use for pruning
     * @param location the location to compare the supplier locations with
     * @return a map of suppliers, sorted on distance
     */
    private SortedMap<Length, Actor> pruneDistance(final Set<Actor> supplierSet, final Length maxDistance,
            final Point<?> location)
    {
        SortedMap<Length, Actor> sortedSuppliers = new TreeMap<>();
        for (Actor actor : sortedSuppliers.values())
        {
            Length distance = getRole().getSimulator().getModel().calculateDistance(actor.getLocation(), location);
            if (distance.le(maxDistance))
            {
                sortedSuppliers.put(distance, actor);
            }
        }
        return sortedSuppliers;
    }

    /**
     * Prune the list of suppliers based on the number.
     * @param suppliers the map of suppliers (sorted on distance)
     * @param maxNumber the maximum number to leave
     */
    private void pruneNumber(final SortedMap<Length, Actor> suppliers, final int maxNumber)
    {
        int count = 0;
        Iterator<Actor> supplierIterator = suppliers.values().iterator();
        while (supplierIterator.hasNext())
        {
            supplierIterator.next();
            if (++count > maxNumber)
            {
                supplierIterator.remove();
            }
        }
    }

    @Override
    public SearchingRole getRole()
    {
        return (SearchingRole) super.getRole();
    }

}
