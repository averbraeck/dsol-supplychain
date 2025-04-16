package nl.tudelft.simulation.supplychain.demo.reference;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.reference.Directory;
import nl.tudelft.simulation.supplychain.role.searching.SearchingRole;
import nl.tudelft.simulation.supplychain.role.searching.handler.SearchRequestHandler;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * DemoDirectory.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemoDirectory extends Directory
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     */
    public DemoDirectory(final String id, final SupplyChainModelInterface model, final Point2d location)
    {
        super(id, id, model, location, id, "X");

        setSearchingRole(new SearchingRole(this));
        new SearchRequestHandler(getSearchingRole(), new DistConstantDuration(new Duration(10.0, DurationUnit.MINUTE)));

        // ANIMATION

        if (getSimulator() instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable<>(this, getSimulator(),
                        DemoDirectory.class.getResource("/nl/tudelft/simulation/supplychain/images/ActorSearch.gif"));
            }
            catch (RemoteException | NamingException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public Bounds2d getBounds()
    {
        return new Bounds2d(25.0, 25.0);
    }

}
