package nl.tudelft.simulation.messaging.comparators;

import java.util.Comparator;

/**
 * A priority-based comparator to be able to compare messages based on fields. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class PriorityCompare extends FieldComparator
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the fieldName used to check priority */
    public static final String FIELDNAME = "priority"; // Message.priority

    /**
     * constructs a new PriorityCompare on the id-field of a message
     */
    public PriorityCompare()
    {
        super(PriorityCompare.FIELDNAME);
    }

    /**
     * constructs a new PriorityCompare
     * @param next the next comparator to use
     */
    public PriorityCompare(final Comparator<Object> next)
    {
        super(PriorityCompare.FIELDNAME, next);
    }
}
