package nl.tudelft.simulation.supplychain.role.buying;

import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyRFQ;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicy;

/**
 * The buying role based on a RFQ is a role that organizes the buying based on a RequestForQuote that is sent to a fixed set of
 * possible suppliers, and continues from there.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BuyingRoleRFQ extends BuyingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Constructs a new BuyingRole for Demand - Quote - Confirmation - Shipment - Bill.
     * @param owner the actor to which this role belongs
     * @param internalDemandPolicy the internal demand handler, results in sending out an RFQ
     * @param quotePolicy the quote handler
     * @param orderConfirmationPolicy the order confirmation handler
     * @param shipmentPolicy the shipment handler
     * @param billPolicy the bill handler
     */
    public BuyingRoleRFQ(final BuyingActor owner, final InternalDemandPolicyRFQ internalDemandPolicy, final QuotePolicy quotePolicy,
            final OrderConfirmationPolicy orderConfirmationPolicy, final ShipmentPolicy shipmentPolicy,
            final BillPolicy billPolicy)
    {
        super(owner);
        setMessagePolicy(internalDemandPolicy);
        setMessagePolicy(quotePolicy);
        setMessagePolicy(orderConfirmationPolicy);
        setMessagePolicy(shipmentPolicy);
        setMessagePolicy(billPolicy);
    }

    @Override
    public String getId()
    {
        return getActor().getId() + "-BUYING(RFQ)";
    }

}
