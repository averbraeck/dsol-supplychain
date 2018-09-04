package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.unit.dist.DistConstantDurationUnit;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * The InternalDemandHandlerYP is a simple implementation of the business logic to handle a request for new products through a
 * yellow page request. When receiving the internal demand, it just creates an YP request, without a given time delay. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InternalDemandHandlerYP extends InternalDemandHandler
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
    public InternalDemandHandlerYP(final SupplyChainActor owner, final DistContinuousDurationUnit handlingTime,
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
    public InternalDemandHandlerYP(final SupplyChainActor owner, final Duration handlingTime, final SupplyChainActor yp,
            final Length maximumDistance, final int maximumNumber, final StockInterface stock)
    {
        this(owner, new DistConstantDurationUnit(handlingTime), yp, maximumDistance, maximumNumber, stock);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        InternalDemand internalDemand = (InternalDemand) checkContent(content);
        if (!isValidContent(internalDemand))
        {
            return false;
        }
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
