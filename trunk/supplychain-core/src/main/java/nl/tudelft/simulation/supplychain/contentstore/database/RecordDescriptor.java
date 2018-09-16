package nl.tudelft.simulation.supplychain.contentstore.database;

import java.io.Serializable;

/**
 * <br>
 * Copyright (c) 2003-2005 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>. The source code and
 * binary code of this software is proprietary information of Delft University of Technology.
 * @version Aug 25, 2005 <br>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/alexandv/index.htm">Alexander Verbraeck </a>
 */
public class RecordDescriptor implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** the field name */
    private String name;

    /** the field types */
    public enum FieldType {
        /** VARCHAR - String */
        VARCHAR,
        /** TEXT - multiline text field */
        TEXT,
        /** INTEGER - long */
        INTEGER,
        /** DOUBLE - double */
        DOUBLE,
        /** DATE - long timeinmillis */
        DATE,
        /** TINYINT - int */
        TINYINT,
        /** BOOLEAN - boolean */
        BOOLEAN
    };

    /** the field type */
    private FieldType type;

    /** key or not */
    private boolean autoKey;

    /**
     * Construstor for the RecordDescriptor
     * 
     * @param name the field name
     * @param type the field type
     * @param autoKey automatic key or not
     */
    public RecordDescriptor(final String name, final FieldType type,
            final boolean autoKey)
    {
        super();
        this.name = name;
        this.type = type;
        this.autoKey = autoKey;
    }

    /**
     * @return Returns the key.
     */
    public boolean isAutoKey()
    {
        return this.autoKey;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return Returns the type.
     */
    public FieldType getType()
    {
        return this.type;
    }
}
