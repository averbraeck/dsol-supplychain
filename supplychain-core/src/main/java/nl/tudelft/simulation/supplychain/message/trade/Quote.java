package nl.tudelft.simulation.supplychain.message.trade;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.transport.TransportOption;

/**
 * A Quote is an answer to a RequestForQuote (or RFQ) and indicates how many items of a certain product could be sold for a
 * certain price at a certain date. The Quote might have a limited validity. <br>
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Quote extends TradeMessage
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the RFQ to which this quote belongs. */
    private RequestForQuote requestForQuote;

    /** the product about which we are talking, might be a replacement. */
    private Product product;

    /** the amount of goods promised, can be less than the amount asked. */
    private double amount;

    /** the price asked for the amount of products. */
    private Money price;

    /** the date on which the goods will be sent. */
    private Time proposedShippingDate;

    /** the transport option offered. */
    private TransportOption transportOption;

    /** the time on the simulator clock until which the quote is valid. */
    private Time validityTime;

    /**
     * The Constructor for a Quote. Note that the Quote contains a product and an amount. This sounds superfluous, but it is
     * not. The quote might contain a replacement product or less amount than the original request in the RFQ.
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param requestForQuote the RFQ for which this is the quote
     * @param product the product of the quote
     * @param amount the amount of products
     * @param price the quotation price
     * @param proposedShippingDate the intended shipping date of the products
     * @param transportOption the transport option offered
     * @param validityTime the time on the simulator clock until which the quote is valid
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Quote(final Actor sender, final Actor receiver, final RequestForQuote requestForQuote,
            final Product product, final double amount, final Money price, final Time proposedShippingDate,
            final TransportOption transportOption, final Time validityTime)
    {
        super(sender, receiver, requestForQuote.getInternalDemandId());
        this.requestForQuote = requestForQuote;
        this.product = product;
        this.amount = amount;
        this.price = price;
        this.proposedShippingDate = proposedShippingDate;
        this.transportOption = transportOption;
        this.validityTime = validityTime;
    }

    /**
     * @return the price.
     */
    public Money getPrice()
    {
        return this.price;
    }

    /**
     * @return the InternalDemand
     */
    public InternalDemand getInternalDemand()
    {
        return this.requestForQuote.getInternalDemand();
    }

    /**
     * @return the product.
     */
    @Override
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * @return the requestForQuote.
     */
    public RequestForQuote getRequestForQuote()
    {
        return this.requestForQuote;
    }

    /**
     * @return the validityTime.
     */
    public Time getValidityTime()
    {
        return this.validityTime;
    }

    /**
     * @return the amount.
     */
    public double getAmount()
    {
        return this.amount;
    }

    /**
     * @return the proposedShippingDate.
     */
    public Time getProposedShippingDate()
    {
        return this.proposedShippingDate;
    }

    /**
     * Return the proposed delivery date, based on the shipping date and the calculated shipping time.
     * @return the proposed delivery date, based on the shipping date and the calculated shipping time.
     */
    public Time getProposedDeliveryDate()
    {
        return this.proposedShippingDate.plus(this.transportOption.estimatedTotalTransportDuration(getProduct().getSku()));
    }

    /**
     * @return transportOption
     */
    public TransportOption getTransportOption()
    {
        return this.transportOption;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " units of product " + this.getProduct().getName()
                + "[belonging to " + this.requestForQuote.toString() + "]";
    }
}
