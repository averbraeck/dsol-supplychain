package nl.tudelft.simulation.supplychain.role.purchasing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;

/**
 * The purchasing role based on a RFQ is a role that organizes the purchasing based on a RequestForQuote that is sent to a fixed
 * set of possible suppliers, and continues from there.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PurchasingRoleRFQ extends PurchasingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /** whether to discard negative quots or not. */
    private boolean discardNegativeQuotes = true;

    /** the map of rfq's that have sent for a groupingId. */
    private Map<Long, List<RequestForQuote>> rfqMap = new LinkedHashMap<>();

    /** the map of quotes that have come in for a groupingId. */
    private Map<Long, List<Quote>> quoteMap = new LinkedHashMap<>();

    /**
     * Constructs a new PurchasingRole for Demand - Quote - Confirmation - Shipment - Invoice.
     * @param owner the actor to which this role belongs
     */
    public PurchasingRoleRFQ(final PurchasingActor owner)
    {
        super(owner);
    }

    /**
     * Add an RFQ to the rfq/quote map to store received quotes.
     * @param rfq the RFQ to store
     */
    public void addRequestForQuoteToMap(final RequestForQuote rfq)
    {
        if (!this.rfqMap.containsKey(rfq.groupingId()))
        {
            this.rfqMap.put(rfq.groupingId(), new ArrayList<>());
            this.quoteMap.put(rfq.groupingId(), new ArrayList<>());
        }
        this.rfqMap.get(rfq.groupingId()).add(rfq);
    }

    /**
     * Add a Quote to the rceived quotes map.
     * @param quote the Quote to store
     */
    public void addQuoteToMap(final Quote quote)
    {
        // if the rfq is not (anymore) there, do not store the quote
        if (this.rfqMap.containsKey(quote.groupingId()))
        {
            this.quoteMap.get(quote.groupingId()).add(quote);
        }
    }

    /**
     * Retrieve the received quotes from the quote map.
     * @param groupingId the grouping id to look up
     * @return the list of quotes or an empty list if the grouping id could not be found
     */
    public List<Quote> getQuotesFromMap(final long groupingId)
    {
        return Objects.requireNonNullElse(this.quoteMap.get(groupingId), new ArrayList<>());
    }

    /**
     * Remove an RFQ from the received quotes map.
     * @param groupingId the overarching transaction id
     */
    public void removeRequestForQuoteFromMap(final long groupingId)
    {
        this.rfqMap.remove(groupingId);
        this.quoteMap.remove(groupingId);
    }

    /**
     * Return the number of sent RFQs belonging to this grouping id.
     * @param groupingId th grouping id to look up
     * @return the number of sent RFQs belonging to this grouping id
     */
    public int getNrSentRfqs(final long groupingId)
    {
        return Objects.requireNonNullElse(this.rfqMap.get(groupingId).size(), 0);
    }

    /**
     * Return the number of received quotes belonging to this grouping id.
     * @param groupingId th grouping id to look up
     * @return the number of received quotes belonging to this grouping id
     */
    public int getNrReceivedQuotes(final long groupingId)
    {
        return Objects.requireNonNullElse(this.quoteMap.get(groupingId).size(), 0);
    }

    @Override
    public String getId()
    {
        return getActor().getId() + "-BUYING(RFQ)";
    }

    /**
     * Return whether to discard negative quots or not.
     * @return whether to discard negative quots or not
     */
    public boolean isDiscardNegativeQuotes()
    {
        return this.discardNegativeQuotes;
    }

    /**
     * Set a new value for whether to discard negative quots or not.
     * @param discardNegativeQuotes whether to discard negative quots or not
     */
    public void setDiscardNegativeQuotes(final boolean discardNegativeQuotes)
    {
        this.discardNegativeQuotes = discardNegativeQuotes;
    }

}
