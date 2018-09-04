package nl.tudelft.simulation.supplychain.banking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.unit.DurationUnit;
import org.djunits.unit.MoneyUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Money;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.event.TimedEvent;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * The BackAccount keeps track of the balance of a SupplyChainActor. This simple implementation just has one number as the
 * account. No investments or loans are possible through this implementation. The BankAccount itself does not contain logic to
 * prevent it from going negative.>br> <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BankAccount extends EventProducer
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the owner of the bank account */
    private SupplyChainActor owner;

    /** the bank */
    private Bank bank;

    /** the simulator for the interest rate */
    private DEVSSimulatorInterfaceUnit simulator;

    /** the balance of the actor */
    private Money balance;

    /** for who is interested, the BankAccount can send updates of changes */
    public static final EventType BANK_ACCOUNT_CHANGED_EVENT = new EventType("BANK_ACCOUNT_CHANGED_EVENT");

    /** the logger. */
    private static Logger logger = LogManager.getLogger(BankAccount.class);

    /**
     * Constructor for BankAccount.
     * @param owner the owner of the bank account
     * @param bank the bank where this account is located
     * @param initialBalance the opening balance
     */
    public BankAccount(final SupplyChainActor owner, final Bank bank, final Money initialBalance)
    {
        super();
        try
        {
            if (owner == null)
            {
                throw new Exception("owner = null");
            }
            this.owner = owner;
            this.bank = bank;
            this.simulator = this.owner.getSimulator();
            if (Double.isInfinite(initialBalance.si))
            {
                throw new Exception("initial bank balance = infinite");
            }
            if (Double.isNaN(initialBalance.si))
            {
                throw new Exception("initial bank balance = NaN");
            }
            this.balance = initialBalance;
            this.roundBalance();
            super.fireEvent(BANK_ACCOUNT_CHANGED_EVENT, this.balance);
            // start the interest process...
            this.simulator.scheduleEventNow(this, this, "interest", null);

        }
        catch (Exception e)
        {
            logger.warn("<init>", e);
        }
    }

    /**
     * Constructor for BankAccount.
     * @param owner the owner of the bank account
     * @param bank the bank where this account is located
     */
    public BankAccount(final SupplyChainActor owner, final Bank bank)
    {
        this(owner, bank, new Money(0.0, MoneyUnit.USD));
    }

    /**
     * Returns the balance.
     * @return double
     */
    public Money getBalance()
    {
        return this.balance;
    }

    /**
     * Adds money to the balance.
     * @param amount The amount of money
     */
    public synchronized void addToBalance(final Money amount)
    {
        this.balance = this.balance.plus(amount);
        this.roundBalance();
        this.fireEvent(new TimedEvent<Time>(BANK_ACCOUNT_CHANGED_EVENT, this, this.balance, this.owner.getSimulatorTime()));
    }

    /**
     * Withdraws money from the balance.
     * @param amount The amount of money
     */
    public synchronized void withdrawFromBalance(final Money amount)
    {
        this.balance = this.balance.minus(amount);
        this.roundBalance();
        this.fireEvent(new TimedEvent<Time>(BANK_ACCOUNT_CHANGED_EVENT, this, this.balance, this.owner.getSimulatorTime()));
    }

    /**
     * Method sendBalanceUpdateEvent.
     */
    public void sendBalanceUpdateEvent()
    {
        this.fireEvent(new TimedEvent<Time>(BANK_ACCOUNT_CHANGED_EVENT, this, this.balance, this.owner.getSimulatorTime()));
    }

    /**
     * Round the balance
     */
    private void roundBalance()
    {
        this.balance = new Money(0.01 * Math.round(100.0 * this.balance.si), this.balance.getUnit());
    }

    /**
     * receive or pay interest according to the current rates
     */
    protected void interest()
    {
        try
        {
            if (this.balance.si < 0)
            {
                addToBalance(this.balance.multiplyBy(this.bank.getAnnualInterestRateNeg() / 365.0));
            }
            else
            {
                addToBalance(this.balance.multiplyBy(this.bank.getAnnualInterestRatePos() / 365.0));
            }
            this.roundBalance();
            this.simulator.scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, this, "interest", null);
        }
        catch (Exception exception)
        {
            logger.fatal("interest", exception);
        }

    }

}
