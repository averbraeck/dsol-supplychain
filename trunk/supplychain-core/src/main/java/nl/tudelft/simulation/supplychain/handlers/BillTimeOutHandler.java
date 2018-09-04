package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * A Bill handler which has a restriction that after a time out the bill is paid automatically if not paid yet. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BillTimeOutHandler extends BillHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 11L;

    /** the maximum time out for a shipment */
    private Duration maximumTimeOut = Duration.ZERO;

    /** true for debug */
    private boolean debug = true;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(BillTimeOutHandler.class);

    /**
     * constructs a new BillTimeOutHandler
     * @param owner the owner
     * @param bankAccount the bank account
     * @param paymentPolicy the payment policy
     * @param paymentDelay the payment delay
     * @param maximumTimeOut the maximum time out for a bill
     */
    public BillTimeOutHandler(final SupplyChainActor owner, final BankAccount bankAccount, final int paymentPolicy,
            final DistContinuousDurationUnit paymentDelay, final Duration maximumTimeOut)
    {
        super(owner, bankAccount, paymentPolicy, paymentDelay);
        this.maximumTimeOut = maximumTimeOut;
    }

    /**
     * Constructs a new BillHandler that takes care of paying exactly on time
     * @param owner the owner of the handler.
     * @param bankAccount the bankaccount to use.
     * @param maximumTimeOut the maximum time out for a bill
     */
    public BillTimeOutHandler(final SupplyChainActor owner, final BankAccount bankAccount, final Duration maximumTimeOut)
    {
        this(owner, bankAccount, 0, null, maximumTimeOut);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (super.handleContent(content))
        {
            Bill bill = (Bill) content;
            try
            {
                bill.getSender().getSimulator().scheduleEventAbs(bill.getFinalPaymentDate().plus(this.maximumTimeOut), this,
                        this, "checkPayment", new Serializable[] { bill });
            }
            catch (Exception exception)
            {
                logger.fatal("handleContent", exception);
            }
            return true;
        }
        return false;
    }

    /**
     * @param bill the bill
     */
    protected void checkPayment(final Bill bill)
    {
        if (!bill.isPaid())
        {
            // sad moment, we have to pay...
            this.forcedPay(bill);
        }
    }

    /**
     * Pay.
     * @param bill the bill to pay.
     */
    private void forcedPay(final Bill bill)
    {
        // make a payment to send out
        super.bankAccount.withdrawFromBalance(bill.getPrice());
        Payment payment = new Payment(getOwner(), bill.getSender(), bill.getInternalDemandID(), bill, bill.getPrice());
        getOwner().sendContent(payment, Duration.ZERO);
        if (this.debug)
        {
            System.out.println("DEBUG -- BILLTIMEOUTHANDLER: FORCED PAYMENT IMPOSED: ");
        }
    }
}
