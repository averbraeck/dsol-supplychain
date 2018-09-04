package nl.tudelft.simulation.actor;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.content.HandlerInterface;
import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.messaging.Message;
import nl.tudelft.simulation.messaging.devices.components.ReceivingDeviceInterface;
import nl.tudelft.simulation.messaging.devices.components.SendingDeviceInterface;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * ActorInterface defines the necessary methods for the 'communicating simulation object' aka actor. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface ActorInterface extends Locatable, HandlerInterface, Serializable
{
    /**
     * Retrieve the sending devices of the actor.
     * @param deviceType the devices to search for
     * @return the devices with the given DeviceType
     */
    SendingDeviceInterface[] getSendingDevices(final DeviceType deviceType);

    /**
     * Retrieve all sending devices of the actor.
     * @return all sending devices
     */
    SendingDeviceInterface[] getSendingDevices();

    /**
     * Retrieve the receiving devices of the actor.
     * @param deviceType the devices to search for
     * @return the devices with the given DeviceType
     */
    ReceivingDeviceInterface[] getReceivingDevices(final DeviceType deviceType);

    /**
     * Retrieve all receiving devices of the actor.
     * @return all receiving devices
     */
    ReceivingDeviceInterface[] getReceivingDevices();

    /**
     * Get the name of the actor.
     * @return the name of the actor
     */
    String getName();

    /**
     * Handles a message and returns an ackowledgement.
     * @param message the message to be handled
     * @return a boolean acknowledgement
     */
    boolean handleMessage(final Message message);

    /**
     * Get the simulator on which this actor schedules. This getSimulator method does <i>not </i> throw a RemoteException.
     * @return Returns the simulator.
     * @uml.associationEnd
     */
    DEVSSimulatorInterfaceUnit getSimulator();

    /**
     * Get the time of the simulator on which this actor schedules. This getSimulatorTime method does <i>not</i> throw a
     * RemoteException.
     * @return Returns the simulator time.
     */
    Time getSimulatorTime();
}
