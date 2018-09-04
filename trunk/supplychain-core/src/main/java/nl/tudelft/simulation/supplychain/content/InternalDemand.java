package nl.tudelft.simulation.supplychain.content;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The InternalDemand object represents content for an internal demand of a supply chain actor. The InternalDemand triggers
 * buying or manufacturing of products, and is usually the first in a long chain of messages that are exchanged between actors.
 * <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InternalDemand extends Content
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the product to order */
    private Product product;

    /** the amount to order */
    private double amount;

    /** the earliest delivery date */
    private Time earliestDeliveryDate;

    /** the latest delivery date */
    private Time latestDeliveryDate;

    /**
     * Constructs a new InternalDemand.
     * @param sender the sender of the internal demand
     * @param product the product which is demanded
     * @param amount the amount of the product
     * @param earliestDeliveryDate the earliest delivery date
     * @param latestDeliveryDate the latest delivery date
     */
    public InternalDemand(final SupplyChainActor sender, final Product product, final double amount,
            final Time earliestDeliveryDate, final Time latestDeliveryDate)
    {
        super(sender, sender, null);
        this.internalDemandID = getUniqueID();
        this.product = product;
        this.amount = amount;
        this.earliestDeliveryDate = earliestDeliveryDate;
        this.latestDeliveryDate = latestDeliveryDate;
    }

    /**
     * Returns the amount of products that was demanded.
     * @return double the amount that was demanded.
     */
    public double getAmount()
    {
        return this.amount;
    }

    /**
     * Returns the earliestDeliveryDate.
     * @return double the earliest delivery date for the product.
     */
    public Time getEarliestDeliveryDate()
    {
        return this.earliestDeliveryDate;
    }

    /**
     * Returns the latestDeliveryDate.
     * @return double the latest delivery date for the product.
     */
    public Time getLatestDeliveryDate()
    {
        return this.latestDeliveryDate;
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
