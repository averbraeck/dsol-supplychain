package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * TODO: Make Shipmetnt with Quality
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ShipmentQuality extends Shipment
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * @param sender the sender actor of the message content
     * @param receiver the receiving actor of the message content
     * @param internalDemandID internal demand that triggered the process
     * @param order the order for which this is the shipment
     * @param product the product type
     * @param amount the number of product units
     * @param totalCargoValue the price of the cargo
     */
    public ShipmentQuality(SupplyChainActor sender, SupplyChainActor receiver, Serializable internalDemandID, Order order,
            Product product, double amount, Money totalCargoValue)
    {
        super(sender, receiver, internalDemandID, order, product, amount, totalCargoValue);
    }

}
