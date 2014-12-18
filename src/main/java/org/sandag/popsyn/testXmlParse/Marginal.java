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

import java.util.HashMap;

public class Marginal
{
    public static final String TYPE = "Marginal";
    
    private Object parentObject;
    
    private String id;
    private String description;
    private String table;
    private String geographyType;
    private String controlType;
    private boolean isTotalHouseholdsControl; 
    private boolean isGroupQuartersControl;
    private boolean isMajorGroupQuartersIndicatorField;
    private boolean isOtherGroupQuartersIndicatorField;
    private HashMap<Integer, Constraint> constraints;
    

    public Marginal ( Object parent ) {
        parentObject = parent;
        constraints = new HashMap<Integer, Constraint>();
    }
    
    
    public Object getParentObject() {
        return parentObject;
    }
    
    public void setDescription( String description ) {
        this.description = description;
    }
    
    public void setId( String id ) {
        this.id = id;
    }
    
    public void setGroupQuartersControl( String flagString ) {
    	boolean flag = flagString.equalsIgnoreCase( "true" );
    	isGroupQuartersControl = flag;
    }
    
    public void setMajorGroupQuartersIndicatorField( String flagString ) {
    	boolean flag = flagString.equalsIgnoreCase( "true" );
    	isMajorGroupQuartersIndicatorField = flag;
    }
    
    public void setOtherGroupQuartersIndicatorField( String flagString ) {
    	boolean flag = flagString.equalsIgnoreCase( "true" );
    	isOtherGroupQuartersIndicatorField = flag;
    }
    
    public int getId() {
        return Integer.parseInt( id );
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setTable( String table ) {
        this.table = table;
    }
    
    public String getTable() {
        return table;
    }
    
    public void setControlType( String type ) {
        controlType = type;
    }
    
    public String getControlType() {
        return controlType;
    }
    
    public void setGeographyType( String type ) {
        geographyType = type;
    }
    
    public String getGeographyType() {
        return geographyType;
    }
    
    public void addConstraint( Constraint c, int id ) {
        constraints.put( id, c );
    }
    
    public String getType() {
        return TYPE;
    }

    public void setIsTotalHouseldsControl( String flagString ) {
    	boolean flag = flagString.equalsIgnoreCase( "true" );
    	isTotalHouseholdsControl = flag;
    }
    
    public boolean getIsTotalHouseldsControl() {
    	return isTotalHouseholdsControl;
    }
    
    public boolean getIsGroupQuartersControl() {
    	return isGroupQuartersControl;
    }

    public boolean getIsMajorGroupQuartersIndicatorField() {
    	return isMajorGroupQuartersIndicatorField;
    }
    
    public boolean getIsOtherGroupQuartersIndicatorField() {
    	return isOtherGroupQuartersIndicatorField;
    }
    
    public HashMap<Integer, Constraint> getConstraintMap() { 
        return constraints;
    }

}
