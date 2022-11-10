package nl.tudelft.simulation.actor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.actor.dsol.SCSimulatorInterface;

/**
 * An ActorGroup is a group of actors that acts like a normal Actor object, but which can also act on behalf of the <i>group</i>
 * of objects. ActorGroups can contain ActorGroups.<br>
 * <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class ActorGroup extends Actor
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the actors of this list. */
    private Set<ActorInterface> actors = new LinkedHashSet<ActorInterface>();

    /**
     * Creates an ActorGroup.
     * @param name the name of the actor.
     * @param simulator the simulator to schedule on
     * @param position the position of the actor
     */
    public ActorGroup(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position)
    {
        super(name, simulator, position);
    }

    /**
     * Adds an actor to the group.
     * @param actor an actor to be added.
     * @return success
     */
    public boolean addActor(final ActorInterface actor)
    {
        return this.actors.add(actor);
    }

    /**
     * Removes an actor from the group.
     * @param actor the actor to be removed
     * @return succes
     */
    public boolean removeActor(final ActorInterface actor)
    {
        return this.actors.remove(actor);
    }

    /**
     * returns the individual actors in this group, by recursively resolving the actor groups that might be present as members
     * of this ActorGroup.
     * @return actors
     */
    public Set<ActorInterface> getIndividualActors()
    {
        Set<ActorInterface> resultSet = new LinkedHashSet<ActorInterface>();
        for (ActorInterface actor : this.actors)
        {
            if ((actor instanceof ActorGroup) && (actor != this))
            {
                resultSet.addAll(((ActorGroup) actor).getIndividualActors());
            }
            else
            {
                resultSet.add(actor);
            }
        }
        return resultSet;
    }
}
