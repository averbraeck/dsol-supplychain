package nl.tudelft.supplychain.actor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.point.DirectedPoint2d;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulator;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;

/**
 * RoleTest tests the method of the Role.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
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
        TestActor actor = new TestActor("TA", "TestActor", model, new DirectedPoint2d(10, 10, 0), "Dallas, TX");
        assertEquals(0, actor.getRoles().size());
        ContentReceiver messageReceiver = new ContentReceiverDirect();
        TestRole role = new TestRole("ROLE", actor, messageReceiver);
        assertEquals(1, actor.getRoles().size());
        assertTrue(actor.getRoles().contains(role));
    }

    static class TestRole extends Role<TestRole>
    {
        private static final long serialVersionUID = 1L;

        TestRole(final String id, final Actor actor, final ContentReceiver messageReceiver)
        {
            super(id, actor, messageReceiver);
        }

        /** {@inheritDoc} */
        @Override
        protected Set<Class<? extends Content>> getNecessaryContentHandlers()
        {
            return Set.of();
        }

        /** {@inheritDoc} */
        @Override
        protected Set<Class<? extends AutonomousProcess<TestRole>>> getNecessaryAutonomousProcesses()
        {
            return Set.of();
        }

    }
}
