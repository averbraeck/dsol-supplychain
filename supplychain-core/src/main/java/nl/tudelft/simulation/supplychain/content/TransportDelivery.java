package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Shipment;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The TransportDelivery is the confirmation from the transport actor to the ReceivingRole of the purchaser that the ordered
 * goods have arrived.
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
 * @param order the order that is fulfilled with delivering the shipment with the goods
 * @param shipment the shipment that needs to be transported
 */
public record TransportDelivery(TransportingActor sender, WarehousingActor receiver, Time timestamp, long uniqueId,
        long groupingId, Order order, Shipment shipment) implements GroupedContent, ProductContent
{
    public TransportDelivery(final TransportingActor sender, final WarehousingActor receiver, final Order order,
            final Shipment shipment)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), order.groupingId(), order,
                shipment);
    }

    @Override
    public Product product()
    {
        return order().product();
    }

    @Override
    public double amount()
    {
        return order().amount();
    }

}
