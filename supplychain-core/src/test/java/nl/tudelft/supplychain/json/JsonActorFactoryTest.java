package nl.tudelft.supplychain.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.TimeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.point.DirectedPoint2d;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulator;
import nl.tudelft.simulation.supplychain.json.JsonActorFactory;
import nl.tudelft.supplychain.actor.TestActor;
import nl.tudelft.supplychain.actor.TestModel;

/**
 * JsomMessageFactoryTest.java.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class JsonActorFactoryTest
{

    /** Test DurationAdapter. */
    @Test
    public void testDurationAdapter()
    {
        SupplyChainSimulator simulator = new SupplyChainSimulator("sim", Time.ZERO);
        TestModel model = new TestModel(simulator);
        Gson gson = JsonActorFactory.instance(model);
        assertNotNull(gson);
        assertEquals(gson, JsonActorFactory.instance(model));
        Duration duration = new Duration(12.4, DurationUnit.WEEK);
        String ds = gson.toJson(duration);
        assertNotNull(ds);
        Duration d2 = gson.fromJson(ds, Duration.class);
        assertEquals(duration, d2);
    }

    /** Test TimeAdapter. */
    @Test
    public void testTimeAdapter()
    {
        SupplyChainSimulator simulator = new SupplyChainSimulator("sim", Time.ZERO);
        TestModel model = new TestModel(simulator);
        Gson gson = JsonActorFactory.instance(model);
        assertNotNull(gson);
        assertEquals(gson, JsonActorFactory.instance(model));
        Time time = new Time(12.4, TimeUnit.BASE_WEEK);
        String ts = gson.toJson(time);
        assertNotNull(ts);
        Time t2 = gson.fromJson(ts, Time.class);
        assertEquals(time, t2);
    }

    /**
     * Test ActorAdapter.
     * @throws ActorAlreadyDefinedException on error
     */
    @Test
    public void testActorAdapter() throws ActorAlreadyDefinedException
    {
        SupplyChainSimulator simulator = new SupplyChainSimulator("sim", Time.ZERO);
        TestModel model = new TestModel(simulator);
        Gson gson = JsonActorFactory.instance(model);
        assertNotNull(gson);
        assertEquals(gson, JsonActorFactory.instance(model));
        Actor actor = new TestActor("TA", "TestActor", model, new DirectedPoint2d(10, 10, 0), "Dallas, TX");
        String as = gson.toJson(actor);
        assertNotNull(as);
        Actor a2 = gson.fromJson(as, Actor.class);
        assertEquals(actor, a2);
    }

}
