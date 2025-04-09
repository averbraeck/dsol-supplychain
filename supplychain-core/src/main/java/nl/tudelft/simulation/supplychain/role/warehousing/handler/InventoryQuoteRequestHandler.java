package nl.tudelft.simulation.supplychain.role.warehousing.handler;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.content.InventoryQuote;
import nl.tudelft.simulation.supplychain.content.InventoryQuoteRequest;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;

/**
 * The InventoryQuoteRequestHandler implements the business logic for a warehouse that receives an InventoryQuoteRequest. It
 * checks whether inventory is available, or can be made available, and responds accordingly.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InventoryQuoteRequestHandler extends ContentHandler<InventoryQuoteRequest, WarehousingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the reaction time of the handler in simulation time units. */
    private DistContinuousDuration handlingTime;

    /**
     * Construct a new RFQ handler.
     * @param owner the role belonging to this handler
     * @param handlingTime the distribution of the time to react on the RFQ
     */
    public InventoryQuoteRequestHandler(final WarehousingRole owner, final DistContinuousDuration handlingTime)
    {
        super("InventoryQuoteRequestHandler", owner, InventoryQuoteRequest.class);
        Throw.whenNull(handlingTime, "handlingTime cannot be null");
        this.handlingTime = handlingTime;
    }

    @Override
    public boolean handleContent(final InventoryQuoteRequest iqr)
    {
        if (!isValidContent(iqr))
        {
            return false;
        }
        // Is the inventory for the product in stock?
        boolean available = getRole().getInventory().getVirtualAmount(iqr.product()) > iqr.amount();
        Money priceWithoutProfit =
                available ? getRole().getInventory().getUnitPrice(iqr.product()).multiplyBy(iqr.amount()) : null;
        Time timeAvailable = available ? getSimulatorTime() : null; // now
        var inventoryQuote = new InventoryQuote(iqr, available, priceWithoutProfit, timeAvailable);
        sendContent(inventoryQuote, this.handlingTime.draw());
        return true;
    }

    /**
     * @param handlingTime The handlingTime to set.
     */
    public void setHandlingTime(final DistContinuousDuration handlingTime)
    {
        this.handlingTime = handlingTime;
    }

}
