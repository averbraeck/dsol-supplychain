package nl.tudelft.simulation.supplychain.handler.shipment;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Shipment;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;

/**
 * A stocking Shipment handler where a check is performed whether the shipment was delivered on time. If not, a fine is imposed.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShipmentHandlerFineStock extends ShipmentHandlerStock
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
    public ShipmentHandlerFineStock(final Role owner, final Inventory stock, final Duration maximumTimeOut,
            final double fineMarginPerDay, final Money fixedFinePerDay)
    {
        super(owner, stock);
        this.maximumTimeOut = maximumTimeOut;
        this.fineMarginPerDay = fineMarginPerDay;
        this.fixedFinePerDay = fixedFinePerDay;
    }

    @Override
    public boolean handleContent(final Shipment shipment)
    {
        if (super.handleContent(shipment))
        {
            Time time = getSimulatorTime();
            if ((time.gt(shipment.order().deliveryDate()))
                    && (time.lt(shipment.order().deliveryDate().plus(this.maximumTimeOut))))
            {
                // YES!! we can fine! Finally we earn some money
                Money fine = (this.fixedFinePerDay.plus(shipment.order().price().multiplyBy(this.fineMarginPerDay)))
                        .multiplyBy((shipment.order().deliveryDate().minus(time).getInUnit(DurationUnit.DAY)));

                /*-
                 * TODO: send the invoice for the fine
                 * Invoice invoice = new Invoice(getOwner(), shipment.getSender(), shipment.getDemandID(), shipment.getOrder(),
                 * getOwner().getSimulatorTime() + (14.0 * day), fine, "FINE");
                 * sendContent(invoice, Duration.ZERO);
                 */

                // we are pragmatic -- just book it through the bank...
                shipment.sender().getFinancingRole().getBank().withdrawFromBalance(shipment.sender(), fine);
                shipment.receiver().getFinancingRole().getBank().addToBalance(shipment.receiver(), fine);
            }
            return true;
        }
        return false;
    }
}
