package nl.tudelft.simulation.supplychain.message.receiver;

import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.policy.MessagePolicy;

/**
 * MessageReceiverDirect implements message queuing for an actor that immediately handles the message upon receipt.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MessageReceiverDirect extends MessageReceiver
{
    /** */
    private static final long serialVersionUID = 20221127L;

    /**
     * Create a message handler for an actor that immediately handles the message upon receipt.
     */
    public MessageReceiverDirect()
    {
        super("MessageReceiverDirect");
    }

    /** {@inheritDoc} */
    @Override
    public <M extends Message> void receiveMessage(final M message, final MessagePolicy<M> messagePolicy)
    {
        messagePolicy.handleMessage(message);
    }

}
