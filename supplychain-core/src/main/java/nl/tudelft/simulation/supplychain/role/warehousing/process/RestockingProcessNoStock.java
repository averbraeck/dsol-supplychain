package nl.tudelft.simulation.supplychain.role.warehousing.process;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * RestockingProcessNoStock is an autonomous process that indicates that restocking will not take place.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RestockingProcessNoStock extends RestockingProcessFixed
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new restocking service that keeps no stock of the product.
     * @param actor the warehousing actor to which the restocking process belongs
     * @param inventory the inventory for which the service holds
     * @param product the product that will not be restocked
     */
    public RestockingProcessNoStock(final WarehousingActor actor, final Inventory inventory, final Product product)
    {
        super(actor, inventory, product, Duration.POS_MAXVALUE, false, 0.0, false, Duration.ZERO);
    }

}
