package nl.tudelft.simulation.supplychain.role.financing.handler;

/**
 * The different payment policies that this InvoiceHandler class can use.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public enum PaymentPolicyEnum
{
    /** The payment handler to for payment at the exact right date. */
    PAYMENT_ON_TIME,

    /** The payment handler to indicate the payment will be done late. */
    PAYMENT_EARLY,

    /** The payment handler to indicate the payment will be done early. */
    PAYMENT_LATE,

    /** The payment handler for payment right now, without waiting. */
    PAYMENT_IMMEDIATE;

}
