package nl.tudelft.simulation.supplychain.role.selling;

import java.util.Set;

import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;

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

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers =
            Set.of(RequestForQuote.class, OrderBasedOnQuote.class);
    
    /** whether to send negative quotes or not. */
    private boolean sendNegativeQuotes = false;

    /**
     * Constructs a new SellingRole for RFQ - Order - Payment.
     * @param owner the owner this role
     */
    public SellingRoleRFQ(final SellingActor owner)
    {
        super(owner);
    }

    /**
     * Return whether to send negative quotes or not.
     * @return whether to send negative quotes or not
     */
    public boolean isSendNegativeQuotes()
    {
        return this.sendNegativeQuotes;
    }

    /**
     * Set whether to send negative quotes or not.
     * @param sendNegativeQuotes set whether to send negative quotes or not
     */
    public void setSendNegativeQuotes(final boolean sendNegativeQuotes)
    {
        this.sendNegativeQuotes = sendNegativeQuotes;
    }

    @Override
    public String getId()
    {
        return getActor().getName() + "-SELLING(RFQ)";
    }

    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }
}
