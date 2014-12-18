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

public class MazControlsTable
{

    public static final String TYPE = "MazControlsTable";
    
    private Object parentObject;
    
    private MazTable controlsTable;

    
    public MazControlsTable ( Object parent ) {
        parentObject = parent;
    }

        
    public Object getParentObject() {
        return parentObject;
    }
      
    public void setControlsTable( MazTable table ) {
        this.controlsTable = table;
    }
    
    public String getControlsTableName() {
        return controlsTable.getTableName();
    }
    
    public String getType() {
        return TYPE;
    }
    
}
