package nl.tudelft.simulation.messaging;

import java.io.Serializable;

import nl.tudelft.simulation.actor.ActorInterface;

/**
 * A message, which can be sent from a sender to a receiver with some content. The Message is actually the 'envelope' around the
 * content of the message. Messages are exchanged between devices. The Serializable content that is part of the message, needs
 * to be handled by separate, domain specific, content handlers. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Message implements Serializable, Cloneable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** sender of the message */
    private ActorInterface sender = null;

    /** the receiver of a message */
    private ActorInterface receiver = null;

    /** the timestamp of a message */
    private double timestamp = Double.NaN;

    /** the priority of a message */
    private int priority = -1;

    /** the body of a message */
    private Serializable body = null;

    /** the id */
    private long id = -1;

    /**
     * Constructs a new message.
     * @param sender the sender
     * @param receiver the receiver
     * @param timestamp the timestamp
     * @param priority the priority
     * @param body the body
     * @param id the id
     */
    public Message(final ActorInterface sender, final ActorInterface receiver, final double timestamp, final int priority,
            final Serializable body, final long id)
    {
        this(sender, receiver, body);
        this.timestamp = timestamp;
        this.priority = priority;
        this.id = id;
    }

    /**
     * constructs a new Message
     * @param sender the sender
     * @param receiver the receiver
     * @param body the body
     */
    public Message(final ActorInterface sender, final ActorInterface receiver, final Serializable body)
    {
        super();
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
    }

    /**
     * @return the sender of the message
     */
    public ActorInterface getSender()
    {
        return this.sender;
    }

    /**
     * @return the receiver or the group of receivers of the message
     */
    public ActorInterface getReceiver()
    {
        return this.receiver;
    }

    /**
     * @return the timestamp of the message
     */
    public double getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * @return the message priority
     */
    public int getPriority()
    {
        return this.priority;
    }

    /**
     * @return the body of the message
     */
    public Serializable getBody()
    {
        return this.body;
    }

    /**
     * @return Returns the id.
     */
    public long getId()
    {
        return this.id;
    }

    /**
     * sets the id of a message
     * @param id The id to set.
     */
    public void setId(final long id)
    {
        this.id = id;
    }

    /**
     * sets the body of a message
     * @param body The body to set.
     */
    public void setBody(final Serializable body)
    {
        this.body = body;
    }

    /**
     * sets the priority of the message
     * @param priority The priority to set.
     */
    public void setPriority(final int priority)
    {
        this.priority = priority;
    }

    /**
     * sets the receiver of the message
     * @param receiver The receiver to set.
     */
    public void setReceiver(final ActorInterface receiver)
    {
        this.receiver = receiver;
    }

    /**
     * sets the timestamp
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(final double timestamp)
    {
        this.timestamp = timestamp;
    }
}
