package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.dsol.simtime.TimeUnit;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.content.Payment;

/**
 * A payment handler where a check is performed whether the payment was paid on time. If not, a fine is imposed. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class PaymentFineHandler extends PaymentHandler
{
    /** true for debug */
    private static final boolean DEBUG = false;

    /** the serial version uid */
    private static final long serialVersionUID = 11L;

    /** the margin for the fine */
    private double fineMarginPerDay = 0.0;

    /** the fixed fine */
    private double fixedFinePerDay = 0.0;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(PaymentFineHandler.class);

    /**
     * constructs a new PaymentFineHandler
     * @param owner the owner
     * @param bankAccount the bank account
     * @param fineMarginPerDay the fine margin per day
     * @param fixedFinePerDay the fixed fine per day
     */
    public PaymentFineHandler(final SupplyChainActor owner, final BankAccount bankAccount, final double fineMarginPerDay,
            final double fixedFinePerDay)
    {
        super(owner, bankAccount);
        this.fineMarginPerDay = fineMarginPerDay;
        this.fixedFinePerDay = fixedFinePerDay;
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface#handleContent(java.io.Serializable)
     */
    public boolean handleContent(final Serializable content)
    {
        try
        {
            if (super.handleContent(content))
            {
                Payment payment = (Payment) content;
                double time = payment.getSender().getSimulatorTime();
                if ((time > payment.getBill().getFinalPaymentDate()))
                {
                    // YES!! we can fine! Finally we earn some money
                    double day = 1.0;
                    day = TimeUnit.convert(1.0, TimeUnit.DAY, getOwner().getSimulator());

                    double fine = ((time - payment.getBill().getFinalPaymentDate()) / day)
                            * (this.fixedFinePerDay + (this.fineMarginPerDay * payment.getPayment()));
                    // send the bill for the fine
                    /*
                     * Bill bill = new Bill(getOwner(), payment.getSender(), payment.getInternalDemandID(), payment.getBill()
                     * .getOrder(), getOwner().getSimulatorTime() + (14.0 * day), fine, "FINE");
                     */
                    // do a forced payment
                    payment.getSender().getBankAccount().withdrawFromBalance(fine);
                    payment.getReceiver().getBankAccount().addToBalance(fine);

                    if (PaymentFineHandler.DEBUG)
                    {
                        System.out.println("DEBUG -- PAYMENTFINEHANDLER: FINE IMPOSED: " + fine);
                    }

                    // getOwner().sendContent(payment, 0.0);
                    // getOwner().sendContent(bill, 0.0);
                }
                return true;
            }
            return false;
        }
        catch (Exception exception)
        {
            logger.fatal("handleContent", exception);
            return false;
        }
    }
}
