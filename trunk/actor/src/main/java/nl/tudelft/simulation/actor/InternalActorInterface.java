package nl.tudelft.simulation.actor;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.content.HandlerInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.messaging.Message;

/**
 * InternalActorInterface defines the necessary methods for handling a message. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface InternalActorInterface extends HandlerInterface, Serializable
{
    /**
     * Get the name of the actor.
     * @return the name of the actor
     */
    String getName();

    /**
     * Handles a message and returns an acknowledgement.
     * @param message the message to be handled
     * @return a boolean acknowledgement
     */
    boolean handleMessage(final Message message);

    /**
     * Get the simulator on which this actor schedules. This getSimulator method does <i>not </i> throw a RemoteException.
     * @return Returns the simulator.
     */
    DEVSSimulatorInterface.TimeDoubleUnit getSimulator();

    /**
     * Get the time of the simulator on which this actor schedules. This getSimulatorTime method does <i>not</i> throw a
     * RemoteException.
     * @return Returns the simulator time.
     */
    Time getSimulatorTime();
    
    
    /**
     * Handle the content of the message.
     * @param content the content to be handled, can be of any type
     * @param logWarnings indicate whether to log warnings or not when handling fails
     * @return a boolean acknowledgement; true or false
     */
    public boolean handleContent(final Serializable content, final boolean logWarnings);
}
