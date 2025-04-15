package nl.tudelft.simulation.supplychain.role.transporting;

import java.io.Serializable;
import java.util.Objects;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.NamedLocation;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Sku;

/**
 * TransportStep models one step of a TransportOption. It describes the origin Node and destination Node (as an NamedLocation --
 * any location where trandsfer takes place, such as a port or terminal, is seen as an actor in the logistics network),
 * estimated loading time at the origin Node and estimated unloading time at the destination Node, the mode of transport between
 * origin and destination, and the costs associated with loading and with unloading (including storage costs).
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportOptionStep implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221202L;

    /** the identifier for this TransportStep. */
    private final String id;

    /** the actor at the origin (company, port, terminal). */
    private final NamedLocation origin;

    /** the actor at the destination (company, port, terminal). */
    private final NamedLocation destination;

    /** the transport mode between origin and destination. */
    private final TransportMode transportMode;

    /** the role containing the rates and durations. */
    private final TransportingRole transportingRole;

    /**
     * @param id the identifier for this TransportStep
     * @param origin the actor at the origin (company, port, terminal)
     * @param destination the actor at the destination (company, port, terminal)
     * @param transportMode the transport mode between origin and destination
     * @param transportingRole the role containing the rates and durations
     */
    public TransportOptionStep(final String id, final NamedLocation origin, final NamedLocation destination,
            final TransportMode transportMode, final TransportingRole transportingRole)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(origin, "origin cannot be null");
        Throw.whenNull(destination, "destination cannot be null");
        Throw.whenNull(transportMode, "transportMode cannot be null");
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.transportMode = transportMode;
        this.transportingRole = transportingRole;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the actor at the origin (company, port, terminal).
     * @return the actor at the origin (company, port, terminal)
     */
    public NamedLocation getOrigin()
    {
        return this.origin;
    }

    /**
     * Return the actor at the destination (company, port, terminal).
     * @return the actor at the destination (company, port, terminal)
     */
    public NamedLocation getDestination()
    {
        return this.destination;
    }

    /**
     * Return the distance of this transport step.
     * @return the distance of this transport step
     */
    public Length getTransportDistance()
    {
        SupplyChainModelInterface model = this.transportingRole.getSimulator().getModel();
        return model.calculateDistance(getOrigin().getLocation(), getDestination().getLocation());
    }

    /**
     * Return the estimated transport time of this transport step.
     * @param sku the sku to use -- (un)loading time may differ per SKU
     * @return the estimated transport time of this transport step
     */
    public Duration getEstimatedTransportDuration(final Sku sku)
    {
        return getTransportDistance().divide(getTransportMode().getAverageSpeed())
                .plus(this.transportingRole.getEstimatedLoadingTime(sku))
                .plus(this.transportingRole.getEstimatedUnloadingTime(sku));
    }

    /**
     * Return the estimated cost per SKU of this transport step.
     * @param sku the SKU to calculate the cost for
     * @return the estimated transport cost per SKU of this transport step
     */
    public Money getEstimatedTransportCost(final Sku sku)
    {
        return this.transportingRole.getEstimatedLoadingCost(sku)
                .plus(this.transportingRole.getEstimatedUnloadingCost(sku).plus(this.transportingRole
                        .getEstimatedTransportCostPerKm(sku).multiplyBy(getTransportDistance().si / 1000.0)));
    }

    /**
     * Return the transport mode between origin and destination.
     * @return the transport mode between origin and destination
     */
    public TransportMode getTransportMode()
    {
        return this.transportMode;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.destination, this.id, this.origin, this.transportMode);
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
        TransportOptionStep other = (TransportOptionStep) obj;
        return Objects.equals(this.destination, other.destination) && Objects.equals(this.id, other.id)
                && Objects.equals(this.origin, other.origin) && Objects.equals(this.transportMode, other.transportMode);
    }

    @Override
    public String toString()
    {
        return "TransportOptionStep [id=" + this.id + ", origin=" + this.origin + ", destination=" + this.destination
                + ", transportMode=" + this.transportMode + "]";
    }

}
