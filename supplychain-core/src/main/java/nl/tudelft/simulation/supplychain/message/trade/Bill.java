package nl.tudelft.simulation.supplychain.message.trade;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * The bill represents a document that asks for payment for a product or service. It contains a pointer to an Order to see for
 * which exact order the actor is invoiced.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Bill extends TradeMessage
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the simulation time for final payment. */
    private Time finalPaymentDate;

    /** the price that has to be paid. */
    private Money price;

    /** the order to which this bill belongs. */
    private Order order;

    /** the description. */
    private String description;

    /** whether the bill is paid or not. */
    private boolean isPaid = false;

    /**
     * Constructs a new Bill.
     * @param sender the sender
     * @param receiver the receiver
     * @param internalDemandId the unique internal demand id of this bill
     * @param order the order the bill is sent for
     * @param finalPaymentDate the final payment date of the bill
     * @param price the amount to be paid
     * @param description the description
     */
    public Bill(final FinancingActor sender, final FinancingActor receiver, final long internalDemandId, final Order order,
            final Time finalPaymentDate, final Money price, final String description)
    {
        super(sender, receiver, internalDemandId);
        this.finalPaymentDate = finalPaymentDate;
        this.order = order;
        this.price = price;
        this.description = description;
    }

    /**
     * Return the finalPaymentDate.
     * @return double the final payment date of the bill
     */
    public Time getFinalPaymentDate()
    {
        return this.finalPaymentDate;
    }

    /**
     * Return the order.
     * @return Order the order to which this bill belongs
     */
    public Order getOrder()
    {
        return this.order;
    }

    /**
     * Return the price.
     * @return double the amount of money to pay
     */
    public Money getPrice()
    {
        return this.price;
    }

    @Override
    public String toString()
    {
        return super.toString() + " for " + this.getOrder().toString() + ", price=" + this.getPrice() + " Description: "
                + this.description;
    }

    @Override
    public Product getProduct()
    {
        return this.order.getProduct();
    }

    /**
     * @return the description.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @return false if the bill has not been paid yet
     */
    public boolean isPaid()
    {
        return this.isPaid;
    }

    /**
     * @param paid true if paid
     */
    public void setPaid(final boolean paid)
    {
        this.isPaid = paid;
    }

    @Override
    public FinancingActor getSender()
    {
        return (FinancingActor) super.getSender();
    }

    @Override
    public FinancingActor getReceiver()
    {
        return (FinancingActor) super.getReceiver();
    }
        
}
