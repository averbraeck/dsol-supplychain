package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The YellowPageAnswer is the answer from a Yellow Page actor to a YellowPageRequest. It contains a list of actors that might
 * sell a product or service that was asked for in the YellowPageRequest. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class YellowPageAnswer extends Content
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the suppliers of the requested product */
    private SupplyChainActor[] suppliers = null;

    /** the request that triggered this yellow page anawer */
    private YellowPageRequest ypRequest = null;

    /**
     * Constructs a new YellowPageAnswer.
     * @param sender the sender of the yellow page answer
     * @param receiver the receiver of the yellow page answer
     * @param internalDemandID the internal demand that triggered the yellow page process
     * @param suppliers the suppliers of the requested product
     * @param ypRequest the request that triggered this YP answer
     */
    public YellowPageAnswer(final SupplyChainActor sender, final SupplyChainActor receiver, final Serializable internalDemandID,
            final SupplyChainActor[] suppliers, final YellowPageRequest ypRequest)
    {
        super(sender, receiver, internalDemandID);
        this.suppliers = suppliers;
        this.ypRequest = ypRequest;
    }

    /**
     * @return Returns the suppliers.
     */
    public SupplyChainActor[] getSuppliers()
    {
        return this.suppliers;
    }

    /**
     * @return Returns the request for which this is the answer.
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
    public String toString()
    {
        return super.toString() + ", for " + this.getYellowPageRequest().toString();
    }
}
