package nl.tudelft.supplychain.actor;

import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreFull;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;

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

    public TestActor(final String id, final String name, final SupplyChainModelInterface model, final Point2d location,
            final String locationDescription)
    {
        super(id, name, model, new Geography(location, locationDescription, "Europe"), new ContentStoreFull());
    }

}
