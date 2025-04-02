package nl.tudelft.simulation.supplychain.reference;

import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.role.buying.BuyingActor;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRole;
import nl.tudelft.simulation.supplychain.role.consuming.DemandGeneratingActor;
import nl.tudelft.simulation.supplychain.role.consuming.DemandGenerationRole;

/**
 * A Customer is an actor which usually orders (pulls) products from a Distributor. <br>
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Customer extends SupplyChainActor implements BuyingActor, DemandGeneratingActor
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** The role to buy products. */
    private BuyingRole buyingRole;

    /** The role to generate demand. */
    private DemandGenerationRole demandGenerationRole;

    /**
     * @param id String, the unique id of the customer
     * @param name the longer name of the customer
     * @param model the model
     * @param location the location of the actor
     * @param locationDescription the location description of the actor (e.g., a city, country)
     * @param messageStore the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Customer(final String id, final String name, final SupplyChainModelInterface model, final OrientedPoint2d location,
            final String locationDescription, final TradeMessageStoreInterface messageStore) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription, messageStore);
    }

    @Override
    public BuyingRole getBuyingRole()
    {
        return this.buyingRole;
    }

    @Override
    public void setBuyingRole(final BuyingRole buyingRole)
    {
        Throw.whenNull(buyingRole, "buyingRole cannot be null");
        Throw.when(this.buyingRole != null, IllegalStateException.class, "buyingRole already initialized");
        addRole(buyingRole);
        this.buyingRole = buyingRole;
    }

    /**
     * Return the demand generation role.
     * @return the demand generation role
     */
    public DemandGenerationRole getDemandGenerationRole()
    {
        return this.demandGenerationRole;
    }

    /**
     * Set the demand generation role.
     * @param demandGenerationRole the new demand generation role
     */
    public void setDemandGenerationRole(final DemandGenerationRole demandGenerationRole)
    {
        Throw.whenNull(demandGenerationRole, "demandGenerationRole cannot be null");
        Throw.when(this.demandGenerationRole != null, IllegalStateException.class, "demandGenerationRole already initialized");
        addRole(demandGenerationRole);
        this.demandGenerationRole = demandGenerationRole;
    }

    @Override
    public void checkNecessaryRoles()
    {
        Throw.whenNull(this.buyingRole, "BuyingRole not initialized for Customer: " + this.getName());
        Throw.whenNull(this.demandGenerationRole, "DemandGenerationRole not initialized for Customer: " + this.getName());
    }
}
