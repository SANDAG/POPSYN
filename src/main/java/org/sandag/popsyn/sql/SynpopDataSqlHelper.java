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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;


public class SynpopDataSqlHelper implements Serializable
{

	private static final long versionNumber = 1L;
	
	private static final long serialVersionUID = versionNumber;
	
    private transient Logger logger = Logger.getLogger(SynpopDataSqlHelper.class);
    
    private String dbServer;
    private String dbHost;
    private String dbName;
    private String user;
    private String password;
    
    
    public SynpopDataSqlHelper( String dbType, String dbName, String ipAddress, String user, String password ) {              
        this.dbServer = dbType;
        this.dbHost = ipAddress;
        this.dbName = dbName;
        this.user = user;
        this.password = password;

        ConnectionHelper.getInstance( dbServer );
    }
        
    
    public double[][] getDoubleTableValues( String tableName, String idFieldName, int startId, int endId, String[] fieldNames ) {
    	String whereClause = " WHERE " + idFieldName + ">=" + startId + " AND " + idFieldName + "<=" + endId;
    	return getDoubleTableValues( tableName, idFieldName, whereClause, fieldNames );
    }

    public double[][] getDoubleTableValues( String tableName, String idFieldName, int id, String[] fieldNames ) {
    	String whereClause = " WHERE " + idFieldName + "=" + id;
    	return getDoubleTableValues( tableName, idFieldName, whereClause, fieldNames );
    }

    public double[][] getDoubleTableValues( String tableName, String idFieldName, String[] fieldNames ) {
    	String whereClause = "";
    	return getDoubleTableValues( tableName, idFieldName, whereClause, fieldNames );
    }

    private double[][] getDoubleTableValues( String tableName, String idFieldName, String whereClause, String[] fieldNames ) {

    	String nameList = fieldNames[0];
    	for ( int i=1; i < fieldNames.length; i++ )
    		nameList += "," + fieldNames[i];
    	
        String query = "SELECT " + nameList + " FROM " + tableName + whereClause;

        
        double[][] result;
        ArrayList<double[]> rsList = new ArrayList<double[]>();
        
        Connection conn = null;
        try
        {

            conn = ConnectionHelper.getConnection( dbServer, dbName, dbHost, user, password );
            PreparedStatement ps = conn.prepareStatement( query );
            ResultSet rs = ps.executeQuery();

            int numColumns = rs.getMetaData().getColumnCount();
            while ( rs.next() ) {
                double[] row = new double[numColumns];
                for ( int i=0; i < numColumns; i++ )
                    row[i] = rs.getDouble( i+1 );
                rsList.add( row );
            }

            result = new double[rsList.size()][];
            for ( int i=0; i < rsList.size(); i++ ) {
                result[i] = rsList.get( i );
            }
            
        }
        catch (SQLException e)
        {
            logger.error( "error executing SQL query:" );
            logger.error( "SQL:  " + query, e );
            throw new DAOException(e.getMessage());
        }
        finally
        {
        	ConnectionHelper.closeConnection(conn);
        }
        
        return result;
        
    }
    

    public int[][] getIntegerTableValues( String tableName, String idFieldName, int id, String[] fieldNames ) {
    	String whereClause = " WHERE " + idFieldName + "=" + id;
    	return getIntegerTableValues( tableName, idFieldName, whereClause, fieldNames );
    }
    
    public int[][] getIntegerTableValues( String tableName, String idFieldName, String[] fieldNames ) {
    	String whereClause = "";
    	return getIntegerTableValues( tableName, idFieldName, whereClause, fieldNames );
    }

    private int[][] getIntegerTableValues( String tableName, String idFieldName, String whereClause, String[] fieldNames ) {

    	String nameList = fieldNames[0];
    	for ( int i=1; i < fieldNames.length; i++ )
    		nameList += "," + fieldNames[i];
    	
        String query = "SELECT " + nameList + " FROM " + tableName + whereClause;

        
        int[][] result;
        ArrayList<int[]> rsList = new ArrayList<int[]>();
        
        Connection conn = null;
        try
        {

            conn = ConnectionHelper.getConnection( dbServer, dbName, dbHost, user, password );
            PreparedStatement ps = conn.prepareStatement( query );
            ResultSet rs = ps.executeQuery();

            int numColumns = rs.getMetaData().getColumnCount();
            while ( rs.next() ) {
                int[] row = new int[numColumns];
                for ( int i=0; i < numColumns; i++ )
                    row[i] = rs.getInt( i+1 );
                rsList.add( row );
            }

            result = new int[rsList.size()][];
            for ( int i=0; i < rsList.size(); i++ ) {
                result[i] = rsList.get( i );
            }
            
        }
        catch (SQLException e)
        {
            logger.error( "error executing SQL query:" );
            logger.error( "SQL:  " + query, e );
            throw new DAOException(e.getMessage());
        }
        finally
        {
        	ConnectionHelper.closeConnection(conn);
        }
        
        return result;
        
    }

}
