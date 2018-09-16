package nl.tudelft.simulation.supplychain.contentstore.database;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SQLDatabaseConnector <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class SQLDatabaseConnector implements Serializable
{
    // TODO: transient, readObject, writeObject

    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** database name */
    private String dbName = "";

    /** database user */
    private String dbUser = "";

    /** database password */
    private String dbPassword = "";

    /**
     * Constructor
     * @param dbName database name
     * @param dbUser database user
     * @param dbPassword database password
     */
    public SQLDatabaseConnector(final String dbName, final String dbUser, final String dbPassword)
    {
        super();
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    /**
     * Open the database with the given username / password combination.
     * @return the JDBC connection
     */
    public Connection dbOpen()
    {
        if (this.dbName.equals(""))
        {
            new Exception("MySQLDatabaseConnector does not have a dbname").printStackTrace();
            System.err.println("MySQLDatabaseConnector does not have a dbname");
            return null;
        }
        // resolve the mySql driver
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (Exception classEx)
        {
            classEx.printStackTrace();
            return null;
        }
        // establish the mySql connection to the database
        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/" + this.dbName + "?user=" + this.dbUser + "&password=" + this.dbPassword);
        }
        catch (SQLException connEx)
        {
            // handle errors
            System.err.println("SQLException: " + connEx.getMessage());
            System.err.println("SQLState: " + connEx.getSQLState());
            System.err.println("VendorError: " + connEx.getErrorCode());
            connEx.printStackTrace();
            conn = null;
            // System.exit(-1);
        }
        return conn;
    }

}
