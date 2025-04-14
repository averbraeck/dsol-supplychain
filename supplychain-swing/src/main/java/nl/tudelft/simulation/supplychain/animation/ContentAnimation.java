package nl.tudelft.simulation.supplychain.animation;

import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.Point2d;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;

/**
 * This class implements the animation of the content of a message that is sent from one Actor to another Actor. Actually, the
 * content itself does not know when it is being sent (the SendingDevice determines this) or when it will be received (the speed
 * of the communications channel between the devices of the actors determine this). It is even worse: the content itself is
 * unaware of being sent: the Message envelope around the Content is being sent; the Content itself is just the Serializable
 * payload... Therefore, the ContentAnimation subscribes itself on the sending time and receiving time by the sending device.
 * <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContentAnimation implements Locatable, Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the simulator on which to schedule. */
    private SupplyChainSimulatorInterface simulator;

    /** a helper instance for linear interpolation. */
    private LinearInterpolation2d linearInterpolation = null;

    /** the content of the source. */
    private Content content = null;

    /** the name of the url for the image. */
    private String imageURLlName = null;

    /** the image renderable. */
    private SingleImageRenderable<ContentAnimation> imageRenderable = null;

    /** the animation delay for the content. */
    private Duration delay = Duration.ZERO;

    /**
     * Constructs a new animation for any type of content. This constructor will try to find a default URL of a picture to
     * display.
     * @param content the Content that should be animated
     * @param delay the time to go from sender to receiver
     */
    public ContentAnimation(final Content content, final Duration delay)
    {
        this(content, delay,
                content.getClass().getName().substring(content.getClass().getPackage().getName().length() + 1) + ".gif");
    }

    /**
     * Constructs a new animation for any type of content. This constructor will try to find a URL of a picture based on a
     * filename.
     * @param content the Content that should be animated
     * @param delay the time to go from sender to receiver
     * @param imageName the filename and extension of the picture
     */
    public ContentAnimation(final Content content, final Duration delay, final String imageName)
    {
        this(content, delay, Content.class.getResource("/nl/tudelft/simulation/supplychain/images/" + imageName));
    }

    /**
     * Constructs a new animation for any type of content, based on a URL of a picture.
     * @param content the Content that should be animated
     * @param delay the time to go from sender to receiver
     * @param imageURL the URL of the picture to display
     */
    public ContentAnimation(final Content content, final Duration delay, final URL imageURL)
    {
        try
        {
            if (imageURL == null)
            {
                throw new IllegalArgumentException("imageURL should not be null. Could not find image");
            }

            this.content = content;
            this.imageURLlName = imageURL.toString();
            this.delay = delay;

            // We set the simulator
            this.simulator = content.sender().getSimulator();

            // We define its location
            this.linearInterpolation = new LinearInterpolation2d(this.simulator.getSimulatorTime().si,
                    this.simulator.getSimulatorTime().si + delay.si, content.sender().getLocation(),
                    content.receiver().getLocation());

            // We load the image
            this.imageRenderable = new SingleImageRenderable<ContentAnimation>(this, this.simulator, imageURL);
            // new GISContentAnimation(this, this.simulator, imageURL);

            // We do rotate and thus not flip the image
            this.imageRenderable.setRotate(false);

            // scale for now.
            this.imageRenderable.setScale(true);

            // We schedule its destroy.
            this.simulator.scheduleEventRel(delay, this.imageRenderable, "destroy", new Object[] {this.simulator});
        }
        catch (Exception remoteException)
        {
            Logger.warn(remoteException, "<init>");
        }
    }

    @Override
    public Point2d getLocation() throws RemoteException
    {
        return this.linearInterpolation.getLocation(this.simulator.getSimulatorTime().si);
    }

    @Override
    public Bounds3d getBounds() throws RemoteException
    {
        // determines the size on the screen (!)
        return new Bounds3d(10, 10, 2.0);
    }

    /**
     * @return the delay.
     */
    public Duration getDelay()
    {
        return this.delay;
    }

    /**
     * @return the content.
     */
    public Content getContent()
    {
        return this.content;
    }

    /**
     * @return the name of the image URL
     */
    public String getImageURLlName()
    {
        return this.imageURLlName;
    }

    /**
     * @return the image renderable
     */
    public SingleImageRenderable getImageRenderable()
    {
        return this.imageRenderable;
    }
}
