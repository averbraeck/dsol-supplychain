package nl.tudelft.simulation.supplychain.role.transporting.handler;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.content.TransportConfirmation;
import nl.tudelft.simulation.supplychain.content.TransportDelivery;
import nl.tudelft.simulation.supplychain.content.TransportOrder;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingRole;

/**
 * The TransportOrderHandler implements the business logic for a transporter that receives an TransportOrder. sends a
 * TransportConfirmation and schedules the transport itself leading to a TransportDelivery.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportOrderHandler extends ContentHandler<TransportOrder, TransportingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Construct a new TransportOrder handler.
     * @param owner the role belonging to this handler
     */
    public TransportOrderHandler(final TransportingRole owner)
    {
        super("TransportOrderHandler", owner, TransportOrder.class);
    }

    @Override
    public boolean handleContent(final TransportOrder transportOrder)
    {
        if (!isValidContent(transportOrder))
        {
            return false;
        }

        // send a TransportConformation to the finance department
        var tc = new TransportConfirmation(transportOrder);
        sendContent(tc, getHandlingTime().draw());

        // start the delivery process
        getSimulator().scheduleEventNow(this, "executeTransportStep", new Object[] {transportOrder, 0});
        return true;
    }

    /**
     * Execute a transport step, moving the shipment to the next location until it reached its destination.
     * @param transportOrder the transport order containing the transport option and the shipment
     * @param step the step number to execute
     */
    protected void executeTransportStep(final TransportOrder transportOrder, final int step)
    {
        var transportOption = transportOrder.transportQuote().transportOption();
        var shipment = transportOrder.shipment();
        var order = transportOrder.order();
        if (step >= transportOption.getTransportSteps().size())
        {
            // arrival
            shipment.setDelivered();
            var td = new TransportDelivery(getRole().getActor(), shipment.getReceivingActor(), order,
                    transportOrder.shipment());
            sendContent(td, Duration.ZERO);
            return;
        }

        var ts = transportOption.getTransportSteps().get(step);
        var sku = shipment.getProduct().getSku();
        shipment.setTransit(ts.getOrigin(), ts.getDestination());
        Duration transportTime = ts.getEstimatedTransportDuration(sku).plus(ts.getEstimatedLoadingTime(sku))
                .plus(ts.getEstimatedUnloadingTime(sku));
        getSimulator().scheduleEventRel(transportTime, this, "executeTransportStep", new Object[] {transportOrder, step + 1});
    }

}
