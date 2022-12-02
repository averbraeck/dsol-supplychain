package nl.tudelft.simulation.supplychain.message.trade;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * TODO: Make Shipmetnt with Quality <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShipmentQuality extends Shipment
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /**
     * @param sender SupplyChainActor; the sender actor of the message content
     * @param receiver SupplyChainActor; the receiving actor of the message content
     * @param internalDemandId internal demand that triggered the process
     * @param order the order for which this is the shipment
     * @param product Product; the product type
     * @param amount double; the number of product units
     * @param totalCargoValue the price of the cargo
     */
    public ShipmentQuality(SupplyChainActor sender, SupplyChainActor receiver, long internalDemandId, Order order,
            Product product, double amount, Money totalCargoValue)
    {
        super(sender, receiver, internalDemandId, order, product, amount, totalCargoValue);
    }

}