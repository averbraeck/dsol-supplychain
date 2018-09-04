package nl.tudelft.simulation.content;

import nl.tudelft.simulation.actor.ActorInterface;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * An abstract definition of a handler, providing a default stream and a constructor method. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class AbstractHandler implements HandlerInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the owner of this handler */
    private final ActorInterface owner;

    /** the default stream to use for the time delays */
    private StreamInterface stream = null;

    /**
     * constructs a new Handler
     * @param owner the owner of this handler
     */
    public AbstractHandler(final ActorInterface owner)
    {
        super();
        this.owner = owner;
        this.stream = owner.getSimulator().getReplication().getStream("default");
    }

    /**
     * @return Returns the default stream.
     */
    public StreamInterface getStream()
    {
        return this.stream;
    }

    /**
     * @return owner
     */
    public ActorInterface getOwner()
    {
        return this.owner;
    }

}
