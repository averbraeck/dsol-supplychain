package nl.tudelft.supplychain.actor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.point.OrientedPoint2d;
import org.junit.Test;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulator;
import nl.tudelft.simulation.supplychain.message.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.message.receiver.MessageReceiverDirect;

/**
 * RoleTest tests the method of the Role.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RoleTest
{
    /**
     * Test the Role.
     * @throws IllegalArgumentException on error
     * @throws ActorAlreadyDefinedException on error
     */
    @Test
    public void testRole() throws ActorAlreadyDefinedException, IllegalArgumentException
    {
        SupplyChainSimulator simulator = new SupplyChainSimulator("sim", Time.ZERO);
        TestModel model = new TestModel(simulator);
        TestActor actor = new TestActor("TA", "TestActor", model, new OrientedPoint2d(10, 10), "Dallas, TX");
        assertEquals(0, actor.getRoles().size());
        ContentReceiver messageReceiver = new MessageReceiverDirect();
        TestRole role = new TestRole("ROLE", actor, messageReceiver);
        assertEquals(1, actor.getRoles().size());
        assertTrue(actor.getRoles().contains(role));
    }

    static class TestRole extends Role
    {
        private static final long serialVersionUID = 1L;

        TestRole(final String id, final Actor actor, final ContentReceiver messageReceiver)
        {
            super(id, actor, messageReceiver);
        }
    }
}
