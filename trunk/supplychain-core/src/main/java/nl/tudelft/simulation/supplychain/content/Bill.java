package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Money;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The bill represents a document that asks for payment for a product or service. It contains a pointer to an Order to see for
 * which exact order the actor is invoiced.<br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Bill extends Content
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the simulation time for final payment */
    private Time finalPaymentDate;

    /** the price that has to be paid */
    private Money price;

    /** the order to which this bill belongs */
    private Order order;

    /** the description */
    private String description;

    /** whether the bill is paid or not */
    private boolean isPaid = false;

    // TODO implement description of bill (e.g. normal, fine, tax, etc)

    /**
     * Constructs a new Bill.
     * @param sender the sender
     * @param receiver the receiver
     * @param internalDemandID the unique internal demand id of this bill
     * @param order the order the bill is sent for
     * @param finalPaymentDate the final payment date of the bill
     * @param price the amount to be paid
     * @param description the description
     */
    public Bill(final SupplyChainActor sender, final SupplyChainActor receiver, final Serializable internalDemandID,
            final Order order, final Time finalPaymentDate, final Money price, final String description)
    {
        super(sender, receiver, internalDemandID);
        this.finalPaymentDate = finalPaymentDate;
        this.order = order;
        this.price = price;
        this.description = description;
    }

    /**
     * Returns the finalPaymentDate.
     * @return double the final payment date of the bill
     */
    public Time getFinalPaymentDate()
    {
        return this.finalPaymentDate;
    }

    /**
     * Returns the order.
     * @return Order the order to which this bill belongs
     */
    public Order getOrder()
    {
        return this.order;
    }

    /**
     * Returns the price.
     * @return double the amount of money to pay
     */
    public Money getPrice()
    {
        return this.price;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + " for " + this.getOrder().toString() + ", price=" + this.getPrice() + " Description: "
                + this.description;
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.order.getProduct();
    }

    /**
     * @return Returns the description.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @return Returns false if the bill has not been paid yet
     */
    public boolean isPaid()
    {
        return this.isPaid;
    }

    /**
     * @param isPaid true if paid
     */
    public void setPaid(final boolean isPaid)
    {
        this.isPaid = isPaid;
    }
}
