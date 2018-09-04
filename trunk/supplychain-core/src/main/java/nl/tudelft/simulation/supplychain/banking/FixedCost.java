package nl.tudelft.simulation.supplychain.banking;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.unit.MoneyUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.unit.simulator.SimTimeUnit;

/**
 * When a supply chain actor is created, one or more FixedCost objects can be created to book fixed costs for e.g. personnel,
 * buildings, other resources on an interval (e.g. monthly) basis. When the interval or amount is changed, the scheduling
 * changes immediately and the amount is effective in the next scheduled fixed cost event. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class FixedCost implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the supply chain actor */
    private SupplyChainActor owner;

    /** The bank account */
    private BankAccount bankAccount;

    /** the description of the type of fixed cost */
    private String description;

    /** The interval for booking the fixed cost */
    private Duration interval = Duration.ZERO;

    /** The amount to book on every interval */
    private Money amount = new Money(0.0, MoneyUnit.USD);

    /** the event for the next period -- stored to be able to remove it */
    private SimEventInterface<SimTimeUnit> fixedAmountEvent = null;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(FixedCost.class);

    /**
     * The constructor for the Fixed cost of an actor
     * @param owner the supply chain actor
     * @param bankAccount the bank account
     * @param description the description
     * @param interval the interval for booking fixed cost
     * @param amount the fixed cost per interval
     */
    public FixedCost(final SupplyChainActor owner, final BankAccount bankAccount, final String description,
            final Duration interval, final Money amount)
    {
        super();
        this.owner = owner;
        this.bankAccount = bankAccount;
        this.description = description;
        this.changeInterval(interval);
        this.changeAmount(amount);
    }

    /**
     * Change the interval to book fixed costs. The booking event is immediately rescheduled to the END of the interval; next
     * deduction takes place after 'interval' days.
     * @param newInterval the new interval
     */
    public void changeInterval(final Duration newInterval)
    {
        this.interval = newInterval;
        try
        {
            if (this.fixedAmountEvent != null)
            {
                // cancel the previous event
                this.owner.getSimulator().cancelEvent(this.fixedAmountEvent);
            }
            this.owner.getSimulator().scheduleEventRel(this.interval, this, this, "bookFixedCost", null);
        }
        catch (Exception exception)
        {
            logger.fatal("changeInterval", exception);
        }
    }

    /**
     * Change the fixed costs to book each interval. The change is effective on the next scheduled event for deduction of fixed
     * costs.
     * @param newAmount the new amount
     */
    public void changeAmount(final Money newAmount)
    {
        this.amount = newAmount;
    }

    /**
     * Scheduled method to book the fixed costs
     */
    protected void bookFixedCost()
    {
        this.bankAccount.withdrawFromBalance(this.amount);
        try
        {
            this.owner.getSimulator().scheduleEventRel(this.interval, this, this, "bookFixedCost", null);
        }
        catch (Exception exception)
        {
            logger.fatal("changeInterval", exception);
        }
    }

    /**
     * @return Returns the amount.
     */
    public Money getAmount()
    {
        return this.amount;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @return Returns the interval.
     */
    public Duration getInterval()
    {
        return this.interval;
    }

    /**
     * @return Returns the owner.
     */
    public SupplyChainActor getOwner()
    {
        return this.owner;
    }
}
