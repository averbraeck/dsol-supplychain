package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.NamedLocation;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The Shipment is the actual goods (a certain amount of products) that are being transferred from one actor to another actor.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Shipment implements Serializable
{
    /** */
    private static final long serialVersionUID = 1L;

    /** sender the sender of the shipment. */
    private final WarehousingActor shippingActor;

    /** receiver the receiver of the shipment. */
    private final WarehousingActor receivingActor;

    /** timestamp the absolute time when the shipment was created. */
    private final Time timestamp;

    /** uniqueId the unique id of the shipment. */
    private final long uniqueId;

    /** groupingId the id used to group multiple messages and content. */
    private final long groupingId;

    /** order the order for which this was the confirmation. */
    private final Order order;

    /** totalCargoValue the total value of the cargo. */
    private final Money totalCargoValue;

    /** is the cargo in transit? */
    private boolean inTransit;

    /** has the cargo been delivered? */
    private boolean delivered = false;

    /** The origin if moving; will be the originating actor before transport and the receiving actor after arrival. */
    private NamedLocation origin;

    /** The destination if moving; will be the originating actor before transport and the receiving actor after arrival. */
    private NamedLocation destination;

    /**
     * Create a shipment.
     * @param shippingActor the sender of the shipment
     * @param receivingActor the receiver of the shipment
     * @param order the order for which this was the confirmation
     * @param totalCargoValue the total value of the cargo
     */
    public Shipment(final WarehousingActor shippingActor, final WarehousingActor receivingActor, final Order order,
            final Money totalCargoValue)
    {
        this.shippingActor = shippingActor;
        this.receivingActor = receivingActor;
        this.timestamp = shippingActor.getSimulatorTime();
        this.uniqueId = shippingActor.getModel().getUniqueContentId();
        this.groupingId = order.groupingId();
        this.order = order;
        this.totalCargoValue = totalCargoValue;
        this.origin = shippingActor;
        this.destination = shippingActor;
        this.inTransit = false;
    }

    /**
     * Return the sender of the shipment (to allow for a reply to be sent).
     * @return the sender of the shipment
     */
    public WarehousingActor getShippingActor()
    {
        return this.shippingActor;
    }

    /**
     * Return the receiver of the shipment.
     * @return the receiver of the shipment
     */
    public WarehousingActor getReceivingActor()
    {
        return this.receivingActor;
    }

    /**
     * Return the timestamp of the shipment.
     * @return the timestamp of the shipment
     */
    public Time getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * Return the unique shipment id.
     * @return the unique shipment id.
     */
    public long getUniqueId()
    {
        return this.uniqueId;
    }

    /**
     * Return the grouping id of the content.
     * @return the grouping id of the content
     */
    public long getGroupingId()
    {
        return this.groupingId;
    }

    /**
     * Return the order on which this shipment is based.
     * @return the order on which this shipment is based
     */
    public Order getOrder()
    {
        return this.order;
    }

    /**
     * Returnt the total value of the cargo.
     * @return the total value of the cargo
     */
    public Money getTotalCargoValue()
    {
        return this.totalCargoValue;
    }

    /**
     * Return whether the cargo is in transit.
     * @return whether the cargo is in transit
     */
    public boolean isInTransit()
    {
        return this.inTransit;
    }

    /**
     * Set whether the cargo is in transit.
     * @param legOrigin the origin of the current transport leg
     * @param legDestination the destination of the current transport leg
     */
    public void setTransit(final NamedLocation legOrigin, final NamedLocation legDestination)
    {
        this.inTransit = true;
        this.origin = legOrigin;
        this.destination = legDestination;
    }

    /**
     * Return whether the cargo has been delivered.
     * @return whether the cargo has been delivered
     */
    public boolean isDelivered()
    {
        return this.delivered;
    }

    /**
     * Set whether the cargo has been delivered.
     */
    public void setDelivered()
    {
        this.delivered = true;
        this.inTransit = false;
        this.origin = this.receivingActor;
        this.destination = this.receivingActor;
    }

    /**
     * Return the origin.
     * @return origin
     */
    public NamedLocation getOrigin()
    {
        return this.origin;
    }

    /**
     * Return the destination.
     * @return destination
     */
    public NamedLocation getDestination()
    {
        return this.destination;
    }

    /**
     * Return the product of the content.
     * @return the product of the content
     */
    public Product getProduct()
    {
        return this.order.product();
    }

    /**
     * Return the amount of product.
     * @return the amount of product
     */
    public double getAmount()
    {
        return this.order.amount();
    }
}
