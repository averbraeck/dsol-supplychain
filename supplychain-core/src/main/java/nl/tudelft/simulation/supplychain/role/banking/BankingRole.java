package nl.tudelft.simulation.supplychain.role.banking;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;

/**
 * The BankingRole maintains the interest rates for the Bank accounts. In this case, we have chosen to not make the Bank work
 * with Messages, but this is of course possible to implement, e.g. to simulate risks of banks handling international
 * transactions slowly, or to simulate cyber attacks on the financial infrastructure.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BankingRole extends Role
{
    /** */
    private static final long serialVersionUID = 20230413L;

    /** the interest rate for a positive bank account. */
    private double annualInterestRatePos = 0.025;

    /** the interest rate for a negative bank account. */
    private double annualInterestRateNeg = 0.08;

    /**
     * Create a new FinancingRole with an attached BankAccount.
     * @param id String; the id of the role
     * @param owner Actor; the actor to which this role belongs
     */
    public BankingRole(final String id, final BankingActor owner)
    {
        super("banking", owner, new ContentReceiverDirect());
    }

    /**
     * Create a new FinancingRole with an attached BankAccount.
     * @param id String; the id of the role
     * @param owner Actor; the actor to which this role belongs
     * @param messageReceiver MessageReceiver; the message handler to use for processing the messages
     */
    public BankingRole(final String id, final BankingActor owner, final ContentReceiver messageReceiver)
    {
        super("banking", owner, messageReceiver);
    }

    /**
     * Return the negative annual interest rate.
     * @return double; negative annual interest rate
     */
    public double getAnnualInterestRateNeg()
    {
        return this.annualInterestRateNeg;
    }

    /**
     * Set a new negative annual interest rate.
     * @param annualInterestRateNeg double; new negative annual interest rate
     */
    public void setAnnualInterestRateNeg(final double annualInterestRateNeg)
    {
        this.annualInterestRateNeg = annualInterestRateNeg;
    }

    /**
     * Return the positive annual interest rate.
     * @return double; positive annual interest rate
     */
    public double getAnnualInterestRatePos()
    {
        return this.annualInterestRatePos;
    }

    /**
     * Set a new positive annual interest rate.
     * @param annualInterestRatePos double; new positive annual interest rate
     */
    public void setAnnualInterestRatePos(final double annualInterestRatePos)
    {
        this.annualInterestRatePos = annualInterestRatePos;
    }

}
