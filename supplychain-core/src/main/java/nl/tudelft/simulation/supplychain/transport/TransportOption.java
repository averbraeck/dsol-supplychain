package nl.tudelft.simulation.supplychain.transport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.ImmutableArrayList;
import org.djutils.immutablecollections.ImmutableList;

import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Sku;

/**
 * TransportOption describes a way to get goods from A to B. The class can incicate a singular transport mode that transports
 * the goods from A to B, e.g., trucking, or a multimodal option that involves, e.g., a truck to the Port, a containrship to
 * another port, and trucking to the final destination. Each of the modes has a different speed, and each of the transfers will
 * take time (and possibly cost money),
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportOption implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221202L;

    /** the id of the TransportOption. */
    private final String id;

    /** the sequence of TransportSteps. */
    private ImmutableList<TransportOptionStep> transportSteps = new ImmutableArrayList<>(new ArrayList<>());

    /**
     * make a new TransportOption.
     * @param id String; the id of the TransportOption
     */
    public TransportOption(final String id)
    {
        Throw.whenNull(id, "id cannot be null");
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the transport steps.
     * @return ImmutableList&lt;TransportOptionStep&gt;; the transport steps
     */
    public ImmutableList<TransportOptionStep> getTransportSteps()
    {
        return this.transportSteps;
    }

    /**
     * Add a transport step.
     * @param transportOptionStep TransportOptionStep; the new transport step
     */
    public void addTransportStep(final TransportOptionStep transportOptionStep)
    {
        Throw.whenNull(transportOptionStep, "transportOptionStep cannot be null");
        List<TransportOptionStep> steps = this.transportSteps.toList();
        steps.add(transportOptionStep);
        this.transportSteps = new ImmutableArrayList<>(steps);
    }

    /**
     * Add a number of transport steps.
     * @param steps List&lt;TransportOptionStep&gt;; the new transport steps
     */
    public void addTransportSteps(final List<TransportOptionStep> steps)
    {
        Throw.whenNull(steps, "steps cannot be null");
        for (TransportOptionStep transportOptionStep : steps)
        {
            addTransportStep(transportOptionStep);
        }
    }

    /**
     * Return the estimated total transport duration from sender to receiver.
     * @param sku Sku; the sku that needs to be transported
     * @return Duration; the total transport duration including transport and transloading
     */
    public Duration estimatedTotalTransportDuration(final Sku sku)
    {
        Duration result = Duration.ZERO;
        for (TransportOptionStep step : this.transportSteps)
        {
            result = result.plus(step.getEstimatedLoadingTime(sku)).plus(step.getEstimatedUnloadingTime(sku));
            SupplyChainModelInterface model = step.getOrigin().getSimulator().getModel();
            Length distance = model.calculateDistance(step.getOrigin().getLocation(), step.getDestination().getLocation());
            result = result.plus(distance.divide(step.getTransportMode().getAverageSpeed()));
        }
        return result;
    }

    /**
     * Return the estimated total transport cost from sender to receiver.
     * @param sku Sku; the sku that needs to be transported
     * @return Money; the total costs including transport and transloading
     */
    public Money estimatedTotalTransportCost(final Sku sku)
    {
        double cost = 0.0;
        MoneyUnit costUnit = null;
        for (TransportOptionStep step : this.transportSteps)
        {
            Money costPerKm = step.getEstimatedTransportCostPerKm(sku);
            if (costUnit == null)
            {
                costUnit = costPerKm.getMoneyUnit();
            }
            double distanceKm = step.getOrigin().getSimulator().getModel().calculateDistance(step.getOrigin().getLocation(),
                    step.getDestination().getLocation()).si / 1000.0;
            cost += step.getEstimatedLoadingCost(sku).getAmount();
            cost += step.getEstimatedUnloadingCost(sku).getAmount();
            cost += costPerKm.getAmount() * distanceKm;
        }
        return new Money(cost, costUnit);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id, this.transportSteps);
    }

    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransportOption other = (TransportOption) obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.transportSteps, other.transportSteps);
    }

    @Override
    public String toString()
    {
        return "TransportOption [id=" + this.id + ", transportSteps=" + this.transportSteps + "]";
    }

}
