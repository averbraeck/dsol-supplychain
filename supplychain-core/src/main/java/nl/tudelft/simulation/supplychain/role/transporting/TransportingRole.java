package nl.tudelft.simulation.supplychain.role.transporting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.TransportOrder;
import nl.tudelft.simulation.supplychain.content.TransportQuote;
import nl.tudelft.simulation.supplychain.content.TransportQuoteRequest;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
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

    /** the estimated time to load SKUs at the origin (including typical waiting times). */
    private Map<Sku, Duration> estimatedLoadingTimes = new LinkedHashMap<>();

    /** the estimated time to unload SKUs at the destination (including typical waiting times). */
    private Map<Sku, Duration> estimatedUnloadingTimes = new LinkedHashMap<>();

    /** the estimated costs for loading and storing SKUs at the origin location. */
    private Map<Sku, Money> estimatedLoadingCosts = new LinkedHashMap<>();

    /** the estimated costs for unloading and storing SKUs at the destination location. */
    private Map<Sku, Money> estimatedUnloadingCosts = new LinkedHashMap<>();

    /** the estimated costs to transport an SKU per km. */
    private Map<Sku, Money> estimatedTransportCostsPerKm = new LinkedHashMap<>();

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
                    new TransportOptionStep(transportOption.getId(), from, to, TransportMode.TRUCK, this);
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
                            transferLocationFrom.namedLocation(), TransportMode.TRUCK, this);
                    transportOption.addTransportStep(transportOptionStep1);
                    double profitMargin = getActor().getDirectingRoleTransporting().getProfitMargin(TransportMode.TRUCK);
                    Money price =
                            transportOptionStep1.getEstimatedTransportCost(sku).multiplyBy(tqr.amount() * (1.0 + profitMargin));

                    // long distance transport between transfer points
                    var transportOptionStep2 = new TransportOptionStep(transportOption.getId(),
                            transferLocationFrom.namedLocation(), transferLocationTo.namedLocation(), mode, this);
                    transportOption.addTransportStep(transportOptionStep2);
                    profitMargin = getActor().getDirectingRoleTransporting().getProfitMargin(mode);
                    price = price.plus(transportOptionStep2.getEstimatedTransportCost(sku)
                            .multiplyBy(tqr.amount() * (1.0 + profitMargin)));

                    // truck from transfer point to buyer
                    var transportOptionStep3 = new TransportOptionStep(transportOption.getId(),
                            transferLocationTo.namedLocation(), to, TransportMode.TRUCK, this);
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

    /**
     * Return the estimated time to load goods at the origin (including typical waiting times) for a given SKU.
     * @param sku the SKU to find the loading time for
     * @return the estimated time to load goods at the origin (including typical waiting times), or null when there is no stored
     *         loading time for the provided SKU
     */
    public Duration getEstimatedLoadingTime(final Sku sku)
    {
        return Objects.requireNonNullElse(this.estimatedLoadingTimes.get(sku), Duration.ZERO);
    }

    /**
     * Return the estimated time to unload goods at the destination (including typical waiting times) for a given SKU.
     * @param sku the SKU to find the unloading time for
     * @return the estimated time to unload goods at the destination (including typical waiting times), or null when there is no
     *         stored unloading time for the provided SKU
     */
    public Duration getEstimatedUnloadingTime(final Sku sku)
    {
        return Objects.requireNonNullElse(this.estimatedUnloadingTimes.get(sku), Duration.ZERO);
    }

    /**
     * Return the estimated costs for loading and storing the goods at the origin location for a given SKU.
     * @param sku the SKU to find the loading cost for
     * @return the estimated costs for loading and storing the goods at the origin location, or null when there is no stored
     *         loading cost for the provided SKU
     */
    public Money getEstimatedLoadingCost(final Sku sku)
    {
        return Objects.requireNonNullElse(this.estimatedLoadingCosts.get(sku), new Money(0.0, MoneyUnit.USD));
    }

    /**
     * Return the estimated costs for loading and storing the goods at the destination location for a given SKU.
     * @param sku the SKU to find the unloading cost for
     * @return the estimated costs for unloading and storing the goods at the destination location, or null when there is no
     *         stored unloading cost for the provided SKU
     */
    public Money getEstimatedUnloadingCost(final Sku sku)
    {
        return Objects.requireNonNullElse(this.estimatedUnloadingCosts.get(sku), new Money(0.0, MoneyUnit.USD));
    }

    /**
     * Return the estimated transport cost for the SKU per km, for the TransportStep's transport mode.
     * @param sku the SKU to find the transport cost for
     * @return the estimated estimated transport cost for the SKU per km, for the TransportStep's transport mode, or null when
     *         there is no stored cost for the provided SKU
     */
    public Money getEstimatedTransportCostPerKm(final Sku sku)
    {
        return Objects.requireNonNullElse(this.estimatedTransportCostsPerKm.get(sku), new Money(2.0, MoneyUnit.USD));
    }

    /**
     * Set a new estimated time to load goods at the origin (including typical waiting times).
     * @param sku the SKU to find the loading duration for
     * @param estimatedLoadingTime new estimated time to load goods at the origin (including typical waiting times)
     */
    public void setEstimatedLoadingTime(final Sku sku, final Duration estimatedLoadingTime)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(estimatedLoadingTime, "estimatedLoadingTime cannot be null");
        this.estimatedLoadingTimes.put(sku, estimatedLoadingTime);
    }

    /**
     * Set a new estimated time to unload goods at the destination (including typical waiting times).
     * @param sku the SKU to set the unloading duration for
     * @param estimatedUnloadingTime new estimated time to unload goods at the destination (including typical waiting times)
     */
    public void setEstimatedUnloadingTime(final Sku sku, final Duration estimatedUnloadingTime)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(estimatedUnloadingTime, "estimatedUnloadingTime cannot be null");
        this.estimatedUnloadingTimes.put(sku, estimatedUnloadingTime);
    }

    /**
     * Set a new cost estimate for loading and storing the goods at the origin location.
     * @param sku the SKU to set the loading cost for
     * @param estimatedLoadingCost new cost estimate for loading and storing the goods at the origin location
     */
    public void setEstimatedLoadingCost(final Sku sku, final Money estimatedLoadingCost)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(estimatedLoadingCost, "estimatedLoadingCost cannot be null");
        this.estimatedLoadingCosts.put(sku, estimatedLoadingCost);
    }

    /**
     * Set a new cost estimate for unloading and storing the goods at the destination location.
     * @param sku the SKU to set the unloading cost for
     * @param estimatedUnloadingCost new cost estimate for unloading and storing the goods at the destination location
     */
    public void setEstimatedUnloadingCost(final Sku sku, final Money estimatedUnloadingCost)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(estimatedUnloadingCost, "estimatedUnloadingCost cannot be null");
        this.estimatedUnloadingCosts.put(sku, estimatedUnloadingCost);
    }

    /**
     * Set a new estimated transport cost for the SKU per km, for the TransportStep's transport mode.
     * @param sku the SKU to find the transport cost for
     * @param estimatedTransportCostPerKm the estimated estimated transport cost for the SKU per km, for the TransportStep's
     *            transport mode
     */
    public void setEstimatedTransportCostPerKm(final Sku sku, final Money estimatedTransportCostPerKm)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(estimatedTransportCostPerKm, "estimatedTransportCostPerKm cannot be null");
        this.estimatedTransportCostsPerKm.put(sku, estimatedTransportCostPerKm);
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
