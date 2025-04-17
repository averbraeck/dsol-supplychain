package nl.tudelft.simulation.supplychain.role.financing.handler;

import nl.tudelft.simulation.supplychain.content.Fulfillment;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * The FulfillmentHandler is a simple implementation of the business logic for a Fulfillment that comes in.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FulfillmentHandler extends ContentHandler<Fulfillment, FinancingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Constructs a new FulfillmentHandler.
     * @param owner the owner of the handler.
     */
    public FulfillmentHandler(final FinancingActor owner)
    {
        super("FulfillmentHandler", owner.getFinancingRole(), Fulfillment.class);
    }

    @Override
    public boolean handleContent(final Fulfillment fulfillemnt)
    {
        if (!isValidContent(fulfillemnt))
        {
            return false;
        }

        // for now, ignore.
        return true;
    }

}
