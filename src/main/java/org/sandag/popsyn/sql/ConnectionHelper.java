/*   
 * Copyright 2014 Parsons Brinckerhoff

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License
   *
   */

package org.sandag.popsyn.sql;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionHelper implements Serializable
{

	private static final long versionNumber = 1L;
	
	private static final long serialVersionUID = versionNumber;
	
    public static final String MYSQL_SERVER_NAME = "MYSQL";
    public static final String MS_SQL_SERVER_NAME = "MS_SQL";
    
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private static final String MYSQL_JDBC_HEADER = "jdbc:mysql://";
    
    private static final String MS_SQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String MS_SQL_JDBC_HEADER = "jdbc:sqlserver://";
    
    private static String jdbcDriver = MYSQL_DRIVER;
    private static String jdbcHeader = MYSQL_JDBC_HEADER;
    
	private static ConnectionHelper instance;


	/**
	 * 
	 * default constructor loads the MYSQL JDBC driver class
	 */
	private ConnectionHelper() {
        // Load default database driver
		try {
			Class.forName( jdbcDriver ).newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 * @param dbServer is an identifier for the database server being accessed.
	 * 
	 * Servers currently support:
	 * 		MYSQL: 	MySQL
	 * 		MS_SQL:	Microsoft SQL Server
	 *  
	 */
	private ConnectionHelper( String dbServer ) {
		
		if ( dbServer.equals( MS_SQL_SERVER_NAME ) ) {
			jdbcDriver = MS_SQL_DRIVER;
			jdbcHeader = MS_SQL_JDBC_HEADER;
		}
		else if ( dbServer.equals( MYSQL_SERVER_NAME ) ) {
			jdbcDriver = MYSQL_DRIVER;
			jdbcHeader = MYSQL_JDBC_HEADER;
		}
		
		
        // Load database driver
		try {
			Class.forName( jdbcDriver ).newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	
	/**
	 * 
	 * @return a concrete instance of the ConnectionHelper class
	 * 
	 */
	public static ConnectionHelper getInstance()
	{
		if (instance == null)
			instance = new ConnectionHelper();
		return instance;
	}

	
	/**
	 * 
	 * @return a concrete instance of the ConnectionHelper class for the specified database server
	 * 
	 */
	public static ConnectionHelper getInstance( String dbServer )
	{
		if (instance == null)
			instance = new ConnectionHelper( dbServer );
		return instance;
	}

	
	/**
	 * 
	 * @param dbServer is an identifier for the database server being accessed.
	 * @param dbHost is the IP address for the machine on which the database server is running
	 * @param dbName is the name of the database we want to use in the server
	 * @param user is the name of the user used to login to access the database.  Note for MS_SQL,
	 * user can be null, which indicates that standard MS Windows authentication will be used.  In this
	 * case sqljdbc_auth.dll must be in the java library path.
	 * @param password is the user's password - not necessary if user is null.
	 * @return the concrete Connection instance for the URL formed for the specific database server specified
	 * @throws java.sql.SQLException
	 */
	public static Connection getConnection( String dbServer, String dbName, String dbHost, String user, String password ) throws java.sql.SQLException
	{

    	String connectionUrl = null;
		
		if ( dbServer.equals( MYSQL_SERVER_NAME ) ) {
	    	connectionUrl = jdbcHeader + dbHost + "/" + dbName + "?user=" + user + "&password=" + password;
		}
		else if ( dbServer.equals( MS_SQL_SERVER_NAME ) ) {
	    	connectionUrl = jdbcHeader + dbHost + ";" + "database=" + dbName + ";" +
	    		( user != null ? ( "user=" + user + ";" + "password=" + password ) : "integratedSecurity=true" );
		}
		
		return DriverManager.getConnection( connectionUrl );
		
	}

	
	/**
	 * 
	 * @param url is a fully specified URL for connecting to a database server
	 * @return the concrete Connection instance for the URL formed for the specific database server specified
	 * @throws java.sql.SQLException
	 */
	public static Connection getConnection( String url ) throws java.sql.SQLException
	{
		return DriverManager.getConnection( url );
	}

	
	/**
	 * 
	 * @param c is the concrete Connection instance to be closed when the database connection is no longer needed.
	 */
	public static void closeConnection(Connection c)
	{
		try
		{
			if (c != null)
			{
			    c.close();
            }
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

}
