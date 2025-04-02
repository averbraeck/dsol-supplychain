package nl.tudelft.simulation.supplychain.handler.demand;

import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;
import nl.tudelft.simulation.supplychain.role.inventory.Inventory;

/**
 * The DemandHandlerYP is a simple implementation of the business logic to handle a request for new products through a yellow
 * page request. When receiving the demand, it just creates an YP request, without a given time delay.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemandHandlerYP extends DemandHandler
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the yellow page actor to use. */
    private Actor yp;

    /** maximum distance to use in the search. */
    private Length maximumDistance;

    /** maximum number of actors to return. */
    private int maximumNumber;

    /**
     * Constructs a new DemandHandlerYP.
     * @param owner the owner of the demand
     * @param handlingTime the handling time distribution delay to use
     * @param yp the Actor that provides the yp service
     * @param maximumDistance the search distance to use for all products
     * @param maximumNumber the max number of suppliers to return
     * @param stock the stock for being able to change the ordered amount
     */
    public DemandHandlerYP(final Role owner, final DistContinuousDuration handlingTime, final Actor yp,
            final Length maximumDistance, final int maximumNumber, final Inventory stock)
    {
        super("DemandHandlerYP", owner, handlingTime, stock);
        this.yp = yp;
        this.maximumDistance = maximumDistance;
        this.maximumNumber = maximumNumber;
    }

    @Override
    public boolean handleContent(final Demand demand)
    {
        if (!isValidMessage(demand))
        {
            return false;
        }
        if (super.inventory != null)
        {
            super.inventory.changeOrderedAmount(demand.getProduct(), demand.getAmount());
        }
        // create a YellowPageRequest
        YellowPageRequest ypRequest = new YellowPageRequest(getActor(), this.yp, demand.getUniqueId(), demand.getProduct(),
                this.maximumDistance, this.maximumNumber);
        // and send it out immediately
        sendMessage(ypRequest, this.handlingTime.draw());
        return true;
    }
}
