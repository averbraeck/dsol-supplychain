package nl.tudelft.simulation.yellowpage;

import java.rmi.RemoteException;

import nl.tudelft.simulation.actor.ActorInterface;

/**
 * A YellowPageInterface, which enables finding of actors based on a registry in which actors can register themselves. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface YellowPageInterface
{
    /**
     * finds an actor based on the regex
     * @param regex the name of the actor as regular expression
     * @return ActorInterface[] the result
     * @throws RemoteException on network failure
     */
    ActorInterface[] findActor(final String regex) throws RemoteException;

    /**
     * finds an actor based on the regex
     * @param regex the name of the actor as regular expression
     * @param category the category for this actor
     * @return ActorInterface[] the result
     * @throws RemoteException on network failure
     */
    ActorInterface[] findActor(final String regex, final Category category) throws RemoteException;

    /**
     * finds an actor based on the category
     * @param category the category for this actor
     * @return ActorInterface[] the result
     * @throws RemoteException on network failure
     */
    ActorInterface[] findActor(final Category category) throws RemoteException;

    /**
     * registers an actor
     * @param actor the actor
     * @param category the category
     * @return success
     * @throws RemoteException on network failure
     */
    boolean register(final ActorInterface actor, final Category category) throws RemoteException;
}
