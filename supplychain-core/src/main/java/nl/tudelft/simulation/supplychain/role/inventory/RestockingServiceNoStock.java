package nl.tudelft.simulation.supplychain.role.inventory;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;

/**
 * RestockingServiceNoStock is a service that indicates that restocking will not take place.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RestockingServiceNoStock extends RestockingServiceFixed
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new restocking service that keeps no stock of the product.
     * @param inventory the inventory for which the service holds
     * @param product the product that will not be restocked
     */
    public RestockingServiceNoStock(final Inventory inventory, final Product product)
    {
        super(inventory, product, new DistConstantDuration(Duration.POS_MAXVALUE), false, 0.0, false, Duration.ZERO);
    }

}
