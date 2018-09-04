package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class OrderHandlerMake extends OrderHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Construct a new OrderHandler that makes the goods when ordered.
     * @param owner the owner of the handler
     * @param stock the stock to use to handle the incoming order
     */
    public OrderHandlerMake(final SupplyChainActor owner, final StockInterface stock)
    {
        super(owner, stock);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        // TODO OrderHandlerMake
        return false;
    }
}
