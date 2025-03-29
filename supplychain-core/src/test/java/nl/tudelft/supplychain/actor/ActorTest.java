package nl.tudelft.supplychain.actor;

import static org.junit.Assert.assertEquals;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.exceptions.Try;
import org.junit.Test;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.ActorNotFoundException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulator;

/**
 * ActorTest tests the methods of the Actor.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ActorTest
{
    /**
     * Test the Actor.
     * @throws ActorAlreadyDefinedException on error
     * @throws IllegalArgumentException on error
     * @throws RemoteException on error
     * @throws ActorNotFoundException on error
     */
    @Test
    public void testActor()
            throws ActorAlreadyDefinedException, IllegalArgumentException, RemoteException, ActorNotFoundException
    {
        SupplyChainSimulator simulator = new SupplyChainSimulator("sim", Time.ZERO);
        TestModel model = new TestModel(simulator);
        TestActor actor = new TestActor("TA", "TestActor", model, new OrientedPoint2d(10, 10), "Dallas, TX");
        assertEquals("TA", actor.getId());
        assertEquals("TestActor", actor.getName());
        assertEquals(model, actor.getModel());
        assertEquals(simulator, actor.getSimulator());
        assertEquals(new OrientedPoint2d(10, 10), actor.getLocation());
        assertEquals("Dallas, TX", actor.getLocationDescription());
        assertEquals(0, actor.getEventListenerMap().size());
        assertEquals(-1.0, actor.getBounds().getMinX(), 0.0001);
        assertEquals(1.0, actor.getBounds().getMaxX(), 0.0001);
        actor.setBounds(new Bounds3d(10, 10, 10));
        assertEquals(-5.0, actor.getBounds().getMinX(), 0.0001);
        assertEquals(5.0, actor.getBounds().getMaxX(), 0.0001);
        double distance0 = model.calculateDistanceKm(actor.getLocation(), actor.getLocation());
        assertEquals(0.0, distance0, 0.0001);
        assertEquals(actor, model.getActor("TA"));
        assertEquals(0, actor.getRoles().size());

        Try.testFail(() -> new TestActor(null, "TestActor", model, new OrientedPoint2d(10, 10), "Dallas, TX"),
                NullPointerException.class);
        Try.testFail(() -> new TestActor("", "TestActor", model, new OrientedPoint2d(10, 10), "Dallas, TX"),
                IllegalArgumentException.class);
        Try.testFail(() -> new TestActor("TA", null, model, new OrientedPoint2d(10, 10), "Dallas, TX"),
                NullPointerException.class);
        Try.testFail(() -> new TestActor("TA", "TestActor", null, new OrientedPoint2d(10, 10), "Dallas, TX"),
                NullPointerException.class);
        Try.testFail(() -> new TestActor("TA", "TestActor", model, null, "Dallas, TX"), NullPointerException.class);
        Try.testFail(() -> new TestActor("TA", "TestActor", model, new OrientedPoint2d(10, 10), null),
                NullPointerException.class);

        Try.testFail(() -> model.getActor("XX"), ActorNotFoundException.class);
        Try.testFail(() -> new TestActor("TA", "TestActor2", model, new OrientedPoint2d(10, 10), "Dallas, TX"),
                ActorAlreadyDefinedException.class);
    }

}
