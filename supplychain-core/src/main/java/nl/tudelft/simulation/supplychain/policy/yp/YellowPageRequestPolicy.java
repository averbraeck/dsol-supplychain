package nl.tudelft.simulation.supplychain.policy.yp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.point.Point3d;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageRequest;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;
import nl.tudelft.simulation.supplychain.yellowpage.YellowPageActor;

/**
 * The YellowPageRequestHandler implements the business logic for a yellow page actor who receives a YellowPageRequest and has
 * to look up supply chain actors within the boundaries of the request For the moment, these are max number, max distance, and
 * product.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPageRequestPolicy extends SupplyChainPolicy<YellowPageRequest>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the handling time of the handler in simulation time units. */
    private DistContinuousDuration handlingTime;

    /**
     * Constructs a new YellowPageRequestHandler.
     * @param owner SupplyChainActorInterface; the owner of the policy
     * @param handlingTime the distribution of the time to react on the YP request
     */
    public YellowPageRequestPolicy(final YellowPageActor owner, final DistContinuousDuration handlingTime)
    {
        super("YellowPageRequestPolicy", owner, YellowPageRequest.class);
        this.handlingTime = handlingTime;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final YellowPageRequest ypRequest)
    {
        if (!isValidMessage(ypRequest))
        {
            return false;
        }
        Set<SupplyChainActor> supplierSet = ((YellowPageActor) getOwner()).getSuppliers(ypRequest.getProduct());
        if (supplierSet == null)
        {
            Logger.warn("YellowPage '{}' has no supplier map for product {}", getOwner().getName(),
                    ypRequest.getProduct().getName());
            return false;
        }
        SortedMap<Length, SupplyChainActor> suppliers =
                pruneDistance(supplierSet, ypRequest.getMaximumDistance(), ypRequest.getSender().getLocation());
        pruneNumber(suppliers, ypRequest.getMaximumNumber());
        List<SupplyChainActor> potentialSuppliers = new ArrayList<>(suppliers.values());
        YellowPageAnswer ypAnswer = new YellowPageAnswer(getOwner(), ypRequest.getSender(), ypRequest.getInternalDemandId(),
                potentialSuppliers, ypRequest);
        getOwner().sendMessage(ypAnswer, this.handlingTime.draw());
        return true;
    }

    /**
     * Prune the list of suppliers based on the maximum distance.
     * @param supplierSet the set of suppliers
     * @param maxDistance the maximum distance tgo use for pruning
     * @param location the location to compare the supplier locations with
     * @return a map of suppliers, sorted on distance
     */
    private SortedMap<Length, SupplyChainActor> pruneDistance(final Set<SupplyChainActor> supplierSet, final Length maxDistance,
            final Point3d location)
    {
        SortedMap<Length, SupplyChainActor> sortedSuppliers = new TreeMap<>();
        for (SupplyChainActor actor : sortedSuppliers.values())
        {
            Length distance = getOwner().getSimulator().getModel().calculateDistance(actor.getLocation(), location);
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
    private void pruneNumber(final SortedMap<Length, SupplyChainActor> suppliers, final int maxNumber)
    {
        int count = 0;
        Iterator<SupplyChainActor> supplierIterator = suppliers.values().iterator();
        while (supplierIterator.hasNext())
        {
            supplierIterator.next();
            if (++count > maxNumber)
            {
                supplierIterator.remove();
            }
        }
    }

}
