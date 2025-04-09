package nl.tudelft.simulation.supplychain.role.purchasing;

import java.util.Set;

import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.QuoteNo;

/**
 * The purchasing role based on a RFQ is a role that organizes the purchasing based on a RequestForQuote that is sent to a fixed
 * set of possible suppliers, and continues from there.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PurchasingRoleRFQ extends PurchasingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers =
            Set.of(Demand.class, Quote.class, QuoteNo.class, OrderConfirmation.class);

    /**
     * Constructs a new PurchasingRole for Demand - Quote - Confirmation - Shipment - Invoice.
     * @param owner the actor to which this role belongs
     */
    public PurchasingRoleRFQ(final PurchasingActor owner)
    {
        super(owner);
    }

    @Override
    public String getId()
    {
        return getActor().getId() + "-BUYING(RFQ)";
    }

    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }
}
