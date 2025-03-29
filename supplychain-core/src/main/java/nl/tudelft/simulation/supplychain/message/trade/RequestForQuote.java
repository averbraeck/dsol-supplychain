package nl.tudelft.simulation.supplychain.message.trade;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.transport.TransportOption;

/**
 * The RequestForQuote is a question to provide the receiver with a certain amount of a certain product at a certain date. It
 * will be answered with a Quote.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RequestForQuote extends TradeMessage
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the InternalDemand for linking purposes. */
    private final InternalDemand internalDemand;

    /** the date when the RFQ will be cut off. */
    private final Time cutoffDate;

    /** the preferred transport option for moving the product from sender to receiver. */
    private final TransportOption preferredTransportOption;

    /**
     * Create a new RFQ, based on an internal demand.
     * @param sender Actor; the sender actor of the message content
     * @param receiver Actor; the receving actor of the message content
     * @param internalDemand InternalDemand; internal demand that triggered the process
     * @param preferredTransportOption TransportOption; the preferred transport option for moving the product from sender to
     *            receiver
     * @param cutoffDuration Duration; after how much time will the RFQ stop collecting quotes?
     */
    public RequestForQuote(final Actor sender, final Actor receiver, final InternalDemand internalDemand,
            final TransportOption preferredTransportOption, final Duration cutoffDuration)
    {
        super(sender, receiver, internalDemand.getInternalDemandId());
        this.internalDemand = internalDemand;
        this.cutoffDate = sender.getSimulatorTime().plus(cutoffDuration);
        this.preferredTransportOption = preferredTransportOption;
    }

    /**
     * Return the InternalDemand that triggerred this RFQ.
     * @return InternalDemand; the InternalDemand that triggerred this RFQ.
     */
    protected InternalDemand getInternalDemand()
    {
        return this.internalDemand;
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.internalDemand.getProduct();
    }

    /**
     * Return the amount of products in the product's SKU that was demanded.
     * @return double the amount that was demanded.
     */
    public double getAmount()
    {
        return this.internalDemand.getAmount();
    }

    /**
     * Return the earliest delivery date.
     * @return double the earliest delivery date for the product.
     */
    public Time getEarliestDeliveryDate()
    {
        return this.internalDemand.getEarliestDeliveryDate();
    }

    /**
     * Return the latest delivery date.
     * @return double the latest delivery date for the product.
     */
    public Time getLatestDeliveryDate()
    {
        return this.internalDemand.getLatestDeliveryDate();
    }

    /**
     * Return the transport request for moving the product from sender to receiver.
     * @return TransportRequest; the transport request for moving the product from sender to receiver
     */
    public TransportOption getPreferredTransportOption()
    {
        return this.preferredTransportOption;
    }

    /**
     * Return the cutoff date.
     * @return Time; the cutoff date
     */
    public Time getCutoffDate()
    {
        return this.cutoffDate;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " " + this.getProduct().getSku().getName() + " of product "
                + this.getProduct().getName();
    }
}
