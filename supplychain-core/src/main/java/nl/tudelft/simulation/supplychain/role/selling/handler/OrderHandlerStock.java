package nl.tudelft.simulation.supplychain.role.selling.handler;

import nl.tudelft.simulation.supplychain.content.InventoryReservationRequest;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;

/**
 * The most simple form of an OrderHandler that reserves the products in the inventory, confirms if they are available, and
 * releases the products at the requested date minus the transportation time.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderHandlerStock extends ContentHandler<Order, SellingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new OrderHandler that takes the goods from stock when ordered.
     * @param owner the owner of the handler
     */
    public OrderHandlerStock(final SellingActor owner)
    {
        super("OrderHandlerStock", owner.getSellingRole(), Order.class);
    }

    @Override
    public boolean handleContent(final Order order)
    {
        if (!isValidContent(order))
        {
            return false;
        }

        // send out an InventoryReservationRequest
        var irr = new InventoryReservationRequest(getActor(), getActor(), order);
        sendContent(irr, getHandlingTime().draw());
        return true;
    }

    @Override
    public SellingActor getActor()
    {
        return (SellingActor) super.getActor();
    }

}
