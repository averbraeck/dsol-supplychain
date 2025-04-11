package nl.tudelft.simulation.supplychain.role.transporting.handler;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.TransportQuoteRequest;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingRole;

/**
 * The TransportQuoteRequestHandler implements the business logic for a transporter that receives an TransportQuoteRequest. It
 * uses a price table from the TransportingRole to make a response.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportQuoteRequestHandler extends ContentHandler<TransportQuoteRequest, TransportingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the reaction time of the handler in simulation time units. */
    private DistContinuousDuration handlingTime;

    /**
     * Construct a new TransportQuoteRequest handler.
     * @param owner the role belonging to this handler
     * @param handlingTime the distribution of the time to react on the TransportQuoteRequest
     */
    public TransportQuoteRequestHandler(final TransportingRole owner, final DistContinuousDuration handlingTime)
    {
        super("TransportQuoteRequestHandler", owner, TransportQuoteRequest.class);
        Throw.whenNull(handlingTime, "handlingTime cannot be null");
        this.handlingTime = handlingTime;
    }

    @Override
    public boolean handleContent(final TransportQuoteRequest tqr)
    {
        if (!isValidContent(tqr))
        {
            return false;
        }
        for (var transportQuote : getRole().makeTransportQuotes(tqr))
        {
            sendContent(transportQuote, this.handlingTime.draw());
        }
        return true;
    }

    /**
     * @param handlingTime The handlingTime to set.
     */
    public void setHandlingTime(final DistContinuousDuration handlingTime)
    {
        this.handlingTime = handlingTime;
    }

}
