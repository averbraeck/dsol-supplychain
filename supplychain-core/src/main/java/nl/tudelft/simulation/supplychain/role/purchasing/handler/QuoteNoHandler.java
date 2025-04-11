package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import nl.tudelft.simulation.supplychain.content.QuoteNo;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;

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
    public QuoteNoHandler(final PurchasingRole owner)
    {
        super("QuoteNoHandler", owner, QuoteNo.class);
    }

    @Override
    public boolean handleContent(final QuoteNo quoteNo)
    {
        if (!isValidContent(quoteNo))
        {
            return false;
        }
        // For now, do nothing. The RFQ will timeout by itself.
        return true;
    }

}
