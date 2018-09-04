package nl.tudelft.simulation.actor.messagehandlers;

import nl.tudelft.simulation.actor.ActorInterface;
import nl.tudelft.simulation.messaging.Message;
import nl.tudelft.simulation.messaging.queues.MessageQueueInterface;

/**
 * The HandleAllMessage class takes out all messages in the MessageQueue at once and handles them right away. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class HandleAllMessages implements MessageHandlerInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the owner of this handler */
    private ActorInterface owner;

    /**
     * Create a new handleAllMessages object to be able to handle all messages right away.
     * @param owner the source of this handler
     */
    public HandleAllMessages(final ActorInterface owner)
    {
        super();
        this.owner = owner;
    }

    /** {@inheritDoc} */
    @Override
    public void handleMessageQueue(final MessageQueueInterface messageQueue)
    {
        while (!messageQueue.isEmpty())
        {
            Message message = messageQueue.removeFirst();
            this.owner.handleMessage(message);
        }
    }
}
