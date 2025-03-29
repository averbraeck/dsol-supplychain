package nl.tudelft.supplychain.actor;

import org.djutils.draw.point.OrientedPoint2d;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStore;

/**
 * TestActor reference implementaion to use in unit tests.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TestActor extends SupplyChainActor
{
    private static final long serialVersionUID = 1L;

    public TestActor(final String id, final String name, final SupplyChainModelInterface model, final OrientedPoint2d location,
            final String locationDescription) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription, new TradeMessageStore());
    }

    /** {@inheritDoc} */
    @Override
    public void checkNecessaryRoles()
    {
        // nothing to check, no roles
    }
}
