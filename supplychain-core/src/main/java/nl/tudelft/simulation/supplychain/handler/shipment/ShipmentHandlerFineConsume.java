package nl.tudelft.simulation.supplychain.handler.shipment;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;

/**
 * When a Shipment comes in, consume it. In other words and in terms of the supply chain simulation: do nothing... <br>
 * However a check is performed whether the shipment was delivered on time. If not,a fine is imposed.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShipmentHandlerFineConsume extends ShipmentHandlerConsume
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
     * constructs a new ShipmentFineHandlerConsume.
     * @param owner the owner
     * @param maximumTimeOut the time out
     * @param fineMarginPerDay the fine margin per day
     * @param fixedFinePerDay the fixed fine per day
     */
    public ShipmentHandlerFineConsume(final WarehousingRole owner, final Duration maximumTimeOut, final double fineMarginPerDay,
            final Money fixedFinePerDay)
    {
        super(owner);
        this.maximumTimeOut = maximumTimeOut;
        this.fineMarginPerDay = fineMarginPerDay;
        this.fixedFinePerDay = fixedFinePerDay;
    }

    @Override
    public boolean handleContent(final Shipment shipment)
    {
        if (super.handleContent(shipment))
        {
            Time time = shipment.sender().getSimulatorTime();
            if (time.gt(shipment.order().deliveryDate()) && time.lt(shipment.order().deliveryDate().plus(this.maximumTimeOut)))
            {
                // YES!! we can fine! Finaly we earn some money
                Money fine =
                        this.fixedFinePerDay.multiplyBy(shipment.order().deliveryDate().minus(time).getInUnit(DurationUnit.DAY))
                                .plus(shipment.order().price().multiplyBy(this.fineMarginPerDay));

                /*-
                 // send the invoice for the fine
                 Invoice invoice = new Invoice(getOwner(), shipment.getSender(), shipment.getDemandID(), shipment.getOrder(),
                         getOwner().getSimulatorTime(), fine, "FINE"); 
                 sendContent(invoice, Duration.ZERO);
                 */

                shipment.sender().getFinancingRole().getBank().withdrawFromBalance(shipment.sender(), fine);
                shipment.receiver().getFinancingRole().getBank().addToBalance(shipment.receiver(), fine);
            }
            return true;
        }
        return false;
    }
}
