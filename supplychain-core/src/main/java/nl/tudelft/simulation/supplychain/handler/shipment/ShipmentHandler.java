package nl.tudelft.simulation.supplychain.handler.shipment;

import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.product.Shipment;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;

/**
 * When a Shipment comes in, it has to be handled.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class ShipmentHandler extends ContentHandler<Shipment, WarehousingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Construct a new ShipmentHandler.
     * @param id the id of the handler
     * @param owner the owner of the handler
     */
    public ShipmentHandler(final String id, final WarehousingRole owner)
    {
        super(id, owner, Shipment.class);
    }

}
