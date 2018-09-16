package nl.tudelft.simulation.supplychain.roles;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.handlers.OrderHandler;
import nl.tudelft.simulation.supplychain.handlers.PaymentHandler;
import nl.tudelft.simulation.supplychain.handlers.RequestForQuoteHandler;

/**
 * The selling role is a role that can handle several types of message content: order and payment in the minimum form. Depending
 * on the type of handling by the seller, several other messages can be handled as well. Examples are to be able to handle an
 * RFQ. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class SellingRole extends Role implements SellingRoleInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Constructs a new SellingRole for Order - Payment.
     * @param owner the owner this role
     * @param simulator the simulator to schedule on
     * @param orderHandler the order handler
     * @param paymentHandler the payment handler
     */
    public SellingRole(final SupplyChainActor owner, final DEVSSimulatorInterface.TimeDoubleUnit simulator,
            final OrderHandler orderHandler, final PaymentHandler paymentHandler)
    {
        super(owner, owner.getName() + "-SELLLING", simulator);
        addContentHandler(Order.class, orderHandler);
        addContentHandler(Payment.class, paymentHandler);
    }

    /**
     * Constructs a new SellingRole for RFQ - Order - Payment.
     * @param owner the owner this role
     * @param simulator the simulator to schedule on
     * @param rfqHandler the Request for Quote handler
     * @param orderHandler the order handler
     * @param paymentHandler the payment handler
     */
    public SellingRole(final SupplyChainActor owner, final DEVSSimulatorInterface.TimeDoubleUnit simulator,
            final RequestForQuoteHandler rfqHandler, final OrderHandler orderHandler, final PaymentHandler paymentHandler)
    {
        super(owner, owner.getName() + "-SELLLING", simulator);
        addContentHandler(Order.class, orderHandler);
        addContentHandler(RequestForQuote.class, rfqHandler);
        addContentHandler(Payment.class, paymentHandler);
    }

}
