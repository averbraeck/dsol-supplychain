package nl.tudelft.simulation.supplychain.handler.rfq;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;

/**
 * The RequestForQuotehandler implements the business logic for a supplier who receives a RequestForQuote. The most simple
 * version answers yes if the product is on stock or ordered, and bases the price on the average costs of the items on stock,
 * after adding a fixed, but changeable, profit margin. The answer is no if the product is not on stock, nor ordered.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RequestForQuoteHandler extends ContentHandler<RequestForQuote, SellingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the reaction time of the handler in simulation time units. */
    private DistContinuousDuration handlingTime;

    /** the profit margin to use in the quotes, 1.0 is no profit. */
    private double profitMargin;

    /** the validity duration of the quote. */
    private final Duration validityDuration;

    /**
     * Construct a new RFQ handler.
     * @param owner the role belonging to this handler
     * @param profitMargin the profit margin to use; 1.0 is no profit
     * @param handlingTime the distribution of the time to react on the RFQ
     * @param validityDuration the validity duration of the quote
     */
    public RequestForQuoteHandler(
            final SellingRole owner, final double profitMargin,
            final DistContinuousDuration handlingTime, final Duration validityDuration)
    {
        super("RequestForQuoteHandler", owner, RequestForQuote.class);
        Throw.whenNull(handlingTime, "handlingTime cannot be null");
        Throw.whenNull(profitMargin, "profitMargin cannot be null");
        Throw.whenNull(validityDuration, "validityDuration cannot be null");
        this.handlingTime = handlingTime;
        this.profitMargin = profitMargin;
        this.validityDuration = validityDuration;
    }

    /**
     * The default implementation is an opportunistic one: send a positive answer after a certain time if the trader has the
     * product on stock or ordered. Do not look at the required quantity of the product, as the Trader might still get enough
     * units of the product on time. React negative if the actual plus ordered amount equals zero. <br>
     * {@inheritDoc}
     */
    @Override
    public boolean handleContent(final RequestForQuote rfq)
    {
        if (!isValidContent(rfq))
        {
            return false;
        }
        Product product = rfq.product();
        // calculate the expected transportation time
        Duration shippingDuration = rfq.preferredTransportOption().estimatedTotalTransportDuration(product.getSku());
        Money transportCosts = rfq.preferredTransportOption().estimatedTotalTransportCost(product.getSku());
        // react with a Quote. First calculate the price
        Money price = this.inventory.getUnitPrice(product).multiplyBy(rfq.amount() * this.profitMargin).plus(transportCosts);
        // then look at the delivery date
        Time proposedShippingDate =
                Time.max(getSimulatorTime(), rfq.demand().earliestDeliveryDate().minus(shippingDuration));
        // construct the quote
        Quote quote = new Quote(getRole().getActor(), rfq.sender(), rfq, product, rfq.amount(), price, proposedShippingDate,
                rfq.preferredTransportOption(), getSimulatorTime().plus(this.validityDuration));
        sendContent(quote, this.handlingTime.draw());
        return true;
    }

    /**
     * @param handlingTime The handlingTime to set.
     */
    public void setHandlingTime(final DistContinuousDuration handlingTime)
    {
        this.handlingTime = handlingTime;
    }

    /**
     * @param profitMargin The profitMargin to set.
     */
    public void setProfitMargin(final double profitMargin)
    {
        this.profitMargin = profitMargin;
    }
}
