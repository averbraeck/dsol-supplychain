package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Shipment;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;

/**
 * The TransportConfirmation is the internal confirmation of the transport by the TransportingActor that triggers the sending of
 * a TransportInvoice.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the TransportConfirmation
 * @param receiver the receiver of the TransportConfirmation, which is the FinancingActor of the transport firm 
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param transportQuote the transport quote that dictates the mode of transport
 * @param shipment the shipment that needs to be transported
 */
public record TransportConfirmation(TransportingActor sender, FinancingActor receiver, Time timestamp, long uniqueId,
        long groupingId, TransportQuote transportQuote, Shipment shipment) implements GroupedContent, ProductContent
{
    public TransportConfirmation(final TransportOrder transportOrder)
    {
        this(transportOrder.receiver(), transportOrder.receiver(), transportOrder.sender().getSimulatorTime(),
                transportOrder.sender().getModel().getUniqueContentId(), transportOrder.groupingId(),
                transportOrder.transportQuote(), transportOrder.shipment());
    }

    @Override
    public Product product()
    {
        return transportQuote().product();
    }

    @Override
    public double amount()
    {
        return transportQuote().amount();
    }

}
