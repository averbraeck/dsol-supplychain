package nl.tudelft.simulation.supplychain.content;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The RequestForQuote is a question to provide the receiver with a certain amount of a certain product at a certain date. It
 * will be answered with a Quote. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class RequestForQuote extends Content
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the product for which we want a quote */
    private Product product;

    /** the amount of the product, in units for that product */
    private double amount;

    /** the earliest delivery date on the simulation timeline */
    private Time earliestDeliveryDate;

    /** the latest delivery date on the simulation timeline */
    private Time latestDeliveryDate;

    /** the date when the RFQ will be cut off */
    private Time cutoffDate;

    /**
     * Create a new RFQ, based on an internal demand.
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemand internal demand that triggered the process
     * @param product the product we want
     * @param amount the amount of products
     */
    public RequestForQuote(final SupplyChainActor sender, final SupplyChainActor receiver, final InternalDemand internalDemand,
            final Product product, final double amount)
    {
        this(sender, receiver, internalDemand, product, amount, internalDemand.getEarliestDeliveryDate(),
                internalDemand.getLatestDeliveryDate());
    }

    /**
     * Create a new RFQ, based on an internal demand, with a time window for delivery.
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemand internal demand that triggered the process
     * @param product the product we want
     * @param amount the amount of products
     * @param earliestDeliveryDate the earliest delivery date
     * @param latestDeliveryDate the latest delivery date
     */
    public RequestForQuote(final SupplyChainActor sender, final SupplyChainActor receiver, final InternalDemand internalDemand,
            final Product product, final double amount, final Time earliestDeliveryDate, final Time latestDeliveryDate)
    {
        this(sender, receiver, internalDemand, product, amount, earliestDeliveryDate, latestDeliveryDate,
                new Duration(7.0, DurationUnit.DAY));
    }

    /**
     * Create a new RFQ, based on an internal demand, with a time window for delivery.
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemand internal demand that triggered the process
     * @param product the product we want
     * @param amount the amount of products
     * @param earliestDeliveryDate the earliest delivery date
     * @param latestDeliveryDate the latest delivery date
     * @param cutoffDuration after how much time will the RFQ stop collecting quotes?
     */
    public RequestForQuote(final SupplyChainActor sender, final SupplyChainActor receiver, final InternalDemand internalDemand,
            final Product product, final double amount, final Time earliestDeliveryDate, final Time latestDeliveryDate,
            final Duration cutoffDuration)
    {
        super(sender, receiver, internalDemand.getInternalDemandID());
        this.product = product;
        this.amount = amount;
        this.earliestDeliveryDate = earliestDeliveryDate;
        this.latestDeliveryDate = latestDeliveryDate;
        this.cutoffDate = sender.getSimulatorTime().plus(cutoffDuration);
    }

    /**
     * @return Returns the amount.
     */
    public double getAmount()
    {
        return this.amount;
    }

    /**
     * @return Returns the earliest delivery date.
     */
    public Time getEarliestDeliveryDate()
    {
        return this.earliestDeliveryDate;
    }

    /**
     * @return Returns the latest delivery date.
     */
    public Time getLatestDeliveryDate()
    {
        return this.latestDeliveryDate;
    }

    /**
     * @return Returns the cutoffDate.
     */
    public Time getCutoffDate()
    {
        return this.cutoffDate;
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.product;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " units of product " + this.getProduct().getName();
    }
}
