package nl.tudelft.simulation.supplychain.actor;

import nl.tudelft.simulation.supplychain.SupplyChainException;

/**
 * ActorAlreadyDefinedException indicates that an actor tried to register itself in the model, but it already existed.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ActorAlreadyDefinedException extends SupplyChainException
{
    /** */
    private static final long serialVersionUID = 20230407L;

    /**
     * Create the exception, identifying the missing actor.
     * @param actorId String; the actor id that could not be found
     */
    public ActorAlreadyDefinedException(final String actorId)
    {
        super("Actor with id " + actorId + " has already been defined in the model's actor map");
    }

}
