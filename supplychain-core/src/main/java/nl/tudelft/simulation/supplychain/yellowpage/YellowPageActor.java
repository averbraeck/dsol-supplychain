package nl.tudelft.simulation.supplychain.yellowpage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.finance.NoBank;
import nl.tudelft.simulation.supplychain.message.handler.DirectMessageHandler;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * YellowPageActor is a base implementation of an organization that provides information about other actors in the model.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPageActor extends SupplyChainActor implements YellowPageInterface
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the dictionary of topic-actor combinations. */
    private Map<Topic, List<Actor>> topicDictionary = new LinkedHashMap<Topic, List<Actor>>();

    /** the dictionary of product-actor combinations. */
    private Map<Product, HashSet<SupplyChainActor>> productDictionary = new LinkedHashMap<>();

    /**
     * Create a new YellowPage organization.
     * @param name String;
     * @param simulator SCSimulatorInterface;
     * @param position OrientedPoint3d;
     * @param messageStore TradeMessageStoreInterface;
     */
    public YellowPageActor(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final TradeMessageStoreInterface messageStore)
    {
        super(name, new DirectMessageHandler(), simulator, position, name, new NoBank(simulator), new Money(0.0, MoneyUnit.USD),
                messageStore);
    }

    /**
     * Add a supplier to for a certain product.
     * @param product Product; the product with a set of suppliers.
     * @param supplier a supplier for that product.
     */
    public void addSupplier(final Product product, final SupplyChainActor supplier)
    {
        HashSet<SupplyChainActor> supplierSet = this.productDictionary.get(product);
        if (supplierSet == null)
        {
            supplierSet = new LinkedHashSet<SupplyChainActor>();
            this.productDictionary.put(product, supplierSet);
        }
        supplierSet.add(supplier);
    }

    /**
     * Remove a supplier for a certain product.
     * @param product Product; the product.
     * @param supplier the supplier for that product to be removed.
     */
    public void removeSupplier(final Product product, final SupplyChainActor supplier)
    {
        HashSet<SupplyChainActor> supplierSet = this.productDictionary.get(product);
        if (supplierSet != null)
        {
            supplierSet.remove(supplier);
        }
    }

    /**
     * @param product Product; the product for which to search for suppliers
     * @return the list of suppliers of the product (or an empty list)
     */
    public Set<SupplyChainActor> getSuppliers(final Product product)
    {
        Set<SupplyChainActor> supplierSet = new LinkedHashSet<>();
        if (this.productDictionary.get(product) != null)
        {
            supplierSet.addAll(this.productDictionary.get(product));
        }
        return supplierSet;
    }

    /** {@inheritDoc} */
    @Override
    public List<Actor> findActor(final String regex)
    {
        List<Actor> result = new ArrayList<Actor>();
        for (List<Actor> actors : this.topicDictionary.values())
        {
            for (Actor actor : actors)
            {
                if (actor.getName().matches(regex))
                {
                    result.add(actor);
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Actor> findActor(final String regex, final Topic topic)
    {
        List<Actor> result = new ArrayList<Actor>();
        for (Topic cat : this.topicDictionary.keySet())
        {
            if (Topic.specializationOf(topic, cat))
            {
                List<Actor> actors = this.topicDictionary.get(cat);
                for (Actor actor : actors)
                {
                    if (actor.getName().matches(regex))
                    {
                        result.add(actor);
                    }
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Actor> findActor(final Topic topic)
    {
        List<Actor> actors = new ArrayList<Actor>();
        for (Topic t : this.topicDictionary.keySet())
        {
            if (Topic.specializationOf(topic, t))
            {
                actors = this.topicDictionary.get(t);
            }
        }
        return actors;
    }

    /** {@inheritDoc} */
    @Override
    public boolean register(final Actor actor, final Topic topic)
    {
        List<Actor> actors = this.topicDictionary.get(topic);
        if (actors == null)
        {
            actors = new ArrayList<Actor>();
            this.topicDictionary.put(topic, actors);
        }
        return actors.add(actor);
    }

}
