package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.dsol.simtime.TimeUnit;
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
    private double maximumTimeOut = 0.0;

    /** the margin for the fine */
    private double fineMarginPerDay = 0.0;

    /** the fixed fine */
    private double fixedFinePerDay = 0.0;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(ShipmentFineHandlerConsume.class);

    /**
     * constructs a new ShipmentFineHandlerConsume
     * @param owner the owner
     * @param maximumTimeOut the time out
     * @param fineMarginPerDay the fine margin per day
     * @param fixedFinePerDay the fixed fine per day
     */
    public ShipmentFineHandlerConsume(final SupplyChainActor owner, final double maximumTimeOut, final double fineMarginPerDay,
            final double fixedFinePerDay)
    {
        super(owner);
        this.maximumTimeOut = maximumTimeOut;
        this.fineMarginPerDay = fineMarginPerDay;
        this.fixedFinePerDay = fixedFinePerDay;
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface#handleContent(java.io.Serializable)
     */
    public boolean handleContent(final Serializable content)
    {
        if (super.handleContent(content))
        {
            Shipment shipment = (Shipment) content;
            double time = shipment.getSender().getSimulatorTime();
            if ((time > shipment.getOrder().getDeliveryDate())
                    && (time < shipment.getOrder().getDeliveryDate() + this.maximumTimeOut))
            {
                // YES!! we can fine! Finaly we earn some money
                double day = 1.0;
                try
                {
                    day = TimeUnit.convert(1.0, TimeUnit.DAY, getOwner().getSimulator());
                }
                catch (Exception exception)
                {
                    logger.fatal("handleContent", exception);
                }

                double fine = ((shipment.getOrder().getDeliveryDate() - time) / day)
                        * (this.fixedFinePerDay + this.fineMarginPerDay * shipment.getOrder().getPrice());

                // send the bill for the fine
                /*
                 * Bill bill = new Bill(getOwner(), shipment.getSender(), shipment.getInternalDemandID(), shipment.getOrder(),
                 * getOwner().getSimulatorTime() + 0.0, fine, "FINE"); getOwner().sendContent(bill, 0.0);
                 */
                shipment.getSender().getBankAccount().withdrawFromBalance(fine);
                shipment.getReceiver().getBankAccount().addToBalance(fine);
            }
            return true;
        }
        return false;
    }
}
