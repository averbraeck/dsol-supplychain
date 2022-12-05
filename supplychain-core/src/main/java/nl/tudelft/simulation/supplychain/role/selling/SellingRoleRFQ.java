package nl.tudelft.simulation.supplychain.role.selling;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.policy.order.AbstractOrderPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicy;
import nl.tudelft.simulation.supplychain.policy.rfq.RequestForQuotePolicy;

/**
 * The selling role is a role that can handle several types of message content: order and payment in the minimum form. Depending
 * on the type of handling by the seller, several other messages can be handled as well. This version of the role handles the
 * seling of a product based on a RFQ-Quote process.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SellingRoleRFQ extends Role implements SellingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Constructs a new SellingRole for RFQ - Order - Payment.
     * @param owner SupplyChainActor; the owner this role
     * @param rfqPolicy the Request for Quote handler
     * @param orderPolicy the order handler
     * @param paymentPolicy the payment handler
     */
    public SellingRoleRFQ(final SupplyChainActor owner, final RequestForQuotePolicy rfqPolicy,
            final AbstractOrderPolicy<?> orderPolicy, final PaymentPolicy paymentPolicy)
    {
        super(owner);
        addMessagePolicy(rfqPolicy);
        addMessagePolicy(orderPolicy);
        addMessagePolicy(paymentPolicy);
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return getOwner().getName() + "-SELLING(RFQ)";
    }
}
