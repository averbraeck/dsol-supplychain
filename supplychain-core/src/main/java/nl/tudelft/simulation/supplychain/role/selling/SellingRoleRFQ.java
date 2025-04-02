package nl.tudelft.simulation.supplychain.role.selling;

import nl.tudelft.simulation.supplychain.handler.order.OrderHandler;
import nl.tudelft.simulation.supplychain.handler.payment.PaymentHandler;
import nl.tudelft.simulation.supplychain.handler.rfq.RequestForQuoteHandler;

/**
 * The selling role is a role that can handle several types of message content: order and payment in the minimum form. Depending
 * on the type of handling by the seller, several other messages can be handled as well. This version of the role handles the
 * seling of a product based on a RFQ-Quote process.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SellingRoleRFQ extends SellingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Constructs a new SellingRole for RFQ - Order - Payment.
     * @param owner the owner this role
     * @param rfqHandler the Request for Quote handler
     * @param orderHandler the order handler
     * @param paymentHandler the payment handler
     */
    public SellingRoleRFQ(final SellingActor owner, final RequestForQuoteHandler rfqHandler, final OrderHandler<?> orderHandler,
            final PaymentHandler paymentHandler)
    {
        super(owner);
        setContentHandler(rfqHandler);
        setContentHandler(orderHandler);
        setContentHandler(paymentHandler);
    }

    @Override
    public String getId()
    {
        return getActor().getName() + "-SELLING(RFQ)";
    }
}
