package nl.tudelft.simulation.supplychain.content.receiver;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;

/**
 * ContentReceiverDelay implements a queuing mechanism for content of an actor that handles contents after a (stochastic) delay.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContentReceiverDelay extends ContentReceiver
{
    /** */
    private static final long serialVersionUID = 20221127L;

    /** the delay distribution that can be changed, e.g., for implementing administrative delays. */
    private DistContinuousDuration delayDistribution;

    /**
     * Create a content queuing mechanism for an actor that handles contents after a (stochastic) delay.
     * @param delayDistribution the delay distribution for handling contents (note that the distribution can be changed later,
     *            e.g., for implementing temporary administrative delays)
     */
    public ContentReceiverDelay(final DistContinuousDuration delayDistribution)
    {
        super("ContentReceiverDelay");
        setDelayDistribution(delayDistribution);
    }

    @Override
    public <C extends Content> void receiveContent(final C content, final ContentHandler<C> contentHandler)
    {
        getRole().getActor().getSimulator().scheduleEventRel(this.delayDistribution.draw(), contentHandler, "handleContent",
                new Object[] {content});
    }

    /**
     * Return the delay distribution for handling contents.
     * @return the delay distribution
     */
    public DistContinuousDuration getDelayDistribution()
    {
        return this.delayDistribution;
    }

    /**
     * Set a new delay distribution for handling content items.
     * @param delayDistribution the new delay distribution
     */
    public void setDelayDistribution(final DistContinuousDuration delayDistribution)
    {
        Throw.whenNull(delayDistribution, "delayDistribution cannot be null");
        this.delayDistribution = delayDistribution;
    }

}
