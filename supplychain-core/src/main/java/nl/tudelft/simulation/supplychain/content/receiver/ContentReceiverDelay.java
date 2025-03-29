package nl.tudelft.simulation.supplychain.content.receiver;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.ContentPolicy;
import nl.tudelft.simulation.supplychain.content.Message;

/**
 * MessageReceiverDelay implements a message queuing mechanism for an actor that handles messages after a (stochastic) delay.
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
     * Create a message queuing mechanism for an actor that handles messages after a (stochastic) delay.
     * @param delayDistribution DistContinuousDuration; the delay distribution for handling messages (note that the distribution
     *            can be changed later, e.g., for implementing temporary administrative delays)
     */
    public ContentReceiverDelay(final DistContinuousDuration delayDistribution)
    {
        super("MessageReceiverDelay");
        setDelayDistribution(delayDistribution);
    }

    /** {@inheritDoc} */
    @Override
    public <M extends Message> void receiveMessage(final M message, final ContentPolicy<M> messagePolicy)
    {
        getRole().getActor().getSimulator().scheduleEventRel(this.delayDistribution.draw(), messagePolicy, "handleMessage",
                new Object[] {message});
    }

    /**
     * Return the delay distribution for handling messages.
     * @return DistContinuousDuration; the delay distribution
     */
    public DistContinuousDuration getDelayDistribution()
    {
        return this.delayDistribution;
    }

    /**
     * Set a new delay distribution for handling messages.
     * @param delayDistribution DistContinuousDuration; the new delay distribution
     */
    public void setDelayDistribution(final DistContinuousDuration delayDistribution)
    {
        Throw.whenNull(delayDistribution, "delayDistribution cannot be null");
        this.delayDistribution = delayDistribution;
    }

}
