package nl.tudelft.simulation.supplychain.reference;

import org.djutils.draw.point.DirectedPoint2d;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.role.consuming.ConsumingActor;
import nl.tudelft.simulation.supplychain.role.consuming.ConsumingRole;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;

/**
 * A Customer is an actor which usually orders (pulls) products from a Distributor. <br>
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Customer extends SupplyChainActor implements PurchasingActor, ConsumingActor, FinancingActor
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** The role to buy products. */
    private PurchasingRole purchasingRole;

    /** The role to generate demand. */
    private ConsumingRole demandGenerationRole;

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
    public Customer(final String id, final String name, final SupplyChainModelInterface model, final DirectedPoint2d location,
            final String locationDescription, final ContentStoreInterface messageStore) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription, messageStore);
    }

    @Override
    public PurchasingRole getPurchasingRole()
    {
        return this.purchasingRole;
    }

    @Override
    public void setPurchasingRole(final PurchasingRole purchasingRole)
    {
        Throw.whenNull(purchasingRole, "purchasingRole cannot be null");
        Throw.when(this.purchasingRole != null, IllegalStateException.class, "purchasingRole already initialized");
        addRole(purchasingRole);
        this.purchasingRole = purchasingRole;
    }

    /**
     * Return the demand generation role.
     * @return the demand generation role
     */
    public ConsumingRole getDemandGenerationRole()
    {
        return this.demandGenerationRole;
    }

    /**
     * Set the demand generation role.
     * @param consumingRole the new demand generation role
     */
    public void setDemandGenerationRole(final ConsumingRole consumingRole)
    {
        Throw.whenNull(consumingRole, "consumingRole cannot be null");
        Throw.when(this.demandGenerationRole != null, IllegalStateException.class, "consumingRole already initialized");
        addRole(consumingRole);
        this.demandGenerationRole = consumingRole;
    }
    
    
}
