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

public class Target
{

    public static final String TYPE = "Target";
    
    private Object parentObject;
    private Database database;
    private Marginal marginals;

    private MazControlsTable mazControlsTable;
    private TazControlsTable tazControlsTable;
    private MetaControlsTables metaControlsTables;
    
    
    public Target ( Object parent ) {
        parentObject = parent;
    }

    
    public Object getParentObject() {
        return parentObject;
    }
    
    public void setDatabase ( Database database ) {    
        this.database = database;
    }
      
    public void setMarginal ( Marginal workersMarginal ) {    
        marginals = workersMarginal;
    }
      
    public void setMazControlsTable ( MazControlsTable table ) {    
        mazControlsTable = table;
    }
      
    public void setTazControlsTable ( TazControlsTable table ) {    
        tazControlsTable = table;
    }
      
    public void setMetaControlsTables ( MetaControlsTables table ) {    
        metaControlsTables = table;
    }
      
    public Database getDatabase() {
        return database;
    }
    
    public Marginal getMarginal() {
        return marginals;
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
