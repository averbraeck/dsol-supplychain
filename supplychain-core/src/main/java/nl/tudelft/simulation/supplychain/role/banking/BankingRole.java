package nl.tudelft.simulation.supplychain.role.banking;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djutils.event.EventType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

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
public class BankingRole extends Role<BankingRole>
{
    /** */
    private static final long serialVersionUID = 20230413L;

    /** the interest rate for a positive bank account. */
    private double annualInterestRatePos = 0.025;

    /** the interest rate for a negative bank account, as a negative number. */
    private double annualInterestRateNeg = -0.08;

    /** the balance of the actors. */
    private final Map<FinancingActor, Money> bankAccounts = new LinkedHashMap<>();

    /** for who is interested, the BankAccount can send updates of changes. */
    public static final EventType BANK_ACCOUNT_CHANGED_EVENT = new EventType("BANK_ACCOUNT_CHANGED_EVENT",
            new MetaData("account", "bank account", new ObjectDescriptor("actor", "account holder", FinancingActor.class),
                    new ObjectDescriptor("balance", "bank balance", Money.class)));

    /**
     * Create a new FinancingRole with an attached BankAccount.
     * @param id the id of the role
     * @param owner the actor to which this role belongs
     */
    public BankingRole(final String id, final BankingActor owner)
    {
        super("banking", owner, new ContentReceiverDirect());
    }

    /**
     * Create a new FinancingRole with an attached BankAccount.
     * @param id the id of the role
     * @param owner the actor to which this role belongs
     * @param messageReceiver the message handler to use for processing the messages
     */
    public BankingRole(final String id, final BankingActor owner, final ContentReceiver messageReceiver)
    {
        super("banking", owner, messageReceiver);
    }

    /**
     * Get the bank balance and set up a bank account if none is available.
     * @param actor the actor for which we need the bank account
     * @return the balance or 0 if newly set up
     */
    public Money getBalance(final FinancingActor actor)
    {
        if (!this.bankAccounts.containsKey(actor))
        {
            this.bankAccounts.put(actor, new Money(0.0, MoneyUnit.USD));
        }
        return this.bankAccounts.get(actor);
    }

    /**
     * Add money to the bank balance.
     * @param actor the actor for which to add money to the bank account
     * @param amount the amount of money to add
     */
    public synchronized void addToBalance(final FinancingActor actor, final Money amount)
    {
        Money newBalance = roundBalance(getBalance(actor).plus(amount));
        this.bankAccounts.put(actor, newBalance);
        sendBalanceUpdateEvent(actor, newBalance);
    }

    /**
     * Withdraw money from the bank balance.
     * @param actor the actor for which to withdraw money from the bank account
     * @param amount the amount of money to withdraw
     */
    public synchronized void withdrawFromBalance(final FinancingActor actor, final Money amount)
    {
        Money newBalance = roundBalance(getBalance(actor).minus(amount));
        this.bankAccounts.put(actor, newBalance);
        sendBalanceUpdateEvent(actor, newBalance);
    }

    /**
     * Send a BANK_ACCOUNT_CHANGED_EVENT to signal an update of the bank balance.
     * @param actor the actor whose balance has changed
     * @param newBalance the new balance of the bank account
     */
    protected void sendBalanceUpdateEvent(final FinancingActor actor, final Money newBalance)
    {
        this.fireTimedEvent(BANK_ACCOUNT_CHANGED_EVENT, new Serializable[] {actor, newBalance}, getActor().getSimulatorTime());
    }

    /**
     * Round the amount of money.
     * @param money the amount of money to round
     * @return the rounded amount of money
     */
    protected Money roundBalance(final Money money)
    {
        return new Money(0.01 * Math.round(100.0 * money.getAmount()), money.getMoneyUnit());
    }

    /**
     * Return the negative annual interest rate, provided as a negative number.
     * @return negative annual interest rate, as a negative number
     */
    public double getAnnualInterestRateNeg()
    {
        return this.annualInterestRateNeg;
    }

    /**
     * Set a new negative annual interest rate, provided as a negative number.
     * @param annualInterestRateNeg new negative annual interest rate, as a negative number
     */
    public void setAnnualInterestRateNeg(final double annualInterestRateNeg)
    {
        Throw.when(annualInterestRateNeg > 0.0, IllegalArgumentException.class, "negative interest rate should be < 0");
        this.annualInterestRateNeg = annualInterestRateNeg;
    }

    /**
     * Return the positive annual interest rate.
     * @return positive annual interest rate
     */
    public double getAnnualInterestRatePos()
    {
        return this.annualInterestRatePos;
    }

    /**
     * Set a new positive annual interest rate.
     * @param annualInterestRatePos new positive annual interest rate
     */
    public void setAnnualInterestRatePos(final double annualInterestRatePos)
    {
        Throw.when(annualInterestRatePos < 0.0, IllegalArgumentException.class, "positive interest rate should be > 0");
        this.annualInterestRatePos = annualInterestRatePos;
    }

    /**
     * Return the bank accounts per actor with their balance.
     * @return the bank accounts per actor with their balance
     */
    public Map<FinancingActor, Money> getBankAccounts()
    {
        return this.bankAccounts;
    }

    @Override
    public BankingActor getActor()
    {
        return (BankingActor) super.getActor();
    }

}
