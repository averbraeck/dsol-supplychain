package nl.tudelft.simulation.supplychain.role.purchasing;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * PurchasingActor is an interface to indicate that an Actor has a PurchasingRole. Since Purchasing usually involves invoices
 * and payments, the PurchasingActor also implements the FinancingActor.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface PurchasingActor extends FinancingActor
{
    /**
     * Return the PurchasingRole for this actor.
     * @return the PurchasingRole for this actor
     */
    default PurchasingRole getPurchasingRole()
    {
        return getRole(PurchasingRole.class);
    }

    /**
     * Set the PurchasingRole for this actor.
     * @param purchasingRole the new PurchasingRole for this actor
     */
    default void setPurchasingRole(final PurchasingRole purchasingRole)
    {
        Throw.whenNull(purchasingRole, "purchasingRole cannot be null");
        registerRole(PurchasingRole.class, purchasingRole);
    }

}
