package nl.tudelft.simulation.supplychain.finance;

import java.io.Serializable;

/**
 * Money.java.
 * <p>
 * Copyright (c) 2019-2019 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Money extends Number implements Serializable
{
    /** */
    private static final long serialVersionUID = 20200211L;
    
    private final double amount;
    private final MoneyUnit moneyUnit;
    
    /**
     * @param amount
     * @param moneyUnit
     */
    public Money(double amount, MoneyUnit moneyUnit)
    {
        super();
        this.amount = amount;
        this.moneyUnit = moneyUnit;
    }

    /**
     * @return amount
     */
    public double getAmount()
    {
        return this.amount;
    }

    /**
     * @return moneyUnit
     */
    public MoneyUnit getMoneyUnit()
    {
        return this.moneyUnit;
    }
    
    public Money plus(Money inc)
    {
        // TODO: check same MoneyUnit
        return new Money(amount + inc.getAmount(), getMoneyUnit());
    }
    
    public Money minus(Money dec)
    {
        // TODO: check same MoneyUnit
        return new Money(amount - dec.getAmount(), getMoneyUnit());
    }

    public Money multiplyBy(double factor)
    {
        return new Money(amount * factor, getMoneyUnit());
    }

    public Money divideBy(double factor)
    {
        return new Money(amount / factor, getMoneyUnit());
    }

    public boolean eq(Money other)
    {
        // TODO: check same MoneyUnit
        return amount == other.getAmount();
    }

    public boolean ne(Money other)
    {
        // TODO: check same MoneyUnit
        return amount != other.getAmount();
    }
    public boolean lt(Money other)
    {
        // TODO: check same MoneyUnit
        return amount < other.getAmount();
    }
    public boolean le(Money other)
    {
        // TODO: check same MoneyUnit
        return amount <= other.getAmount();
    }
    public boolean gt(Money other)
    {
        // TODO: check same MoneyUnit
        return amount > other.getAmount();
    }
    public boolean ge(Money other)
    {
        // TODO: check same MoneyUnit
        return amount >= other.getAmount();
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

