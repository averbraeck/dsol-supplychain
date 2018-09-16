package nl.tudelft.simulation.supplychain.actor.capabilities;

import java.util.Map;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * BuyerYPInterface.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface BuyerOrderInterface extends BuyerInterface
{
    /**
     * get the supplier per product where we initiate the buying chain by placing a direct order.
     * @return the unique supplier per product
     */
    Map<Product, SupplyChainActor> getSupplier();
}
