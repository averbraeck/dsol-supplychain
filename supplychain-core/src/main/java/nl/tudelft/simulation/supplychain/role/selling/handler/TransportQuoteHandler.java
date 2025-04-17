package nl.tudelft.simulation.supplychain.role.selling.handler;

import nl.tudelft.simulation.supplychain.content.TransportQuote;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.selling.SellingActorRFQ;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingRoleRFQ;

/**
 * The TransportQuoteHandler implements the business logic for a supplier who receives an TransportQuote from the transporter.
 * The action is to store the quote in the list of quotes at the role, and wait till all quotes are in.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportQuoteHandler extends ContentHandler<TransportQuote, SellingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new TransportQuote handler.
     * @param owner the actor belonging to this handler
     */
    public TransportQuoteHandler(final SellingActorRFQ owner)
    {
        super("TransportQuoteHandler", owner.getSellingRole(), TransportQuote.class);

    }

    @Override
    public boolean handleContent(final TransportQuote tq)
    {
        if (!isValidContent(tq))
        {
            return false;
        }
        getRole().addReceivedTransportQuote(tq);
        return true;
    }

    @Override
    public SellingRoleRFQ getRole()
    {
        return (SellingRoleRFQ) super.getRole();
    }

}
