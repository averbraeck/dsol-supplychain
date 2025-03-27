package nl.tudelft.simulation.supplychain.finance;

import java.io.Serializable;

import org.djutils.exceptions.Throw;

/**
 * Money implements a monetary value and is modeled after the Scalar and Unit in djunits.
 * <p>
 * Copyright (c) 2019-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Money extends Number implements Serializable
{
    /** */
    private static final long serialVersionUID = 20200211L;

    /** the monetary value in the money unit. */
    private final double amount;

    /** the money unit for this monetary value. */
    private final MoneyUnit moneyUnit;

    /**
     * Create a monetary value.
     * @param amount double; the monetary value in the money unit
     * @param moneyUnit MoneyUnit; the money unit for this monetary value
     */
    public Money(final double amount, final MoneyUnit moneyUnit)
    {
        Throw.whenNull(moneyUnit, "moneyUnit cannot be null");
        Throw.when(Double.isNaN(amount), IllegalArgumentException.class, "amount cannot be NaN");
        this.amount = amount;
        this.moneyUnit = moneyUnit;
    }

    /**
     * Return the monetary value in the money unit.
     * @return double; the monetary value in the money unit
     */
    public double getAmount()
    {
        return this.amount;
    }

    /**
     * Return the money unit for this monetary value.
     * @return moneyUnit MoneyUnit; the money unit for this monetary value
     */
    public MoneyUnit getMoneyUnit()
    {
        return this.moneyUnit;
    }

    /**
     * Return a monetary amount that is the sum of this monetary amount and the increment.
     * @param inc Money; the amount of money to add
     * @return Money; a monetary amount that is the sum of this monetary amount and the increment
     */
    public Money plus(final Money inc)
    {
        Throw.when(!getMoneyUnit().equals(inc.getMoneyUnit()), IllegalArgumentException.class, "unequal money units");
        return new Money(this.amount + inc.getAmount(), getMoneyUnit());
    }

    /**
     * Return a monetary amount that is the difference of this monetary amount and the decrement.
     * @param dec Money; the amount of money to subtract
     * @return Money; a monetary amount that is the diffference of this monetary amount and the decrement
     */
    public Money minus(final Money dec)
    {
        Throw.when(!getMoneyUnit().equals(dec.getMoneyUnit()), IllegalArgumentException.class, "unequal money units");
        return new Money(this.amount - dec.getAmount(), getMoneyUnit());
    }

    /**
     * Return a monetary amount that is the multiplication of this monetary amount and the factor.
     * @param factor double; the multiplication factor
     * @return Money; a monetary amount that is the multiplication of this monetary amount and the factor
     */
    public Money multiplyBy(final double factor)
    {
        return new Money(this.amount * factor, getMoneyUnit());
    }

    /**
     * Return a monetary amount that is the division of this monetary amount by the factor.
     * @param factor double; the division factor
     * @return Money; a monetary amount that is the division of this monetary amount by the factor
     */
    public Money divideBy(final double factor)
    {
        return new Money(this.amount / factor, getMoneyUnit());
    }

    /**
     * Return whether this monetary amount is equal to the other monetary amount.
     * @param other Money; the other monetary amount to compare with
     * @return boolean; whether this monetary amount is equal to the other monetary amount
     */
    public boolean eq(final Money other)
    {
        Throw.when(!getMoneyUnit().equals(other.getMoneyUnit()), IllegalArgumentException.class, "unequal money units");
        return this.amount == other.getAmount();
    }

    /**
     * Return whether this monetary amount is unequal to the other monetary amount.
     * @param other Money; the other monetary amount to compare with
     * @return boolean; whether this monetary amount is unequal to the other monetary amount
     */
    public boolean ne(final Money other)
    {
        Throw.when(!getMoneyUnit().equals(other.getMoneyUnit()), IllegalArgumentException.class, "unequal money units");
        return this.amount != other.getAmount();
    }

    /**
     * Return whether this monetary amount is less than the other monetary amount.
     * @param other Money; the other monetary amount to compare with
     * @return boolean; whether this monetary amount is less than the other monetary amount
     */
    public boolean lt(final Money other)
    {
        Throw.when(!getMoneyUnit().equals(other.getMoneyUnit()), IllegalArgumentException.class, "unequal money units");
        return this.amount < other.getAmount();
    }

    /**
     * Return whether this monetary amount is less than or equal to the other monetary amount.
     * @param other Money; the other monetary amount to compare with
     * @return boolean; whether this monetary amount is less than or equal to the other monetary amount
     */
    public boolean le(final Money other)
    {
        Throw.when(!getMoneyUnit().equals(other.getMoneyUnit()), IllegalArgumentException.class, "unequal money units");
        return this.amount <= other.getAmount();
    }

    /**
     * Return whether this monetary amount is greater than the other monetary amount.
     * @param other Money; the other monetary amount to compare with
     * @return boolean; whether this monetary amount is greater than the other monetary amount
     */
    public boolean gt(final Money other)
    {
        Throw.when(!getMoneyUnit().equals(other.getMoneyUnit()), IllegalArgumentException.class, "unequal money units");
        return this.amount > other.getAmount();
    }

    /**
     * Return whether this monetary amount is greater than or equal to the other monetary amount.
     * @param other Money; the other monetary amount to compare with
     * @return boolean; whether this monetary amount is greater than or equal to the other monetary amount
     */
    public boolean ge(final Money other)
    {
        Throw.when(!getMoneyUnit().equals(other.getMoneyUnit()), IllegalArgumentException.class, "unequal money units");
        return this.amount >= other.getAmount();
    }

    /** {@inheritDoc} */
    @Override
    public int intValue()
    {
        return (int) Math.round(this.amount);
    }

    /** {@inheritDoc} */
    @Override
    public long longValue()
    {
        return Math.round(this.amount);
    }

    /** {@inheritDoc} */
    @Override
    public float floatValue()
    {
        return (float) this.amount;
    }

    /** {@inheritDoc} */
    @Override
    public double doubleValue()
    {
        return this.amount;
    }

}
