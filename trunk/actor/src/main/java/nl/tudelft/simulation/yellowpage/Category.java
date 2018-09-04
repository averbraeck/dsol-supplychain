package nl.tudelft.simulation.yellowpage;

/**
 * A Category to be used in storing and finding 'topics' in the YellowPage. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Category
{
    /** the serial version uid */
    public static final long serialVersionUID = 12L;

    /** the parent category */
    private Category parent = null;

    /** the description of the category */
    private String description = null;

    /**
     * constructs a new Category
     * @param description the description of the category
     */
    public Category(final String description)
    {
        super();
        this.description = description;
    }

    /**
     * returns the parent of this category
     * @return Category the parent
     */
    public Category getParent()
    {
        return this.parent;
    }

    /**
     * determines whether cat1 is a specialization of cat2
     * @param cat1 the first category
     * @param cat2 the second category
     * @return boolean success
     */
    public static boolean specializationOf(final Category cat1, final Category cat2)
    {
        if (cat1 == null || cat2 == null)
        {
            return false;
        }
        if (cat1.equals(cat2))
        {
            return true;
        }
        return Category.specializationOf(cat1.getParent(), cat2);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return this.description;
    }
}
