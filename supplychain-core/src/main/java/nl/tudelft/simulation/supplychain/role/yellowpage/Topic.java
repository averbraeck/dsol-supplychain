package nl.tudelft.simulation.supplychain.role.yellowpage;

import java.io.Serializable;

/**
 * A Topic to be used in storing and finding 'topics' in the YellowPage. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Topic implements Serializable
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the parent topic. */
    private Topic parent = null;

    /** the description of the topic. */
    private final String description;

    /** a quick-and-dirty default topic for simple models. */
    public static final Topic DEFAULT = new Topic("Default");

    /**
     * constructs a new Topic.
     * @param description the description of the topic
     */
    public Topic(final String description)
    {
        this.description = description;
    }

    /**
     * returns the parent of this topic.
     * @return Topic the parent
     */
    public Topic getParent()
    {
        return this.parent;
    }

    /**
     * determines whether cat1 is a specialization of cat2.
     * @param cat1 the first topic
     * @param cat2 the second topic
     * @return boolean success
     */
    public static boolean specializationOf(final Topic cat1, final Topic cat2)
    {
        if (cat1 == null || cat2 == null)
        {
            return false;
        }
        if (cat1.equals(cat2))
        {
            return true;
        }
        return Topic.specializationOf(cat1.getParent(), cat2);
    }

    @Override
    public String toString()
    {
        return this.description;
    }
}
