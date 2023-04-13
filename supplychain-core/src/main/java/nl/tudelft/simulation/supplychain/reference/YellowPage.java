package nl.tudelft.simulation.supplychain.reference;

import java.io.Serializable;

import org.djunits.Throw;
import org.djutils.draw.point.OrientedPoint2d;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.role.yellowpage.YellowPageRole;

/**
 * Reference implementation of the YellowPage.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPage extends Actor implements Serializable
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /** The yellow page role. */
    private YellowPageRole yellowPageRole = null;

    /**
     * Create a YellowPage actor.
     * @param id String, the unique id of the customer
     * @param name String; the longer name of the customer
     * @param model SupplyChainModelInterface; the model
     * @param location OrientedPoint2d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     * @param bank Bank; the bank for the BankAccount
     * @param initialBalance Money; the initial balance for the actor
     * @param messageStore TradeMessageStoreInterface; the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    public YellowPage(final String id, final String name, final SupplyChainModelInterface model, final OrientedPoint2d location,
            final String locationDescription, final Bank bank, final Money initialBalance,
            final TradeMessageStoreInterface messageStore) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription, bank, initialBalance, messageStore);
    }

    /**
     * Return the yellow page role.
     * @return YellowPageRole; the yellow page role
     */
    public YellowPageRole getYellowPageRole()
    {
        return this.yellowPageRole;
    }

    /**
     * Set the yellow page role.
     * @param yellowPageRole YellowPageRole; the new yellow page role
     */
    public void setYellowPageRole(final YellowPageRole yellowPageRole)
    {
        Throw.whenNull(yellowPageRole, "yellowpageRole cannot be null");
        Throw.when(this.yellowPageRole != null, IllegalStateException.class, "yellowpageRole already initialized");
        addRole(yellowPageRole);
        this.yellowPageRole = yellowPageRole;
    }

}
