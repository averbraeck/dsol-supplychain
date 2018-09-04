package nl.tudelft.simulation.supplychain.stock.policies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * Restocking according to economic order quantity. Needs fixed and variable costs when it has to be implemented. For that
 * reason: still abstract... <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class RestockingPolicyEOQ extends RestockingPolicy
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(RestockingPolicyEOQ.class);

    /**
     * @param stock the stock for which the policy holds
     * @param product the product that has to be restocked
     * @param frequency the frequency distribution for restocking or checking
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicyEOQ(final StockInterface stock, final Product product, final DistContinuousDurationUnit frequency,
            final Duration maxDeliveryTime)
    {
        super(stock, product, frequency, maxDeliveryTime);
    }

    /**
     * @param stock the stock for which the policy holds
     * @param product the product that has to be restocked
     * @param frequency the constant frequency for restocking or checking
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicyEOQ(final StockInterface stock, final Product product, final Duration frequency,
            final Duration maxDeliveryTime)
    {
        super(stock, product, frequency, maxDeliveryTime);
    }

    /** {@inheritDoc} */
    @Override
    protected void checkStockLevel()
    {
        // TODO: EOQ
        logger.warn("checkStockLevel", "EOQ not yet implemented...");
    }
}
