package nl.tudelft.simulation.supplychain.policy.internaldemand;

import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.inventory.InventoryInterface;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageRequest;

/**
 * The InternalDemandPolicyYP is a simple implementation of the business logic to handle a request for new products through a
 * yellow page request. When receiving the internal demand, it just creates an YP request, without a given time delay.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InternalDemandPolicyYP extends AbstractInternalDemandPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the yellow page actor to use. */
    private SupplyChainActor yp;

    /** maximum distance to use in the search. */
    private Length maximumDistance;

    /** maximum number of actors to return. */
    private int maximumNumber;

    /**
     * Constructs a new InternalDemandPolicyYP.
     * @param owner the owner of the internal demand
     * @param handlingTime the handling time distribution delay to use
     * @param yp the SupplyChainActor that provides the yp service
     * @param maximumDistance the search distance to use for all products
     * @param maximumNumber the max number of suppliers to return
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandPolicyYP(final SupplyChainActor owner, final DistContinuousDuration handlingTime,
            final SupplyChainActor yp, final Length maximumDistance, final int maximumNumber, final InventoryInterface stock)
    {
        super("InternalDemandPolicyYP", owner, handlingTime, stock);
        this.yp = yp;
        this.maximumDistance = maximumDistance;
        this.maximumNumber = maximumNumber;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final InternalDemand internalDemand)
    {
        if (!isValidMessage(internalDemand))
        {
            return false;
        }
        if (super.stock != null)
        {
            super.stock.changeOrderedAmount(internalDemand.getProduct(), internalDemand.getAmount());
        }
        // create a YellowPageRequest
        YellowPageRequest ypRequest = new YellowPageRequest(getOwner(), this.yp, internalDemand.getUniqueId(),
                internalDemand.getProduct(), this.maximumDistance, this.maximumNumber);
        // and send it out immediately
        getOwner().sendMessage(ypRequest, this.handlingTime.draw());
        return true;
    }
}
