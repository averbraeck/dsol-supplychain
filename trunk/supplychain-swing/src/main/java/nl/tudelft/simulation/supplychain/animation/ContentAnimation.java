package nl.tudelft.simulation.supplychain.animation;

import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;

import javax.media.j3d.Bounds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.animation.interpolation.LinearInterpolation;
import nl.tudelft.simulation.language.d3.BoundingBox;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * This class implements the animation of the content of a message that is sent from one Actor to another Actor. Actually, the
 * content itself does not know when it is being sent (the SendingDevice determines this) or when it will be received (the speed
 * of the communications channel between the devices of the actors determine this). It is even worse: the content itself is
 * unaware of being sent: the Message envelope around the Content is being sent; the Content itself is just the Serializable
 * payload... Therefore, the ContentAnimation subscribes itself on the sending time and receiving time by the sending device.
 * <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ContentAnimation implements Locatable, Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the simulator on which to schedule */
    private DEVSSimulatorInterfaceUnit simulator;

    /** a helper instance for linear interpolation */
    private LinearInterpolation linearInterpolation = null;

    /** the content of the source */
    private Content content = null;

    /** the name of the url for the image */
    private String imageURLlName = null;

    /** the image renderable */
    private SingleImageRenderable imageRenderable = null;

    /** the animation delay for the content */
    private Duration delay = Duration.ZERO;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(ContentAnimation.class);

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
            this.simulator = content.getSender().getSimulator();

            // We define its location
            this.linearInterpolation = new LinearInterpolation(this.simulator.getSimulatorTime().get().si,
                    this.simulator.getSimulatorTime().get().si + delay.si, content.getSender().getLocation(),
                    content.getReceiver().getLocation());

            // We load the image
            this.imageRenderable = new SingleImageRenderable(this, this.simulator, imageURL);
            // new GISContentAnimation(this, this.simulator, imageURL);

            // We do rotate and thus not flip the image
            this.imageRenderable.setRotate(false);

            // scale for now.
            this.imageRenderable.setScale(true);

            // We schedule its destroy.
            this.simulator.scheduleEventRel(delay, this, this.imageRenderable, "destroy", null);
        }
        catch (Exception remoteException)
        {
            logger.warn("<init>", remoteException);
        }
    }

    /** {@inheritDoc} */
    @Override
    public DirectedPoint getLocation() throws RemoteException
    {
        DirectedPoint dp = this.linearInterpolation.getLocation(this.simulator.getSimulatorTime().get().si);
        return new DirectedPoint(dp.x, dp.y, 100.0);
    }

    /** {@inheritDoc} */
    @Override
    public Bounds getBounds() throws RemoteException
    {
        // determines the size on the screen (!)
        return new BoundingBox(10, 10, 2.0);
    }

    /**
     * @return Returns the delay.
     */
    public Duration getDelay()
    {
        return this.delay;
    }

    /**
     * @return Returns the content.
     */
    public Content getContent()
    {
        return this.content;
    }

    /**
     * @return Returns the name of the image URL
     */
    public String getImageURLlName()
    {
        return this.imageURLlName;
    }

    /**
     * @return Returns the image renderable
     */
    public SingleImageRenderable getImageRenderable()
    {
        return this.imageRenderable;
    }
}
