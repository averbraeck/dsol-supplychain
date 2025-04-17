package nl.tudelft.simulation.supplychain.role.selling.handler;

import nl.tudelft.simulation.supplychain.content.InventoryQuoteRequest;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.selling.SellingActorRFQ;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingRoleRFQ;

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

    /**
     * Construct a new RFQ handler.
     * @param owner the actor belonging to this handler
     */
    public RequestForQuoteHandler(final SellingActorRFQ owner)
    {
        super("RequestForQuoteHandler", owner.getSellingRole(), RequestForQuote.class);
    }

    @Override
    public boolean handleContent(final RequestForQuote rfq)
    {
        if (!isValidContent(rfq))
        {
            return false;
        }
        var inventoryQuoteRequest = new InventoryQuoteRequest(getRole().getActor(), getRole().getActor(), rfq);
        sendContent(inventoryQuoteRequest, getHandlingTime().draw());
        return true;
    }

    @Override
    public SellingRoleRFQ getRole()
    {
        return (SellingRoleRFQ) super.getRole();
    }

}
