package nl.tudelft.simulation.messaging.comparators;

import java.util.Comparator;

/**
 * A FiFo sorting mechanism for messages. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class FiFo extends FieldComparator
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the fieldName used to check order */
    public static final String FIELDNAME = "id"; // Message.id is used

    /**
     * constructs a new FiFo on the id-field of a message
     */
    public FiFo()
    {
        super(FiFo.FIELDNAME);
    }

    /**
     * constructs a new FiFo
     * @param next the next comparator to use
     */
    public FiFo(final Comparator<Object> next)
    {
        super(FiFo.FIELDNAME, next);
    }
}
