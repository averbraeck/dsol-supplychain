package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Money;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * This implementation of an Order contains a link to a Quote on which the order is based. The Order contains a link to the
 * Quote on which it was based. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class OrderBasedOnQuote extends Order
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the Quote the order is based on */
    private Quote quote;

    /**
     * The constructor for the OrderBasedOnQuote
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemandID the internal demand that triggered the order
     * @param deliveryDate the intended delivery date of the products
     * @param quote the quote on which the order is based
     */
    public OrderBasedOnQuote(final SupplyChainActor sender, final SupplyChainActor receiver,
            final Serializable internalDemandID, final Time deliveryDate, final Quote quote)
    {
        super(sender, receiver, internalDemandID, deliveryDate);
        this.quote = quote;
    }

    /** {@inheritDoc} */
    @Override
    public double getAmount()
    {
        return this.quote.getAmount();
    }

    /** {@inheritDoc} */
    @Override
    public Money getPrice()
    {
        return this.quote.getPrice();
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.quote.getProduct();
    }

    /**
     * @return Returns the quote.
     */
    public Quote getQuote()
    {
        return this.quote;
    }
}
