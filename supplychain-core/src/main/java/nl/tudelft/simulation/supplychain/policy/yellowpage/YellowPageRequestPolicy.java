package nl.tudelft.simulation.supplychain.policy.yellowpage;

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
import nl.tudelft.simulation.supplychain.content.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;
import nl.tudelft.simulation.supplychain.role.yellowpage.YellowPageRole;

/**
 * The YellowPageRequestHandler implements the business logic for a yellow page actor who receives a YellowPageRequest and has
 * to look up supply chain actors within the boundaries of the request For the moment, these are max number, max distance, and
 * product.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPageRequestHandler extends ContentHandler<YellowPageRequest>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the handling time of the policy in simulation time units. */
    private DistContinuousDuration handlingTime;

    /**
     * Constructs a new YellowPageRequestHandler.
     * @param owner the owner of the policy
     * @param handlingTime the distribution of the time to react on the YP request
     */
    public YellowPageRequestPolicy(final YellowPageRole owner, final DistContinuousDuration handlingTime)
    {
        super("YellowPageRequestPolicy", owner, YellowPageRequest.class);
        this.handlingTime = handlingTime;
    }

    @Override
    public boolean handleContent(final YellowPageRequest ypRequest)
    {
        if (!isValidMessage(ypRequest))
        {
            return false;
        }
        Set<Actor> supplierSet = ((YellowPageRole) getRole()).getSuppliers(ypRequest.getProduct());
        if (supplierSet == null)
        {
            Logger.warn("YellowPage '{}' has no supplier map for product {}", getActor().getName(),
                    ypRequest.getProduct().getName());
            return false;
        }
        SortedMap<Length, Actor> suppliers =
                pruneDistance(supplierSet, ypRequest.getMaximumDistance(), ypRequest.getSender().getLocation());
        pruneNumber(suppliers, ypRequest.getMaximumNumber());
        List<Actor> potentialSuppliers = new ArrayList<>(suppliers.values());
        YellowPageAnswer ypAnswer =
                new YellowPageAnswer(getActor(), ypRequest.getSender(), ypRequest.getDemandId(), potentialSuppliers, ypRequest);
        sendMessage(ypAnswer, this.handlingTime.draw());
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
    public YellowPageRole getRole()
    {
        return (YellowPageRole) super.getRole();
    }

}
