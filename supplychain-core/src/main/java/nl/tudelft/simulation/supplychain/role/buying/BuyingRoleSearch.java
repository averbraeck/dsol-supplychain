package nl.tudelft.simulation.supplychain.role.buying;

import nl.tudelft.simulation.supplychain.handler.demand.DemandHandlerSearch;
import nl.tudelft.simulation.supplychain.handler.search.SearchAnswerPolicy;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicy;

/**
 * The buying role with searchs is a role that organizes the buying based on a SearchRequest, and continues from there.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BuyingRoleSearch extends BuyingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Construct a new BuyingRole for Demand - YPAnswer - Quote - Confirmation - Shipment - Bill.
     * @param owner the actor to which this role belongs
     * @param demandPolicy the demand handler, results in sending out an RFQ
     * @param searchAnswerPolicy the search answer handler
     * @param quotePolicy the quote handler
     * @param orderConfirmationPolicy the order confirmation handler
     * @param shipmentPolicy the shipment handler
     * @param billPolicy the bill handler
     */
    public BuyingRoleSearch(final BuyingActor owner, final DemandHandlerSearch demandPolicy,
            final SearchAnswerPolicy searchAnswerPolicy, final QuotePolicy quotePolicy,
            final OrderConfirmationPolicy orderConfirmationPolicy, final ShipmentPolicy shipmentPolicy,
            final BillPolicy billPolicy)
    {
        super(owner);
        setContentHandler(demandPolicy);
        setContentHandler(searchAnswerPolicy);
        setContentHandler(quotePolicy);
        setContentHandler(orderConfirmationPolicy);
        setContentHandler(shipmentPolicy);
        setContentHandler(billPolicy);
    }

    @Override
    public String getId()
    {
        return getActor().getId() + "-BUYING(yp)";
    }

}
