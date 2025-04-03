package nl.tudelft.simulation.supplychain.animation;

import java.net.URL;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point3d;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;

/**
 * The GISContentAnimation is useful in combination with a GIS based map as a background image. Based on the zoom level of an
 * animation panel, choices can be made what to show, or what not. It is comparable with showing layers when using a GIS map.
 * <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class GISContentAnimation extends SingleImageRenderable
{
    /**
     * constructs a new GISContentAnimation.
     * @param locatable the locatable
     * @param simulator the simulator
     * @param imageURL the image url
     * @throws NamingException
     * @throws RemoteException
     */
    public GISContentAnimation(final Locatable locatable, final SupplyChainSimulatorInterface simulator, final URL imageURL)
            throws RemoteException, NamingException
    {
        super(locatable, simulator, imageURL);
    }

    /**
     * constructs a new GISContentAnimation.
     * @param staticLocation the static location
     * @param size the size
     * @param simulator the simulator
     * @param image the image
     * @throws NamingException
     * @throws RemoteException
     */
    public GISContentAnimation(final Point3d staticLocation, final Bounds3d size, final SupplyChainSimulatorInterface simulator,
            final URL image) throws RemoteException, NamingException
    {
        super(staticLocation, size, simulator, image);
    }

    /**
     * constructs a new GISContentAnimation.
     * @param staticLocation the static location
     * @param size the size of the image
     * @param simulator the simulator
     * @param image the image
     * @throws NamingException
     * @throws RemoteException
     */
    public GISContentAnimation(final OrientedPoint3d staticLocation, final Bounds3d size,
            final SupplyChainSimulatorInterface simulator, final URL image) throws RemoteException, NamingException
    {
        super(staticLocation, size, simulator, image);
    }

    /*- * {@inheritDoc} */
    /*- TODO: see whether and how to implement this in DSOL 4
    @Override
    public synchronized void paint(final Graphics2D graphics2d, final ImageObserver observer)
    {
        AnimationPanel animationPanel = (AnimationPanel) observer;
        double _scale = Renderable2DInterface.Util.getScale(animationPanel.getExtent(), animationPanel.getSize());
    
        // we only draw our images if their is a certain zoom level
        if (_scale < 0.05063)
        {
            super.setScale(true);
        }
    }
    */
}
