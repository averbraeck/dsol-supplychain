package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.Shipment;

/**
 * An OrderConfirmationFineHandler checks whether a promised delivery is on time or even delivered at all. If too late, a fine
 * will be imposed. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class OrderConfirmationFineHandler extends OrderConfirmationHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 11L;

    /** the maximum time out for a shipment */
    private double maximumTimeOut = 0.0;

    /** the margin for the fine */
    private double fineMargin = 0.0;

    /** the fixed fine */
    private double fixedFine = 0.0;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(OrderConfirmationFineHandler.class);

    /**
     * constructs a new OrderConfirmationFineHandler
     * @param owner the owner
     * @param maximumTimeOut the time out
     * @param fineMargin the margin
     * @param fixedFine the fixed fine
     */
    public OrderConfirmationFineHandler(final SupplyChainActor owner, final double maximumTimeOut, final double fineMargin,
            final double fixedFine)
    {
        super(owner);
        this.maximumTimeOut = maximumTimeOut;
        this.fineMargin = fineMargin;
        this.fixedFine = fixedFine;
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface#handleContent(java.io.Serializable)
     */
    public boolean handleContent(final Serializable content)
    {
        if (super.handleContent(content))
        {
            OrderConfirmation orderConfirmation = (OrderConfirmation) content;
            if (orderConfirmation.isAccepted())
            {
                try
                {
                    orderConfirmation.getSender().getSimulator().scheduleEvent(
                            orderConfirmation.getOrder().getDeliveryDate() - getOwner().getSimulatorTime()
                                    + this.maximumTimeOut,
                            this, this, "checkShipment", new Serializable[] { orderConfirmation });
                }
                catch (Exception exception)
                {
                    logger.fatal("handleContent", exception);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @param orderConfirmation the order confirmation
     */
    protected void checkShipment(final OrderConfirmation orderConfirmation)
    {
        if (getOwner().getContentStore().getContentList(orderConfirmation.getInternalDemandID(), Shipment.class).isEmpty())
        {

            // there is still an order, but no shipment... we fine!
            double fine = this.fixedFine + this.fineMargin * orderConfirmation.getOrder().getPrice();

            /*
             * System.err.println("BILL FOR SUPPLIER ORDERCONF FINE, ACTOR " + getOwner()); double day = 1.0; try { day =
             * TimeUnit.convert(1.0, TimeUnit.DAY, getOwner() .getSimulator()); } catch (RemoteException exception) {
             * Logger.severe(this, "checkShipment", exception); } // send the bill for the fine Bill bill = new Bill(getOwner(),
             * orderConfirmation.getSender(), orderConfirmation.getInternalDemandID(), orderConfirmation .getOrder(),
             * getOwner().getSimulatorTime() + 14.0 day, fine, "FINE - LATE PAYMENT"); getOwner().sendContent(bill, 0.0);
             */
            orderConfirmation.getSender().getBankAccount().withdrawFromBalance(fine);
            orderConfirmation.getReceiver().getBankAccount().addToBalance(fine);
        }

    }
}
