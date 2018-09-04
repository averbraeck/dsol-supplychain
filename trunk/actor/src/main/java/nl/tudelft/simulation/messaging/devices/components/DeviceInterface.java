package nl.tudelft.simulation.messaging.devices.components;

import java.io.Serializable;

import nl.tudelft.simulation.event.EventProducerInterface;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;

/**
 * A DeviceInterface <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface DeviceInterface extends EventProducerInterface, Serializable
{
    /** STATE_CHANGE_EVENT */
    EventType STATE_CHANGE_EVENT = new EventType("STATE_CHANGE_EVENT");

    /**
     * returns the name of the device
     * @return the name
     */
    String getName();

    /**
     * returns the device type
     * @return Returns the deviceType.
     */
    DeviceType getDeviceType();

    /**
     * returns the state of the device
     * @return the state
     */
    DeviceState getState();

    /**
     * The transmission delay is defined on a log-10 scale, indicating the time it takes averagely in seconds to reach the other
     * Actor.
     * @return the transmission delay of the device
     */
    int getTransmissionDelay();

    /**
     * The maximum transmission frequency indicates the average number of messages (say 1 page with data) that can be sent per
     * second over the communication channel.
     * @return the maximum transmission frequency of the device
     */
    double getTransmissionFrequency();
}
