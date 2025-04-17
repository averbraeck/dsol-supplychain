package nl.tudelft.simulation.supplychain.role.selling.handler;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.content.InventoryQuote;
import nl.tudelft.simulation.supplychain.content.QuoteNo;
import nl.tudelft.simulation.supplychain.content.TransportQuoteRequest;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.selling.SellingActorRFQ;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingRoleRFQ;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;

/**
 * The InventoryQuoteHandler implements the business logic for a supplier who receives an InventoryQuote from the warehouse. If
 * inventory is available, a TransportQuoteRequest will be sent. If not, a negative quote will be sent, depending on the
 * settings of the SellingRole.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InventoryQuoteHandler extends ContentHandler<InventoryQuote, SellingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the validity duration of the transport quote request. */
    private Duration transportQuoteRequestValidityDuration = new Duration(24.0, DurationUnit.HOUR);

    /**
     * Construct a new InventoryQuote handler.
     * @param owner the role belonging to this handler
     */
    public InventoryQuoteHandler(final SellingActorRFQ owner)
    {
        super("InventoryQuoteHandler", owner.getSellingRole(), InventoryQuote.class);
    }

    @Override
    public boolean handleContent(final InventoryQuote iq)
    {
        if (!isValidContent(iq))
        {
            return false;
        }
        if (!iq.possible() || getRole().getTransporters().size() == 0)
        {
            if (getRole().isSendNegativeQuotes())
            {
                var quoteNo = new QuoteNo(iq.inventoryQuoteRequest().rfq());
                sendContent(quoteNo, getHandlingTime().draw());
            }
            return true;
        }
        Time cutoffDate = getSimulatorTime().plus(getTransportQuoteRequestValidityDuration());
        getRole().addTransportQuoteRequestRecord(iq, cutoffDate);
        for (TransportingActor transporter : getRole().getTransporters())
        {
            var transportQuoteRequest =
                    new TransportQuoteRequest(getRole().getActor(), transporter, iq.inventoryQuoteRequest().rfq(), cutoffDate);
            sendContent(transportQuoteRequest, getHandlingTime().draw());
            getRole().addSentTransportQuoteRequest(transportQuoteRequest);
        }
        return true;
    }

    /**
     * Return the validity duration of the transport quote request.
     * @return the validity duration of the transport quote request
     */
    protected Duration getTransportQuoteRequestValidityDuration()
    {
        return this.transportQuoteRequestValidityDuration;
    }

    /**
     * Set a new the validity duration of the transport quote request.
     * @param transportQuoteRequestValidityDuration the new the validity duration of the transport quote request
     */
    protected void setTransportQuoteRequestValidityDuration(final Duration transportQuoteRequestValidityDuration)
    {
        this.transportQuoteRequestValidityDuration = transportQuoteRequestValidityDuration;
    }

    @Override
    public SellingRoleRFQ getRole()
    {
        return (SellingRoleRFQ) super.getRole();
    }

}
