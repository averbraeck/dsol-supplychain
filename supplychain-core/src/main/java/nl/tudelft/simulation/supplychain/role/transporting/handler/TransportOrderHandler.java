package nl.tudelft.simulation.supplychain.role.transporting.handler;

import nl.tudelft.simulation.supplychain.content.TransportConfirmation;
import nl.tudelft.simulation.supplychain.content.TransportOrder;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingRole;

/**
 * The TransportOrderHandler implements the business logic for a transporter that receives an TransportOrder. sends a
 * TransportConfirmation and schedules the transport itself leading to a TransportDelivery.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportOrderHandler extends ContentHandler<TransportOrder, TransportingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new TransportOrder handler.
     * @param owner the role belonging to this handler
     */
    public TransportOrderHandler(final TransportingRole owner)
    {
        super("TransportOrderHandler", owner, TransportOrder.class);
    }

    @Override
    public boolean handleContent(final TransportOrder transportOrder)
    {
        if (!isValidContent(transportOrder))
        {
            return false;
        }
        
        // send a TransportConformation to the finance department
        var transportQuote = getRole().getTransportQuote(transportOrder.groupingId());
        var tc = new TransportConfirmation(getRole().getActor(), transportQuote, shipment);
        
        // start the delivery process
        
        return true;
    }

}
