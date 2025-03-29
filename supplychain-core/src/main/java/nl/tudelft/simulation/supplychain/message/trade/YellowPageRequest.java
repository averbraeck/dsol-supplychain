package nl.tudelft.simulation.supplychain.message.trade;

import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.yellowpage.YellowPageActor;

/**
 * The YellowPageRequest is a request to a YellowPageActor to provide a list, based on some contraints, of actors who could
 * provide a certain service or sell a certain product.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPageRequest extends TradeMessage
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** maximum distance to use in the search. */
    private Length maximumDistance;

    /** maximum number to return. */
    private int maximumNumber;

    /** product to look for. */
    private Product product;

    /**
     * Construct a YellowPageRequest with a maximum distance.
     * @param sender the sender of the yellow page request
     * @param receiver the receiver of the yellow page request
     * @param internalDemandId the internal demand that triggered the yellow page process
     * @param product the product we are interested in
     * @param maximumDistance the maximum distance around the 'sender' to search for suppliers
     */
    public YellowPageRequest(final Actor sender, final YellowPageActor receiver, final long internalDemandId,
            final Product product, final Length maximumDistance)
    {
        this(sender, receiver, internalDemandId, product, maximumDistance, Integer.MAX_VALUE);
    }

    /**
     * Construct a YellowPageRequest with a maximum number of answers. The nearest 'maximumNumber' suppliers are returned.
     * @param sender the sender of the yellow page request
     * @param receiver the receiver of the yellow page request
     * @param internalDemandId the internal demand that triggered the yellow page process
     * @param product the product we are interested in
     * @param maximumNumber the maximum number of supplier to return
     */
    public YellowPageRequest(final Actor sender, final YellowPageActor receiver, final long internalDemandId,
            final Product product, final int maximumNumber)
    {
        this(sender, receiver, internalDemandId, product, new Length(Double.MAX_VALUE, LengthUnit.SI), maximumNumber);
    }

    /**
     * Construct a YellowPageRequest without constraints.
     * @param sender the sender of the yellow page request
     * @param receiver the receiver of the yellow page request
     * @param internalDemandId the internal demand that triggered the yellow page process
     * @param product the product we are interested in
     */
    public YellowPageRequest(final Actor sender, final YellowPageActor receiver, final long internalDemandId,
            final Product product)
    {
        this(sender, receiver, internalDemandId, product, new Length(Double.MAX_VALUE, LengthUnit.SI), Integer.MAX_VALUE);
    }

    /**
     * Construct a YellowPageRequest with a maximum number of answers and a geographic range to search in. Only nearest
     * 'maximumNumber' suppliers within the given range are returned.
     * @param sender the sender of the yellow page request
     * @param receiver the receiver of the yellow page request
     * @param internalDemandId the internal demand that triggered the yellow page process
     * @param product the product we are interested in
     * @param maximumDistance the maximum distance around the 'sender' to search for suppliers
     * @param maximumNumber the maximum number of supplier to return
     */
    public YellowPageRequest(final Actor sender, final YellowPageActor receiver, final long internalDemandId,
            final Product product, final Length maximumDistance, final int maximumNumber)
    {
        super(sender, receiver, internalDemandId);
        this.maximumDistance = maximumDistance;
        this.maximumNumber = maximumNumber;
        this.product = product;
    }

    /**
     * Method getMaximumDistance.
     * @return the maximum distance to look for suppliers.
     */
    public Length getMaximumDistance()
    {
        return this.maximumDistance;
    }

    /**
     * Return the maximumNumber.
     * @return the maximum number of suppliers to return.
     */
    public int getMaximumNumber()
    {
        return this.maximumNumber;
    }

    @Override
    public Product getProduct()
    {
        return this.product;
    }

    @Override
    public YellowPageActor getReceiver()
    {
        return (YellowPageActor) super.getReceiver();
    }

    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getProduct().getName();
    }
}
