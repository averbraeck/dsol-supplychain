package nl.tudelft.supplychain.actor;

import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.point.Point;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModel;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;

/**
 * TestModel to be used in unit tests.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TestModel extends SupplyChainModel
{
    private static final long serialVersionUID = 1L;

    public TestModel(final SupplyChainSimulatorInterface simulator)
    {
        super(simulator);
    }

    @Override
    public Length calculateDistance(final Point<?> loc1, final Point<?> loc2)
    {
        double dx = loc2.getX() - loc1.getX();
        double dy = loc2.getY() - loc1.getY();
        return Length.instantiateSI(Math.sqrt(dx * dx + dy * dy));
    }

    @Override
    public void constructModel() throws SimRuntimeException
    {
        //
    }

}
