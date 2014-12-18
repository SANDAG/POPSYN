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

package org.sandag.popsyn.testXmlParse;

public class Database
{

    public static final String TYPE = "Database";
    
    private Object parentObject;
    
    private String type;
    private String serverAddress;
    private String user;
    private String password;
    private String dbName;

    public Database ( Object parent ) {
        parentObject = parent;
    }

    
    public Object getParentObject() {
        return parentObject;
    }
    
    public void setServerType( String serverType ) {
        this.type = serverType;
    }

    public void setServerAddress( String serverAddress ) {
        this.serverAddress = serverAddress;
    }

    public void setUser( String user ) {
        this.user = user;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public void setDbName( String dbName ) {
        this.dbName = dbName;
    }

    public String getDbType() {
        return type;        
    }
    
    public String getDbName() {
        return dbName;        
    }
    
    public String getDbUser() {
        return user;        
    }
    
    public String getDbPassword() {
        return password;        
    }
    
    public String getDbHost() {
        return serverAddress;        
    }
    
    public String getType() {
        return TYPE;
    }
    
}
