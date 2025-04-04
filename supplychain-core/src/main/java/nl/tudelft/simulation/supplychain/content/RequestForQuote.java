package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.transporting.TransportOption;

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
 * @param cutoffDate after what point in time will the RFQ stop collecting quotes?
 */
public record RequestForQuote(PurchasingActor sender, SellingActor receiver, Time timestamp, long uniqueId, long groupingId,
        Demand demand, TransportOption preferredTransportOption, Time cutoffDate) implements GroupedContent, ProductContent
{
    public RequestForQuote(final PurchasingActor sender, final SellingActor receiver, final Demand demand,
            final TransportOption preferredTransportOption, final Time cutoffDate)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), demand.groupingId(), demand,
                preferredTransportOption, cutoffDate);
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

    /**
     * @return earliestDeliveryDate the earliest delivery date
     */
    public Time earliestDeliveryDate()
    {
        return demand().earliestDeliveryDate();
    }

    /**
     * @return latestDeliveryDate the latest delivery date
     */
    public Time latestDeliveryDate()
    {
        return demand().latestDeliveryDate();
    }

}
