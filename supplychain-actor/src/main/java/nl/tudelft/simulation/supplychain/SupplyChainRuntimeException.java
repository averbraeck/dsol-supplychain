package nl.tudelft.simulation.supplychain;

import org.djutils.logger.CategoryLogger;

/**
 * SupplyChainRuntimeException to indicate and create a specific (silent) run time exception for the supply chain models. The
 * exception is logged using the CategoryLogger.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SupplyChainRuntimeException extends RuntimeException
{
    /** */
    private static final long serialVersionUID = 20230407L;

    /** the default message. */
    private static final String MESSAGE = "Supply chain runtime exception";

    /**
     * Create an exception without a message.
     */
    public SupplyChainRuntimeException()
    {
        this(MESSAGE);
    }

    /**
     * Create the exception with a message.
     * @param message String; the message to explain the exception
     */
    public SupplyChainRuntimeException(final String message)
    {
        super(message);
        log();
    }

    /**
     * Wrap an earlier exception into this exception.
     * @param cause Throwable; the earlier exception to wrap
     */
    public SupplyChainRuntimeException(final Throwable cause)
    {
        this(MESSAGE, cause);
    }

    /**
     * Wrap an earlier exception into this exception, and give it a description.
     * @param message String; the message to explain the exception
     * @param cause Throwable; the earlier exception to wrap
     */
    public SupplyChainRuntimeException(final String message, final Throwable cause)
    {
        super(message, cause);
        log();
    }

    /**
     * Create an exception that wraps an earlier exception into this exception, and gives it a description. This construstor
     * allows to set the flags on the exception for suppression enabled or disabled, and writable stack trace enabled or
     * disabled.
     * @param message String; the message to explain the exception
     * @param cause Throwable; the earlier exception to wrap
     * @param enableSuppression boolean; whether or not suppression is enabled or disabled
     * @param writableStackTrace boolean; whether or not the stack trace should be writable
     */
    public SupplyChainRuntimeException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
        log();
    }

    /**
     * Send appropriate information to the logger to identify the error.
     */
    private void log()
    {
        CategoryLogger.always()
                .error(getMessage() + " in " + getStackTrace()[0].getClassName() + "." + getStackTrace()[0].getMethodName()
                        + "(), " + getStackTrace()[0].getFileName() + ":" + getStackTrace()[0].getLineNumber());
    }

}
