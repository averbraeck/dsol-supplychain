package nl.tudelft.simulation.supplychain.content.receiver;

import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.ContentPolicy;

/**
 * MessageReceiverDirect implements content queuing for an actor that immediately handles the content upon receipt.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContentReceiverDirect extends ContentReceiver
{
    /** */
    private static final long serialVersionUID = 20221127L;

    /**
     * Create a message handler for an actor that immediately handles the message upon receipt.
     */
    public ContentReceiverDirect()
    {
        super("ContentReceiverDirect");
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Content> void receiveContent(final C content, final ContentPolicy<C> contentPolicy)
    {
        contentPolicy.handleContent(content);
    }

}
