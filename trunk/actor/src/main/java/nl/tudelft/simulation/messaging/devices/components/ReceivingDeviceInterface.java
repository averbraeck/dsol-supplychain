package nl.tudelft.simulation.messaging.devices.components;

import java.io.Serializable;

import org.djutils.event.EventType;

import nl.tudelft.simulation.messaging.Message;
import nl.tudelft.simulation.messaging.queues.MessageQueueInterface;

/**
 * The ReceivingDeviceInterface, which extends the standard functionality of a device by adding a method to receive a message,
 * and a method to retrieve the queue of messages that have already been received. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface ReceivingDeviceInterface extends DeviceInterface
{
    /** The event to indicate we received a message */
    EventType RECEIVED_NEW_MESSAGE_EVENT = new EventType("RECEIVED_NEW_MESSAGE_EVENT");

    /**
     * receives a message
     * @param message the object or message to receive
     * @return an acknowledgement object to indicate success, <b>null</b> is success, an Exception object indicates no
     *         success...
     */
    Serializable receive(Message message);

    /**
     * get the queue of messages that have already been received.
     * @return Returns the message queue.
     */
    MessageQueueInterface getQueue();
}
