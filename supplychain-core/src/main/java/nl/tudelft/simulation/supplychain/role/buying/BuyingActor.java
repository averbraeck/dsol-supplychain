package nl.tudelft.simulation.supplychain.role.buying;

import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * BuyingActor is an interface to indicate that an Actor has a BuyingRole. Since Buying usually involves invoices and payments, the
 * BuyingActor also implements the FinancingActor.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface BuyingActor extends FinancingActor
{
    /**
     * Return the BuyingRole for this actor.
     * @return the BuyingRole for this actor
     */
    BuyingRole getBuyingRole();

    /**
     * Set the BuyingRole for this actor.
     * @param buyingRole the new BuyingRole for this actor
     */
    void setBuyingRole(BuyingRole buyingRole);

}
