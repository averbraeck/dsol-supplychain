package nl.tudelft.simulation.supplychain.finance;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.Dist;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousMoney wraps any continuous distribution and can draw Money instances with a given MoneyUnit.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistContinuousMoney extends Dist
{
    /** */
    private static final long serialVersionUID = 20221202L;

    /** the wrapped distribution. */
    private DistContinuous wrappedDistribution;

    /** the money unit for the values of the distribution. */
    private MoneyUnit moneyUnit;

    /**
     * constructs a new continuous distribution for Money.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param moneyUnit MoneyUnit; the money unit for the values of the distribution
     */
    public DistContinuousMoney(final DistContinuous wrappedDistribution, final MoneyUnit moneyUnit)
    {
        super(wrappedDistribution.getStream());
        Throw.whenNull(wrappedDistribution, "wrappedDistribution cannot be null");
        Throw.whenNull(moneyUnit, "moneyUnit cannot be null");
        this.wrappedDistribution = wrappedDistribution;
        this.moneyUnit = moneyUnit;
    }

    /**
     * Draw the next stream value according to the probability of this this distribution.
     * @return the next Money value based on the wrapped distribution.
     */
    public Money draw()
    {
        return new Money(this.wrappedDistribution.draw(), this.moneyUnit);
    }

    /**
     * returns the probability density for a Money scalar.
     * @param money Money; the value for which to calculate the probability density.
     * @return double; the probability density for value scalar
     */
    public double probDensity(final Money money)
    {
        Throw.when(!this.moneyUnit.equals(money.getMoneyUnit()), IllegalArgumentException.class,
                "cannot calculate probability for different mney units"); // also covers null
        return this.wrappedDistribution.getProbabilityDensity(money.doubleValue());
    }

    /**
     * Return the wrapped distribution.
     * @return DistContinuous; the wrapped distribution
     */
    public DistContinuous getWrappedDistribution()
    {
        return this.wrappedDistribution;
    }

    /**
     * Return the money unit in which the samples from the wrapped distribution are returned.
     * @return MoneyUnit; the money unit in which the samples from the wrapped distribution are returned
     */
    public MoneyUnit getMMoneyUnit()
    {
        return this.moneyUnit;
    }
}
