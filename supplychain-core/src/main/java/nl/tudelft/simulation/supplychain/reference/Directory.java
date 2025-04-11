package nl.tudelft.simulation.supplychain.reference;

import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreEmpty;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.role.searching.SearchingActor;

/**
 * Reference implementation of the Search function.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Directory extends SupplyChainActor implements SearchingActor
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /**
     * Construct a new Directory, give it an empty message store, and register it in the model.
     * @param id String, the unique id of the directory
     * @param name the longer name of the directory
     * @param model the model
     * @param location the location of the directory
     * @param locationDescription the location description of the directory (e.g., a city, country)
     * @param landmass the continent or island where the directory is located
     * @throws ActorAlreadyDefinedException when the directory was already registered in the model
     */
    public Directory(final String id, final String name, final SupplyChainModelInterface model, final Point2d location,
            final String locationDescription, final String landmass) throws ActorAlreadyDefinedException
    {
        super(id, name, model, new Geography(location, locationDescription, landmass), new ContentStoreEmpty());
    }

}
