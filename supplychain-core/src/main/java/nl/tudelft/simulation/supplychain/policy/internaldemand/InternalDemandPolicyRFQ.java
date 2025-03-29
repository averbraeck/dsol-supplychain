package nl.tudelft.simulation.supplychain.policy.internaldemand;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.RequestForQuote;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.transport.TransportChoiceProvider;
import nl.tudelft.simulation.supplychain.transport.TransportOption;
import nl.tudelft.simulation.supplychain.transport.TransportOptionProvider;

/**
 * The InternalDemandPolicyRFQ is a simple implementation of the business logic to handle a request for new products through
 * sending out a number of RFQs to a list of preselected suppliers. When receiving the internal demand, it just creates a number
 * of RFQs based on a table that maps Products onto a list of Actors, and sends them out, all at the same time, after a given
 * time delay.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InternalDemandPolicyRFQ extends InternalDemandPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** a table to map the products onto a list of possible suppliers. */
    private Map<Product, HashSet<Actor>> suppliers = new LinkedHashMap<Product, HashSet<Actor>>();

    /** the provider of transport options betwween two locations. */
    private final TransportOptionProvider transportOptionProvider;

    /** the provider to choose between transport options. */
    private final TransportChoiceProvider transportChoiceProvider;

    /** the maximum time after which the RFQ will stop collecting quotes. */
    private final Duration cutoffDuration;

    /**
     * Constructs a new InternalDemandPolicyRFQ.
     * @param owner Role; the owner of the internal demand
     * @param transportOptionProvider TransportOptionProvider; the provider of transport options betwween two locations
     * @param transportChoiceProvider TransportChoiceProvider; the provider to choose between transport options
     * @param handlingTime DistContinuousDuration; the distribution of the time to react on the YP answer
     * @param cutoffDuration Duration; the maximum time after which the RFQ will stop collecting quotes
     * @param stock the stock for being able to change the ordered amount
     */
    public InternalDemandPolicyRFQ(final Role owner, final TransportOptionProvider transportOptionProvider,
            final TransportChoiceProvider transportChoiceProvider, final DistContinuousDuration handlingTime,
            final Duration cutoffDuration, final Inventory stock)
    {
        super("InternalDemandPolicyRFQ", owner, handlingTime, stock);
        Throw.whenNull(transportOptionProvider, "transportOptionProvider cannot be null");
        Throw.whenNull(transportChoiceProvider, "transportChoiceProvider cannot be null");
        Throw.whenNull(cutoffDuration, "cutoffDuration cannot be null");
        this.transportOptionProvider = transportOptionProvider;
        this.transportChoiceProvider = transportChoiceProvider;
        this.cutoffDuration = cutoffDuration;
    }

    /**
     * Add a supplier to send an RFQ to for a certain product.
     * @param product Product; the product with a set of suppliers.
     * @param supplier a supplier for that product.
     */
    public void addSupplier(final Product product, final Actor supplier)
    {
        HashSet<Actor> supplierSet = this.suppliers.get(product);
        if (supplierSet == null)
        {
            supplierSet = new LinkedHashSet<Actor>();
            this.suppliers.put(product, supplierSet);
        }
        supplierSet.add(supplier);
    }

    /**
     * Remove a supplier to send an RFQ to for a certain product.
     * @param product Product; the product.
     * @param supplier the supplier for that product to be removed.
     */
    public void removeSupplier(final Product product, final Actor supplier)
    {
        HashSet<Actor> supplierSet = this.suppliers.get(product);
        if (supplierSet != null)
        {
            supplierSet.remove(supplier);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final InternalDemand internalDemand)
    {
        if (!isValidMessage(internalDemand))
        {
            Logger.warn("handleContent",
                    "InternalDemand " + internalDemand.toString() + " for actor " + getRole() + " not considered valid.");
            return false;
        }
        // resolve the suplier
        Set<Actor> supplierSet = this.suppliers.get(internalDemand.getProduct());
        if (supplierSet == null)
        {
            Logger.warn("handleContent", "InternalDemand for actor " + getRole() + " contains product "
                    + internalDemand.getProduct().toString() + " without any suppliers.");
            return false;
        }
        // create an RFQ for each of the suppliers
        if (super.inventory != null)
        {
            super.inventory.changeOrderedAmount(internalDemand.getProduct(), internalDemand.getAmount());
        }
        Duration delay = this.handlingTime.draw();
        for (Actor supplier : supplierSet)
        {
            Set<TransportOption> transportOptions = this.transportOptionProvider.provideTransportOptions(supplier, getActor());
            TransportOption transportOption =
                    this.transportChoiceProvider.chooseTransportOptions(transportOptions, internalDemand.getProduct().getSku());
            RequestForQuote rfq =
                    new RequestForQuote(getActor(), supplier, internalDemand, transportOption, this.cutoffDuration);
            sendMessage(rfq, delay);
        }
        return true;
    }
}
