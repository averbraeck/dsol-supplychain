package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;

/**
 * The abstract DemandHandler class provides the general methods that all DemandHandler classes need, such as checking whether
 * the message is really an Demand.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class DemandHandler extends ContentHandler<Demand, PurchasingRole>
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new DemandHandler.
     * @param id the id of the handler
     * @param owner the Actor that has this handler.
     */
    public DemandHandler(final String id, final PurchasingActor owner)
    {
        super(id, owner.getPurchasingRole(), Demand.class);
    }

}
