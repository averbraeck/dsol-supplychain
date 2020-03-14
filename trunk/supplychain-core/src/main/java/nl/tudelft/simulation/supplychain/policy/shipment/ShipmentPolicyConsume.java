package nl.tudelft.simulation.supplychain.policy.shipment;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Shipment;

/**
 * When a Shipment comes in, consume it. In other words and in terms of the supply chain simulation: do nothing... <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ShipmentPolicyConsume extends ShipmentPolicy
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Construct a new ShipmentHandlerConsume handler.
     * @param owner the owner of the handler
     */
    public ShipmentPolicyConsume(final SupplyChainActor owner)
    {
        super(owner);
    }

    /**
     * Do nothing with the incoming cargo. <br>
     * {@inheritDoc}
     */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (!isValidContent(content))
        {
            return false;
        }
        Shipment shipment = (Shipment) content;
        shipment.setInTransit(false);
        shipment.setDelivered(true);
        // do nothing
        return true;
    }
}
