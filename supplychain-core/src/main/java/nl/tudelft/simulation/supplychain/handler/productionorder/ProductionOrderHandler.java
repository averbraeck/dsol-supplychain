package nl.tudelft.simulation.supplychain.handler.productionorder;

import nl.tudelft.simulation.supplychain.content.ProductionOrder;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.manufacturing.ManufacturingRole;

/**
 * Handles ProductionOrders.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ProductionOrderHandler extends ContentHandler<ProductionOrder>
{
    /** Serial version ID. */
    private static final long serialVersionUID = 20221201L;

    /** the producing role. */
    private final ManufacturingRole producingRole;

    /**
     * constructs a new ProductionOrderHandler.
     * @param owner the owner of the production order handler
     */
    public ProductionOrderHandler(final ManufacturingRole owner)
    {
        super("ProductionOrderHandler", owner, ProductionOrder.class);
        this.producingRole = owner;
    }

    @Override
    public boolean handleContent(final ProductionOrder productionOrder)
    {
        return this.producingRole.acceptProductionOrder(productionOrder);
    }

}
