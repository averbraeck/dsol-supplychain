package nl.tudelft.simulation.supplychain.role.purchasing;

/**
 * The purchasing role with searchs is a role that organizes the purchasing based on a SearchRequest, and continues from there.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class PurchasingRoleSearch extends PurchasingRoleRFQ
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Construct a new PurchasingRole for Demand - SearchAnswer - Quote - Confirmation - Shipment - Invoice.
     * @param owner the actor to which this role belongs
     */
    public PurchasingRoleSearch(final PurchasingActor owner)
    {
        super(owner);
    }

    @Override
    public String getId()
    {
        return getActor().getId() + "-BUYING(search)";
    }

}
