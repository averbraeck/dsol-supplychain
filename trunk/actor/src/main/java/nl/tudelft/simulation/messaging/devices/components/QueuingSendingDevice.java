package nl.tudelft.simulation.messaging.devices.components;

import java.io.Serializable;

import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.messaging.Message;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;
import nl.tudelft.simulation.messaging.queues.MessageQueueInterface;

/**
 * Models a sending device with a queue of messages. The queue can be implemented as FiFo, LiFo, priority, or any other queuing
 * mechanism that has been defined. The device just puts the messages in the queue; another mechanism (probably from the actor)
 * needs to take out the messages from the queue. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class QueuingSendingDevice extends SendingDevice
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** The event to indicate there is a message ready to be sent */
    public static final EventType NEW_MESSAGE_TO_SEND_EVENT = new EventType("NEW_MESSAGE_TO_SEND_EVENT");

    /** The queue to store the messages in */
    protected MessageQueueInterface queue = null;

    /**
     * constructs a new sending device with a message queue, override the default transmission delay and frequency from the
     * DeviceType.
     * @param name the name or description of the device
     * @param deviceType the type of device
     * @param queue the type of queue to store the messages in
     */
    public QueuingSendingDevice(final String name, final DeviceType deviceType, final MessageQueueInterface queue)
    {
        super(name, deviceType);
        this.queue = queue;
    }

    /**
     * constructs a new sending device with a message queue, override the default transmission delay and frequency from the
     * DeviceType.
     * @param name the name or description of the device
     * @param deviceType the type of device
     * @param transmissionDelay the default logarithmic transmission delay of the device.
     * @param transmissionFrequency the maximum transmission frequency of the device.
     * @param queue the type of queue to store the messages in
     */
    public QueuingSendingDevice(final String name, final DeviceType deviceType, final int transmissionDelay,
            final double transmissionFrequency, final MessageQueueInterface queue)
    {
        super(name, deviceType, transmissionDelay, transmissionFrequency);
        this.queue = queue;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable send(final Message message)
    {
        if (!this.getState().isWorking())
        {
            return Boolean.FALSE;
        }
        this.queue.add(message);
        this.fireEvent(QueuingSendingDevice.NEW_MESSAGE_TO_SEND_EVENT, null);
        return Boolean.TRUE;
    }
}
