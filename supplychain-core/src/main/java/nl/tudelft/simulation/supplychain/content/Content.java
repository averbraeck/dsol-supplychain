package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * A message, which can be sent from a sender to a receiver. Extend this interface to add content.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface Content extends Serializable
{
    /**
     * Return the sender of the message (to allow for a reply to be sent).
     * @return the sender of the message
     */
    Actor sender();

    /**
     * Return the receiver of the message.
     * @return the receiver of the message
     */
    Actor receiver();

    /**
     * Return the timestamp of the message.
     * @return the timestamp of the message
     */
    Time timestamp();

    /**
     * Return the unique message id.
     * @return the unique message id.
     */
    long uniqueId();

}
