package nl.tudelft.simulation.messaging.queues;

import java.io.Serializable;

import nl.tudelft.simulation.messaging.Message;

/**
 * The MessageQueueInterface gives the possibility to store and retrieve messages, according to different types of sorting
 * mechanisms. Examples are FIFO and LIFO mechanisms.<br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */

public interface MessageQueueInterface extends Serializable
{
    /**
     * @param message new message to add
     */
    void add(final Message message);

    /**
     * @param message message to remove
     */
    void remove(final Message message);

    /**
     * @return the first message according to the sorting mechanism
     */
    Message first();

    /**
     * @return the first message removed according to the sorting mechanism
     */
    Message removeFirst();

    /**
     * @return the number of messages in the queue
     */
    int size();

    /**
     * @return whether the queue is empty or not
     */
    boolean isEmpty();
}
