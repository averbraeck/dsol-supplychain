package nl.tudelft.simulation.supplychain.product;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.content.GroupedContent;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.ProductContent;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;

/**
 * The Shipment is the information for an amount of products that can be transferred from one actor to another actor.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Shipment implements GroupedContent, ProductContent
{
    /** */
    private static final long serialVersionUID = 1L;

    /** sender the sender of the shipment. */
    private final SellingActor sender;

    /** receiver the receiver of the shipment. */
    private final PurchasingActor receiver;

    /** timestamp the absolute time when the message was created. */
    private final Time timestamp;

    /** uniqueId the unique id of the message. */
    private final long uniqueId;

    /** groupingId the id used to group multiple messages, such as the demandId or the orderId. */
    private final long groupingId;

    /** order the order for which this was the confirmation. */
    private final Order order;

    /** totalCargoValue the total value of the cargo. */
    private final Money totalCargoValue;

    /** is the cargo in transit? */
    private boolean inTransit = false;

    /** has the cargo been delivered? */
    private boolean delivered = false;

    /**
     * Create a shipment.
     * @param sender the sender of the shipment
     * @param receiver the receiver of the shipment
     * @param timestamp the absolute time when the message was created
     * @param uniqueId the unique id of the message
     * @param groupingId the id used to group multiple messages, such as the demandId or the orderId
     * @param order the order for which this was the confirmation
     * @param totalCargoValue the total value of the cargo
     */
    public Shipment(final SellingActor sender, final PurchasingActor receiver, final Time timestamp, final long uniqueId,
            final long groupingId, final Order order, final Money totalCargoValue)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.uniqueId = uniqueId;
        this.groupingId = groupingId;
        this.order = order;
        this.totalCargoValue = totalCargoValue;
    }

    /**
     * Create a shipment.
     * @param sender the sender of the shipment
     * @param receiver the receiver of the shipment
     * @param order the order for which this was the confirmation
     * @param totalCargoValue the total value of the cargo
     */
    public Shipment(final SellingActor sender, final PurchasingActor receiver, final Order order, final Money totalCargoValue)
    {
        this(sender, receiver, sender.getSimulatorTime(), sender.getModel().getUniqueContentId(), order.groupingId(), order,
                totalCargoValue);
    }

    @Override
    public SellingActor sender()
    {
        return this.sender;
    }

    @Override
    public PurchasingActor receiver()
    {
        return this.receiver;
    }

    @Override
    public Time timestamp()
    {
        return this.timestamp;
    }

    @Override
    public long uniqueId()
    {
        return this.uniqueId;
    }

    @Override
    public long groupingId()
    {
        return this.groupingId;
    }

    /**
     * Return the order on which this shipment is based.
     * @return the order on which this shipment is based
     */
    public Order order()
    {
        return this.order;
    }

    /**
     * Returnt the total value of the cargo.
     * @return the total value of the cargo
     */
    public Money totalCargoValue()
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
     * @param inTransit set whether the cargo is in transit
     */
    public void setInTransit(final boolean inTransit)
    {
        this.inTransit = inTransit;
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
     * @param delivered true whether the cargo has been delivered
     */
    public void setDelivered(final boolean delivered)
    {
        this.delivered = delivered;
    }

    @Override
    public Product product()
    {
        return this.order.product();
    }

    @Override
    public double amount()
    {
        return this.order.amount();
    }
}
