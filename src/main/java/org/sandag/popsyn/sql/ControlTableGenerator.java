/*   
 * Copyright 2015 SANDAG

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
/**
 *   
   @author: Wu.Sun@sandag.org
 *
 */
package org.sandag.popsyn.sql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.sandag.popsyn.testXmlParse.Balance;
import org.sandag.popsyn.testXmlParse.Database;
import org.sandag.popsyn.testXmlParse.TargetsSAXParser;

public class ControlTableGenerator {
    private static String jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";    
    private String dbServer=null;
    private String dbHost=null;
    private String dbName=null;
    private String user=null;
    private String password=null;
    
    public ControlTableGenerator(String setting) {            
		TargetsSAXParser saxParser = new TargetsSAXParser();
		saxParser.parseConditions(setting);
		Balance balanceObject = saxParser.getBalanceObject();
		Database db = balanceObject.getDatabase(); 
		this.dbServer=db.getDbType();
        this.dbHost=db.getDbHost();
        this.dbName=db.getDbName();
        this.user=db.getDbUser();
        this.password=db.getDbPassword();
        ConnectionHelper.getInstance( dbServer);
    }
    
    public ControlTableGenerator( String dbType, String dbName, String user, String password, String ipAddress ) {            
        this.dbServer = dbType;
        this.dbHost = ipAddress;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        ConnectionHelper.getInstance( dbServer);
    }
    
    private ResultSet generateControlTable(String funName, int lu_version, int year){
        Connection con = null;
        ResultSet rs=null;
        try {
            Class.forName(jdbcDriver);
            con = ConnectionHelper.getConnection( dbServer, dbName, dbHost, user, password);        
            String query = "SELECT * from "+funName+"("+lu_version+","+year+")";
            PreparedStatement ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            int count=0;
            while (rs.next()&count<5000) {
                System.out.println(rs.getInt("taz13") + "\t"); 
                count++;
            }   
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return rs;
    }
    
    public static void main(String[] args){
    	String setting="T:\\ABM\\ABM_FY15\\PopSynIII\\popsyn3_DEMO\\runtime\\config\\settings.xml";
    	ControlTableGenerator ctg=new ControlTableGenerator(setting);
    	ResultSet rs=ctg.generateControlTable("[input].[fn_control_targets_mgra_sr13]",1,2012);
    }
}

