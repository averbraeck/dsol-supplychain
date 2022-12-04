package nl.tudelft.simulation.supplychain.policy.shipment;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessageTypes;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;

/**
 * When a Shipment comes in, it has to be handled.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractShipmentPolicy extends SupplyChainPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /**
     * Construct a new ShipmentHandler.
     * @param id String; the id of the ;olicy
     * @param owner SupplyChainActor; the owner of the policy
     */
    public AbstractShipmentPolicy(final String id, final SupplyChainActor owner)
    {
        super(id, owner, TradeMessageTypes.SHIPMENT);
    }

}