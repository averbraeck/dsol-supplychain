package nl.tudelft.simulation.supplychain.actor;

/**
 * The SupplyChainRole that is aware of the SupplyChainActor rather than the Actor.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SupplyChainRole extends Role
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /**
     * Create a SupplyChainRole that is aware of the SupplyChainActor rather than the Actor.
     * @param owner SupplyChainActorInterface; the owner of this role
     */
    public SupplyChainRole(final SupplyChainActorInterface owner)
    {
        super(owner);
    }

    /** {@inheritDoc} */
    @Override
    public SupplyChainActorInterface getOwner()
    {
        return (SupplyChainActorInterface) super.getOwner();
    }

}
