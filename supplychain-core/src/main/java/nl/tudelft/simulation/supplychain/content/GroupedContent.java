package nl.tudelft.simulation.supplychain.content;

/**
 * Content with a groupingId.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface GroupedContent extends Content
{
    /**
     * Return the grouping id of the content.
     * @return the grouping id of the content
     */
    long groupingId();

}
