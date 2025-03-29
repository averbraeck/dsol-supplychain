package nl.tudelft.simulation.supplychain.transport;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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

    /** SKUs that the TransportMode can handle. */
    private ImmutableSet<Sku> handledSkuSet = new ImmutableLinkedHashSet<>(new LinkedHashSet<>());

    /**
     * Constructor for TransportMode.
     * @param id String; the name of the transport mode
     * @param averageSpeed Speed; the average transportation speed of the mode
     */
    public TransportMode(final String id, final Speed averageSpeed)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(averageSpeed, "averageSpeed cannot be null");
        this.id = id;
        this.averageSpeed = averageSpeed;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the average transportation speed of the mode.
     * @return averageSpeed Speed; the average transportation speed of the mode
     */
    public Speed getAverageSpeed()
    {
        return this.averageSpeed;
    }

    /**
     * Set a new the average transportation speed for the mode.
     * @param averageSpeed Speed; a new average transportation speed for the mode
     */
    public void setAverageSpeed(final Speed averageSpeed)
    {
        Throw.whenNull(averageSpeed, "averageSpeed cannot be null");
        this.averageSpeed = averageSpeed;
    }

    /**
     * Add a SKU to the set of SKUs that this TransportMode can handle.
     * @param sku SKU; a SKU to be added to the set of SKUs that this TransportMode can handle
     */
    public void addHandledSku(final Sku sku)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Set<Sku> handledSkus = this.handledSkuSet.toSet();
        handledSkus.add(sku);
        this.handledSkuSet = new ImmutableLinkedHashSet<>(handledSkus);
    }

    /**
     * Return the set of SKUs that this TransportMode can handle.
     * @return handledSkuSet ImmutableSet&lt;Sku&gt;; the set of SKUs that this TransportMode can handle
     */
    public ImmutableSet<Sku> getHandledSkuSet()
    {
        return this.handledSkuSet;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
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
        TransportMode other = (TransportMode) obj;
        return Objects.equals(this.id, other.id);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.id;
    }
}
