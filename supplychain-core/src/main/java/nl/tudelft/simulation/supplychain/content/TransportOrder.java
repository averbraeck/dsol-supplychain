package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Shipment;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The TransportOrder is the request from the ShippingRole to the transport actor to pickup the goods for transport.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param sender the sender of the RFW
 * @param receiver the receiver of the RFQ
 * @param timestamp the absolute time when the message was created
 * @param uniqueId the unique id of the message
 * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
 * @param transportQuote the transport quote that dictates the mode of transport
 * @param shipment the shipment that needs to be transported
 */
public record TransportOrder(WarehousingActor sender, TransportingActor receiver, Time timestamp, long uniqueId,
        long groupingId, TransportQuote transportQuote, Shipment shipment) implements GroupedContent, ProductContent
{
    public TransportOrder(final TransportQuote transportQuote, final Shipment shipment)
    {
        this(transportQuote.transportOption().getPickupActor(), transportQuote.transportOption().getTransportingActor(),
                transportQuote.sender().getSimulatorTime(), transportQuote.sender().getModel().getUniqueContentId(),
                transportQuote.groupingId(), transportQuote, shipment);
    }

    @Override
    public Product product()
    {
        return shipment().getProduct();
    }

    @Override
    public double amount()
    {
        return shipment().getAmount();
    }

}
