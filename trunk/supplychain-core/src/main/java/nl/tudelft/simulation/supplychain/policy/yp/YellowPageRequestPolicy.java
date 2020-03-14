package nl.tudelft.simulation.supplychain.policy.yp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;
import nl.tudelft.simulation.supplychain.policy.SupplyChainHandler;
import nl.tudelft.simulation.supplychain.yellowpage.SupplyChainYellowPage;
import nl.tudelft.simulation.unit.dist.DistConstantDuration;

/**
 * The YellowPageRequestHandler implements the business logic for a yellow page actor who receives a YellowPageRequest and has
 * to look up supply chain actors within the boundaries of the request For the moment, these are max number, max distance, and
 * product. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class YellowPageRequestPolicy extends SupplyChainHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the handling time of the handler in simulation time units */
    private DistContinuousDuration handlingTime;

    /**
     * Constructs a new YellowPageRequestHandler.
     * @param owner the owner of the handler
     * @param handlingTime the distribution of the time to react on the YP request
     */
    public YellowPageRequestPolicy(final SupplyChainYellowPage owner, final DistContinuousDuration handlingTime)
    {
        super(owner);
        this.handlingTime = handlingTime;
    }

    /**
     * Constructs a new YellowPageRequestHandler.
     * @param owner the owner of the handler
     * @param handlingTime the constant time to react on the YP request
     */
    public YellowPageRequestPolicy(final SupplyChainYellowPage owner, final Duration handlingTime)
    {
        this(owner, new DistConstantDuration(handlingTime));
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (!isValidContent(content))
        {
            return false;
        }
        YellowPageRequest ypRequest = (YellowPageRequest) content;
        Set<SupplyChainActor> supplierSet = ((SupplyChainYellowPage) getOwner()).getSuppliers(ypRequest.getProduct());
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
        YellowPageAnswer ypAnswer = new YellowPageAnswer(getOwner(), ypRequest.getSender(), ypRequest.getInternalDemandID(),
                potentialSuppliers, ypRequest);
        getOwner().sendContent(ypAnswer, this.handlingTime.draw());
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
            final DirectedPoint location)
    {
        SortedMap<Length, SupplyChainActor> sortedSuppliers = new TreeMap<>();
        Iterator<SupplyChainActor> i = supplierSet.iterator();
        while (i.hasNext())
        {
            SupplyChainActor actor = i.next();
            // TODO: get proper locations; assume km for now...
            Length distance = new Length(actor.getLocation().distance(location), LengthUnit.KILOMETER);
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

    /** {@inheritDoc} */
    @Override
    public Class<? extends Content> getContentClass()
    {
        return YellowPageRequest.class;
    }

}
