package nl.tudelft.simulation.supplychain.transporting;

import java.util.Set;

import nl.tudelft.simulation.supplychain.product.Sku;
import nl.tudelft.simulation.supplychain.role.transporting.TransportOption;

/**
 * TransportChoiceProvider chooses between options for transport.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface TransportChoiceProvider
{
    /**
     * Give the transport options for a transport from a sender actor to a receiver actor. The SKU is provided to see whether a
     * certain transport mode can transport the given SKU. For instance, an airplane cannot transport 40ft containers; a
     * container ship cannot transport liquid bulk; etc.
     * @param transportOptions a set of transport options from sender to receiver
     * @param sku the SKU to see which transport option can thatsport the given SKU
     * @return a preferred option for transport
     */
    TransportOption chooseTransportOptions(Set<TransportOption> transportOptions, Sku sku);

}
