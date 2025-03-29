package nl.tudelft.simulation.supplychain.message.trade;

import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * The Payment follows on a Bill, and it contains a pointer to the Bill for which it is the payment.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Payment extends TradeMessage
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the bill to which this payment belongs. */
    private Bill bill;

    /** the amount reflecting the payment. */
    private Money payment;

    /**
     * Constructs a new Payment.
     * @param sender FinancingActor; the sender actor of the message content
     * @param receiver FinancingActor; the receving actor of the message content
     * @param internalDemandId the internal demand that triggered the supply chain
     * @param bill the bill for which this is the payment
     * @param payment the payment
     */
    public Payment(final FinancingActor sender, final FinancingActor receiver, final long internalDemandId, final Bill bill,
            final Money payment)
    {
        super(sender, receiver, internalDemandId);
        this.bill = bill;
        this.payment = payment;
    }

    /**
     * Return the payment.
     * @return double returns the amount reflecting the payment
     */
    public Money getPayment()
    {
        return this.payment;
    }

    /**
     * Return the bill.
     * @return Serializable returns the id of the bill this payment belongs to
     */
    public Bill getBill()
    {
        return this.bill;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getBill().toString();
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.bill.getProduct();
    }

    /** {@inheritDoc} */
    @Override
    public FinancingActor getSender()
    {
        return (FinancingActor) super.getSender();
    }

    /** {@inheritDoc} */
    @Override
    public FinancingActor getReceiver()
    {
        return (FinancingActor) super.getReceiver();
    }

}
