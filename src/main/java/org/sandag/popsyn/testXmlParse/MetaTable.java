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

public class MetaTable
{

    public static final String TYPE = "MetaTable";
    
    private Object parentObject;
    private int id;
    
    private String tableName;
    private String idFieldName;
    private String aggregationLevel;

    
    public MetaTable ( Object parent, int id ) {
        parentObject = parent;
        this.id = id;
    }

        
    public Object getParentObject() {
        return parentObject;
    }
      
    public void setTableName( String table ) {
        this.tableName = table;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setIdFieldName( String field ) {
        this.idFieldName = field;
    }
    
    public String getIdFieldName() {
        return idFieldName;
    }
    
    public void setAggregationLevel( String value ) {
        this.aggregationLevel = value;
    }
    
    public String getAggregationLevel() {
        return aggregationLevel;
    }
    
    public String getType() {
        return TYPE;
    }
    
}
