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

import java.util.ArrayList;
import java.util.HashMap;

public class MetaControlsTables
{

    public static final String TYPE = "MetaControlsTables";
    
    private Object parentObject;
    
    private HashMap<Integer, MetaTable> controlsTables;
    private ArrayList<Integer> controlIds;

    
    public MetaControlsTables ( Object parent ) {
        parentObject = parent;
        controlsTables = new HashMap<Integer, MetaTable>();
        controlIds = new ArrayList<Integer>();
    }

        
    public Object getParentObject() {
        return parentObject;
    }
      
    public void addControlsTable( MetaTable c, int id ) {
    	controlsTables.put( id, c );
    	controlIds.add( id );
    }
    
    public String[] getControlsTableNames() {
    	int numTables = controlsTables.size();
    	String[] returnArray = new String[numTables];
    	for ( int i=0; i < numTables; i++ ) {
    		int id = controlIds.get(i);
    		returnArray[i] = controlsTables.get(id).getTableName();
    	}
    	return returnArray;
    }
    
    public String[] getControlsTableAggregations() {
    	int numTables = controlsTables.size();
    	String[] returnArray = new String[numTables];
    	for ( int i=0; i < numTables; i++ ) {
    		int id = controlIds.get(i);
    		returnArray[i] = controlsTables.get(id).getAggregationLevel();
    	}
    	return returnArray;
    }
    
    public String getType() {
        return TYPE;
    }
    
}
