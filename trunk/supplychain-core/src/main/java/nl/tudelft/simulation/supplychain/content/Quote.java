package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import org.djunits.unit.TimeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Money;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * A Quote is an answer to a RequestForQuote (or RFQ) and indicates how many items of a certain product could be sold for a
 * certain price at a certain date. The Quote might have a limited validity. <br>
 * <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Quote extends Content
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the RFQ id to which this quote belongs */
    private RequestForQuote requestForQuote;

    /** the product about which we are talking, might be a replacement */
    private Product product;

    /** the amount of goods promised */
    private double amount;

    /** the price asked for the amount of products (in number of units) */
    private Money price;

    /** the date on which the goods will be sent */
    protected Time proposedShippingDate;

    /** the time on the simulator until which the quote is valid */
    private Time validityTime = new Time(1.0E24, TimeUnit.BASE);

    /** the transport mode */
    private TransportMode transportMode;

    /** the calculated transportation time. */
    private Duration calculatedTransportationTime;

    /**
     * The Constructor for a Quote. Note that the Quote contains a product and a price. This sounds superfluous, but it is not.
     * The quote might contain a replacement product or a product in different units than the original request in the RFQ.
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemandID the internal demand that triggered the order
     * @param requestForQuote the RFQ for which this is the quote
     * @param product the product of the quote
     * @param amount the amount of products
     * @param price the quotation price
     * @param proposedShippingDate the intended shipping date of the products
     * @param transportMode the transport mode
     */
    public Quote(final SupplyChainActor sender, final SupplyChainActor receiver, final Serializable internalDemandID,
            final RequestForQuote requestForQuote, final Product product, final double amount, final Money price,
            final Time proposedShippingDate, final TransportMode transportMode)
    {
        super(sender, receiver, internalDemandID);
        this.requestForQuote = requestForQuote;
        this.product = product;
        this.amount = amount;
        this.price = price;
        this.proposedShippingDate = proposedShippingDate;
        this.transportMode = transportMode;
        this.calculatedTransportationTime = this.transportMode.transportTime(sender, receiver);
    }

    /**
     * The Constructor for a Quote with a 'timeout' for validity. Note that the Quote contains a product and a price. This
     * sounds superfluous, but it is not. The quote might contain a replacement product or a product in different units than the
     * original request in the RFQ.
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemandID the internal demand that triggered the order
     * @param requestForQuote the RFQ for which this is the quote
     * @param product the product of the quote
     * @param amount the amount of products
     * @param price the quotation price
     * @param proposedShippingDate the intended shipping date of the products
     * @param validityTime the time when the quote has limited validity
     * @param transportMode the transport mode
     */
    public Quote(final SupplyChainActor sender, final SupplyChainActor receiver, final Serializable internalDemandID,
            final RequestForQuote requestForQuote, final Product product, final double amount, final Money price,
            final Time proposedShippingDate, final Time validityTime, final TransportMode transportMode)
    {
        this(sender, receiver, internalDemandID, requestForQuote, product, amount, price, proposedShippingDate, transportMode);
        this.validityTime = validityTime;
    }

    /**
     * @return Returns the price.
     */
    public Money getPrice()
    {
        return this.price;
    }

    /**
     * @return Returns the product.
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * @return Returns the proposedDeliveryDate.
     */
    public Time getProposedDeliveryDate()
    {
        // TODO ALEXANDER: carrier or merchant haulage?
        return this.proposedShippingDate;
        // return this.proposedShippingDate + this.calculatedTransportationTime;
    }

    /**
     * @return Returns the requestForQuote.
     */
    public RequestForQuote getRequestForQuote()
    {
        return this.requestForQuote;
    }

    /**
     * @return Returns the validityTime.
     */
    public Time getValidityTime()
    {
        return this.validityTime;
    }

    /**
     * @return Returns the amount.
     */
    public double getAmount()
    {
        return this.amount;
    }

    /**
     * @return Returns the calculatedTransportationTime.
     */
    public Duration getCalculatedTransportationTime()
    {
        return this.calculatedTransportationTime;
    }

    /**
     * @return Returns the proposedShippingDate.
     */
    public Time getProposedShippingDate()
    {
        return this.proposedShippingDate;
    }

    /**
     * @return Returns the transportMode.
     */
    public TransportMode getTransportMode()
    {
        return this.transportMode;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " units of product " + this.getProduct().getName()
                + "[belonging to " + this.requestForQuote.toString() + "]";
    }
}
