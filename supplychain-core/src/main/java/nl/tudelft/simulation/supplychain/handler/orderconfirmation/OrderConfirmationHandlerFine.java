package nl.tudelft.simulation.supplychain.handler.orderconfirmation;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;

/**
 * An OrderConfirmationFineHandler checks whether a promised delivery is on time or even delivered at all. If too late, a fine
 * will be imposed.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OrderConfirmationHandlerFine extends OrderConfirmationHandler
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
    public OrderConfirmationHandlerFine(final Role owner, final Duration maximumTimeOut, final double fineMargin,
            final Money fixedFine)
    {
        super(owner);
        this.maximumTimeOut = maximumTimeOut;
        this.fineMargin = fineMargin;
        this.fixedFine = fixedFine;
    }

    @Override
    public String getId()
    {
        return "OrderConfirmationHandlerFine";
    }

    @Override
    public boolean handleContent(final OrderConfirmation orderConfirmation)
    {
        if (super.handleContent(orderConfirmation))
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
        if (getActor().getContentStore().getContentList(orderConfirmation.getDemandId(), Shipment.class).isEmpty())
        {

            // there is still an order, but no shipment... we fine!
            Money fine = this.fixedFine.plus(orderConfirmation.getOrder().getPrice().multiplyBy(this.fineMargin));

            /*-
            // TODO: send a invoice for the fine instead of direct booking through the bank
            System.err.println("BILL FOR SUPPLIER ORDERCONF FINE, ACTOR " + getOwner());
            // send the invoice for the fine
            Invoice invoice = new Invoice(getOwner(), orderConfirmation.getSender(), orderConfirmation.getDemandID(),
                    orderConfirmation.getOrder(), getOwner().getSimulatorTime().plus(new Duration(14.0, DurationUnit.DAY)),
                    fine, "FINE - LATE PAYMENT");
            sendContent(invoice, Duration.ZERO);
            */

            orderConfirmation.getSender().getFinancingRole().getBankAccount().withdrawFromBalance(fine);
            orderConfirmation.getReceiver().getFinancingRole().getBankAccount().addToBalance(fine);
        }
    }
}
