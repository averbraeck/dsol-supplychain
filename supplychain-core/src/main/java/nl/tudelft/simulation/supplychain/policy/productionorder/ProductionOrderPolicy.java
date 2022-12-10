package nl.tudelft.simulation.supplychain.policy.productionorder;

import nl.tudelft.simulation.supplychain.message.trade.ProductionOrder;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;
import nl.tudelft.simulation.supplychain.role.producing.ProducingActorInterface;
import nl.tudelft.simulation.supplychain.role.producing.ProducingRole;

/**
 * Handles ProductionOrders.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ProductionOrderPolicy extends SupplyChainPolicy<ProductionOrder>
{
    /** Serial version ID. */
    private static final long serialVersionUID = 20221201L;

    /** the producing role. */
    private final ProducingRole producingRole;

    /**
     * constructs a new ProductionOrderHandler.
     * @param owner the owner of the production order handler
     */
    public ProductionOrderPolicy(final ProducingActorInterface owner)
    {
        super("ProductionOrderPolicy", owner, ProductionOrder.class);
        this.producingRole = owner.getProducingRole();
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final ProductionOrder productionOrder)
    {
        return this.producingRole.acceptProductionOrder(productionOrder);
    }

}
