package nl.tudelft.simulation.supplychain.transport;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.finance.DistContinuousMoney;
import nl.tudelft.simulation.supplychain.product.Sku;

/**
 * StochasticTransportStep models one step of a StochasticTransportOption. It describes the origin Node and destination Node (as
 * an Actor -- any location where transfer takes place, such as a port or terminal, is seen as an actor in the logistics
 * network), stochastically estimated loading time at the origin Node and stochastically estimated unloading time at the
 * destination Node, the mode of transport between origin and destination, and the stochastic costs associated with loading,
 * unloading (including storage costs), and transport per km.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class StochasticTransportOptionStep implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221202L;

    /** the identifier for this TransportStep. */
    private final String id;

    /** the actor at the origin (company, port, terminal). */
    private final Actor origin;

    /** the actor at the destination (company, port, terminal). */
    private final Actor destination;

    /** the transport mode between origin and destination. */
    private final TransportMode transportMode;

    /** the estimated time to load SKUs at the origin (including typical waiting times). */
    private Map<Sku, DistContinuousDuration> estimatedLoadingTimes = new LinkedHashMap<>();

    /** the estimated time to unload SKUs at the destination (including typical waiting times). */
    private Map<Sku, DistContinuousDuration> estimatedUnloadingTimes = new LinkedHashMap<>();

    /** the estimated costs for loading and storing SKUs at the origin location. */
    private Map<Sku, DistContinuousMoney> estimatedLoadingCosts = new LinkedHashMap<>();

    /** the estimated costs for unloading and storing SKUs at the destination location. */
    private Map<Sku, DistContinuousMoney> estimatedUnloadingCosts = new LinkedHashMap<>();

    /** the estimated costs to transport an SKU per km. */
    private Map<Sku, DistContinuousMoney> estimatedTransportCostsPerKm = new LinkedHashMap<>();

    /**
     * @param id String; the identifier for this TransportStep
     * @param origin Actor; the actor at the origin (company, port, terminal)
     * @param destination Actor; the actor at the destination (company, port, terminal)
     * @param transportMode TransportMode; the transport mode between origin and destination
     */
    public StochasticTransportOptionStep(final String id, final Actor origin, final Actor destination,
            final TransportMode transportMode)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(origin, "origin cannot be null");
        Throw.whenNull(destination, "destination cannot be null");
        Throw.whenNull(transportMode, "transportMode cannot be null");
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.transportMode = transportMode;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the actor at the origin (company, port, terminal).
     * @return origin Actor; the actor at the origin (company, port, terminal)
     */
    public Actor getOrigin()
    {
        return this.origin;
    }

    /**
     * Return the actor at the destination (company, port, terminal).
     * @return destination Actor; the actor at the destination (company, port, terminal)
     */
    public Actor getDestination()
    {
        return this.destination;
    }

    /**
     * Return the transport mode between origin and destination.
     * @return TransportMode; the transport mode between origin and destination
     */
    public TransportMode getTransportMode()
    {
        return this.transportMode;
    }

    /**
     * Return the estimated time to load goods at the origin (including typical waiting times) for a given SKU.
     * @param sku Sku; the SKU to find the loading time for
     * @return DistContinuousDuration; the estimated time to load goods at the origin (including typical waiting times), or null
     *         when there is no stored loading time for the provided SKU
     */
    public DistContinuousDuration getEstimatedLoadingTime(final Sku sku)
    {
        return this.estimatedLoadingTimes.get(sku);
    }

    /**
     * Return the estimated time to unload goods at the destination (including typical waiting times) for a given SKU.
     * @param sku Sku; the SKU to find the unloading time for
     * @return DistContinuousDuration; the estimated time to unload goods at the destination (including typical waiting times),
     *         or null when there is no stored unloading time for the provided SKU
     */
    public DistContinuousDuration getEstimatedUnloadingTime(final Sku sku)
    {
        return this.estimatedUnloadingTimes.get(sku);
    }

    /**
     * Return the estimated costs for loading and storing the goods at the origin location for a given SKU.
     * @param sku Sku; the SKU to find the loading cost for
     * @return DistContinuousMoney; the estimated costs for loading and storing the goods at the origin location, or null when
     *         there is no stored loading cost for the provided SKU
     */
    public DistContinuousMoney getEstimatedLoadingCost(final Sku sku)
    {
        return this.estimatedLoadingCosts.get(sku);
    }

    /**
     * Return the estimated costs for loading and storing the goods at the destination location for a given SKU.
     * @param sku Sku; the SKU to find the unloading cost for
     * @return DistContinuousMoney; the estimated costs for unloading and storing the goods at the destination location, or null
     *         when there is no stored unloading cost for the provided SKU
     */
    public DistContinuousMoney getEstimatedUnloadingCost(final Sku sku)
    {
        return this.estimatedUnloadingCosts.get(sku);
    }

    /**
     * Return the estimated transport cost for the SKU per km, for the TransportStep's transport mode.
     * @param sku Sku; the SKU to find the transport cost for
     * @return DistContinuousMoney; the estimated estimated transport cost for the SKU per km, for the TransportStep's transport
     *         mode, or null when there is no stored cost for the provided SKU
     */
    public DistContinuousMoney getEstimatedTransportCostPerKm(final Sku sku)
    {
        return this.estimatedTransportCostsPerKm.get(sku);
    }

    /**
     * Set a new estimated time to load goods at the origin (including typical waiting times).
     * @param sku Sku; the SKU to find the loading duration for
     * @param estimatedLoadingTime DistContinuousDuration; new estimated time to load goods at the origin (including typical
     *            waiting times)
     */
    public void setEstimatedLoadingTime(final Sku sku, final DistContinuousDuration estimatedLoadingTime)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(estimatedLoadingTime, "estimatedLoadingTime cannot be null");
        this.estimatedLoadingTimes.put(sku, estimatedLoadingTime);
    }

    /**
     * Set a new estimated time to unload goods at the destination (including typical waiting times).
     * @param sku Sku; the SKU to set the unloading duration for
     * @param estimatedUnloadingTime DistContinuousDuration; new estimated time to unload goods at the destination (including
     *            typical waiting times)
     */
    public void setEstimatedUnloadingTime(final Sku sku, final DistContinuousDuration estimatedUnloadingTime)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(estimatedUnloadingTime, "estimatedUnloadingTime cannot be null");
        this.estimatedUnloadingTimes.put(sku, estimatedUnloadingTime);
    }

    /**
     * Set a new cost estimate for loading and storing the goods at the origin location.
     * @param sku Sku; the SKU to set the loading cost for
     * @param estimatedLoadingCost DistContinuousMoney; new cost estimate for loading and storing the goods at the origin
     *            location
     */
    public void setEstimatedLoadingCost(final Sku sku, final DistContinuousMoney estimatedLoadingCost)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(estimatedLoadingCost, "estimatedLoadingCost cannot be null");
        this.estimatedLoadingCosts.put(sku, estimatedLoadingCost);
    }

    /**
     * Set a new cost estimate for unloading and storing the goods at the destination location.
     * @param sku Sku; the SKU to set the unloading cost for
     * @param estimatedUnloadingCost DistContinuousMoney; new cost estimate for unloading and storing the goods at the
     *            destination location
     */
    public void setEstimatedUnloadingCost(final Sku sku, final DistContinuousMoney estimatedUnloadingCost)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(estimatedUnloadingCost, "estimatedUnloadingCost cannot be null");
        this.estimatedUnloadingCosts.put(sku, estimatedUnloadingCost);
    }

    /**
     * Set a new estimated transport cost for the SKU per km, for the TransportStep's transport mode.
     * @param sku Sku; the SKU to find the transport cost for
     * @param estimatedTransportCostPerKm DistContinuousMoney; the estimated estimated transport cost for the SKU per km, for
     *            the TransportStep's transport mode
     */
    public void setEstimatedTransportCostPerKm(final Sku sku, final DistContinuousMoney estimatedTransportCostPerKm)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(estimatedTransportCostPerKm, "estimatedTransportCostPerKm cannot be null");
        this.estimatedTransportCostsPerKm.put(sku, estimatedTransportCostPerKm);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.destination, this.id, this.origin, this.transportMode);
    }

    /** {@inheritDoc} */
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
        StochasticTransportOptionStep other = (StochasticTransportOptionStep) obj;
        return Objects.equals(this.destination, other.destination) && Objects.equals(this.id, other.id)
                && Objects.equals(this.origin, other.origin) && Objects.equals(this.transportMode, other.transportMode);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "TransportOptionStep [id=" + this.id + ", origin=" + this.origin + ", destination=" + this.destination
                + ", transportMode=" + this.transportMode + "]";
    }

}
