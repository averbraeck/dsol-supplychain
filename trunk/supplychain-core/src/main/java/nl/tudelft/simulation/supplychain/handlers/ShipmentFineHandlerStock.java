package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.unit.TimeUnit;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * A stocking Shipment handler where a check is performed whether the shipment was delivered on time. If not, a fine is imposed.
 * <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ShipmentFineHandlerStock extends ShipmentHandlerStock
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
    private static Logger logger = LogManager.getLogger(ShipmentFineHandlerStock.class);

    /**
     * constructs a new ShipmentFineHandlerStock
     * @param owner the owner
     * @param stock the stock
     * @param maximumTimeOut the time out
     * @param fineMarginPerDay the fine margin per day
     * @param fixedFinePerDay the fixed fine per day
     */
    public ShipmentFineHandlerStock(final SupplyChainActor owner, final StockInterface stock, final double maximumTimeOut,
            final double fineMarginPerDay, final double fixedFinePerDay)
    {
        super(owner, stock);
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
                // YES!! we can fine! Finally we earn some money
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

                // we are pragmatic
                // send the bill for the fine

                /*
                 * Bill bill = new Bill(getOwner(), shipment.getSender(), shipment.getInternalDemandID(), shipment.getOrder(),
                 * getOwner().getSimulatorTime() + (14.0 * day), fine, "FINE");
                 */
                shipment.getSender().getBankAccount().withdrawFromBalance(fine);
                shipment.getReceiver().getBankAccount().addToBalance(fine);

                // getOwner().sendContent(bill, 0.0);
            }
            return true;
        }
        return false;
    }
}
