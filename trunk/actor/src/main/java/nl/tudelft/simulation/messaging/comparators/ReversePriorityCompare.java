package nl.tudelft.simulation.messaging.comparators;

import java.util.Comparator;

/**
 * A reverse priority-based comparator to be able to compare messages based on fields. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ReversePriorityCompare extends PriorityCompare
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * constructs a new ReversePriorityCompare
     */
    public ReversePriorityCompare()
    {
        super();
        super.setNegative(true);
    }

    /**
     * constructs a new ReversePriorityCompare
     * @param next the next comparator to use
     */
    public ReversePriorityCompare(final Comparator<Object> next)
    {
        super(next);
        super.setNegative(true);
    }
}
