package nl.tudelft.simulation.supplychain.role.searching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.SearchRequest;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * SearchingRole is a base implementation of providing information about other actors in the model. Actors can register
 * themselves in the registry.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SearchingRole extends Role<SearchingRole>
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the dictionary of topic-actor combinations. */
    private Map<Topic, List<Actor>> topicDictionary = new LinkedHashMap<Topic, List<Actor>>();

    /** the dictionary of product-actor combinations. */
    private Map<Product, HashSet<Actor>> productDictionary = new LinkedHashMap<>();

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers = Set.of(SearchRequest.class);

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<SearchingRole>>> necessaryAutonomousProcesses = Set.of();

    /**
     * Create a new Search role.
     * @param owner the actor that owns the Search role
     */
    public SearchingRole(final SearchingActor owner)
    {
        super("searching", owner, new ContentReceiverDirect());
    }

    /**
     * Add a supplier to for a certain product.
     * @param product the product with a set of suppliers.
     * @param supplier a supplier for that product.
     */
    public void addSupplier(final Product product, final Actor supplier)
    {
        HashSet<Actor> supplierSet = this.productDictionary.get(product);
        if (supplierSet == null)
        {
            supplierSet = new LinkedHashSet<Actor>();
            this.productDictionary.put(product, supplierSet);
        }
        supplierSet.add(supplier);
    }

    /**
     * Remove a supplier for a certain product.
     * @param product the product.
     * @param supplier the supplier for that product to be removed.
     */
    public void removeSupplier(final Product product, final Actor supplier)
    {
        HashSet<Actor> supplierSet = this.productDictionary.get(product);
        if (supplierSet != null)
        {
            supplierSet.remove(supplier);
        }
    }

    /**
     * @param product the product for which to search for suppliers
     * @return the list of suppliers of the product (or an empty list)
     */
    public Set<Actor> getSuppliers(final Product product)
    {
        Set<Actor> supplierSet = new LinkedHashSet<>();
        if (this.productDictionary.get(product) != null)
        {
            supplierSet.addAll(this.productDictionary.get(product));
        }
        return supplierSet;
    }

    /**
     * finds actors based on the regex.
     * @param regex the name of the actor as regular expression
     * @return Actor[] the result
     */
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

    /**
     * finds an actor based on the regex.
     * @param regex the name of the actor as regular expression
     * @param topic the topic for which this actor is registered
     * @return Actor[] the result
     */
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

    /**
     * finds an actor based on the category.
     * @param topic the category for this actor
     * @return Actor[] the result
     */
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

    /**
     * registers an actor.
     * @param actor the actor
     * @param topic the category
     * @return success
     */
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


    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }


    @Override
    protected Set<Class<? extends AutonomousProcess<SearchingRole>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }

}
