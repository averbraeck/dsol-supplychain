package nl.tudelft.simulation.actor.messagehandlers;

import java.io.Serializable;

import nl.tudelft.simulation.messaging.queues.MessageQueueInterface;

/**
 * A MessageHandler that implements the MessageHandlerInterface should be able to take a MessageQueue from a device, and take
 * one or more messages out of the queue, and prepare the messages fro further handling. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface MessageHandlerInterface extends Serializable
{
    /**
     * Looks at a message queue, takes out the messages one by one, unpacks the message, and handles the content. This method
     * can be extended to handle the messages differently, e.g. by just working on one message and then stopping, or taking time
     * to handle the message (and blocking if needed)
     * @param messageQueue the queue to look at
     */
    void handleMessageQueue(final MessageQueueInterface messageQueue);
}
