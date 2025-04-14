package nl.tudelft.simulation.supplychain.role.receiving.handler;

import nl.tudelft.simulation.supplychain.content.Fulfillment;
import nl.tudelft.simulation.supplychain.content.TransportDelivery;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.receiving.ReceivingRole;

/**
 * The TransportDeliveryHandler handles the shipment that comes in. This version consumes the shipment (no warehousing).
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportDeliveryHandlerConsume extends ContentHandler<TransportDelivery, ReceivingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Constructs a new TransportDeliveryHandler.
     * @param owner the owner of the handler.
     */
    public TransportDeliveryHandlerConsume(final ReceivingRole owner)
    {
        super("TransportDeliveryHandler", owner, TransportDelivery.class);
    }

    /**
     * For the moment, the handler will just reorder the products from the start of the process, in case the confirmation is
     * negative.<br>
     * {@inheritDoc}
     */
    @Override
    public boolean handleContent(final TransportDelivery transportDelivery)
    {
        if (!isValidContent(transportDelivery))
        {
            return false;
        }

        Fulfillment fulfillment = new Fulfillment(transportDelivery);
        sendContent(fulfillment, getHandlingTime().draw());
        return true;
    }

}
