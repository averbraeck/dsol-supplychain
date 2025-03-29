package nl.tudelft.simulation.supplychain.content;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * A message, which can be sent from a sender to a receiver. Extend this class to add further fields.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class Message extends Content
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new message.
     * @param sender Actor; the sender (necessary for a possible reply)
     * @param receiver Actor; the receiver
     */
    public Message(final Actor sender, final Actor receiver)
    {
        super(sender, receiver);
    }

}
