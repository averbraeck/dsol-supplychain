package nl.tudelft.simulation.supplychain.finance;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.supplychain.role.banking.BankingActor;
import nl.tudelft.simulation.supplychain.role.financing.FinancingActor;

/**
 * The BackAccount keeps track of the balance of a Actor. This simple implementation just has one number as the account. No
 * investments or loans are possible through this implementation. The BankAccount itself does not contain logic to prevent it
 * from going negative.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BankAccount extends LocalEventProducer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the owner of the bank account, which has a FinancingRole. */
    private FinancingActor owner;

    /** the bank actor, which has a BankingRole. */
    private BankingActor bank;

    /** the balance of the actor. */
    private Money balance;

    /** for who is interested, the BankAccount can send updates of changes. */
    public static final EventType BANK_ACCOUNT_CHANGED_EVENT = new EventType("BANK_ACCOUNT_CHANGED_EVENT",
            new MetaData("account", "bank account", new ObjectDescriptor("balance", "bank balance", double.class)));

    /**
     * Constructor for BankAccount.
     * @param owner FinancingActor; the owner of the bank account, which has a FinancingRole
     * @param bank BankingActor; the bank actor, , which has a BankingRole, where this account is located
     * @param initialBalance Money; the opening balance
     */
    public BankAccount(final FinancingActor owner, final BankingActor bank, final Money initialBalance)
    {
        Throw.whenNull(owner, "owner cannot be null");
        Throw.whenNull(bank, "bank cannot be null");
        Throw.whenNull(initialBalance, "initialBalance cannot be null");
        this.owner = owner;
        this.bank = bank;
        this.balance = initialBalance;
        this.roundBalance();
        sendBalanceUpdateEvent();
        // start the interest process...
        this.owner.getSimulator().scheduleEventNow(this, "interest", null);
    }

    /**
     * Return the bank balance.
     * @return Money; the bank balance
     */
    public Money getBalance()
    {
        return this.balance;
    }

    /**
     * Add money to the bank balance.
     * @param amount Money; the amount of money to add
     */
    public synchronized void addToBalance(final Money amount)
    {
        this.balance = this.balance.plus(amount);
        this.roundBalance();
        sendBalanceUpdateEvent();
    }

    /**
     * Withdraw money from the bank balance.
     * @param amount Money; the amount of money to withdraw
     */
    public synchronized void withdrawFromBalance(final Money amount)
    {
        this.balance = this.balance.minus(amount);
        this.roundBalance();
        sendBalanceUpdateEvent();
    }

    /**
     * Send a BANK_ACCOUNT_CHANGED_EVENT to signal an update of the bank balance.
     */
    protected void sendBalanceUpdateEvent()
    {
        this.fireTimedEvent(BANK_ACCOUNT_CHANGED_EVENT, this.balance, this.owner.getSimulatorTime());
    }

    /**
     * Round the balance.
     */
    protected void roundBalance()
    {
        this.balance = new Money(0.01 * Math.round(100.0 * this.balance.getAmount()), this.balance.getMoneyUnit());
    }

    /**
     * receive or pay interest according to the current rates.
     */
    protected void interest()
    {
        if (this.balance.getAmount() < 0)
        {
            addToBalance(this.balance.multiplyBy(this.bank.getBankingRole().getAnnualInterestRateNeg() / 365.0));
        }
        else
        {
            addToBalance(this.balance.multiplyBy(this.bank.getBankingRole().getAnnualInterestRatePos() / 365.0));
        }
        this.roundBalance();
        this.owner.getSimulator().scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, "interest", null);
    }

}
