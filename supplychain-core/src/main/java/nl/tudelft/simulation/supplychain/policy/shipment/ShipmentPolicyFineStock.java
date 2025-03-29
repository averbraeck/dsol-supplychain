package nl.tudelft.simulation.supplychain.policy.shipment;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;

/**
 * A stocking Shipment handler where a check is performed whether the shipment was delivered on time. If not, a fine is imposed.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShipmentPolicyFineStock extends ShipmentPolicyStock
{
    /** the serial version uid. */
    private static final long serialVersionUID = 11L;

    /** the maximum time out for a shipment. */
    private Duration maximumTimeOut = Duration.ZERO;

    /** the margin for the fine. */
    private double fineMarginPerDay = 0.0;

    /** the fixed fine. */
    private Money fixedFinePerDay = new Money(0.0, MoneyUnit.USD);

    /**
     * constructs a new ShipmentFineHandlerStock.
     * @param owner the owner
     * @param stock the stock
     * @param maximumTimeOut the time out
     * @param fineMarginPerDay the fine margin per day
     * @param fixedFinePerDay the fixed fine per day
     */
    public ShipmentPolicyFineStock(final Role owner, final Inventory stock, final Duration maximumTimeOut,
            final double fineMarginPerDay, final Money fixedFinePerDay)
    {
        super(owner, stock);
        this.maximumTimeOut = maximumTimeOut;
        this.fineMarginPerDay = fineMarginPerDay;
        this.fixedFinePerDay = fixedFinePerDay;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Shipment shipment)
    {
        if (super.handleContent(shipment))
        {
            Time time = shipment.getSender().getSimulatorTime();
            if ((time.gt(shipment.getOrder().getDeliveryDate()))
                    && (time.lt(shipment.getOrder().getDeliveryDate().plus(this.maximumTimeOut))))
            {
                // YES!! we can fine! Finally we earn some money
                Money fine = (this.fixedFinePerDay.plus(shipment.getOrder().getPrice().multiplyBy(this.fineMarginPerDay)))
                        .multiplyBy((shipment.getOrder().getDeliveryDate().minus(time).getInUnit(DurationUnit.DAY)));

                /*-
                 * TODO: send the bill for the fine
                 * Bill bill = new Bill(getOwner(), shipment.getSender(), shipment.getInternalDemandID(), shipment.getOrder(),
                 * getOwner().getSimulatorTime() + (14.0 * day), fine, "FINE");
                 * sendMessage(bill, Duration.ZERO);
                 */

                // we are pragmatic -- just book it through the bank...
                shipment.getSender().getBankAccount().withdrawFromBalance(fine);
                shipment.getReceiver().getBankAccount().addToBalance(fine);
            }
            return true;
        }
        return false;
    }
}
