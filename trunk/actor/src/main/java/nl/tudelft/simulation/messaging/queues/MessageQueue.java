package nl.tudelft.simulation.messaging.queues;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.tudelft.simulation.messaging.Message;

/**
 * The MessageQueue sorts messages on priority, and within the same priority on the moment the messages entered the queue.<br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class MessageQueue implements MessageQueueInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** The set of messages */
    private SortedSet<Message> messages;

    /** The message counter to use for the messages in the queue */
    private static long messageCounter = 0;

    /**
     * Make a new MessageQueue
     * @param comparator (or chain) the comparator to use
     */
    public MessageQueue(final Comparator<Message> comparator)
    {
        super();
        this.messages = new TreeSet<Message>(comparator);
    }

    /** {@inheritDoc} */
    @Override
    public void add(final Message message)
    {
        message.setId(messageCounter++);
        this.messages.add(message);
    }

    /** {@inheritDoc} */
    @Override
    public void remove(final Message message)
    {
        this.messages.remove(message);
    }

    /** {@inheritDoc} */
    @Override
    public Message first()
    {
        return this.messages.first();
    }

    /** {@inheritDoc} */
    @Override
    public Message removeFirst()
    {
        Message message = this.messages.first();
        this.messages.remove(message);
        return message;
    }

    /** {@inheritDoc} */
    @Override
    public int size()
    {
        return this.messages.size();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty()
    {
        return this.messages.isEmpty();
    }
}
