package nl.tudelft.simulation.supplychain.content.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * DataList <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <E>
 */
public class RecordList<E extends Record> extends ArrayList<E>
{
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** true for debug */
    private static final boolean DEBUG = false;

    /** the table descriptor */
    private TableDescriptor tableDescriptor;

    /** the database connector */
    private SQLDatabaseConnector sqlDatabaseConnector;

    /**
     * Constructor
     * @param tableDescriptor the table descriptor
     * @param sqlDatabaseConnector the database connector
     */
    public RecordList(final TableDescriptor tableDescriptor, final SQLDatabaseConnector sqlDatabaseConnector)
    {
        super();
        this.tableDescriptor = tableDescriptor;
        this.sqlDatabaseConnector = sqlDatabaseConnector;
    }

    /**
     * Find an item by its id
     * @param id id to search for
     * @return item
     */
    public E getItem(final String id)
    {
        E item = null;
        Iterator<E> i = this.iterator();
        boolean found = false;
        while ((!found) && (i.hasNext()))
        {
            E current = i.next();
            if (current.getValue(this.tableDescriptor.getKeyField()).equals(id))
            {
                found = true;
                item = current;
            }
        }
        return item;
    }

    /**
     * Reads in the list from the database
     */
    @SuppressWarnings("unchecked")
    public void read()
    {
        String query = "";
        this.clear();
        try
        {
            E item = null;
            Connection connection = this.sqlDatabaseConnector.dbOpen();
            Statement statement = connection.createStatement();
            if (this.tableDescriptor.getQuery().length() == 0)
            {
                query = "SELECT * FROM `" + this.tableDescriptor.getTableName() + "`";
                boolean first = true;
                for (String whereString : this.tableDescriptor.getWhereStrings())
                {
                    if (first)
                    {

                        query += " WHERE ";
                    }
                    else
                    {
                        query += " AND ";
                    }
                    query += whereString + " ";
                    first = false;
                }
            }
            else
            {
                query = this.tableDescriptor.getQuery();
            }
            if (this.tableDescriptor.getSortField() != "")
            {
                query += " ORDER BY " + this.tableDescriptor.getSortField();
                if (this.tableDescriptor.issortAscending())
                {
                    query += " ASC";
                }
                else
                {
                    query += " DESC";
                }
            }

            if (DEBUG)
            {
                System.out.println(query);
            }

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next())
            {
                // item = new E(this.tableDescriptor, resultSet) not allowed
                // use reflection instead
                item = (E) Record.createData(this.tableDescriptor, resultSet);
                this.add(item);
            }
        }
        catch (Exception exception)
        {
            System.err.println("QSLException Query=" + query);
            exception.printStackTrace();
            System.err.println(exception.getMessage());
        }
    }
}
