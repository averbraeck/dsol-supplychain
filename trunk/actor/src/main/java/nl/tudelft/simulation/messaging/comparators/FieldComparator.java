package nl.tudelft.simulation.messaging.comparators;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Comparator;

import nl.tudelft.simulation.language.reflection.ClassUtil;
import nl.tudelft.simulation.messaging.Message;

/**
 * A FieldComparator <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class FieldComparator implements Comparator<Message>, Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the field to question */
    protected String fieldName = null;

    /** the next comparator in the chain */
    protected Comparator<Object> next = new NullComparator();

    /** normal of negative operation */
    protected boolean negative = false;

    /**
     * constructs a new FieldComparator
     * @param field the field to compare to
     */
    public FieldComparator(final String field)
    {
        super();
        this.fieldName = field;
    }

    /**
     * constructs a new FieldComparator
     * @param field the field to compare to
     * @param next the next comparator to use
     */
    public FieldComparator(final String field, final Comparator<Object> next)
    {
        this(field);
        this.next = next;
    }

    /**
     * Method compare
     * @param o1 object 1
     * @param o2 object 2
     * @return returns 1 or -1
     */
    @SuppressWarnings("unchecked")
    public int compare(final Message o1, final Message o2)
    {
        try
        {
            Field field = ClassUtil.resolveField(o1.getClass(), this.fieldName);
            field.setAccessible(true);

            Comparable<Object> comparable1 = (Comparable<Object>) field.get(o1);
            Comparable<Object> comparable2 = (Comparable<Object>) field.get(o2);

            int compare = comparable1.compareTo(comparable2);
            if (compare == 0)
            {
                return this.next.compare(o1, o2);
            }
            if (this.negative)
            {
                return -1 * compare;
            }
            return compare;
        }
        catch (Exception exception)
        {
            return 0;
        }
    }

    /**
     * INNER CLASS defining a default NullComparator that does not do any real comparisons <br>
     * <br>
     * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>.
     * The source code and binary code of this software is proprietary information of Delft University of Technology.
     * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
     */
    protected static class NullComparator implements Comparator<Object>, Serializable
    {
        /** the serial version uid */
        private static final long serialVersionUID = 12L;

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(final Object o1, final Object o2)
        {
            return 0;
        }
    }

    /**
     * @return Returns the negative.
     */
    public boolean isNegative()
    {
        return this.negative;
    }

    /**
     * @param negative The negative to set.
     */
    public void setNegative(final boolean negative)
    {
        this.negative = negative;
    }
}
