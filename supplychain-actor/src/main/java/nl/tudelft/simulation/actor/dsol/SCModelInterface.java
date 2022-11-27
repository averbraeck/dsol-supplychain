package nl.tudelft.simulation.actor.dsol;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.immutablecollections.ImmutableMap;

import nl.tudelft.simulation.actor.ActorType;
import nl.tudelft.simulation.actor.RoleType;
import nl.tudelft.simulation.dsol.model.DSOLModel;

/**
 * SCModelInterface defines the extra methods of a supply chain model.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SCModelInterface extends DSOLModel<Duration, SCSimulatorInterface>
{
    /**
     * Register a new actor type.
     * @param actorType ActorType; the actor type to register
     */
    void registerActorType(ActorType actorType);
    
    /**
     * Register a new role type.
     * @param roleType RoleType; the role type to register
     */
    void registerRoleType(RoleType roleType);
    
    /**
     * Return the actor types registered in the model.
     * @return Map&lt;String, ActorType&gt;; the registred actor types
     */
    ImmutableMap<String, ActorType> getActorTypes();

    /**
     * Return the role types registered in the model.
     * @return Map&lt;String, RoleType&gt;; the registred role types
     */
    ImmutableMap<String, RoleType> getRoleTypes();

}
