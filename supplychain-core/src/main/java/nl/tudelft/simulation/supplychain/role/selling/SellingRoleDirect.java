package nl.tudelft.simulation.supplychain.role.selling;

/**
 * The selling role is a role that can handle several types of message content: order and payment in the minimum form. Depending
 * on the type of handling by the seller, several other messages can be handled as well. Examples are to be able to handle an
 * RFQ.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SellingRoleDirect extends SellingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Constructs a new SellingRole for Order - Payment.
     * @param owner the owner this role
     */
    public SellingRoleDirect(final SellingActor owner)
    {
        super(owner);
    }

    @Override
    public String getId()
    {
        return getActor().getName() + "-SELLING(direct)";
    }

}
