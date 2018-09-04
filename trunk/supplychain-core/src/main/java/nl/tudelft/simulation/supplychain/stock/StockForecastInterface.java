package nl.tudelft.simulation.supplychain.stock;

import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * Defines the methods that are needed to calculate stock forecasts. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */

public interface StockForecastInterface extends StockInterface
{
    /** An event to indicate that there is a new stock forecast */
    EventType STOCK_FORECAST_UPDATE_EVENT = new EventType("STOCK_FORECAST_UPDATE_EVENT");

    /**
     * Method changeFutureClaimedAmount.
     * @param product the product
     * @param delta the delta (positive or negative)
     * @param time the time the change is scheduled to take place
     * @return boolean success or not
     */
    boolean changeFutureClaimedAmount(Product product, double delta, double time);

    /**
     * Method changeFutureOrderedAmount.
     * @param product the product
     * @param delta the delta (positive or negative)
     * @param time the time the change is scheduled to take place
     * @return boolean success or not
     */
    boolean changeFutureOrderedAmount(Product product, double delta, double time);
}
