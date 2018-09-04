package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;

/**
 * The OrderConfirmationHandler is a simple implementation of the business logic for a OrderConfirmation that comes in. When the
 * confirmation is positive: just ignore it. When it is negative: it is more difficult. The easiest is to go to the 'next'
 * option, e.g. to the next Quote when there were quotes. It is also possible to redo the entire ordering process from scratch.
 * The latter strategy is implemented in this version of the handler. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class OrderConfirmationHandler extends SupplyChainHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** for debugging */
    private static final boolean DEBUG = false;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(OrderConfirmationHandler.class);

    /**
     * Constructs a new OrderConfirmationHandler.
     * @param owner the owner of the handler.
     */
    public OrderConfirmationHandler(final SupplyChainActor owner)
    {
        super(owner);
    }

    /**
     * For the moment, the handler will just reorder the products from the start of the process, in case the confirmation is
     * negative. {@inheritDoc}
     */
    @Override
    public boolean handleContent(final Serializable content)
    {
        OrderConfirmation orderConfirmation = (OrderConfirmation) checkContent(content);
        if (!isValidContent(orderConfirmation))
        {
            return false;
        }
        if (!orderConfirmation.isAccepted())
        {
            if (OrderConfirmationHandler.DEBUG)
            {
                System.out.println("OrderConfirmationHandler: handleContent: !orderConfirmation.isAccepted()");
            }

            InternalDemand oldID = null;
            try
            {
                // TODO: place some business logic here to handle the problem
                oldID = getOwner().getContentStore()
                        .getContentList(orderConfirmation.getInternalDemandID(), InternalDemand.class).get(0);

                if (oldID == null)
                {
                    logger.warn("handleContent",
                            "Could not find InternalDemand for OrderConfirmation " + orderConfirmation.toString());
                    return false;
                }
            }
            catch (Exception exception)
            {
                logger.warn("handleContent",
                        "Could not find InternalDemand for OrderConfirmation " + orderConfirmation.toString());
                return false;
            }

            InternalDemand newID = new InternalDemand(oldID.getSender(), oldID.getProduct(), oldID.getAmount(),
                    oldID.getEarliestDeliveryDate(), oldID.getLatestDeliveryDate());
            getOwner().sendContent(newID, Duration.ZERO);

            // also clean the contentStore for the old internal demand
            getOwner().getContentStore().removeAllContent(orderConfirmation.getInternalDemandID());
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean checkContentClass(final Serializable content)
    {
        return (content instanceof OrderConfirmation);
    }
}
