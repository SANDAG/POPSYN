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

public class Constraint
{

    public static final String TYPE = "Constraint";
    
    private Object parentObject;
    private int id;
    
    private String field;
    private String importance;
    private String controlField;
    private String intervalType;
    private String value;
    private String loType;
    private String loValue;
    private String hiType;
    private String hiValue;
    
    private int constraintValue;
    

    public Constraint ( Object parent, int id ) {
        parentObject = parent;
        this.id = id;
    }
    
    
    public Object getParentObject() {
        return parentObject;
    }

    public int getId() {
        return id;
    }
    
    
    public void setField( String field ) {
        this.field = field;
    }
    
    public String getImportance() {
        return importance;
    }
    
    public String getField() {
        return field;
    }
    
    public String getControlField() {
        return controlField;
    }
    
    public void setImportance( String importance ) {
    	this.importance = importance;
    }
    
    public void setControlField( String field ) {
        controlField = field;
    }
    
    public void setIntervalType( String type ) {
        this.intervalType = type;
    }
    
    public String getIntervalType() {
        return intervalType;
    }
    
    public void setValue( String value ) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setLoValue( String value ) {
        this.loValue = value;
    }
    
    public String getLoValue() {
        return loValue;
    }
    
    public void setLoType( String type ) {
        this.loType = type;
    }
    
    public String getLoType() {
        return loType;
    }
    
    public void setHiValue( String value ) {
        this.hiValue = value;
    }
    
    public String getHiValue() {
        return hiValue;
    }
    
    public void setHiType( String type ) {
        this.hiType = type;
    }
    
    public String getHiType() {
        return hiType;
    }
    
    public int getConstraintValue() {
        return constraintValue;
    }
    
    public void setConstraintValue( int value ) {
        constraintValue = value;
    }
    
    public String getType() {
        return TYPE;
    }

}
