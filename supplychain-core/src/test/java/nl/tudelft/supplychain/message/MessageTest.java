package nl.tudelft.supplychain.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.point.OrientedPoint2d;
import org.junit.Test;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.content.Message;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulator;
import nl.tudelft.supplychain.actor.TestActor;
import nl.tudelft.supplychain.actor.TestModel;

/**
 * MessageTest.java.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MessageTest
{
    /**
     * Test the Message class.
     * @throws ActorAlreadyDefinedException on error
     */
    @Test
    public void messageTest() throws ActorAlreadyDefinedException
    {
        SupplyChainSimulator simulator = new SupplyChainSimulator("sim", Time.ZERO);
        TestModel model = new TestModel(simulator);
        SingleReplication<Duration> replication =
                new SingleReplication<Duration>("rep", Duration.ZERO, Duration.ZERO, new Duration(1, DurationUnit.DAY));
        simulator.initialize(model, replication);
        TestActor actor1 = new TestActor("TA1", "TestActor1", model, new OrientedPoint2d(10, 10), "Dallas, TX");
        TestActor actor2 = new TestActor("TA2", "TestActor2", model, new OrientedPoint2d(20, 20), "Austin, TX");
        TestMessage message = new TestMessage(actor1, actor2);
        assertEquals(actor1, message.getSender());
        assertEquals(actor2, message.getReceiver());
        assertTrue(message.getUniqueId() > 0);
        assertTrue(message.getTimestamp().si == 0.0);

        TestMessage message12 = new TestMessage(actor1, actor2);
        TestMessage message21 = new TestMessage(actor2, actor1);
        TestMessage message11 = new TestMessage(actor1, actor1);
        assertEquals(message, message);
        assertNotEquals(message, message12);
        assertNotEquals(message, null);
        assertNotEquals(message, "abc");
        assertNotEquals(message, message11);
        assertNotEquals(message, message21);
        assertNotEquals(message.hashCode(), message12.hashCode());
    }

    /** Test message. */
    public static class TestMessage extends Message
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * @param sender Actor
         * @param receiver Actor
         */
        public TestMessage(final Actor sender, final Actor receiver)
        {
            super(sender, receiver);
        }
    }

}
