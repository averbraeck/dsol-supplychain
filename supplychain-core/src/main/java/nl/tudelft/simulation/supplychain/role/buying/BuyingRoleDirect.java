package nl.tudelft.simulation.supplychain.role.buying;

import nl.tudelft.simulation.supplychain.handler.demand.DemandHandlerOrder;
import nl.tudelft.simulation.supplychain.policy.bill.BillHandler;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentHandler;

/**
 * The direct buying role is a role that organizes the buying based on a single supplier, and continues from there.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BuyingRoleDirect extends BuyingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Construct a new BuyingRole for Generic Demand - Confirmation - Shipment - Bill.
     * @param owner the actor to which this role belongs
     * @param demandPolicy the demand handler
     * @param orderConfirmationPolicy the order confirmation handler
     * @param shipmentPolicy the shipment handler
     * @param billPolicy the bill handler
     */
    public BuyingRoleDirect(final BuyingActor owner, final DemandHandlerOrder demandPolicy,
            final OrderConfirmationHandler orderConfirmationPolicy, final ShipmentHandler shipmentPolicy,
            final BillHandler billPolicy)
    {
        super(owner);
        setContentHandler(demandPolicy);
        setContentHandler(orderConfirmationPolicy);
        setContentHandler(shipmentPolicy);
        setContentHandler(billPolicy);
    }

    @Override
    public String getId()
    {
        return getActor().getId() + "-BUYING(direct)";
    }

}
