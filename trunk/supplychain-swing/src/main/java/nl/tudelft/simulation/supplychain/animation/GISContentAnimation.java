package nl.tudelft.simulation.supplychain.animation;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.D2.Renderable2DInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.animation.D2.AnimationPanel;
import nl.tudelft.simulation.language.d3.DirectedPoint;

/**
 * The GISContentAnimation is useful in combination with a GIS based map as a background image. Based on the zoom level of an
 * animation panel, choices can be made what to show, or what not. It is comparable with showing layers when using a GIS map.
 * <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class GISContentAnimation extends SingleImageRenderable
{
    /**
     * constructs a new GISContentAnimation
     * @param locatable the locatable
     * @param simulator the simulator
     * @param imageURL the image url
     * @throws NamingException
     * @throws RemoteException
     */
    public GISContentAnimation(final Locatable locatable, final SimulatorInterface.TimeDoubleUnit simulator, final URL imageURL)
            throws RemoteException, NamingException
    {
        super(locatable, simulator, imageURL);
    }

    /**
     * constructs a new GISContentAnimation
     * @param staticLocation the static location
     * @param size the size
     * @param simulator the simulator
     * @param image the image
     * @throws NamingException
     * @throws RemoteException
     */
    public GISContentAnimation(final Point2D staticLocation, final Dimension size,
            final SimulatorInterface.TimeDoubleUnit simulator, final URL image) throws RemoteException, NamingException
    {
        super(staticLocation, size, simulator, image);
    }

    /**
     * constructs a new GISContentAnimation
     * @param staticLocation the static location
     * @param size the size of the image
     * @param simulator the simulator
     * @param image the image
     * @throws NamingException
     * @throws RemoteException
     */
    public GISContentAnimation(final DirectedPoint staticLocation, final Dimension size,
            final SimulatorInterface.TimeDoubleUnit simulator, final URL image) throws RemoteException, NamingException
    {
        super(staticLocation, size, simulator, image);
    }

    /** {@inheritDoc} */
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
}
