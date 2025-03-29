package nl.tudelft.supplychain.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.TimeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.point.OrientedPoint2d;
import org.junit.Test;

import com.google.gson.Gson;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.content.Message;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulator;
import nl.tudelft.simulation.supplychain.json.JsonMessageFactory;
import nl.tudelft.supplychain.actor.TestActor;
import nl.tudelft.supplychain.actor.TestModel;
import nl.tudelft.supplychain.message.MessageTest.TestMessage;
import nl.tudelft.supplychain.message.TestMessageFields;

/**
 * JsomMessageFactoryTest tests the JsonMessageFactory.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class JsonMessageFactoryTest
{

    /**
     * Test MessageAdapter.
     * @throws ActorAlreadyDefinedException on error
     */
    @Test
    public void testMessageAdapter() throws ActorAlreadyDefinedException
    {
        SupplyChainSimulator simulator = new SupplyChainSimulator("sim", new Time(1.0, TimeUnit.BASE_HOUR));
        TestModel model = new TestModel(simulator);
        SingleReplication<Duration> replication =
                new SingleReplication<Duration>("rep", Duration.ZERO, Duration.ZERO, new Duration(1, DurationUnit.DAY));
        simulator.initialize(model, replication);
        Gson gson = JsonMessageFactory.instance(model);
        assertNotNull(gson);
        assertEquals(gson, JsonMessageFactory.instance(model));
        Actor actor1 = new TestActor("TA1", "TestActor 1", model, new OrientedPoint2d(10, 10), "Dallas, TX");
        Actor actor2 = new TestActor("TA2", "TestActor 2", model, new OrientedPoint2d(20, 20), "Austin, TX");
        TestMessage testMessage = new TestMessage(actor1, actor2);
        String ms = gson.toJson(testMessage);
        assertNotNull(ms);
        Message m2 = gson.fromJson(ms, Message.class);
        assertTrue(m2.getClass().toString().contains("MessageTest$TestMessage"));
        assertEquals(testMessage.getSender(), m2.getSender());
        assertEquals(testMessage.getReceiver(), m2.getReceiver());
        assertEquals(testMessage.getTimestamp(), m2.getTimestamp());
        assertEquals(testMessage.getUniqueId(), m2.getUniqueId());

        TestMessageFields tmf = new TestMessageFields(actor1, actor2, new Duration(24, DurationUnit.HOUR), "ABC", false);
        String tmfs = gson.toJson(tmf);
        assertNotNull(tmfs);
        TestMessageFields tmf2 = (TestMessageFields) gson.fromJson(tmfs, Message.class);
        assertTrue(tmf2.getClass().toString().contains("message.TestMessageFields"));
        assertEquals(tmf.getSender(), tmf2.getSender());
        assertEquals(tmf.getReceiver(), tmf2.getReceiver());
        assertEquals(tmf.getTimestamp(), tmf2.getTimestamp());
        assertEquals(tmf.getUniqueId(), tmf2.getUniqueId());
        assertEquals(tmf.getDuration(), tmf2.getDuration());
        assertEquals(tmf.getName(), tmf2.getName());
        assertEquals(tmf.isYesno(), tmf2.isYesno());
    }

}
