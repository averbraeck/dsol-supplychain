package nl.tudelft.simulation.supplychain.role.selling;

import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * SellingActor is an interface to indicate that an Actor has a SellingRole. Since Selling usually involves bills and payments,
 * the SellingActor also implements the FinancingActor.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SellingActor extends FinancingActor
{
    /**
     * Return the SellingRole for this actor.
     * @return SellingRole; the SellingRole for this actor
     */
    SellingRole getSellingRole();

    /**
     * Set the SellingRole for this actor.
     * @param sellingRole SellingRole; the new SellingRole for this actor
     */
    void setSellingRole(SellingRole sellingRole);

}
