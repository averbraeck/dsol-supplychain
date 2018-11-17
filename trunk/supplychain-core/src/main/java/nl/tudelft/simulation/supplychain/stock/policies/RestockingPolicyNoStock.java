package nl.tudelft.simulation.supplychain.stock.policies;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * RestockingPolicyNoStock.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class RestockingPolicyNoStock extends RestockingPolicyFixed
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param stock
     * @param product
     */
    public RestockingPolicyNoStock(StockInterface stock, Product product)
    {
        super(stock, product, Duration.POS_MAXVALUE, false, 0.0, false, Duration.ZERO);
    }

}

