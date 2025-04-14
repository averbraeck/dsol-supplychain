package nl.tudelft.simulation.supplychain.role.transporting.handler;

import nl.tudelft.simulation.supplychain.content.TransportConfirmation;
import nl.tudelft.simulation.supplychain.content.TransportPickup;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingRole;

/**
 * The TransportPickupHandler implements the business logic for a transporter that receives an TransportPickup. sends a
 * TransportConfirmation and schedules the transport itself leading to a transportDelivery.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportPickupHandler extends ContentHandler<TransportPickup, TransportingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new TransportPickup handler.
     * @param owner the role belonging to this handler
     */
    public TransportPickupHandler(final TransportingRole owner)
    {
        super("TransportPickupHandler", owner, TransportPickup.class);
    }

    @Override
    public boolean handleContent(final TransportPickup transportPickup)
    {
        if (!isValidContent(transportPickup))
        {
            return false;
        }
        
        // send a TransportConformation to the finance department
        var transportQuote = getRole().getTransportQuote(transportPickup.groupingId());
        var tc = new TransportConfirmation(getRole().getActor(), transportQuote, shipment)
        
        // start the delivery process
        
        return true;
    }

}
