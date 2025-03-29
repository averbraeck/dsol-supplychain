package nl.tudelft.supplychain.message;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Message;

/**
 * TestMessageFields is a test message class with several fields.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TestMessageFields extends Message
{
    /** */
    private static final long serialVersionUID = 1L;

    /** Duration. */
    private final Duration duration;

    /** Name. */
    private final String name;

    /** YesNo. */
    private final boolean yesno;

    /**
     * @param sender Actor
     * @param receiver Actor
     * @param duration Duration
     * @param name String
     * @param yesno boolean
     */
    public TestMessageFields(final Actor sender, final Actor receiver, final Duration duration, final String name,
            final boolean yesno)
    {
        super(sender, receiver);
        this.duration = duration;
        this.name = name;
        this.yesno = yesno;
    }

    /**
     * @return duration
     */
    public Duration getDuration()
    {
        return this.duration;
    }

    /**
     * @return name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return yesno
     */
    public boolean isYesno()
    {
        return this.yesno;
    }

}
