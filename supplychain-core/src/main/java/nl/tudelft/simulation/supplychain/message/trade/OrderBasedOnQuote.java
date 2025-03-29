package nl.tudelft.simulation.supplychain.message.trade;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.buying.BuyingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.transport.TransportOption;

/**
 * This implementation of an Order contains a link to a Quote on which the order is based. The Order contains a link to the
 * Quote on which it was based.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderBasedOnQuote extends Order
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the Quote the order is based on. */
    private Quote quote;

    /**
     * The constructor for the OrderBasedOnQuote.
     * @param sender BuyingActor; the sender actor of the message content
     * @param receiver SellingActor; the receving actor of the message content
     * @param deliveryDate the intended delivery date of the products
     * @param quote the quote on which the order is based
     * @param transportOption TransportOption; the accepted transport option
     */
    public OrderBasedOnQuote(final BuyingActor sender, final SellingActor receiver, final Time deliveryDate,
            final Quote quote, final TransportOption transportOption)
    {
        super(sender, receiver, quote.getInternalDemand(), deliveryDate, transportOption);
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
     * @return the quote.
     */
    public Quote getQuote()
    {
        return this.quote;
    }
}
