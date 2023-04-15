package nl.tudelft.simulation.supplychain.policy.orderconfirmation;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.message.trade.OrderConfirmation;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;

/**
 * An OrderConfirmationFineHandler checks whether a promised delivery is on time or even delivered at all. If too late, a fine
 * will be imposed.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderConfirmationPolicyFine extends OrderConfirmationPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 11L;

    /** the maximum time out for a shipment. */
    private Duration maximumTimeOut = Duration.ZERO;

    /** the margin for the fine. */
    private double fineMargin = 0.0;

    /** the fixed fine. */
    private Money fixedFine = new Money(0.0, MoneyUnit.USD);

    /**
     * constructs a new OrderConfirmationFineHandler.
     * @param owner the owner
     * @param maximumTimeOut the time out
     * @param fineMargin the margin
     * @param fixedFine the fixed fine
     */
    public OrderConfirmationPolicyFine(final Role owner, final Duration maximumTimeOut, final double fineMargin,
            final Money fixedFine)
    {
        super(owner);
        this.maximumTimeOut = maximumTimeOut;
        this.fineMargin = fineMargin;
        this.fixedFine = fixedFine;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return "OrderConfirmationPolicyFine";
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final OrderConfirmation orderConfirmation)
    {
        if (super.handleMessage(orderConfirmation))
        {
            if (orderConfirmation.isAccepted())
            {
                try
                {
                    getSimulator()
                            .scheduleEventRel(
                                    orderConfirmation.getOrder().getDeliveryDate().minus(getSimulator().getAbsSimulatorTime())
                                            .plus(this.maximumTimeOut),
                                    this, "checkShipment", new Serializable[] {orderConfirmation});
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
        if (getActor().getMessageStore().getMessageList(orderConfirmation.getInternalDemandId(), Shipment.class).isEmpty())
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
            sendMessage(bill, Duration.ZERO);
            */

            orderConfirmation.getSender().getFinancingRole().getBankAccount().withdrawFromBalance(fine);
            orderConfirmation.getReceiver().getFinancingRole().getBankAccount().addToBalance(fine);
        }
    }
}
