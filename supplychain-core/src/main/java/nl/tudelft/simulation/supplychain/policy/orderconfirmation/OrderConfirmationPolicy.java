package nl.tudelft.simulation.supplychain.policy.orderconfirmation;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;

/**
 * The OrderConfirmationHandler is a simple implementation of the business logic for a OrderConfirmation that comes in. When the
 * confirmation is positive: just ignore it. When it is negative: it is more difficult. The easiest is to go to the 'next'
 * option, e.g. to the next Quote when there were quotes. It is also possible to redo the entire ordering process from scratch.
 * The latter strategy is implemented in this version of the policy.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderConfirmationHandler extends ContentHandler<OrderConfirmation>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** for debugging. */
    private static final boolean DEBUG = false;

    /**
     * Constructs a new OrderConfirmationHandler.
     * @param owner the owner of the policy.
     */
    public OrderConfirmationPolicy(final Role owner)
    {
        super("OrderConfirmationPolicy", owner, OrderConfirmation.class);
    }

    /**
     * For the moment, the handler will just reorder the products from the start of the process, in case the confirmation is
     * negative.<br>
     * {@inheritDoc}
     */
    @Override
    public boolean handleContent(final OrderConfirmation orderConfirmation)
    {
        if (!isValidMessage(orderConfirmation))
        {
            return false;
        }
        if (!orderConfirmation.isAccepted())
        {
            if (OrderConfirmationPolicy.DEBUG)
            {
                System.out.println("OrderConfirmationHandler: handleContent: !orderConfirmation.isAccepted()");
            }

            InternalDemand oldID = null;
            try
            {
                // TODO: place some business logic here to handle the problem
                oldID = getActor().getContentStore()
                        .getMessageList(orderConfirmation.getInternalDemandId(), InternalDemand.class).get(0);

                if (oldID == null)
                {
                    Logger.warn("handleContent",
                            "Could not find InternalDemand for OrderConfirmation " + orderConfirmation.toString());
                    return false;
                }
            }
            catch (Exception exception)
            {
                Logger.warn("handleContent",
                        "Could not find InternalDemand for OrderConfirmation " + orderConfirmation.toString());
                return false;
            }

            InternalDemand newID = new InternalDemand(oldID.getSender(), oldID.getProduct(), oldID.getAmount(),
                    oldID.getEarliestDeliveryDate(), oldID.getLatestDeliveryDate());
            sendMessage(newID, Duration.ZERO);

            // also clean the messageStore for the old internal demand
            getActor().getContentStore().removeAllMessages(orderConfirmation.getInternalDemandId());
        }
        return true;
    }

}
