package nl.tudelft.simulation.supplychain.contentstore.database;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.djunits.value.vdouble.scalar.base.DoubleScalarInterface;
import org.djutils.reflection.ClassUtil;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Record implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** the data values */
    private Map<String, String> values = new HashMap<String, String>();

    /** the table descriptor */
    private TableDescriptor tableDescriptor;

    /** the database connector */
    private SQLDatabaseConnector sqlDatabaseConnector;

    /** debug output */
    private static final boolean DEBUG = false;

    /**
     * Create an empty record element
     * @param tableDescriptor the table descriptor
     * @param sqlDatabaseConnector the database connector
     */
    public Record(final TableDescriptor tableDescriptor, final SQLDatabaseConnector sqlDatabaseConnector)
    {
        super();
        this.tableDescriptor = tableDescriptor;
        this.sqlDatabaseConnector = sqlDatabaseConnector;
    }

    /**
     * Create a record element from an SQL query
     * @param tableDescriptor the table descriptor
     * @param resultSet the resultSet from the query
     */
    public Record(final TableDescriptor tableDescriptor, final ResultSet resultSet)
    {
        super();
        this.tableDescriptor = tableDescriptor;
        this.fillFromResultSet(resultSet);
    }

    /**
     * Create an empty record element
     * @param tableDescriptor the table descriptor
     * @param sqlDatabaseConnector the database connector
     * @return Data
     */
    public static Record createData(final TableDescriptor tableDescriptor, final SQLDatabaseConnector sqlDatabaseConnector)
    {
        return new Record(tableDescriptor, sqlDatabaseConnector);
    }

    /**
     * Create a record element from an SQL query
     * @param tableDescriptor the table descriptor
     * @param resultSet the resultSet from the query
     * @return Data
     */
    public static Record createData(final TableDescriptor tableDescriptor, final ResultSet resultSet)
    {
        return new Record(tableDescriptor, resultSet);
    }

    /**
     * Fill the record from a ResultSet
     * @param resultSet the database resultset
     */
    public void fillFromResultSet(final ResultSet resultSet)
    {
        try
        {
            for (RecordDescriptor recordDescriptor : this.tableDescriptor)
            {
                this.values.put(recordDescriptor.getName(), resultSet.getString(recordDescriptor.getName()));
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Fill the record via reflection from an object
     * @param object the object
     */
    public void fillFromReflection(final Object object)
    {
        try
        {
            for (RecordDescriptor recordDescriptor : this.tableDescriptor)
            {
                Field field = null;
                Object value = null;
                String valueString = "";
                try
                {
                    field = ClassUtil.resolveField(object, recordDescriptor.getName());
                }
                catch (Exception e)
                {
                    // ignore
                }
                if (field != null)
                {
                    field.setAccessible(true);
                    value = field.get(object);
                    if (value != null)
                    {
                        if (value instanceof DoubleScalarInterface<?, ?>)
                            valueString = "" + ((DoubleScalarInterface<?, ?>) value).getSI();
                        else
                            valueString = value.toString();
                        this.values.put(recordDescriptor.getName(), valueString);
                    }
                }
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Adds a new item to the database, ignore duplication
     */
    public void addRecordIgnoreDuplicate()
    {
        if (!this.recordExists(this.getValue(this.tableDescriptor.getKeyField())))
        {
            this.addRecord();
        }
    }

    /**
     * Adds a new item to the database
     */
    public void addRecord()
    {
        String query = "";
        try
        {
            Connection connection = this.sqlDatabaseConnector.dbOpen();
            Statement statement = connection.createStatement();
            query = "INSERT INTO `" + this.tableDescriptor.getTableName() + "` (";
            for (RecordDescriptor recordDescriptor : this.tableDescriptor)
            {
                if (!recordDescriptor.isAutoKey())
                {
                    query += "`" + recordDescriptor.getName() + "`, ";
                }
            }
            query = query.substring(0, query.length() - 2);
            query += ") VALUES (";
            for (RecordDescriptor recordDescriptor : this.tableDescriptor)
            {
                if (!recordDescriptor.isAutoKey())
                {
                    query += this.formatDatabaseValue(recordDescriptor.getName()) + ", ";
                }
            }
            query = query.substring(0, query.length() - 2);
            query += ")";
            if (Record.DEBUG)
            {
                System.out.println("Query = " + query);
            }
            statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet result = statement.getGeneratedKeys();
            if (result.next())
            {
                String key = this.tableDescriptor.getKeyField();
                this.values.put(key, result.getString(1));
            }
            else
            {
                if (Record.DEBUG)
                {
                    System.err.println("INSERT -- NO ResultSet after INSERT");
                }
            }
        }
        catch (SQLException exception)
        {
            System.err.println("QSLException Query=" + query);
            exception.printStackTrace();
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Delete an item.
     * @param id id of the item to delete
     */
    public void deleteRecord(final String id)
    {
        String query = "";
        try
        {
            Connection connection = this.sqlDatabaseConnector.dbOpen();
            Statement statement = connection.createStatement();
            query = "DELETE FROM `" + this.tableDescriptor.getTableName() + "` WHERE " + this.tableDescriptor.getKeyField()
                    + "='" + id + "'";
            if (Record.DEBUG)
            {
                System.out.println("Query = " + query);
            }
            statement.execute(query);
        }
        catch (SQLException exception)
        {
            System.err.println("QSLException Query=" + query);
            exception.printStackTrace();
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Find an item by its id, and fill this record element
     * @param id id to search for
     */
    public void readRecord(final String id)
    {
        String query = "";
        try
        {
            Connection connection = this.sqlDatabaseConnector.dbOpen();
            Statement statement = connection.createStatement();
            query = "SELECT * FROM `" + this.tableDescriptor.getTableName() + "` WHERE " + this.tableDescriptor.getKeyField()
                    + "='" + id + "' LIMIT 1";
            if (Record.DEBUG)
            {
                System.out.println("Query = " + query);
            }
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
            {
                this.fillFromResultSet(statement.getResultSet());
            }
            else
                throw new Exception("Record " + id + " not found in query " + query);
        }
        catch (Exception exception)
        {
            System.err.println("QSLException Query=" + query);
            exception.printStackTrace();
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Find an item by its id, and fill this record element
     * @param id id to search for
     * @return record exists or not
     */
    public boolean recordExists(final String id)
    {
        String query = "";
        try
        {
            Connection connection = this.sqlDatabaseConnector.dbOpen();
            Statement statement = connection.createStatement();
            query = "SELECT * FROM `" + this.tableDescriptor.getTableName() + "` WHERE " + this.tableDescriptor.getKeyField()
                    + "='" + id + "' LIMIT 1";
            if (Record.DEBUG)
            {
                System.out.println("Query = " + query);
            }
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
            {
                return true;
            }
        }
        catch (Exception exception)
        {
            System.err.println("QSLException Query=" + query);
            exception.printStackTrace();
            System.err.println(exception.getMessage());
        }
        return false;

    }

    /**
     * Reads in one item from the database with a WHERE clause
     * @param whereField the field
     * @param whereValue the value
     * @return successful or not
     */
    public boolean readRecordWhere(final String whereField, final String whereValue)
    {
        String query = "";
        try
        {
            Connection connection = this.sqlDatabaseConnector.dbOpen();
            Statement statement = connection.createStatement();
            if (this.tableDescriptor.getQuery().length() == 0)
            {
                query = "SELECT * FROM `" + this.tableDescriptor.getTableName();
                query += "` WHERE `" + whereField + "`=" + whereValue;
                if (Record.DEBUG)
                {
                    System.out.println(query);
                }
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next())
                {
                    this.fillFromResultSet(resultSet);
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                query = this.tableDescriptor.getQuery();
                query = query + " HAVING `" + whereField + "`=" + whereValue;

                if (Record.DEBUG)
                {
                    System.out.println(query);
                }
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next())
                {
                    this.fillFromResultSet(resultSet);
                    return true;
                }
                else
                {
                    return false;
                }

            }
        }
        catch (Exception exception)
        {
            System.err.println("QSLException Query=" + query);
            exception.printStackTrace();
            System.err.println(exception.getMessage());
        }
        return false;
    }

    /**
     * Update and save an existing record to the database
     */
    public void updateRecord()
    {
        String query = "";
        try
        {
            Connection connection = this.sqlDatabaseConnector.dbOpen();
            Statement statement = connection.createStatement();
            query = "UPDATE `" + this.tableDescriptor.getTableName() + "` SET ";
            for (RecordDescriptor recordDescriptor : this.tableDescriptor)
            {
                query += "`" + recordDescriptor.getName() + "`=";
                query += this.formatDatabaseValue(recordDescriptor.getName()) + ", ";
            }
            query = query.substring(0, query.length() - 2);
            query += " WHERE " + this.tableDescriptor.getKeyField() + "="
                    + this.formatDatabaseValue(this.tableDescriptor.getKeyField());
            if (Record.DEBUG)
            {
                System.out.println("Query = " + query);
            }
            statement.execute(query);
        }
        catch (SQLException exception)
        {
            System.err.println("QSLException Query=" + query);
            exception.printStackTrace();
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Get a value corresponding to a field name
     * @param name the field name
     * @return Returns the element corresponding to a name.
     */
    private String formatValue(final String name)
    {
        if ((name == null) || ("".equals(name)))
        {
            Exception e = new Exception("name for formatScreenValue is null");
            e.printStackTrace();
            System.err.println(e.getMessage());
            return new String("");
        }
        if (this.tableDescriptor.getDataDescriptor(name) == null)
        {
            Exception e = new Exception("recorddescriptor is null for tabledescriptor=" + this.tableDescriptor.getTableName()
                    + ", recorddescriptor name=" + name);
            e.printStackTrace();
            System.err.println(e.getMessage());
            return new String("");
        }
        if (!this.values.containsKey(name))
        {
            String error = "RECORD.formatScreenValue: values does not contain field: " + name + ", table "
                    + this.tableDescriptor.getTableName();
            new Exception(error).printStackTrace();
            System.err.println(error);
            return "''";
        }
        switch (this.tableDescriptor.getDataDescriptor(name).getType())
        {
            case DATE:
                if ((this.values.get(name).equals("")) || (this.values.get(name).equals("-")))
                {
                    this.values.put(name, new String("0"));
                }
                return CalendarUtility.formatDate(Long.parseLong(this.values.get(name)));
            case BOOLEAN:
                String value = this.values.get(name);
                if (value.startsWith("1"))
                {
                    value = "Y";
                }
                else
                {
                    value = "N";
                }
                return value;
            default:
                return this.values.get(name);
        }
    }

    /**
     * Get a screen-formatted value corresponding to a field name
     * @param name the field name
     * @return Returns the element corresponding to a name.
     */
    public String formatScreenValue(final String name)
    {
        return StringUtility.formatScreen(formatValue(name));
    }

    /**
     * Get an HTML-formatted value corresponding to a field name
     * @param name the field name
     * @return Returns the element corresponding to a name.
     */
    public String formatHTMLValue(final String name)
    {
        return StringUtility.formatHTML(formatValue(name));
    }

    /**
     * Get a value corresponding to a field name
     * @param name the field name
     * @return Returns the element corresponding to a name.
     */
    public String getValue(final String name)
    {
        if (!this.values.containsKey(name))
        {
            String error = "RECORD.getValue: values does not contain field: " + name + ", table "
                    + this.tableDescriptor.getTableName();
            new Exception(error).printStackTrace();
            System.err.println(error);
            return "''";
        }
        return this.values.get(name);
    }

    /**
     * Get a value corresponding to a field name
     * @param name the field name
     * @return Returns the element corresponding to a name.
     */
    public String formatDatabaseValue(final String name)
    {
        if (!this.values.containsKey(name))
        {
            String error = "RECORD.formatDatabaseValue: values does not contain field: " + name + ", table "
                    + this.tableDescriptor.getTableName();
            new Exception(error).printStackTrace();
            System.err.println(error);
            return "''";
        }
        switch (this.tableDescriptor.getDataDescriptor(name).getType())
        {
            case DATE:
            {
                String value = this.values.get(name);
                if (("".equals(value)) || value == null)
                {
                    value = "01-01-2000";
                }
                return "" + CalendarUtility.getMillis(value);
            }
            case INTEGER:
            {
                String value = this.values.get(name);
                if (("".equals(value)) || value == null)
                {
                    value = "0";
                }
                return value;
            }
            case TINYINT:
            {
                String value = this.values.get(name);
                if (("".equals(value)) || value == null)
                {
                    value = "0";
                }
                return value;
            }
            case BOOLEAN:
            {
                String value = this.values.get(name);
                if (value.startsWith("Y") || value.startsWith("y") || value.startsWith("1"))
                {
                    value = "1";
                }
                else
                {
                    value = "0";
                }
                return value;
            }
            default:
                return "'" + StringUtility.formatDatabase(this.values.get(name)) + "'";
        }
    }

    /**
     * Set a value corresponding to a field name
     * @param name the field name
     * @param value the value
     */
    public void setValue(final String name, final String value)
    {
        if (!this.tableDescriptor.getFields().containsKey(name))
        {
            String error = "RECORD.setValue: tabledescriptor does not contain field: " + name + ", table "
                    + this.tableDescriptor.getTableName();
            new Exception(error).printStackTrace();
            System.err.println(error);
        }
        else
        {
            this.values.put(name, value);
        }
    }

    /**
     * @return Returns the tableDescriptor.
     */
    public TableDescriptor getTableDescriptor()
    {
        return this.tableDescriptor;
    }

    /**
     * @return Returns the values.
     */
    public Map<String, String> getValues()
    {
        return this.values;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.values.get(this.tableDescriptor.getDisplayField());
    }
}
