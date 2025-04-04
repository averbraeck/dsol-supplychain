package nl.tudelft.simulation.supplychain.handler.search;

import java.util.List;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.SearchAnswer;
import nl.tudelft.simulation.supplychain.content.SearchRequest;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.transporting.TransportChoiceProvider;
import nl.tudelft.simulation.supplychain.transporting.TransportOption;
import nl.tudelft.simulation.supplychain.transporting.TransportOptionProvider;

/**
 * The SearchAnswerHandler implements the business logic for a buyer who receives a SearchAnswer from a search supply chain
 * actor. The most simple version that is implemented here, sends out RFQs to <b>all </b> the actors that are reported back
 * inside the SearchAnswer.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SearchAnswerHandler extends ContentHandler<SearchAnswer, PurchasingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 120221203;

    /** the provider of transport options betwween two locations. */
    private final TransportOptionProvider transportOptionProvider;

    /** the provider to choose between transport options. */
    private final TransportChoiceProvider transportChoiceProvider;

    /** the handling time of the handler in simulation time units. */
    private DistContinuousDuration handlingTime;

    /** the maximum time after which the RFQ will stop collecting quotes. */
    private final Duration cutoffDuration;

    /**
     * Constructs a new SearchAnswerHandler.
     * @param owner the owner of the handler
     * @param transportOptionProvider the provider of transport options betwween two locations
     * @param transportChoiceProvider the provider to choose between transport options
     * @param handlingTime the distribution of the time to react on the Search answer
     * @param cutoffDuration the maximum time after which the RFQ will stop collecting quotes
     */
    public SearchAnswerHandler(final PurchasingRole owner, final TransportOptionProvider transportOptionProvider,
            final TransportChoiceProvider transportChoiceProvider, final DistContinuousDuration handlingTime,
            final Duration cutoffDuration)
    {
        super("SearchAnswerHandler", owner, SearchAnswer.class);
        Throw.whenNull(handlingTime, "handlingTime cannot be null");
        Throw.whenNull(transportOptionProvider, "transportOptionProvider cannot be null");
        Throw.whenNull(transportChoiceProvider, "transportChoiceProvider cannot be null");
        Throw.whenNull(cutoffDuration, "cutoffDuration cannot be null");
        this.transportOptionProvider = transportOptionProvider;
        this.transportChoiceProvider = transportChoiceProvider;
        this.handlingTime = handlingTime;
        this.cutoffDuration = cutoffDuration;
    }

    @Override
    public boolean handleContent(final SearchAnswer searchAnswer)
    {
        if (!isValidContent(searchAnswer))
        {
            return false;
        }
        ContentStoreInterface messageStore = getActor().getContentStore();
        SearchRequest searchRequest = searchAnswer.searchRequest();
        List<Demand> demandList = messageStore.getContentList(searchRequest.groupingId(), Demand.class);
        if (demandList.size() == 0) // we send it to ourselves, so it is 2x in the content store
        {
            Logger.warn("SearchAnswerHandler - Actor '{}' could not find groupingId '{}' for SearchAnswer '{}'",
                    getActor().getName(), searchRequest.groupingId(), searchAnswer.toString());
            return false;
        }
        Demand demand = demandList.get(0);
        List<Actor> potentialSuppliers = searchAnswer.actorList();
        Duration delay = this.handlingTime.draw();
        for (Actor supplier : potentialSuppliers)
        {
            Set<TransportOption> transportOptions = this.transportOptionProvider.provideTransportOptions(supplier, getActor());
            TransportOption transportOption =
                    this.transportChoiceProvider.chooseTransportOptions(transportOptions, searchRequest.product().getSku());
            RequestForQuote rfq = new RequestForQuote(getRole().getActor(), (SellingActor) supplier, demand, transportOption,
                    getSimulatorTime().plus(this.cutoffDuration));
            sendContent(rfq, delay);
        }
        return true;
    }

}
