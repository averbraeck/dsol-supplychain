package nl.tudelft.simulation.supplychain.role.selling.handler;

import nl.tudelft.simulation.supplychain.content.InventoryReleaseRequest;
import nl.tudelft.simulation.supplychain.content.InventoryReservation;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;

/**
 * The InventoryReservationHandler implements the business logic for a supplier who receives an InventoryReservation from the
 * warehouse. This will trigger the sending of an OrderConfirmation, and -- after a certain time -- an InventoryRelease message
 * and an InventoryReleaseFinance message.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InventoryReservationHandler extends ContentHandler<InventoryReservation, SellingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new InventoryReservation handler.
     * @param owner the role belonging to this handler
     */
    public InventoryReservationHandler(final SellingRole owner)
    {
        super("InventoryReservationHandler", owner, InventoryReservation.class);
    }

    @Override
    public boolean handleContent(final InventoryReservation ir)
    {
        if (!isValidContent(ir))
        {
            return false;
        }

        // make and send an OrderConfirmation
        Order order = ir.inventoryReservationRequest().order();
        var orderConfirmation = new OrderConfirmation(order, true);
        sendContent(orderConfirmation, getHandlingTime().draw());

        // schedule the release: delivery time minus transport time
        var releaseTime =
                order.deliveryDate().minus(order.transportOption().estimatedTotalTransportDuration(ir.product().getSku()));
        getSimulator().scheduleEventAbs(releaseTime, this, "releaseInventory", new Object[] {ir});
        return true;
    }

    /**
     * Scheduled method to release the inventory at the delivery time minus transport time.
     * @param ir the inventory reservation relating to the order.
     */
    protected void releaseInventory(final InventoryReservation ir)
    {
        var irr = new InventoryReleaseRequest(ir);
        sendContent(irr, getHandlingTime().draw());
    }
}
