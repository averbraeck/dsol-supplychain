package nl.tudelft.simulation.actor.messagehandlers;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.actor.ActorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.messaging.Message;
import nl.tudelft.simulation.messaging.queues.MessageQueueInterface;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * The MessageHandlerTimeDelay takes messages one by one and only looks at the next message after a certain time delay. A busy
 * flag indicates whether the message handler is already working, to avoid two separate threads of execution to be scheduled on
 * the same handler at the same time (effectively doubling its capacity). <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class HandleMessagesTimeDelay implements MessageHandlerInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the owner of this handler */
    private ActorInterface owner;

    /** the simulator to schedule on */
    private DEVSSimulatorInterface.TimeDoubleUnit simulator;

    /** the time distribution for handling the message */
    private DistContinuousDurationUnit handlingTime;

    /** a flag to indicate that the message handler is already busy */
    private boolean busy;

    /**
     * Create a Messagehandler that takes a stochastic time to handle messages, and that is only able to handle messages one by
     * one.
     * @param owner the owner (actor) of this handler
     * @param simulator to schedule on
     * @param handlingTime distribution for handling the message
     */
    public HandleMessagesTimeDelay(final ActorInterface owner, final DEVSSimulatorInterface.TimeDoubleUnit simulator,
            final DistContinuousDurationUnit handlingTime)
    {
        super();
        this.owner = owner;
        this.simulator = simulator;
        this.handlingTime = handlingTime;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void handleMessageQueue(final MessageQueueInterface messageQueue)
    {
        if (this.busy)
        {
            return;
        }
        this.busy = true;
        check(messageQueue);
    }

    /**
     * @param messageQueue the queue with messages
     */
    protected synchronized void check(final MessageQueueInterface messageQueue)
    {
        if (messageQueue.isEmpty())
        {
            this.busy = false;
            return;
        }
        // handle one message now. Schedule the next handling. If the handler method
        // returns false, leave message in the queue, and stop handling
        Message message = messageQueue.first();
        boolean success = this.owner.handleContent(message);
        if (success)
        {
            messageQueue.remove(message);
            try
            {
                this.simulator.scheduleEventRel(this.handlingTime.draw(), this, this, "check", new Object[] { messageQueue });
                return;
            }
            catch (Exception e)
            {
                Logger.error(e, "check");
            }
        }
        this.busy = false;
    }
}
