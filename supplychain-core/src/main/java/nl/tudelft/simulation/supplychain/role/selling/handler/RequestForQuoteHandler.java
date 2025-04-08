package nl.tudelft.simulation.supplychain.role.selling.handler;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.InventoryQuoteRequest;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;

/**
 * The RequestForQuotehandler implements the business logic for a supplier who receives a RequestForQuote. The first step is to
 * check if the inventory is available, or can be made available.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RequestForQuoteHandler extends ContentHandler<RequestForQuote, SellingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the reaction time of the handler in simulation time units. */
    private DistContinuousDuration handlingTime;

    /**
     * Construct a new RFQ handler.
     * @param owner the role belonging to this handler
     * @param handlingTime the distribution of the time to react on the RFQ
     */
    public RequestForQuoteHandler(final SellingRole owner, final DistContinuousDuration handlingTime)
    {
        super("RequestForQuoteHandler", owner, RequestForQuote.class);
        Throw.whenNull(handlingTime, "handlingTime cannot be null");
        this.handlingTime = handlingTime;
    }

    @Override
    public boolean handleContent(final RequestForQuote rfq)
    {
        if (!isValidContent(rfq))
        {
            return false;
        }
        var inventoryQuoteRequest = new InventoryQuoteRequest(getRole().getActor(), getRole().getActor(), rfq);
        sendContent(inventoryQuoteRequest, this.handlingTime.draw());
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
