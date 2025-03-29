package nl.tudelft.simulation.supplychain.actor;

import nl.tudelft.simulation.supplychain.SupplyChainException;

/**
 * ActorNotFoundException to indicate that an actor could not be identified based on a given id.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ActorNotFoundException extends SupplyChainException
{
    /** */
    private static final long serialVersionUID = 20230407L;

    /**
     * Create the exception, identifying the missing actor.
     * @param actorId String; the actor id that could not be found
     */
    public ActorNotFoundException(final String actorId)
    {
        super("Could not find " + actorId + " in the model's actor map");
    }

}
