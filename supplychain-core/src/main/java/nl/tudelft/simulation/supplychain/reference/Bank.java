package nl.tudelft.simulation.supplychain.reference;

import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreEmpty;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.role.banking.BankingActor;

/**
 * Bank is a reference implementation of a BankingActor..
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Bank extends SupplyChainActor implements BankingActor
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new Bank, give it an empty message store, and register it in the model.
     * @param id String, the unique id of the bank
     * @param name the longer name of the bank
     * @param model the model
     * @param location the location of the bank
     * @param locationDescription the location description of the bank (e.g., a city, country)
     * @param landmass the continent or island where the bank is located
     * @throws ActorAlreadyDefinedException when the bank was already registered in the model
     */
    public Bank(final String id, final String name, final SupplyChainModelInterface model, final Point2d location,
            final String locationDescription, final String landmass)
    {
        super(id, name, model, new Geography(location, locationDescription, landmass), new ContentStoreEmpty());
    }

}
