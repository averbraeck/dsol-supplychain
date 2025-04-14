package nl.tudelft.simulation.supplychain.role.transporting;

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

import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Sku;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

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

    /** the transporting organization. */
    private final TransportingActor transportingActor;

    /** the pickup location. */
    private final WarehousingActor pickupActor;

    /** the delivery location. */
    private final WarehousingActor deliveryActor;

    /** the sequence of TransportSteps. */
    private ImmutableList<TransportOptionStep> transportSteps = new ImmutableArrayList<>(new ArrayList<>());

    /**
     * Make a new TransportOption.
     * @param id the id of the TransportOption
     * @param transportingActor the transporting organization
     * @param pickupActor the pickup location
     * @param deliveryActor the delivery location
     */
    public TransportOption(final String id, final TransportingActor transportingActor, final WarehousingActor pickupActor,
            final WarehousingActor deliveryActor)
    {
        this.id = id;
        this.transportingActor = transportingActor;
        this.pickupActor = pickupActor;
        this.deliveryActor = deliveryActor;
    }

    /**
     * Return the transport steps.
     * @return the transport steps
     */
    public ImmutableList<TransportOptionStep> getTransportSteps()
    {
        return this.transportSteps;
    }

    /**
     * Add a transport step.
     * @param transportOptionStep the new transport step
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
     * @param steps the new transport steps
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
     * Return the total transport distance from sender to receiver.
     * @return the total transport distance including transport and transloading
     */
    public Length totalTransportDistance()
    {
        Length result = Length.ZERO;
        for (TransportOptionStep step : this.transportSteps)
        {
            result = result.plus(step.getTransportDistance());
        }
        return result;
    }

    /**
     * Return the estimated total transport duration from sender to receiver.
     * @param sku the sku that needs to be transported
     * @return the total transport duration including transport and transloading
     */
    public Duration estimatedTotalTransportDuration(final Sku sku)
    {
        Duration result = Duration.ZERO;
        for (TransportOptionStep step : this.transportSteps)
        {
            result = result.plus(step.getEstimatedTransportDuration(sku));
        }
        return result;
    }

    /**
     * Return the estimated total transport cost from sender to receiver.
     * @param sku the sku that needs to be transported
     * @return the total costs including transport and transloading
     */
    public Money estimatedTotalTransportCost(final Sku sku)
    {
        double cost = 0.0;
        MoneyUnit costUnit = null;
        for (TransportOptionStep step : this.transportSteps)
        {
            Money stepCost = step.getEstimatedTransportCost(sku);
            if (costUnit == null)
            {
                costUnit = stepCost.getMoneyUnit();
            }
            cost += stepCost.getAmount();
        }
        return new Money(cost, costUnit);
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the transportingActor.
     * @return transportingActor
     */
    public TransportingActor getTransportingActor()
    {
        return this.transportingActor;
    }

    /**
     * Return the pickupActor.
     * @return pickupActor
     */
    public WarehousingActor getPickupActor()
    {
        return this.pickupActor;
    }

    /**
     * Return the deliveryActor.
     * @return deliveryActor
     */
    public WarehousingActor getDeliveryActor()
    {
        return this.deliveryActor;
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
