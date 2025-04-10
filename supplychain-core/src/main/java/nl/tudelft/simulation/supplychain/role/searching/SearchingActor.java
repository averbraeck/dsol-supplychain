package nl.tudelft.simulation.supplychain.role.searching;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * SearchingActor is an interface to indicate that an Actor has a SearchingRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SearchingActor extends Actor
{
    /**
     * Return the SearchingRole for this actor.
     * @return the SearchingRole for this actor
     */
    SearchingRole getSearchingRole();

    /**
     * Set the SearchingRole for this actor.
     * @param searchingRole the new SearchingRole for this actor
     */
    void setSearchingRole(SearchingRole searchingRole);

}
