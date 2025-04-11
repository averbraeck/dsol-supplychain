package nl.tudelft.simulation.supplychain.reference;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.role.manufacturing.ManufacturingActor;

/**
 * Reference implementation for a manufacturer.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Manufacturer extends DistributionCenter implements ManufacturingActor
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * @param id String, the unique id of the supplier
     * @param name the longer name of the supplier
     * @param model the model
     * @param geography the geography of the actor
     * @param contentStore the content store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    public Manufacturer(final String id, final String name, final SupplyChainModelInterface model, final Geography geography,
            final ContentStoreInterface contentStore) throws ActorAlreadyDefinedException
    {
        super(id, name, model, geography, contentStore);
    }

}
