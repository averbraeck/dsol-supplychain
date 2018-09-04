package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.MoneyUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Money;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Shipment;

/**
 * * When a Shipment comes in, consume it. In other words and in terms of the supply chain simulation: do nothing... <br>
 * However a check is performed whether the shipment was delivered on time. If not,a fine is imposed. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ShipmentFineHandlerConsume extends ShipmentHandlerConsume
{
    /** the serial version uid */
    private static final long serialVersionUID = 11L;

    /** the maximum time out for a shipment */
    private Duration maximumTimeOut = Duration.ZERO;

    /** the margin for the fine */
    private double fineMarginPerDay = 0.0;

    /** the fixed fine */
    private Money fixedFinePerDay = new Money(0.0, MoneyUnit.USD);

    /**
     * constructs a new ShipmentFineHandlerConsume
     * @param owner the owner
     * @param maximumTimeOut the time out
     * @param fineMarginPerDay the fine margin per day
     * @param fixedFinePerDay the fixed fine per day
     */
    public ShipmentFineHandlerConsume(final SupplyChainActor owner, final Duration maximumTimeOut,
            final double fineMarginPerDay, final Money fixedFinePerDay)
    {
        super(owner);
        this.maximumTimeOut = maximumTimeOut;
        this.fineMarginPerDay = fineMarginPerDay;
        this.fixedFinePerDay = fixedFinePerDay;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (super.handleContent(content))
        {
            Shipment shipment = (Shipment) content;
            Time time = shipment.getSender().getSimulatorTime();
            if (time.gt(shipment.getOrder().getDeliveryDate())
                    && time.lt(shipment.getOrder().getDeliveryDate().plus(this.maximumTimeOut)))
            {
                // YES!! we can fine! Finaly we earn some money
                Money fine = this.fixedFinePerDay
                        .multiplyBy(shipment.getOrder().getDeliveryDate().minus(time).getInUnit(DurationUnit.DAY))
                        .plus(shipment.getOrder().getPrice().multiplyBy(this.fineMarginPerDay));

                /*-
                 // send the bill for the fine
                 Bill bill = new Bill(getOwner(), shipment.getSender(), shipment.getInternalDemandID(), shipment.getOrder(),
                         getOwner().getSimulatorTime(), fine, "FINE"); 
                 getOwner().sendContent(bill, Duration.ZERO);
                 */

                shipment.getSender().getBankAccount().withdrawFromBalance(fine);
                shipment.getReceiver().getBankAccount().addToBalance(fine);
            }
            return true;
        }
        return false;
    }
}
