package nl.tudelft.simulation.supplychain.role.warehousing;

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
    WarehousingRole getWarehousingRole();

    /**
     * Set the WarehousingRole for this actor.
     * @param warehousingRole the new WarehousingRole for this actor
     */
    void setWarehousingRole(WarehousingRole warehousingRole);

    /**
     * Return the ShippingRole for this actor.
     * @return the ShippingRole for this actor
     */
    ShippingRole getShippingRole();

    /**
     * Set the ShippingRole for this actor.
     * @param shippingRole the new ShippingRole for this actor
     */
    void setShippingRole(ShippingRole shippingRole);

    /**
     * Return the ReceivingRole for this actor.
     * @return the ReceivingRole for this actor
     */
    ReceivingRole getReceivingRole();

    /**
     * Set the ReceivingRole for this actor.
     * @param receivingRole the new ReceivingRole for this actor
     */
    void setReceivingRole(ReceivingRole receivingRole);

}
