package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;
import nl.tudelft.simulation.supplychain.product.Product;

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
public class YellowPageRequestHandler extends SupplyChainHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the handling time of the handler in simulation time units */
    private DistContinuous handlingTime;

    /** the dictionary of product-actor combinations */
    private Map<Product, HashSet<SupplyChainActor>> dictionary = new HashMap<Product, HashSet<SupplyChainActor>>();

    /**
     * Constructs a new YellowPageRequestHandler.
     * @param owner the owner of the handler
     * @param handlingTime the distribution of the time to react on the YP request
     */
    public YellowPageRequestHandler(final SupplyChainActor owner, final DistContinuous handlingTime)
    {
        super(owner);
        this.handlingTime = handlingTime;
    }

    /**
     * Constructs a new YellowPageRequestHandler.
     * @param owner the owner of the handler
     * @param handlingTime the constant time to react on the YP request
     */
    public YellowPageRequestHandler(final SupplyChainActor owner, final double handlingTime)
    {
        this(owner, new DistConstant(new Java2Random(), handlingTime));
    }

    /**
     * Add a supplier to for a certain product
     * @param product the product with a set of suppliers.
     * @param supplier a supplier for that product.
     */
    public void addSupplier(final Product product, final SupplyChainActor supplier)
    {
        HashSet<SupplyChainActor> supplierSet = this.dictionary.get(product);
        if (supplierSet == null)
        {
            supplierSet = new HashSet<SupplyChainActor>();
            this.dictionary.put(product, supplierSet);
        }
        supplierSet.add(supplier);
    }

    /**
     * Remove a supplier for a certain product
     * @param product the product.
     * @param supplier the supplier for that product to be removed.
     */
    public void removeSupplier(final Product product, final SupplyChainActor supplier)
    {
        HashSet<SupplyChainActor> supplierSet = this.dictionary.get(product);
        if (supplierSet != null)
        {
            supplierSet.remove(supplier);
        }
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface#handleContent(java.io.Serializable)
     */
    public boolean handleContent(final Serializable content)
    {
        YellowPageRequest ypRequest = (YellowPageRequest) checkContent(content);
        if (!isValidContent(ypRequest))
        {
            return false;
        }
        HashSet<SupplyChainActor> supplierSet = this.dictionary.get(ypRequest.getProduct());
        SortedMap<Double, SupplyChainActor> suppliers =
                pruneDistance(supplierSet, ypRequest.getMaximumDistance(), ypRequest.getSender().getLocation());
        pruneNumber(suppliers, ypRequest.getMaximumNumber());
        SupplyChainActor[] potentialSuppliers = (SupplyChainActor[]) suppliers.values().toArray();
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
    private SortedMap<Double, SupplyChainActor> pruneDistance(final HashSet<SupplyChainActor> supplierSet,
            final double maxDistance, final DirectedPoint location)
    {
        SortedMap<Double, SupplyChainActor> sortedSuppliers = new TreeMap<Double, SupplyChainActor>();
        Iterator<SupplyChainActor> i = supplierSet.iterator();
        while (i.hasNext())
        {
            SupplyChainActor actor = i.next();
            double distance = actor.getLocation().distance(location);
            if (distance < maxDistance)
            {
                sortedSuppliers.put(new Double(distance), actor);
            }
        }
        return sortedSuppliers;
    }

    /**
     * Prune the list of suppliers based on the number.
     * @param suppliers the map of suppliers (sorted on distance)
     * @param maxNumber the maximum number to leave
     */
    private void pruneNumber(final SortedMap suppliers, final int maxNumber)
    {
        int count = 0;
        Iterator i = suppliers.values().iterator();
        while (i.hasNext())
        {
            i.next();
            if (++count > maxNumber)
            {
                i.remove();
            }
        }
    }

    /**
     * @see nl.tudelft.simulation.supplychain.handlers.SupplyChainHandler#checkContentClass(java.io.Serializable)
     */
    protected boolean checkContentClass(final Serializable content)
    {
        return (content instanceof YellowPageRequest);
    }
}
