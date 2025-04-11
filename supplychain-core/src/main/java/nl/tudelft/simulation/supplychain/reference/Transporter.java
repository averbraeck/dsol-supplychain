package nl.tudelft.simulation.supplychain.reference;

import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreEmpty;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;

/**
 * Transporter is a reference implementation of a TransportingActor.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Transporter extends SupplyChainActor implements TransportingActor, FinancingActor
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new Transport, give it an empty message store, and register it in the model.
     * @param id String, the unique id of the transporter
     * @param name the longer name of the transporter
     * @param model the model
     * @param location the location of the transporter
     * @param locationDescription the location description of the transporter (e.g., a city, country)
     * @param landmass the continent or island where the transporter is located
     * @throws ActorAlreadyDefinedException when the transporter was already registered in the model
     */
    public Transporter(final String id, final String name, final SupplyChainModelInterface model, final Point2d location,
            final String locationDescription, final String landmass)
    {
        super(id, name, model, new Geography(location, locationDescription, landmass), new ContentStoreEmpty());
    }

}
