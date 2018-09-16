package nl.tudelft.simulation.supplychain.contentstore.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A descriptor of a table in the database <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class TableDescriptor extends ArrayList<RecordDescriptor>
{
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** the table name in SQL */
    private String tableName;

    /** the table key */
    private String keyField;

    /** the field name to display in toString */
    private String displayField;

    /** the data descriptors as a searchable map */
    private Map<String, RecordDescriptor> fields = new HashMap<String, RecordDescriptor>();

    /** the needed WHERE strings to display this specific table */
    private List<String> whereStrings = new ArrayList<String>();

    /** the query when a specific query has been given */
    private String query = "";

    /** the sort field, none if empty string */
    private String sortField = "";

    /** the sort direction, ascending is true */
    private boolean sortAscending = true;

    /**
     * @param tableName the table name in SQL
     * @param keyField the key of the table
     * @param displayField the field name to display in toString
     */
    public TableDescriptor(final String tableName, final String keyField, final String displayField)
    {
        super();
        this.tableName = tableName;
        this.keyField = keyField;
        this.displayField = displayField;
    }

    /**
     * @param c see ArrayList
     * @param tableName the table name in SQL
     * @param keyField the key of the table
     * @param displayField the field name to display in toString
     */
    public TableDescriptor(final Collection<? extends RecordDescriptor> c, final String tableName, final String keyField,
            final String displayField)
    {
        super(c);
        this.tableName = tableName;
        this.keyField = keyField;
        this.displayField = displayField;
    }

    /**
     * @param initialCapacity see ArrayList
     * @param tableName the table name in SQL
     * @param keyField the key of the table
     * @param displayField the field name to display in toString
     */
    public TableDescriptor(final int initialCapacity, final String tableName, final String keyField, final String displayField)
    {
        super(initialCapacity);
        this.tableName = tableName;
        this.keyField = keyField;
        this.displayField = displayField;
    }

    /**
     * @return Returns the displayField.
     */
    public String getDisplayField()
    {
        return this.displayField;
    }

    /**
     * @return Returns the tableName.
     */
    public String getTableName()
    {
        return this.tableName;
    }

    /**
     * @return Returns the keyField.
     */
    public String getKeyField()
    {
        return this.keyField;
    }

    /**
     * add a data decriptor
     * @param dataDescriptor the data descriptor to add
     * @return succeeded or not
     */
    public boolean add(final RecordDescriptor dataDescriptor)
    {
        this.fields.put(dataDescriptor.getName(), dataDescriptor);
        return super.add(dataDescriptor);
    }

    /**
     * add a data decriptor
     * @param index the location to insert the element
     * @param dataDescriptor the data descriptor to add
     */
    public void add(final int index, final RecordDescriptor dataDescriptor)
    {
        this.fields.put(dataDescriptor.getName(), dataDescriptor);
        super.add(index, dataDescriptor);
    }

    /**
     * return the data descriptor belonging to a fieldname
     * @param fieldName the field to search for
     * @return the datadescriptor belonging to the fieldname
     */
    public RecordDescriptor getDataDescriptor(final String fieldName)
    {
        return this.fields.get(fieldName);
    }

    /**
     * @return Returns the whereStrings.
     */
    public List<String> getWhereStrings()
    {
        return this.whereStrings;
    }

    /**
     * @param whereString The whereString to add.
     */
    public void addWhereString(final String whereString)
    {
        this.whereStrings.add(whereString);
    }

    /**
     * @return Returns the query.
     */
    public String getQuery()
    {
        return this.query;
    }

    /**
     * @param query The query to set.
     */
    public void setQuery(final String query)
    {
        this.query = query;
    }

    /**
     * @return Returns the sortAscending.
     */
    public boolean issortAscending()
    {
        return this.sortAscending;
    }

    /**
     * @param sortAscending The sortAscending to set.
     */
    public void setsortAscending(final boolean sortAscending)
    {
        this.sortAscending = sortAscending;
    }

    /**
     * @return Returns the sortField.
     */
    public String getSortField()
    {
        return this.sortField;
    }

    /**
     * @param sortField The sortField to set.
     */
    public void setSortField(final String sortField)
    {
        this.sortField = sortField;
    }

    /**
     * @param sortField The sortField to set.
     * @param sortAscending The sortAscending to set.
     */
    public void setSortField(final String sortField, final boolean sortAscending)
    {
        this.sortField = sortField;
        this.sortAscending = sortAscending;
    }

    /**
     * @return Returns the fields.
     */
    public Map<String, RecordDescriptor> getFields()
    {
        return this.fields;
    }
}
