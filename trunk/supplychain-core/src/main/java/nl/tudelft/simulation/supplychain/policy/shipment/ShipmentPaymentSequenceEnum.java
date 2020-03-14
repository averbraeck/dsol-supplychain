package nl.tudelft.simulation.supplychain.policy.shipment;

/**
 * ShipmentPaymentSequence.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public enum ShipmentPaymentSequenceEnum
{
    /** Mode of operation for an shipping and paying: paying at delivery */
    SHIPMENT_WITH_PAYMENT,

    /** Mode of operation for an shipping and paying: paying before delivery */
    SHIPMENT_AFTER_PAYMENT,

    /** Mode of operation for an shipping and paying: paying after delivery */
    SHIPMENT_BEFORE_PAYMENT;
}

