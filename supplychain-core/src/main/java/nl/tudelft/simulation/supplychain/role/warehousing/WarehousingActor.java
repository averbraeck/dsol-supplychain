package nl.tudelft.simulation.supplychain.role.warehousing;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;
import nl.tudelft.simulation.supplychain.role.receiving.ReceivingRole;
import nl.tudelft.simulation.supplychain.role.shipping.ShippingRole;

/**
 * WarehousingActor is an interface to indicate that an Actor has a WarehousingRole, a ShippingRole, and a ReceivingRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface WarehousingActor extends FinancingActor
{
    /**
     * Return the WarehousingRole for this actor.
     * @return the WarehousingRole for this actor
     */
    default WarehousingRole getWarehousingRole()
    {
        return getRole(WarehousingRole.class);
    }

    /**
     * Set the WarehousingRole for this actor.
     * @param warehousingRole the new WarehousingRole for this actor
     */
    default void setWarehousingRole(final WarehousingRole warehousingRole)
    {
        Throw.whenNull(warehousingRole, "warehousingRole cannot be null");
        registerRole(WarehousingRole.class, warehousingRole);
    }

    /**
     * Return the ShippingRole for this actor.
     * @return the ShippingRole for this actor
     */
    default ShippingRole getShippingRole()
    {
        return getRole(ShippingRole.class);
    }

    /**
     * Set the ShippingRole for this actor.
     * @param shippingRole the new ShippingRole for this actor
     */
    default void setShippingRole(final ShippingRole shippingRole)
    {
        Throw.whenNull(shippingRole, "shippingRole cannot be null");
        registerRole(ShippingRole.class, shippingRole);
    }

    /**
     * Return the ReceivingRole for this actor.
     * @return the ReceivingRole for this actor
     */
    default ReceivingRole getReceivingRole()
    {
        return getRole(ReceivingRole.class);
    }

    /**
     * Set the ReceivingRole for this actor.
     * @param receivingRole the new ReceivingRole for this actor
     */
    default void setReceivingRole(final ReceivingRole receivingRole)
    {
        Throw.whenNull(receivingRole, "receivingRole cannot be null");
        registerRole(ReceivingRole.class, receivingRole);
    }

}
