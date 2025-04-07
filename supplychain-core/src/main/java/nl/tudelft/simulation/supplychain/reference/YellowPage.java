package nl.tudelft.simulation.supplychain.reference;

import java.io.Serializable;

import org.djutils.draw.point.DirectedPoint2d;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.banking.BankingActor;
import nl.tudelft.simulation.supplychain.role.searching.SearchingRole;

/**
 * Reference implementation of the Search.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPage extends SupplyChainActor implements Serializable
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /** The search role. */
    private SearchingRole searchingRole = null;

    /**
     * Create a Search actor.
     * @param id String, the unique id of the customer
     * @param name the longer name of the customer
     * @param model the model
     * @param location the location of the actor
     * @param locationDescription the location description of the actor (e.g., a city, country)
     * @param bank the bank for the BankAccount
     * @param initialBalance the initial balance for the actor
     * @param messageStore the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    public YellowPage(final String id, final String name, final SupplyChainModelInterface model, final DirectedPoint2d location,
            final String locationDescription, final BankingActor bank, final Money initialBalance,
            final ContentStoreInterface messageStore) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription, messageStore);
    }

    /**
     * Return the search role.
     * @return the search role
     */
    public SearchingRole getSearchingRole()
    {
        return this.searchingRole;
    }

    /**
     * Set the search role.
     * @param yellowPageRole the new search role
     */
    public void setSearchingRole(final SearchingRole yellowPageRole)
    {
        Throw.whenNull(yellowPageRole, "yellowpageRole cannot be null");
        Throw.when(this.searchingRole != null, IllegalStateException.class, "yellowpageRole already initialized");
        addRole(yellowPageRole);
        this.searchingRole = yellowPageRole;
    }

}
