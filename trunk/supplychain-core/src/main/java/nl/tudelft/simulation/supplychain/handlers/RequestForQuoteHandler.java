package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.dsol.simtime.TimeUnit;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.supplychain.actor.Trader;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * The RequestForQuotehandler implements the business logic for a supplier who receives a RequestForQuote. The most simple
 * version answers yes if the product is on stock or ordered, and bases the price on the average costs of the items on stock,
 * after adding a fixed, but changeable, profit margin. The answer is no if the product is not on stock, nor ordered. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class RequestForQuoteHandler extends SupplyChainHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the stock on which checks can take place */
    protected StockInterface stock;

    /** the reaction time of the handler in simulation time units */
    protected DistContinuous handlingTime;

    /** the profit margin to use in the quotes, 1.0 is no profit */
    protected double profitMargin;

    /** the transport mode */
    protected TransportMode transportMode;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(RequestForQuoteHandler.class);

    /**
     * Construct a new RFQ handler.
     * @param owner a trader in this case as only traders handle RFQs
     * @param stock the stock to check for products when quoting
     * @param profitMargin the profit margin to use; 1.0 is no profit
     * @param handlingTime the distribution of the time to react on the RFQ
     * @param transportMode the default transport mode
     */
    public RequestForQuoteHandler(final Trader owner, final StockInterface stock, final double profitMargin,
            final DistContinuous handlingTime, final TransportMode transportMode)
    {
        super(owner);
        this.stock = stock;
        this.handlingTime = handlingTime;
        this.profitMargin = profitMargin;
        this.transportMode = transportMode;
    }

    /**
     * Construct a new RFQ handler.
     * @param owner a trader in this case as only traders handle RFQs
     * @param stock the stock to check for products when quoting
     * @param profitMargin the profit margin to use; 1.0 is no profit
     * @param handlingTime the constant time to react on the RFQ
     * @param transportMode the default transport mode
     */
    public RequestForQuoteHandler(final Trader owner, final StockInterface stock, final double profitMargin,
            final double handlingTime, final TransportMode transportMode)
    {
        this(owner, stock, profitMargin, new DistConstant(new Java2Random(), handlingTime), transportMode);
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface#handleContent(java.io.Serializable) The default implementation is an
     *      opportunistic one: send a positive answer after a certain time if the trader has the product on stock or ordered. Do
     *      not look at the required quantity of the product, as the Trader might still get enough units of the product on time.
     *      React negative if the actual plus ordered amount equals zero.
     */
    public boolean handleContent(final Serializable content)
    {
        RequestForQuote rfq = (RequestForQuote) checkContent(content);
        if (!isValidContent(rfq))
        {
            return false;
        }
        Product product = rfq.getProduct();
        // calculate the expected transportation time (in hours)
        // add half a day for handling to be sure it arrives on time
        double shippingTimeHours = this.transportMode.transportTime(rfq.getSender(), rfq.getReceiver()) + 12.0;
        double shippingTime = 0.0;
        try
        {
            shippingTime = TimeUnit.convert(shippingTimeHours, TimeUnit.HOUR, getOwner().getSimulator());
        }
        catch (RemoteException exception)
        {
            logger.fatal("handleContent", exception);
        }
        double weight = rfq.getAmount() * rfq.getProduct().getAverageUnitWeight();
        double transportCosts = this.transportMode.transportCosts(rfq.getSender(), rfq.getReceiver(), weight);
        // react with a Quote. First calculate the price
        double price = rfq.getAmount() * this.stock.getUnitPrice(product) * this.profitMargin + transportCosts;
        // then look at the delivery date
        double proposedShippingDate = Math.max(getOwner().getSimulatorTime(), rfq.getEarliestDeliveryDate() - shippingTime);
        // construct the quote
        Quote quote = new Quote(getOwner(), rfq.getSender(), rfq.getInternalDemandID(), rfq, product, rfq.getAmount(), price,
                proposedShippingDate, this.transportMode);
        getOwner().sendContent(quote, this.handlingTime.draw());
        return true;
    }

    /**
     * @see nl.tudelft.simulation.supplychain.handlers.SupplyChainHandler#checkContentClass(java.io.Serializable)
     */
    protected boolean checkContentClass(final Serializable content)
    {
        return (content instanceof RequestForQuote);
    }

    /**
     * @param handlingTime The handlingTime to set.
     */
    public void setReactionTime(final DistContinuous handlingTime)
    {
        this.handlingTime = handlingTime;
    }

    /**
     * @param handlingTime The handlingTime to set.
     */
    public void setHandlingTime(final DistContinuous handlingTime)
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
