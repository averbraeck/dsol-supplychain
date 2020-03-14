package nl.tudelft.simulation.supplychain.policy.internaldemand;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.unit.dist.DistConstantDuration;

/**
 * The InternalDemandHandlerYP is a simple implementation of the business logic to handle a request for new products through a
 * yellow page request. When receiving the internal demand, it just creates an YP request, without a given time delay. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InternalDemandPolicyYP extends InternalDemandPolicy
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the yellow page actor to use */
    private SupplyChainActor yp;

    /** maximum distance to use in the search */
    private Length maximumDistance;

    /** maximum number of actors to return */
    private int maximumNumber;

    /**
     * Constructs a new InternalDemandHandlerYP
     * @param owner the owner of the internal demand
     * @param handlingTime the handling time distribution delay to use
     * @param yp the SupplyChainActor that provides the yp service
     * @param maximumDistance the search distance to use for all products
     * @param maximumNumber the max number of suppliers to return
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandPolicyYP(final SupplyChainActor owner, final DistContinuousDuration handlingTime,
            final SupplyChainActor yp, final Length maximumDistance, final int maximumNumber, final StockInterface stock)
    {
        super(owner, handlingTime, stock);
        this.yp = yp;
        this.maximumDistance = maximumDistance;
        this.maximumNumber = maximumNumber;
    }

    /**
     * Constructs a new InternalDemandHandlerYP
     * @param owner the owner of the internal demand
     * @param handlingTime the constant handling time delay to use
     * @param yp the SupplyChainActor that provides the yp service
     * @param maximumDistance the search distance to use for all products
     * @param maximumNumber the max number of suppliers to return
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandPolicyYP(final SupplyChainActor owner, final Duration handlingTime, final SupplyChainActor yp,
            final Length maximumDistance, final int maximumNumber, final StockInterface stock)
    {
        this(owner, new DistConstantDuration(handlingTime), yp, maximumDistance, maximumNumber, stock);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (!isValidContent(content))
        {
            return false;
        }
        InternalDemand internalDemand = (InternalDemand) content;
        if (super.stock != null)
        {
            super.stock.changeOrderedAmount(internalDemand.getProduct(), internalDemand.getAmount());
        }
        // create a YellowPageRequest
        YellowPageRequest ypRequest = new YellowPageRequest(getOwner(), this.yp, internalDemand.getUniqueID(),
                internalDemand.getProduct(), this.maximumDistance, this.maximumNumber);
        // and send it out immediately
        getOwner().sendContent(ypRequest, this.handlingTime.draw());
        return true;
    }
}
