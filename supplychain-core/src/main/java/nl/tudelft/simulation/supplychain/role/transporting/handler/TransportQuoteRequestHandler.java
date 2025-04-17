package nl.tudelft.simulation.supplychain.role.transporting.handler;

import nl.tudelft.simulation.supplychain.content.TransportQuoteRequest;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;
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

    /**
     * Construct a new TransportQuoteRequest handler.
     * @param owner the actor belonging to this handler
     */
    public TransportQuoteRequestHandler(final TransportingActor owner)
    {
        super("TransportQuoteRequestHandler", owner.getTransportingRole(), TransportQuoteRequest.class);
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
            sendContent(transportQuote, getHandlingTime().draw());
        }
        return true;
    }

}
