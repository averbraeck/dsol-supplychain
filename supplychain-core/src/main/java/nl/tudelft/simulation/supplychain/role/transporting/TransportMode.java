package nl.tudelft.simulation.supplychain.role.transporting;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.ImmutableLinkedHashSet;
import org.djutils.immutablecollections.ImmutableSet;

import nl.tudelft.simulation.supplychain.product.Sku;

/**
 * TransportMode stores the information on a mode of transport, to transport products from an origin to a destination.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportMode implements Identifiable, Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221202L;

    /** id (name) of the transport mode. */
    private String id;

    /** average transportation speed of the mode. */
    private Speed averageSpeed;

    /** average handling time on either side. */
    private Duration averageHandlingTime;

    /** is this mode typically used on the same landmass (continental)? */
    private final boolean continental;

    /** is this mode typically used between landmasses (intercontinental)? */
    private final boolean intercontinental;

    /** SKUs that the TransportMode can handle. */
    private ImmutableSet<Sku> handledSkuSet = new ImmutableLinkedHashSet<>(new LinkedHashSet<>());

    /** Airplane. */
    public static final TransportMode AIRPLANE =
            new TransportMode("airplane", true, true).setAverageSpeed(new Speed(600.0, SpeedUnit.KM_PER_HOUR))
                    .setAverageHandlingTime(new Duration(8.0, DurationUnit.HOUR));

    /** Truck. */
    public static final TransportMode TRUCK =
            new TransportMode("truck", true, false).setAverageSpeed(new Speed(80.0, SpeedUnit.KM_PER_HOUR))
                    .setAverageHandlingTime(new Duration(2.0, DurationUnit.HOUR));

    /** Ship. */
    public static final TransportMode SHIP = new TransportMode("ship", false, true)
            .setAverageSpeed(new Speed(14.0, SpeedUnit.KNOT)).setAverageHandlingTime(new Duration(24.0, DurationUnit.HOUR));

    /** Rail. */
    public static final TransportMode RAIL =
            new TransportMode("rail", true, false).setAverageSpeed(new Speed(40.0, SpeedUnit.KM_PER_HOUR))
                    .setAverageHandlingTime(new Duration(12.0, DurationUnit.HOUR));

    /** Barge. */
    public static final TransportMode BARGE =
            new TransportMode("barge", true, false).setAverageSpeed(new Speed(16.0, SpeedUnit.KM_PER_HOUR))
                    .setAverageHandlingTime(new Duration(8.0, DurationUnit.HOUR));

    /**
     * Constructor for TransportMode.
     * @param id the name of the transport mode
     * @param continental is this mode typically used on the same landmass (continental)?
     * @param intercontinental is this mode typically used between landmasses (intercontinental)?
     */
    public TransportMode(final String id, final boolean continental, final boolean intercontinental)
    {
        Throw.whenNull(id, "id cannot be null");
        this.id = id;
        this.continental = continental;
        this.intercontinental = intercontinental;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the average transportation speed of the mode.
     * @return the average transportation speed of the mode
     */
    public Speed getAverageSpeed()
    {
        return this.averageSpeed;
    }

    /**
     * Set a new average transportation speed for the mode.
     * @param newAverageSpeed a new average transportation speed for the mode
     * @return TransportMode for method chaining
     */
    public TransportMode setAverageSpeed(final Speed newAverageSpeed)
    {
        Throw.whenNull(newAverageSpeed, "newAverageSpeed cannot be null");
        this.averageSpeed = newAverageSpeed;
        return this;
    }

    /**
     * Return the average handling time before and after transport for this mode.
     * @return the average handling time before and after transport for this mode
     */
    public Duration getAverageHandlingTime()
    {
        return this.averageHandlingTime;
    }

    /**
     * Set a new average handling time before and after transport for this mode.
     * @param newAverageHandlingTime a new average handling time before and after transport for this mode
     * @return TransportMode for method chaining
     */
    public TransportMode setAverageHandlingTime(final Duration newAverageHandlingTime)
    {
        Throw.whenNull(newAverageHandlingTime, "newAverageHandlingTime cannot be null");
        this.averageHandlingTime = newAverageHandlingTime;
        return this;
    }

    /**
     * Add SKUs to the set of SKUs that this TransportMode can handle.
     * @param skus one or more SKUs to be added to the set of SKUs that this TransportMode can handle
     * @return TransportMode for method chaining
     */
    public TransportMode addHandledSku(final Sku... skus)
    {
        Throw.whenNull(skus, "skus cannot be null");
        Set<Sku> handledSkus = this.handledSkuSet.toSet();
        handledSkus.addAll(List.of(skus));
        this.handledSkuSet = new ImmutableLinkedHashSet<>(handledSkus);
        return this;
    }

    /**
     * Return the set of SKUs that this TransportMode can handle.
     * @return the set of SKUs that this TransportMode can handle
     */
    public ImmutableSet<Sku> getHandledSkuSet()
    {
        return this.handledSkuSet;
    }

    /**
     * Return whether this mode typically used on the same landmass (continental).
     * @return whether this mode typically used on the same landmass (continental)
     */
    public boolean isContinental()
    {
        return this.continental;
    }

    /**
     * Return whether this mode typically used between landmasses (intercontinental).
     * @return whether this mode typically used between landmasses (intercontinental)
     */
    public boolean isIntercontinental()
    {
        return this.intercontinental;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
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
        TransportMode other = (TransportMode) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString()
    {
        return this.id;
    }
}
