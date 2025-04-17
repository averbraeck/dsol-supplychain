package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import nl.tudelft.simulation.supplychain.content.QuoteNo;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRoleRFQ;

/**
 * The QuoteNoHandler implements the business logic for a buyer who receives a negative Quote from a selling actor.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class QuoteNoHandler extends ContentHandler<QuoteNo, PurchasingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 120221203;

    /**
     * Constructs a new QuoteNoHandler.
     * @param owner the owner of the handler
     */
    public QuoteNoHandler(final PurchasingActor owner)
    {
        super("QuoteNoHandler", owner.getPurchasingRole(), QuoteNo.class);
    }

    @Override
    public boolean handleContent(final QuoteNo quoteNo)
    {
        if (!isValidContent(quoteNo))
        {
            return false;
        }
        if (getRole().isDiscardNegativeQuotes())
        {
            // TODO implement handling of negative quotes
        }
        return true;
    }

    @Override
    public PurchasingRoleRFQ getRole()
    {
        return (PurchasingRoleRFQ) super.getRole();
    }

}
