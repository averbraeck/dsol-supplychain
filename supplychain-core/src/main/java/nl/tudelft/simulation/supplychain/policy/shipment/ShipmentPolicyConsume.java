package nl.tudelft.simulation.supplychain.policy.shipment;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;

/**
 * When a Shipment comes in, consume it. In other words and in terms of the supply chain simulation: do nothing...
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShipmentPolicyConsume extends ShipmentPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new ShipmentHandlerConsume handler.
     * @param owner Role; the owner of the policy
     */
    public ShipmentPolicyConsume(final Role owner)
    {
        super("ShipmentPolicyConsume", owner);
    }

    /**
     * Do nothing with the incoming cargo. <br>
     * {@inheritDoc}
     */
    @Override
    public boolean handleContent(final Shipment shipment)
    {
        if (!isValidMessage(shipment))
        {
            return false;
        }
        shipment.setInTransit(false);
        shipment.setDelivered(true);
        // do nothing
        return true;
    }
}
