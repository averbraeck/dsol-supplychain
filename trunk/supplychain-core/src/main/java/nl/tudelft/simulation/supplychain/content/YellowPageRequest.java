package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The YellowPageRequest is a request to a YellowPageActor to provide a list, based on some contraints, of actors who could
 * provide a certain service or sell a certain product. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class YellowPageRequest extends Content
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** maximum distance to use in the search */
    private double maximumDistance;

    /** maximum number to return */
    private int maximumNumber;

    /** product to look for */
    private Product product;

    /**
     * Construct a YellowPageRequest with a maximum distance.
     * @param sender the sender of the yellow page request
     * @param receiver the receiver of the yellow page request
     * @param internalDemandID the internal demand that triggered the yellow page process
     * @param product the product we are interested in
     * @param maximumDistance the maximum distance around the 'sender' to search for suppliers
     */
    public YellowPageRequest(final SupplyChainActor sender, final SupplyChainActor receiver,
            final Serializable internalDemandID, final Product product, final double maximumDistance)
    {
        this(sender, receiver, internalDemandID, product, maximumDistance, Integer.MAX_VALUE);
    }

    /**
     * Construct a YellowPageRequest with a maximum number of answers. The nearest 'maximumNumber' suppliers are returned.
     * @param sender the sender of the yellow page request
     * @param receiver the receiver of the yellow page request
     * @param internalDemandID the internal demand that triggered the yellow page process
     * @param product the product we are interested in
     * @param maximumNumber the maximum number of supplier to return
     */
    public YellowPageRequest(final SupplyChainActor sender, final SupplyChainActor receiver,
            final Serializable internalDemandID, final Product product, final int maximumNumber)
    {
        this(sender, receiver, internalDemandID, product, Double.MAX_VALUE, maximumNumber);
    }

    /**
     * Construct a YellowPageRequest without constraints.
     * @param sender the sender of the yellow page request
     * @param receiver the receiver of the yellow page request
     * @param internalDemandID the internal demand that triggered the yellow page process
     * @param product the product we are interested in
     */
    public YellowPageRequest(final SupplyChainActor sender, final SupplyChainActor receiver,
            final Serializable internalDemandID, final Product product)
    {
        this(sender, receiver, internalDemandID, product, Double.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Construct a YellowPageRequest with a maximum number of answers and a geographic range to search in. Only nearest
     * 'maximumNumber' suppliers within the given range are returned.
     * @param sender the sender of the yellow page request
     * @param receiver the receiver of the yellow page request
     * @param internalDemandID the internal demand that triggered the yellow page process
     * @param product the product we are interested in
     * @param maximumDistance the maximum distance around the 'sender' to search for suppliers
     * @param maximumNumber the maximum number of supplier to return
     */
    public YellowPageRequest(final SupplyChainActor sender, final SupplyChainActor receiver,
            final Serializable internalDemandID, final Product product, final double maximumDistance, final int maximumNumber)
    {
        super(sender, receiver, internalDemandID);
        this.maximumDistance = maximumDistance;
        this.maximumNumber = maximumNumber;
        this.product = product;
    }

    /**
     * Method getMaximumDistance.
     * @return the maximum distance to look for suppliers.
     */
    public double getMaximumDistance()
    {
        return this.maximumDistance;
    }

    /**
     * Returns the maximumNumber.
     * @return the maximum number of suppliers to return.
     */
    public int getMaximumNumber()
    {
        return this.maximumNumber;
    }

    /**
     * Returns the product.
     * @return the product we are looking for.
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return super.toString() + ", for " + this.getProduct().getName();
    }
}
