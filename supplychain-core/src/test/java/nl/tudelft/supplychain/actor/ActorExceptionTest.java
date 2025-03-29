package nl.tudelft.supplychain.actor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.ActorNotFoundException;

/**
 * ActorExceptionTest tests the ActorNotFoundException and ActorAlreadyDefinedException classes.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ActorExceptionTest
{

    /**
     * Test ActorNotFoundException.
     */
    @Test
    public void testActorNotFoundException()
    {
        Exception e1 = new ActorNotFoundException("ACTOR");
        assertTrue(e1.getMessage().contains("ACTOR"));
        assertTrue(e1.getMessage().contains("Could not find"));
        assertEquals(null, e1.getCause());
        assertEquals("testActorNotFoundException", e1.getStackTrace()[0].getMethodName());
    }

    /**
     * Test ActorAlreadyDefinedException.
     */
    @Test
    public void testActorAlreadyDefinedException()
    {
        Exception e1 = new ActorAlreadyDefinedException("ACTOR");
        assertTrue(e1.getMessage().contains("ACTOR"));
        assertTrue(e1.getMessage().contains("has already been defined"));
        assertEquals(null, e1.getCause());
        assertEquals("testActorAlreadyDefinedException", e1.getStackTrace()[0].getMethodName());
    }

}
