package nl.tudelft.simulation.supplychain.policy.shipment;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;

/**
 * When a Shipment comes in, it has to be handled.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class ShipmentPolicy extends SupplyChainPolicy<Shipment>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Construct a new ShipmentPolicy.
     * @param id the id of the policy
     * @param owner the owner of the policy
     */
    public ShipmentPolicy(final String id, final Role owner)
    {
        super(id, owner, Shipment.class);
    }

}
