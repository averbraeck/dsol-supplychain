package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The Payment follows on a Bill, and it contains a pointer to the Bill for which it is the payment. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Payment extends Content
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the bill to which this payment belongs */
    private Bill bill;

    /** the amount reflecting the payment */
    private double payment;

    /**
     * Constructs a new Payment.
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemandID the internal demand that triggered the supply chain
     * @param bill the bill for which this is the payment
     * @param payment the payment
     */
    public Payment(final SupplyChainActor sender, final SupplyChainActor receiver, final Serializable internalDemandID,
            final Bill bill, final double payment)
    {
        super(sender, receiver, internalDemandID);
        this.bill = bill;
        this.payment = payment;
    }

    /**
     * Returns the payment.
     * @return double returns the amount reflecting the payment
     */
    public double getPayment()
    {
        return this.payment;
    }

    /**
     * Returns the bill.
     * @return Serializable returns the id of the bill this payment belongs to
     */
    public Bill getBill()
    {
        return this.bill;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return super.toString() + ", for " + this.getBill().toString();
    }

    /**
     * @see nl.tudelft.simulation.supplychain.content.Content#getProduct()
     */
    public Product getProduct()
    {
        return this.bill.getProduct();
    }
}
