package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;
import nl.tudelft.simulation.supplychain.transporting.TransportOption;

/**
 * The InventoryQuoteRequest is a question to the warehouse to check whether there is inventory to fulfill the demand for a
 * certain amount of product at a certain date. It will be answered with an InventoryQuote. Note that the warehouse can decide
 * to look at buying this produt on the market, producing it on time, or just looking in the inventory.
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
public record InventoryQuoteRequest(SellingActor sender, WarehousingActor receiver, Time timestamp, long uniqueId,
        long groupingId, Demand demand, TransportOption preferredTransportOption, Time cutoffDate)
        implements GroupedContent, ProductContent
{
    public InventoryQuoteRequest(final SellingActor sender, final WarehousingActor receiver, final Demand demand,
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
