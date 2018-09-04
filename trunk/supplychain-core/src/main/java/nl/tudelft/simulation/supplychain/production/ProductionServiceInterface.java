package nl.tudelft.simulation.supplychain.production;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.content.ProductionOrder;

/**
 * The ProductionServiceInterface represents a production service. An internal production order can be handed to a class that
 * implements the ProductionServiceInterface, and after some time, depending on the internal logic of the production class, the
 * end products will end up in the stock of the company. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface ProductionServiceInterface extends Serializable
{
    /**
     * @param productionOrder the order to produce
     */
    void acceptProductionOrder(ProductionOrder productionOrder);

    /**
     * Method getExpectedProductionTime
     * @param productionOrder the production order
     * @return returns the expected production time for an order in simulator time units
     */
    double getExpectedProductionTime(final ProductionOrder productionOrder);
}
