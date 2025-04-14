package nl.tudelft.simulation.supplychain.role.transporting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.TransportOrder;
import nl.tudelft.simulation.supplychain.content.TransportQuote;
import nl.tudelft.simulation.supplychain.content.TransportQuoteRequest;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.product.Sku;

/**
 * The Transporting role takes care of making transport quotes, doing the actual transporting, and sending a transport invoice.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TransportingRole extends Role<TransportingRole>
{
    /** */
    private static final long serialVersionUID = 20250406L;

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers =
            Set.of(TransportQuoteRequest.class, TransportOrder.class);

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<TransportingRole>>> necessaryAutonomousProcesses = Set.of();
    
    /**
     * Create a new Search role.
     * @param owner the actor that owns the Search role
     */
    public TransportingRole(final TransportingActor owner)
    {
        super("transporting", owner, new ContentReceiverDirect());
    }

    /**
     * Make a list of transport quotes based on the geographic locations of the sender and receiver, and on the capabilities of
     * this transporting organization.
     * @param tqr The transport quote request containing the details about the goods and locations
     * @return a list of possible transport options.
     */
    public List<TransportQuote> makeTransportQuotes(final TransportQuoteRequest tqr)
    {
        var product = tqr.product();
        Sku sku = product.getSku();
        var tql = new ArrayList<TransportQuote>();
        var from = tqr.rfq().receiver();
        var to = tqr.rfq().sender();

        // if the transporter does not do business on either landmass, no quotes.
        if (!getActor().getDirectingRoleTransporting().isTransportOnLandmass(from.getGeography().landmass())
                || !getActor().getDirectingRoleTransporting().isTransportOnLandmass(to.getGeography().landmass()))
        {
            return tql;
        }

        // if we are on the same landmass, direct trucking is an option (if we do direct trucking)
        var continental = from.getGeography().landmass().equals(to.getGeography().landmass());
        if (continental && !Double.isNaN(getActor().getDirectingRoleTransporting().getProfitMargin(TransportMode.TRUCK)))
        {
            var transportOption = new TransportOption(getActor().getId() + "-transport for " + tqr.groupingId() + " by truck",
                    getActor(), from, to);
            var transportOptionStep =
                    new TransportOptionStep(transportOption.getId(), from, to, TransportMode.TRUCK, getSimulator());
            transportOption.addTransportStep(transportOptionStep);
            double profitMargin = getActor().getDirectingRoleTransporting().getProfitMargin(TransportMode.TRUCK);
            Money price = transportOptionStep.getEstimatedTransportCost(sku).multiplyBy(tqr.amount() * (1.0 + profitMargin));
            var transportQuote = new TransportQuote(tqr, transportOption, price);
            tql.add(transportQuote);
        }

        // add options for matching pairs of transport modes between seller and purchaser
        // note that there can be multiple transfer locations using the same mode per actor (e.g., two closeby ports)
        for (var transferLocationFrom : from.getGeography().transferLocations())
        {
            TransportMode mode = transferLocationFrom.mode();
            for (var transferLocationTo : to.getGeography().transferLocations())
            {
                if (mode.equals(transferLocationTo.mode())
                        && ((mode.isContinental() && continental) || (mode.isIntercontinental() && !continental)))
                {
                    var transportOption = new TransportOption(
                            getActor().getId() + "-transport for " + tqr.groupingId() + " by " + mode.getId(), getActor(), from,
                            to);

                    // truck from seller to transfer point
                    var transportOptionStep1 = new TransportOptionStep(transportOption.getId(), from,
                            transferLocationFrom.namedLocation(), TransportMode.TRUCK, getSimulator());
                    transportOption.addTransportStep(transportOptionStep1);
                    double profitMargin = getActor().getDirectingRoleTransporting().getProfitMargin(TransportMode.TRUCK);
                    Money price =
                            transportOptionStep1.getEstimatedTransportCost(sku).multiplyBy(tqr.amount() * (1.0 + profitMargin));

                    // long distance transport between transfer points
                    var transportOptionStep2 = new TransportOptionStep(transportOption.getId(),
                            transferLocationFrom.namedLocation(), transferLocationTo.namedLocation(), mode, getSimulator());
                    transportOption.addTransportStep(transportOptionStep2);
                    profitMargin = getActor().getDirectingRoleTransporting().getProfitMargin(mode);
                    price = price.plus(transportOptionStep2.getEstimatedTransportCost(sku)
                            .multiplyBy(tqr.amount() * (1.0 + profitMargin)));

                    // truck from transfer point to buyer
                    var transportOptionStep3 = new TransportOptionStep(transportOption.getId(),
                            transferLocationTo.namedLocation(), to, TransportMode.TRUCK, getSimulator());
                    transportOption.addTransportStep(transportOptionStep3);
                    profitMargin = getActor().getDirectingRoleTransporting().getProfitMargin(TransportMode.TRUCK);
                    price = price.plus(transportOptionStep3.getEstimatedTransportCost(sku)
                            .multiplyBy(tqr.amount() * (1.0 + profitMargin)));

                    var transportQuote = new TransportQuote(tqr, transportOption, price);
                    tql.add(transportQuote);
                }
            }
        }
        return tql;
    }

    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }

    @Override
    protected Set<Class<? extends AutonomousProcess<TransportingRole>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }

    @Override
    public TransportingActor getActor()
    {
        return (TransportingActor) super.getActor();
    }

}
