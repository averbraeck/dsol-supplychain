package nl.tudelft.simulation.supplychain.policy.orderconfirmation;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;

/**
 * An OrderConfirmationFineHandler checks whether a promised delivery is on time or even delivered at all. If too late, a fine
 * will be imposed. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class OrderConfirmationPolicyFine extends OrderConfirmationPolicy
{
    /** the serial version uid */
    private static final long serialVersionUID = 11L;

    /** the maximum time out for a shipment */
    private Duration maximumTimeOut = Duration.ZERO;

    /** the margin for the fine */
    private double fineMargin = 0.0;

    /** the fixed fine */
    private Money fixedFine = new Money(0.0, MoneyUnit.USD);

    /**
     * constructs a new OrderConfirmationFineHandler
     * @param owner the owner
     * @param maximumTimeOut the time out
     * @param fineMargin the margin
     * @param fixedFine the fixed fine
     */
    public OrderConfirmationPolicyFine(final SupplyChainActor owner, final Duration maximumTimeOut, final double fineMargin,
            final Money fixedFine)
    {
        super(owner);
        this.maximumTimeOut = maximumTimeOut;
        this.fineMargin = fineMargin;
        this.fixedFine = fixedFine;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (super.handleContent(content))
        {
            OrderConfirmation orderConfirmation = (OrderConfirmation) content;
            if (orderConfirmation.isAccepted())
            {
                try
                {
                    orderConfirmation.getSender().getSimulator().scheduleEventRel(
                            orderConfirmation.getOrder().getDeliveryDate().minus(getOwner().getSimulatorTime())
                                    .plus(this.maximumTimeOut),
                            this, this, "checkShipment", new Serializable[] { orderConfirmation });
                }
                catch (Exception exception)
                {
                    Logger.error(exception, "handleContent");
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
            Money fine = this.fixedFine.plus(orderConfirmation.getOrder().getPrice().multiplyBy(this.fineMargin));

            /*-
            // TODO: send a bill for the fine instead of direct booking through the bank
            System.err.println("BILL FOR SUPPLIER ORDERCONF FINE, ACTOR " + getOwner());
            // send the bill for the fine
            Bill bill = new Bill(getOwner(), orderConfirmation.getSender(), orderConfirmation.getInternalDemandID(),
                    orderConfirmation.getOrder(), getOwner().getSimulatorTime().plus(new Duration(14.0, DurationUnit.DAY)),
                    fine, "FINE - LATE PAYMENT");
            getOwner().sendContent(bill, Duration.ZERO);
            */

            orderConfirmation.getSender().getBankAccount().withdrawFromBalance(fine);
            orderConfirmation.getReceiver().getBankAccount().addToBalance(fine);
        }

    }
}
