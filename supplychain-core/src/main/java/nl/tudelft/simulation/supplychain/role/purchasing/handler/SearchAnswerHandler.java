package nl.tudelft.simulation.supplychain.role.purchasing.handler;

import java.util.List;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.SearchAnswer;
import nl.tudelft.simulation.supplychain.content.SearchRequest;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingActor;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRole;
import nl.tudelft.simulation.supplychain.role.purchasing.PurchasingRoleRFQ;
import nl.tudelft.simulation.supplychain.role.selling.SellingActor;
import nl.tudelft.simulation.supplychain.role.transporting.TransportPreference;

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

    /** the maximum time after which the RFQ will stop collecting quotes. */
    private final Duration cutoffDuration;

    /** the generic transport preference for this actor. */
    private final TransportPreference transportPreference;

    /**
     * Constructs a new SearchAnswerHandler.
     * @param owner the owner of the handler
     * @param cutoffDuration the maximum time after which the RFQ will stop collecting quotes
     * @param transportPreference the generic transport preference for this actor
     */
    public SearchAnswerHandler(final PurchasingActor owner, final Duration cutoffDuration,
            final TransportPreference transportPreference)
    {
        super("SearchAnswerHandler", owner.getPurchasingRole(), SearchAnswer.class);
        Throw.whenNull(cutoffDuration, "cutoffDuration cannot be null");
        Throw.whenNull(transportPreference, "transportPreference cannot be null");
        this.cutoffDuration = cutoffDuration;
        this.transportPreference = transportPreference;
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
        Duration delay = getHandlingTime().draw();
        for (Actor supplier : potentialSuppliers)
        {
            RequestForQuote rfq = new RequestForQuote(getRole().getActor(), (SellingActor) supplier, demand,
                    this.transportPreference, getSimulatorTime().plus(this.cutoffDuration));
            getRole().addRequestForQuoteToMap(rfq);
            sendContent(rfq, delay);
        }
        return true;
    }

    @Override
    public PurchasingRoleRFQ getRole()
    {
        return (PurchasingRoleRFQ) super.getRole();
    }

}
