package nl.tudelft.supplychain.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.TimeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.point.Point2d;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulator;
import nl.tudelft.simulation.supplychain.json.JsonContentFactory;
import nl.tudelft.supplychain.actor.TestActor;
import nl.tudelft.supplychain.actor.TestModel;
import nl.tudelft.supplychain.content.ContentTest.TestContent;
import nl.tudelft.supplychain.content.TestContentFields;

/**
 * JsomContentFactoryTest tests the JsonContentFactory.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class JsonContentFactoryTest
{

    /**
     * Test ContentAdapter.
     * @throws ActorAlreadyDefinedException on error
     */
    @Test
    public void testContentAdapter() throws ActorAlreadyDefinedException
    {
        SupplyChainSimulator simulator = new SupplyChainSimulator("sim", new Time(1.0, TimeUnit.BASE_HOUR));
        TestModel model = new TestModel(simulator);
        SingleReplication<Duration> replication =
                new SingleReplication<Duration>("rep", Duration.ZERO, Duration.ZERO, new Duration(1, DurationUnit.DAY));
        simulator.initialize(model, replication);
        Gson gson = JsonContentFactory.instance(model);
        assertNotNull(gson);
        assertEquals(gson, JsonContentFactory.instance(model));
        Actor actor1 = new TestActor("TA1", "TestActor 1", model, new Point2d(10, 10), "Dallas, TX");
        Actor actor2 = new TestActor("TA2", "TestActor 2", model, new Point2d(20, 20), "Austin, TX");
        TestContent testContent = new TestContent(actor1, actor2);
        String ms = gson.toJson(testContent);
        assertNotNull(ms);
        TestContent m2 = gson.fromJson(ms, TestContent.class);
        assertTrue(m2.getClass().toString().contains("ContentTest$TestContent"));
        assertEquals(testContent.sender(), m2.sender());
        assertEquals(testContent.receiver(), m2.receiver());
        assertEquals(testContent.timestamp(), m2.timestamp());
        assertEquals(testContent.uniqueId(), m2.uniqueId());

        TestContentFields tmf = new TestContentFields(actor1, actor2, new Duration(24, DurationUnit.HOUR), "ABC", false);
        String tmfs = gson.toJson(tmf);
        assertNotNull(tmfs);
        TestContentFields tmf2 = (TestContentFields) gson.fromJson(tmfs, Content.class);
        assertTrue(tmf2.getClass().toString().contains("message.TestContentFields"));
        assertEquals(tmf.sender(), tmf2.sender());
        assertEquals(tmf.receiver(), tmf2.receiver());
        assertEquals(tmf.timestamp(), tmf2.timestamp());
        assertEquals(tmf.uniqueId(), tmf2.uniqueId());
        assertEquals(tmf.duration(), tmf2.duration());
        assertEquals(tmf.name(), tmf2.name());
        assertEquals(tmf.yesno(), tmf2.yesno());
    }

}
