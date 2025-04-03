package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.transport.TransportOption;

/**
 * The RequestForQuote is a question to provide the receiver with a certain amount of a certain product at a certain date. It
 * will be answered with a Quote.
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
 * @param demand demand that triggered the process
 * @param preferredTransportOption the preferred transport option for moving the product from sender to receiver
 * @param cutoffDuration after how much time will the RFQ stop collecting quotes?
 */
public record RequestForQuote(PurchasingActor sender, SellingActor receiver, Time timestamp, long uniqueId, long groupingId,
        Demand demand, TransportOption preferredTransportOption, Duration cutoffDuration)
        implements GroupedContent, ProductContent
{
    public RequestForQuote(final PurchasingActor sender, final SellingActor receiver, final Demand demand,
            final TransportOption preferredTransportOption, final Duration cutoffDuration)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueMessageId(), demand.groupingId(), demand,
                preferredTransportOption, cutoffDuration);
    }

    @Override
    public Product product()
    {
        return this.demand.product();
    }

    @Override
    public double amount()
    {
        return this.demand.amount();
    }
}
