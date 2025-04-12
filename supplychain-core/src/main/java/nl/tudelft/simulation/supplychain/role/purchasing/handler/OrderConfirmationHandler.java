package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;

/**
 * The OrderConfirmationHandler is a simple implementation of the business logic for a OrderConfirmation that comes in. When the
 * confirmation is positive: just ignore it. When it is negative: it is more difficult. The easiest is to go to the 'next'
 * option, e.g. to the next Quote when there were quotes. It is also possible to redo the entire ordering process from scratch.
 * The latter strategy is implemented in this version of the handler.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderConfirmationHandler extends ContentHandler<OrderConfirmation, PurchasingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Constructs a new OrderConfirmationHandler.
     * @param owner the owner of the handler.
     */
    public OrderConfirmationHandler(final PurchasingRole owner)
    {
        super("OrderConfirmationHandler", owner, OrderConfirmation.class);
    }

    /**
     * For the moment, the handler will just reorder the products from the start of the process, in case the confirmation is
     * negative.<br>
     * {@inheritDoc}
     */
    @Override
    public boolean handleContent(final OrderConfirmation orderConfirmation)
    {
        if (!isValidContent(orderConfirmation))
        {
            return false;
        }
        if (!orderConfirmation.confirmed())
        {
            Demand oldDemand = null;
            var demandList = getActor().getContentStore().getContentList(orderConfirmation.groupingId(), Demand.class);
            if (demandList.size() == 0)
            {
                Logger.warn("handleContent", "Could not find Demand for OrderConfirmation " + orderConfirmation.toString());
                return false;
            }
            oldDemand = demandList.get(0);
            Demand newID = new Demand(oldDemand.sender(), oldDemand.product(), oldDemand.amount(),
                    oldDemand.earliestDeliveryDate(), oldDemand.latestDeliveryDate());
            sendContent(newID, Duration.ZERO);

            // also clean the messageStore for the old demand
            getActor().getContentStore().removeAllContent(orderConfirmation.groupingId());
        }
        return true;
    }

}
