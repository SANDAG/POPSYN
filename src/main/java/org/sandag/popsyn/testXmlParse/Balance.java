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

public class Balance
{

    public static final String TYPE = "Balance";
    
    private Database database;
    private PumsData pumsData;
    
    private MazControlsTable mazControlsTable;
    private TazControlsTable tazControlsTable;
    private MetaControlsTables metaControlsTables;

    public Balance () {    
    }
      
    public void setDatabase ( Database database ) {    
        this.database = database;
    }
      
    public void setPumsData( PumsData pumsData ) {
    	this.pumsData = pumsData;
    }
    
    public void setMazControlsTable ( MazControlsTable mazControlsTable ) {    
        this.mazControlsTable = mazControlsTable;
    }
      
    public void setTazControlsTable ( TazControlsTable tazControlsTable ) {    
        this.tazControlsTable = tazControlsTable;
    }
      
    public void setMetaControlsTables ( MetaControlsTables metaControlsTables ) {    
        this.metaControlsTables = metaControlsTables;
    }
      
    public Database getDatabase() {
        return database;
    }
    
    public PumsData getPumsData() {
        return pumsData;
    }
    
    public MazControlsTable getMazControlsTable() {
        return mazControlsTable;
    }

    public TazControlsTable getTazControlsTable() {
        return tazControlsTable;
    }

    public MetaControlsTables getMetaControlsTables() {
        return metaControlsTables;
    }

    public String getType() {
        return TYPE;
    }
    
}
