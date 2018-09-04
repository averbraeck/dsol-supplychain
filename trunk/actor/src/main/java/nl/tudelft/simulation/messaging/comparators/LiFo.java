package nl.tudelft.simulation.messaging.comparators;

import java.util.Comparator;

/**
 * A LiFo sorting mechanism for messages. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class LiFo extends FiFo
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * constructs a new LiFo on the id-field of a message
     */
    public LiFo()
    {
        super();
        super.negative = true;
    }

    /**
     * constructs a new LiFo
     * @param next the next comparator to use
     */
    public LiFo(final Comparator<Object> next)
    {
        super(next);
        super.negative = true;
    }
}
