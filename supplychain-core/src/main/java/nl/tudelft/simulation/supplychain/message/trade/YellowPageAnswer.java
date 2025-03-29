package nl.tudelft.simulation.supplychain.message.trade;

import java.util.List;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.yellowpage.YellowPageActor;

/**
 * The YellowPageAnswer is the answer from a Yellow Page actor to a YellowPageRequest. It contains a list of actors that might
 * sell a product or service that was asked for in the YellowPageRequest.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPageAnswer extends TradeMessage
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the suppliers of the requested product. */
    private final List<Actor> suppliers;

    /** the request that triggered this yellow page anawer. */
    private final YellowPageRequest ypRequest;

    /**
     * Constructs a new YellowPageAnswer.
     * @param sender YellowPageActor; the sender of the yellow page answer
     * @param receiver Actor; the receiver of the yellow page answer
     * @param internalDemandId the internal demand that triggered the yellow page process
     * @param suppliers the suppliers of the requested product
     * @param ypRequest the request that triggered this YP answer
     */
    public YellowPageAnswer(final YellowPageActor sender, final Actor receiver, final long internalDemandId,
            final List<Actor> suppliers, final YellowPageRequest ypRequest)
    {
        super(sender, receiver, internalDemandId);
        this.suppliers = suppliers;
        this.ypRequest = ypRequest;
    }

    /**
     * @return the suppliers.
     */
    public List<Actor> getSuppliers()
    {
        return this.suppliers;
    }

    /**
     * @return the request for which this is the answer.
     */
    public YellowPageRequest getYellowPageRequest()
    {
        return this.ypRequest;
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.ypRequest.getProduct();
    }

    /** {@inheritDoc} */
    @Override
    public YellowPageActor getSender()
    {
        return (YellowPageActor) super.getSender();
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getYellowPageRequest().toString();
    }
}
