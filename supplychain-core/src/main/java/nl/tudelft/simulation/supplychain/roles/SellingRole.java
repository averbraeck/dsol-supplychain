package nl.tudelft.simulation.supplychain.roles;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicy;
import nl.tudelft.simulation.supplychain.policy.rfq.RequestForQuotePolicy;

/**
 * The selling role is a role that can handle several types of message content: order and payment in the minimum form. Depending
 * on the type of handling by the seller, several other messages can be handled as well. Examples are to be able to handle an
 * RFQ.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
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
    public SellingRole(final SupplyChainActor owner, final SCSimulatorInterface simulator, final OrderPolicy orderHandler,
            final PaymentPolicy paymentHandler)
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
    public SellingRole(final SupplyChainActor owner, final SCSimulatorInterface simulator,
            final RequestForQuotePolicy rfqHandler, final OrderPolicy orderHandler, final PaymentPolicy paymentHandler)
    {
        super(owner, owner.getName() + "-SELLLING", simulator);
        addContentHandler(Order.class, orderHandler);
        addContentHandler(RequestForQuote.class, rfqHandler);
        addContentHandler(Payment.class, paymentHandler);
    }

}
