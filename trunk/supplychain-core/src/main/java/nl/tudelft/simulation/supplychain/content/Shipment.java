package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * A Shipment is the information for an amount of products that can be transferred from the Stock of one actor to the Stock of
 * another actor. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Shipment extends Content
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the order for which this shipment is sent */
    private Order order;

    /** the product type */
    private Product product;

    /** the number of units of products in this cargo */
    private double amount;

    /** the total value of the cargo */
    private Money totalCargoValue;

    /** is the shipment in transit? */
    private boolean inTransit = false;

    /** has the shipment been delivered? */
    private boolean delivered = false;

    /**
     * @param sender the sender actor of the message content
     * @param receiver the receiving actor of the message content
     * @param internalDemandID internal demand that triggered the process
     * @param order the order for which this is the shipment
     * @param product the product type
     * @param amount the number of product units
     * @param totalCargoValue the price of the cargo
     */
    public Shipment(final SupplyChainActor sender, final SupplyChainActor receiver, final Serializable internalDemandID,
            final Order order, final Product product, final double amount, final Money totalCargoValue)
    {
        super(sender, receiver, internalDemandID);
        this.order = order;
        this.product = product;
        this.amount = amount;
        this.totalCargoValue = totalCargoValue;
    }

    /**
     * @return Returns the order.
     */
    public Order getOrder()
    {
        return this.order;
    }

    /**
     * @return Returns the delivered.
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
     * @return Returns the inTransit.
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
     * Returns the amount.
     * @return double
     */
    public double getAmount()
    {
        return this.amount;
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * Returns the price per unit.
     * @return double
     */
    public Money getTotalCargoValue()
    {
        return this.totalCargoValue;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", content [" + this.getAmount() + " units of " + this.getProduct().getName() + ", value = "
                + this.getTotalCargoValue() + "], [belongs to " + this.getOrder().toString() + "]";
    }
}
