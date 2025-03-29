package nl.tudelft.simulation.supplychain.message.trade;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * A Shipment is the information for an amount of products that can be transferred from the Stock of one actor to the Stock of
 * another actor.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Shipment extends TradeMessage
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the order for which this shipment is sent. */
    private Order order;

    /** the product type. */
    private Product product;

    /** the number of units of products in this cargo. */
    private double amount;

    /** the total value of the cargo. */
    private Money totalCargoValue;

    /** is the shipment in transit? */
    private boolean inTransit = false;

    /** has the shipment been delivered? */
    private boolean delivered = false;

    /**
     * @param sender Actor; the sender actor of the message content
     * @param receiver Actor; the receiving actor of the message content
     * @param internalDemandId internal demand that triggered the process
     * @param order the order for which this is the shipment
     * @param product Product; the product type
     * @param amount double; the number of product units
     * @param totalCargoValue the price of the cargo
     */
    public Shipment(final Actor sender, final Actor receiver, final long internalDemandId,
            final Order order, final Product product, final double amount, final Money totalCargoValue)
    {
        super(sender, receiver, internalDemandId);
        this.order = order;
        this.product = product;
        this.amount = amount;
        this.totalCargoValue = totalCargoValue;
    }

    /**
     * @return the order.
     */
    public Order getOrder()
    {
        return this.order;
    }

    /**
     * @return the delivered.
     */
    public boolean isDelivered()
    {
        return this.delivered;
    }

    /**
     * @param delivered The delivered to set.
     */
    public void setDelivered(final boolean delivered)
    {
        this.delivered = delivered;
    }

    /**
     * @return the inTransit.
     */
    public boolean isInTransit()
    {
        return this.inTransit;
    }

    /**
     * @param inTransit The inTransit to set.
     */
    public void setInTransit(final boolean inTransit)
    {
        this.inTransit = inTransit;
    }

    /**
     * Return the amount.
     * @return double
     */
    public double getAmount()
    {
        return this.amount;
    }

    @Override
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * Return the price per unit.
     * @return double
     */
    public Money getTotalCargoValue()
    {
        return this.totalCargoValue;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", content [" + this.getAmount() + " units of " + this.getProduct().getName() + ", value = "
                + this.getTotalCargoValue() + "], [belongs to " + this.getOrder().toString() + "]";
    }
}
