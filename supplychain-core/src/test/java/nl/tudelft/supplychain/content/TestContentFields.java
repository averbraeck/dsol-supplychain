package nl.tudelft.supplychain.content;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Content;

/**
 * TestMessageFields is a test message class with several fields.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender sender
 * @param receiver receiver
 * @param timestamp timestamp
 * @param uniqueId unique id
 * @param duration duration
 * @param name name
 * @param yesno true or false
 */
public record TestContentFields(Actor sender, Actor receiver, Time timestamp, long uniqueId, Duration duration, String name,
        boolean yesno) implements Content
{
    public TestContentFields(final Actor sender, final Actor receiver, final Duration duration, final String name,
            final boolean yesno)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(), duration, name, yesno);
    }
}
