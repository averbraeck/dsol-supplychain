package nl.tudelft.simulation.supplychain.role.selling.handler;

import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;

/**
 * An OrderHandler that purchases the goods when receiving an order (no stock).
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderHandlerPurchase extends ContentHandler<Order, SellingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new OrderHandler that purchases the goods when ordered.
     * @param owner the owner of the handler
     */
    public OrderHandlerPurchase(final SellingRole owner)
    {
        super("OrderHandlerPurchase", owner, Order.class);
    }

    @Override
    public boolean handleContent(final Order order)
    {
        if (!isValidContent(order))
        {
            return false;
        }

        // TODO: implement
        
        return true;
    }
}
