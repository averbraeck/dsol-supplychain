package nl.tudelft.simulation.supplychain.policy.shipment;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * When a Shipment comes in, it just has to be added to the Stock.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShipmentPolicyStock extends ShipmentPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** access to the owner's stock to look at availability of products. */
    protected Inventory stock;

    /**
     * Construct a new ShipmentHandlerStock handler.
     * @param owner Role; the owner of the policy
     * @param stock the stock to use for storing the incoming cargo
     */
    public ShipmentPolicyStock(final Role owner, final Inventory stock)
    {
        super("ShipmentPolicyStock", owner);
        this.stock = stock;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final Shipment shipment)
    {
        if (!isValidMessage(shipment))
        {
            return false;
        }
        // get the cargo from the shipment, and add its contents to the stock
        Product product = shipment.getProduct();
        double amount = shipment.getAmount();
        this.stock.addToInventory(product, amount, shipment.getTotalCargoValue());
        // update the administration
        this.stock.changeOrderedAmount(product, -amount);
        shipment.setInTransit(false);
        shipment.setDelivered(true);
        return true;
    }
}
