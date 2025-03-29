package nl.tudelft.simulation.supplychain.message.trade;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The InternalDemand object represents content for an internal demand of a supply chain actor. The InternalDemand triggers
 * buying or manufacturing of products, and is usually the first in a long chain of messages that are exchanged between actors.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InternalDemand extends TradeMessage
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221127L;

    /** the internal demand id (copy of uniqueId). */
    private final long internalDemandId;

    /** the product to order. */
    private Product product;

    /** the amount to order in the product's SKU. */
    private double amount;

    /** the earliest delivery date. */
    private Time earliestDeliveryDate;

    /** the latest delivery date. */
    private Time latestDeliveryDate;

    /**
     * Constructs a new InternalDemand.
     * @param sender Actor; the sender of the internal demand
     * @param product Product; the product which is demanded
     * @param amount double; the amount of the product in the product's SKU
     * @param earliestDeliveryDate Time; the earliest delivery date
     * @param latestDeliveryDate Time; the latest delivery date
     */
    public InternalDemand(final Actor sender, final Product product, final double amount,
            final Time earliestDeliveryDate, final Time latestDeliveryDate)
    {
        super(sender, sender);
        this.internalDemandId = getUniqueId();
        this.product = product;
        this.amount = amount;
        this.earliestDeliveryDate = earliestDeliveryDate;
        this.latestDeliveryDate = latestDeliveryDate;
    }

    /**
     * Return the product for which this internal demand applies.
     * @return Product; the product for which this internal demand applies
     */
    @Override
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * Return the amount of products that was demanded, in the product's SKU.
     * @return double the amount that was demanded.
     */
    public double getAmount()
    {
        return this.amount;
    }

    /**
     * Return the earliest delivery date.
     * @return double the earliest delivery date for the product.
     */
    public Time getEarliestDeliveryDate()
    {
        return this.earliestDeliveryDate;
    }

    /**
     * Return the latest delivery date.
     * @return double the latest delivery date for the product.
     */
    public Time getLatestDeliveryDate()
    {
        return this.latestDeliveryDate;
    }

    /**
     * Return the internalDemandId.
     * @return long; the id of the internal demand that triggered the TradeMessage chain.
     */
    @Override
    public long getInternalDemandId()
    {
        return this.internalDemandId;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " " + this.getProduct().getSku().getName() + " of product "
                + this.getProduct().getName();
    }
}
