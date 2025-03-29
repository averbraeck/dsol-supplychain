package nl.tudelft.simulation.supplychain.message.trade;

import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.buying.BuyingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;

/**
 * The OrderConfirmation is the response when an Actor sends in an Order to another actor. The conformation can be positive or
 * negative, and when it is negative, it contains a reason for not being able to satisfy the Order. The reason can be a
 * combination of a number of status indicators, e.g. OrderConfirmation.NO_CONFIRM_DATE | OrderConfirmation.NO_CONFIRM_AMOUNT.
 * This would mean: we cannot get the products to you it at the requested date, nor do we have the amount you asked for in
 * stock. Helper methods are available to find out the reason(s) for failing when the confirmation is negative.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderConfirmation extends TradeMessage
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** Confirmation status order confirmed. */
    public static final int CONFIRMED = 0;

    /** Confirmation status order not confirmed: date not possible. */
    public static final int NO_CONFIRM = 1;

    /** textual representation of the confirmation options. */
    public static final String[] CONFIRMATION_OPTIONS = {"confirm the order", "I don't confirm the order"};

    /** the order for which this was the confirmation. */
    private Order order;

    /** indicating whether the order was accepted or not. */
    private int status;

    /**
     * @param sender the sender actor of the message content
     * @param receiver the receving actor of the message content
     * @param internalDemandId the internal demand that triggered the order
     * @param order the order for which this is the confirmation
     * @param status the confirmation status (accepted, not accepted)
     */
    public OrderConfirmation(final SellingActor sender, final BuyingActor receiver, final long internalDemandId,
            final Order order, final int status)
    {
        super(sender, receiver, internalDemandId);
        this.order = order;
        this.status = status;
    }

    /**
     * Method getOrder.
     * @return the Order.
     */
    public Order getOrder()
    {
        return this.order;
    }

    /**
     * Method isAccepted.
     * @return whether the order has been accepted or not.
     */
    public boolean isAccepted()
    {
        return (this.status == OrderConfirmation.CONFIRMED);
    }

    /**
     * @return the status.
     */
    public int getStatus()
    {
        return this.status;
    }

    /**
     * @return the status string.
     */
    public String getStatusString()
    {
        if (isAccepted())
        {
            return "CONFIRMED";
        }

        return "NOT CONFIRMED";
    }

    @Override
    public Product getProduct()
    {
        return this.order.getProduct();
    }

    @Override
    public SellingActor getSender()
    {
        return (SellingActor) super.getSender();
    }

    @Override
    public BuyingActor getReceiver()
    {
        return (BuyingActor) super.getReceiver();
    }

}
