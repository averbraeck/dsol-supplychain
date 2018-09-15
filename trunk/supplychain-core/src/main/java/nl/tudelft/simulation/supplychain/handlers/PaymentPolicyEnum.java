package nl.tudelft.simulation.supplychain.handlers;

/**
 * The different payment policies that this BillHandler class can use. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public enum PaymentPolicyEnum
{
    /** The payment policy to for payment at the exact right date */
    PAYMENT_ON_TIME,

    /** The payment policy to indicate the payment will be done late. */
    PAYMENT_EARLY,

    /** The payment policy to indicate the payment will be done early. */
    PAYMENT_LATE,

    /** The payment policy for payment right now, without waiting. */
    PAYMENT_IMMEDIATE;

}
