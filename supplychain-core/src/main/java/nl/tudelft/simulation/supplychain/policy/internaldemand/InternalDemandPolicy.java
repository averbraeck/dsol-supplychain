package nl.tudelft.simulation.supplychain.policy.internaldemand;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;
import nl.tudelft.simulation.supplychain.role.inventory.Inventory;

/**
 * The abstract InternalDemandPolicy class provides the general methods that all InternalDemandPolicy classes need, such as
 * checking whether the message is really an InternalDemand.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class InternalDemandPolicy extends SupplyChainPolicy<InternalDemand>
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the handling time distribution to handle internal demand. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DistContinuousDuration handlingTime;

    /** the inventory for changing 'ordered amount. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Inventory inventory;

    /**
     * Construct a new InternalDemandPolicy.
     * @param id the id of the policy
     * @param owner the Role that has this policy.
     * @param handlingTime the distribution of the time to handle an internal demand
     * @param inventory the inventory for being able to change the ordered amount
     */
    public InternalDemandPolicy(final String id, final Role owner,
            final DistContinuousDuration handlingTime, final Inventory inventory)
    {
        super(id, owner, InternalDemand.class);
        this.handlingTime = handlingTime;
        this.inventory = inventory;
    }

    /**
     * @param handlingTime The handlingTime to set.
     */
    public void setHandlingTime(final DistContinuousDuration handlingTime)
    {
        this.handlingTime = handlingTime;
    }

}
