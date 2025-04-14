package nl.tudelft.simulation.supplychain.role.financing.process;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.role.financing.FinancingRole;

/**
 * When a supply chain actor is created, one or more FixedCost objects can be created to book fixed costs for e.g. personnel,
 * buildings, other resources on an interval (e.g. monthly) basis. When the interval or amount is changed, the scheduling
 * changes immediately and the amount is effective in the next scheduled fixed cost event.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FixedCostProcess extends AutonomousProcess<FinancingRole>
{
    /** the description of the type of fixed cost. */
    private String description;

    /** The interval for booking the fixed cost. */
    private Duration interval;

    /** The amount to book on every interval. */
    private Money amount;

    /** the event for the next period -- stored to be able to remove it. */
    private SimEventInterface<Duration> fixedAmountEvent;

    /**
     * Create the autonomous process for a fixed cost item for an actor.
     * @param role the FinancingRole to wich these fixed costs belong
     * @param description the description
     * @param interval the interval for booking fixed cost
     * @param amount the fixed cost per interval
     */
    public FixedCostProcess(final FinancingRole role, final String description, final Duration interval, final Money amount)
    {
        super(role);
        Throw.whenNull(description, "description cannot be null");
        Throw.whenNull(interval, "interval cannot be null");
        Throw.when(interval.le0(), IllegalArgumentException.class, "interval duration cannot be <= 0");
        Throw.whenNull(amount, "amount cannot be null");
        this.description = description;
        this.interval = interval;
        this.amount = amount;
        schedule();
    }

    /**
     * Schedule the next withdrawal event.
     */
    private void schedule()
    {
        this.fixedAmountEvent = getSimulator().scheduleEventRel(this.interval, this, "bookFixedCost", null);
    }

    /**
     * Change the interval to book fixed costs. The booking event is immediately rescheduled to the END of the interval; next
     * deduction takes place after 'interval' days.
     * @param newInterval the new interval
     */
    public void changeInterval(final Duration newInterval)
    {
        Throw.whenNull(newInterval, "interval cannot be null");
        Throw.when(newInterval.le0(), IllegalArgumentException.class, "interval duration cannot be <= 0");
        this.interval = newInterval;
        if (this.fixedAmountEvent != null)
        {
            // cancel the previous event
            getSimulator().cancelEvent(this.fixedAmountEvent);
        }
        schedule();
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
     * Scheduled method to book the fixed costs.
     */
    protected void bookFixedCost()
    {
        getRole().getBank().withdrawFromBalance(getRole().getActor(), this.amount);
        schedule();
    }

    /**
     * Return the fixed cost per interval.
     * @return the fixed cost per interval
     */
    public Money getAmount()
    {
        return this.amount;
    }

    /**
     * Return the description of the fixed cost item.
     * @return the description of the fixed cost item
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Return the withdrawal interval.
     * @return the withdrawal interval
     */
    public Duration getInterval()
    {
        return this.interval;
    }
}
