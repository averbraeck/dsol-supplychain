package nl.tudelft.simulation.supplychain.role.selling;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.role.directing.DirectingActorSelling;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * SellingActor is an interface to indicate that an Actor has a SellingRole. Since Selling usually involves invoices and
 * payments, the SellingActor also implements the FinancingActor.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SellingActor extends FinancingActor, WarehousingActor, DirectingActorSelling
{
    /**
     * Return the SellingRole for this actor.
     * @return the SellingRole for this actor
     */
    default SellingRole getSellingRole()
    {
        return getRole(SellingRole.class);
    }

    /**
     * Set the SellingRole for this actor.
     * @param sellingRole the new SellingRole for this actor
     */
    default void setSellingRole(final SellingRole sellingRole)
    {
        Throw.whenNull(sellingRole, "sellingRole cannot be null");
        registerRole(SellingRole.class, sellingRole);
    }

}
