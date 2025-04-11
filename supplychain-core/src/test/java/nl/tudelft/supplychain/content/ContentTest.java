package nl.tudelft.supplychain.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.point.Point2d;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulator;
import nl.tudelft.supplychain.actor.TestActor;
import nl.tudelft.supplychain.actor.TestModel;

/**
 * ContentTest.java.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContentTest
{
    /**
     * Test the Content class.
     * @throws ActorAlreadyDefinedException on error
     */
    @Test
    public void contentTest() throws ActorAlreadyDefinedException
    {
        SupplyChainSimulator simulator = new SupplyChainSimulator("sim", Time.ZERO);
        TestModel model = new TestModel(simulator);
        SingleReplication<Duration> replication =
                new SingleReplication<Duration>("rep", Duration.ZERO, Duration.ZERO, new Duration(1, DurationUnit.DAY));
        simulator.initialize(model, replication);
        TestActor actor1 = new TestActor("TA1", "TestActor1", model, new Point2d(10, 10, 0), "Dallas, TX");
        TestActor actor2 = new TestActor("TA2", "TestActor2", model, new Point2d(20, 20, 0), "Austin, TX");
        TestContent content = new TestContent(actor1, actor2);
        assertEquals(actor1, content.sender());
        assertEquals(actor2, content.receiver());
        assertTrue(content.uniqueId() > 0);
        assertTrue(content.timestamp().si == 0.0);

        TestContent content12 = new TestContent(actor1, actor2);
        TestContent content21 = new TestContent(actor2, actor1);
        TestContent content11 = new TestContent(actor1, actor1);
        assertEquals(content, content);
        assertNotEquals(content, content12);
        assertNotEquals(content, null);
        assertNotEquals(content, "abc");
        assertNotEquals(content, content11);
        assertNotEquals(content, content21);
        assertNotEquals(content.hashCode(), content12.hashCode());
    }

    /**
     * Test content.
     * @param sender sender
     * @param receiver receiver
     * @param timestamp timestamp
     * @param uniqueId unique id
     */
    public record TestContent(Actor sender, Actor receiver, Time timestamp, long uniqueId) implements Content
    {
        public TestContent(final Actor sender, final Actor receiver)
        {
            this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId());
        }
    }
}
