package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.simtime.TimeUnit;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Payment;

/**
 * The BillHandler is a simple implementation of the business logic to pay a bill. Four different policies are available in this
 * version -- which can be extended, of course: paying immediately, paying on time, paying early, and paying late. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BillHandler extends SupplyChainHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    // the different payment policies that this BillHandler class can use
    /** The payment policy to for payment at the exact right date */
    public static final int PAYMENT_ON_TIME = 0;

    /** The payment policy to indicate the payment will be done late */
    public static final int PAYMENT_EARLY = 1;

    /** The payment policy to indicate the payment will be done early */
    public static final int PAYMENT_LATE = 2;

    /** The payment policy for payment right now, without waiting */
    public static final int PAYMENT_IMMEDIATE = 3;

    /** the bank account to use */
    protected BankAccount bankAccount = null;

    /** the payment policy to use */
    private int paymentPolicy = 0;

    /** the delay distribution to use with certain policies */
    private DistContinuous paymentDelay = null;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(BillHandler.class);

    /**
     * Constructs a new BillHandler with possibilities to pay early or late.
     * @param owner the owner of the handler.
     * @param bankAccount the bankaccount to use.
     * @param paymentPolicy the payment policy to use (early, late, etc.).
     * @param paymentDelay the delay to use in early or late payment
     */
    public BillHandler(final SupplyChainActor owner, final BankAccount bankAccount, final int paymentPolicy,
            final DistContinuous paymentDelay)
    {
        super(owner);
        this.bankAccount = bankAccount;
        this.paymentPolicy = paymentPolicy;
        this.paymentDelay = paymentDelay;
    }

    /**
     * Constructs a new BillHandler that takes care of paying exactly on time
     * @param owner the owner of the handler.
     * @param bankAccount the bankaccount to use.
     */
    public BillHandler(final SupplyChainActor owner, final BankAccount bankAccount)
    {
        this(owner, bankAccount, 0, null);
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface#handleContent(java.io.Serializable)
     */
    public boolean handleContent(final Serializable content)
    {
        Bill bill = (Bill) checkContent(content);
        if (!isValidContent(bill))
        {
            return false;
        }
        // schedule the payment
        double currentTime = Double.NaN;
        try
        {
            currentTime = getOwner().getSimulator().getSimulatorTime();
        }
        catch (RemoteException remoteException)
        {
            logger.fatal("handleContent", remoteException);
            return false;
        }
        double paymentTime = bill.getFinalPaymentDate();
        switch (this.paymentPolicy)
        {
            case BillHandler.PAYMENT_ON_TIME:
                // do nothing, we pay on the requested date
                break;
            case BillHandler.PAYMENT_EARLY:
                paymentTime -= this.paymentDelay.draw();
                break;
            case BillHandler.PAYMENT_LATE:
                paymentTime += this.paymentDelay.draw();
                break;
            case BillHandler.PAYMENT_IMMEDIATE:
                paymentTime = currentTime;
                break;
            default:
                logger.warn("handleContant - unknown paymentPolicy: " + this.paymentPolicy);
                break;
        }
        // check if payment is still possible, if it already should have taken
        // place, schedule it for now.
        paymentTime = Math.max(paymentTime, currentTime);
        try
        {
            Serializable[] args = new Serializable[] { bill };
            SimEvent simEvent = new SimEvent(paymentTime, this, this, "pay", args);
            getOwner().getSimulator().scheduleEvent(simEvent);
        }
        catch (Exception exception)
        {
            logger.fatal("handleContent", exception);
            return false;
        }
        return true;
    }

    /**
     * Try to pay. If it does not succeed, try later.
     * @param bill - the bill to pay.
     */
    protected void pay(final Bill bill)
    {
        if (this.bankAccount.getBalance() < bill.getPrice())
        {
            // the bank account is not enough. Try one day later.
            try
            {
                double currentTime = getOwner().getSimulator().getSimulatorTime();
                currentTime += TimeUnit.convert(1.0, TimeUnit.DAY, getOwner().getSimulator());
                Serializable[] args = new Serializable[] { bill };
                SimEvent simEvent = new SimEvent(currentTime, this, this, "pay", args);
                getOwner().getSimulator().scheduleEvent(simEvent);
            }
            catch (Exception exception)
            {
                logger.fatal("handleContent", exception);
            }
            return;
        }
        // make a payment to send out
        this.bankAccount.withdrawFromBalance(bill.getPrice());
        Payment payment = new Payment(getOwner(), bill.getSender(), bill.getInternalDemandID(), bill, bill.getPrice());
        getOwner().sendContent(payment, 0.0);
    }

    /**
     * @param paymentDelay The paymentDelay to set.
     */
    public void setPaymentDelay(final DistContinuous paymentDelay)
    {
        this.paymentDelay = paymentDelay;
    }

    /**
     * @param paymentPolicy The paymentPolicy to set.
     */
    public void setPaymentPolicy(final int paymentPolicy)
    {
        this.paymentPolicy = paymentPolicy;
    }

    /**
     * @see nl.tudelft.simulation.supplychain.handlers.SupplyChainHandler#checkContentClass(java.io.Serializable)
     */
    protected boolean checkContentClass(final Serializable content)
    {
        return (content instanceof Bill);
    }
}
