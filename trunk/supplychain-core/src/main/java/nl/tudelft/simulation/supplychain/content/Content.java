package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;
import java.util.Random;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * Content is the generic content for a Message. It creates a unique ID for itself, that is also unique over networks and in
 * distributed settings. Furthermore, it knows nothing more than a sender and a receiver. Content is abstract, as it should be
 * subclassed to give it a sensible 'payload'. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class Content implements Serializable
{
    /** */
    private static final long serialVersionUID = 1L;

    /** unique id of the message */
    private Serializable uniqueID;

    /** unique id of the InternalDemand that triggered the message chain */
    protected Serializable internalDemandID;

    /** the creation time of the content */
    private Time creationTime;

    /** sender of the message */
    private SupplyChainActor sender;

    /** receiver of the message */
    private SupplyChainActor receiver;

    /** the random stream */
    private static Random random = new Random();

    /**
     * Constructs a new Content object.
     * @param sender the sending actor of the message content
     * @param receiver the receiving actor of the message content
     * @param internalDemandID the InternalDemand id that triggered the chain. If it is equal to zero, assume that it is a new
     *            InternalDemand and allocate the uniqueID of the Content to it.
     */
    public Content(final SupplyChainActor sender, final SupplyChainActor receiver, final Serializable internalDemandID)
    {
        super();
        this.sender = sender;
        this.receiver = receiver;
        this.uniqueID = createUniqueID();
        this.internalDemandID = internalDemandID;
        this.creationTime = sender.getSimulatorTime();
    }

    /**
     * Returns the intended receiving actor of the message.
     * @return the receiving actor of the message.
     */
    public SupplyChainActor getReceiver()
    {
        return this.receiver;
    }

    /**
     * Returns the sending actor of the message.
     * @return the sending actor of the message.
     */
    public SupplyChainActor getSender()
    {
        return this.sender;
    }

    /**
     * Returns Returns the unique ID of this message
     * @return the uniqueID.
     */
    public Serializable getUniqueID()
    {
        return this.uniqueID;
    }

    /**
     * Returns Sets the unique ID of this message
     * @param id the uniqueID.
     */
    public void setUniqueID(final Serializable id)
    {
        this.uniqueID = id;
    }

    /**
     * Returns a unique Serializable ID for this message.
     * @return a unique ID.
     */
    private String createUniqueID()
    {
        return System.currentTimeMillis() + "-" + Math.abs(Content.random.nextLong());
    }

    /**
     * Returns the internalDemandID.
     * @return the Serializable unique ID of the Content.
     */
    public Serializable getInternalDemandID()
    {
        return this.internalDemandID;
    }

    /**
     * Returns the creationTime.
     * @return the time when the content was created.
     */
    public Time getCreationTime()
    {
        return this.creationTime;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1) + " from "
                + this.getSender().getName() + " to " + this.getReceiver().getName();
    }

    /**
     * Initially a null product is returned.
     * @return Returns the product.
     */
    public Product getProduct()
    {
        return null;
    }
}
