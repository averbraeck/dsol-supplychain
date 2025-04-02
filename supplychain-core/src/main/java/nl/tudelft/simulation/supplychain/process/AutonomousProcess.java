package nl.tudelft.simulation.supplychain.process;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorMethods;
import nl.tudelft.simulation.supplychain.actor.Role;

/**
 * An AutonomousProcess belong to a role, and carries out actions that do not relate to a received message. Examples are
 * consumption, calculating interest, or calculating depriciation of a product in inventory.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <R> the role to which this autonomous process belongs
 */
public abstract class AutonomousProcess<R extends Role<R>> implements ActorMethods
{
    /** the specific Role (R) to which this process belongs. */
    private final R role;

    /**
     * Create the Autonomous process.
     * @param role the specific Role (R) to which this process belongs
     */
    public AutonomousProcess(final R role)
    {
        Throw.whenNull(role, "role cannot be null");
        this.role = role;
    }

    /**
     * Return the specific Role (R) to which this process belongs.
     * @return the specific Role (R) to which this process belongs
     */
    protected R getRole()
    {
        return this.role;
    }

    @Override
    public Actor getActor()
    {
        return this.role.getActor();
    }

}
