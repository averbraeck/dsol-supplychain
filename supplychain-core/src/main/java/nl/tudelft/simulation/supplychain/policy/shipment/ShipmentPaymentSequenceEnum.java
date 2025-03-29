package nl.tudelft.simulation.supplychain.policy.shipment;

/**
 * ShipmentPaymentSequence.java.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public enum ShipmentPaymentSequenceEnum
{
    /** Mode of operation for an shipping and paying: paying at delivery. */
    SHIPMENT_WITH_PAYMENT,

    /** Mode of operation for an shipping and paying: paying before delivery. */
    SHIPMENT_AFTER_PAYMENT,

    /** Mode of operation for an shipping and paying: paying after delivery. */
    SHIPMENT_BEFORE_PAYMENT;
}
