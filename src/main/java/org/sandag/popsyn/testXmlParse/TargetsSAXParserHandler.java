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


import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

//import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;


public class TargetsSAXParserHandler extends DefaultHandler{

    //private Logger logger = Logger.getLogger(TargetsSAXParserHandler.class);

    private List<Object> targetList;    
    private Balance balance;    
    
    //to maintain context
    private Object currentObject;
    private String currentContext;
    private String parentContext;
    private String tempVal;
    //Wu added for adjustment to fit SANDAG database 
    private int tempIntVal;
    
    //private Database db;
    private PumsData pumsData;

    private MazControlsTable mazControlsTable;
    private TazControlsTable tazControlsTable;
    private MetaControlsTables metaControlsTables;

    //private Writer out;
    
    
    
    public TargetsSAXParserHandler( Writer out ){
        //this.out = out;
        targetList = new ArrayList<Object>();
    }
        

    //===========================================================
    // Override Methods in the DefaultHandler to give our own logic to this specific xml file being parsed
    //===========================================================

    public void startDocument ()
    throws SAXException
    {
        showData ("<?xml version='1.0' encoding='UTF-8'?>");
        newLine();
    }

    
    public void endDocument ()
    throws SAXException
    {
//        try {
//            newLine();
//            out.flush ();
//        } catch (IOException e) {
//            throw new SAXException ("I/O error", e);
//        }
    }

    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        tempVal = "";
        
        if( qName.equalsIgnoreCase("targets") ) {
            balance = new Balance();
            currentObject = balance;
            currentContext = Balance.TYPE;
            parentContext = null;
        }
        else if( qName.equalsIgnoreCase("target") ) {
            currentObject = new Target( balance );
            currentContext = Target.TYPE;
            parentContext = Balance.TYPE;
        }
        else if( qName.equalsIgnoreCase( "database" ) ) {
        	Database db = new Database( currentObject );
            if ( parentContext == null ) {
                ((Balance)currentObject).setDatabase(db);
                parentContext = Balance.TYPE;
            }
            else if ( parentContext.equals( Target.TYPE ) ) {
                ((Target)currentObject).setDatabase(db);
                parentContext = Target.TYPE;
            }
            currentObject = db;
            currentContext = Database.TYPE;
        }
        else if( qName.equalsIgnoreCase( "pumsData" ) ) {
        	pumsData = new PumsData( currentObject );
            ((Balance)currentObject).setPumsData(pumsData);
            parentContext = Balance.TYPE;
            currentObject = pumsData;
            currentContext = PumsData.TYPE;
        }
        else if( qName.equalsIgnoreCase( "mazControlsTable" ) ) {
        	mazControlsTable = new MazControlsTable( currentObject );
            ((Balance)currentObject).setMazControlsTable(mazControlsTable);
            parentContext = Balance.TYPE;
            currentObject = mazControlsTable;
            currentContext = MazControlsTable.TYPE;
        }
        else if( qName.equalsIgnoreCase( "tazControlsTable" ) ) {
        	tazControlsTable = new TazControlsTable( currentObject );
            ((Balance)currentObject).setTazControlsTable(tazControlsTable);
            parentContext = Balance.TYPE;
            currentObject = tazControlsTable;
            currentContext = TazControlsTable.TYPE;
        }
        else if( qName.equalsIgnoreCase( "metaControlsTables" ) ) {
        	metaControlsTables = new MetaControlsTables( currentObject );
            ((Balance)currentObject).setMetaControlsTables(metaControlsTables);
            parentContext = Balance.TYPE;
            currentObject = metaControlsTables;
            currentContext = MetaControlsTables.TYPE;
        }
        else if( qName.equals("mazTable") ) {
            int id = Integer.parseInt( attributes.getValue( "id" ) );
            MazTable c = new MazTable( currentObject, id );
            ((MazControlsTable)currentObject).setControlsTable( c );
            currentObject = c;
            currentContext = MazTable.TYPE;
            parentContext = MazControlsTable.TYPE;
        }
        else if( qName.equals("tazTable") ) {
            int id = Integer.parseInt( attributes.getValue( "id" ) );
            TazTable c = new TazTable( currentObject, id );
            ((TazControlsTable)currentObject).setControlsTable( c );
            currentObject = c;
            currentContext = TazTable.TYPE;
            parentContext = TazControlsTable.TYPE;
        }
        else if( qName.equals("metaTable") ) {
            int id = Integer.parseInt( attributes.getValue( "id" ) );
            MetaTable c = new MetaTable( currentObject, id );
            ((MetaControlsTables)currentObject).addControlsTable(c, id);
            currentObject = c;
            currentContext = MetaTable.TYPE;
            parentContext = MetaControlsTables.TYPE;
        }
        else if( qName.equals("marginals") ) {
            Marginal m = new Marginal( currentObject );
            ((Target)currentObject).setMarginal(m);
            currentObject = m;
            currentContext = Marginal.TYPE;
            parentContext = Target.TYPE;
        }
        else if( qName.equals("constraint") ) {
            int id = Integer.parseInt( attributes.getValue( "id" ) );
            Constraint c = new Constraint( currentObject, id );
            ((Marginal)currentObject).addConstraint( c, id );
            currentObject = c;
            currentContext = Constraint.TYPE;
            parentContext = Marginal.TYPE;
        }

        showData ("<"+qName);
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength (); i++) {
                showData (" ");
                showData (attributes.getQName(i)+"=\""+attributes.getValue (i)+"\"");
            }
        }
        showData (">");
        
    }
    

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch,start,length);
        showData (tempVal);
    }

    
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if( qName.equalsIgnoreCase("target") ) {
            //add it to the list
            targetList.add( currentObject );
            currentObject = ((Target)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("database") ) {
            currentObject = ((Database)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("pumsdata") ) {
            currentObject = ((PumsData)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("marginals") ) {
            currentObject = ((Marginal)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("constraint") ) {
            currentObject = ((Constraint)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("mazControlsTable") ) {
            currentObject = ((MazControlsTable)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("tazControlsTable") ) {
            currentObject = ((TazControlsTable)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("metaControlsTables") ) {
            currentObject = ((MetaControlsTables)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("mazTable") ) {
            currentObject = ((MazTable)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("tazTable") ) {
            currentObject = ((TazTable)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("metaTable") ) {
            currentObject = ((MetaTable)currentObject).getParentObject();
            currentContext = parentContext;
        }
        else if( qName.equalsIgnoreCase("table_name") && currentContext.equals( MazTable.TYPE ) && parentContext.equals( MazControlsTable.TYPE ) ) {
        	handleMazControlsTableNameTag( (MazTable)currentObject );
        }
        else if( qName.equalsIgnoreCase("id_field_name") && currentContext.equals( MazTable.TYPE ) && parentContext.equals( MazControlsTable.TYPE ) ) {
        	handleMazControlsTableIdTag( (MazTable)currentObject );
        }
        else if( qName.equalsIgnoreCase("aggregation_level") && currentContext.equals( MazTable.TYPE ) && parentContext.equals( MazControlsTable.TYPE ) ) {
        	handleMazControlsTableAggregationTag( (MazTable)currentObject );
        }
        else if( qName.equalsIgnoreCase("table_name") && currentContext.equals( TazTable.TYPE ) && parentContext.equals( TazControlsTable.TYPE ) ) {
        	handleTazControlsTableNameTag( (TazTable)currentObject );
        }
        else if( qName.equalsIgnoreCase("id_field_name") && currentContext.equals( TazTable.TYPE ) && parentContext.equals( TazControlsTable.TYPE ) ) {
        	handleTazControlsTableIdTag( (TazTable)currentObject );
        }
        else if( qName.equalsIgnoreCase("aggregation_level") && currentContext.equals( TazTable.TYPE ) && parentContext.equals( TazControlsTable.TYPE ) ) {
        	handleTazControlsTableAggregationTag( (TazTable)currentObject );
        }
        else if( qName.equalsIgnoreCase("table_name") && currentContext.equals( MetaTable.TYPE ) && parentContext.equals( MetaControlsTables.TYPE ) ) {
        	handleMetaControlsTablesNameTag( (MetaTable)currentObject );
        }
        else if( qName.equalsIgnoreCase("id_field_name") && currentContext.equals( MetaTable.TYPE ) && parentContext.equals( MetaControlsTables.TYPE ) ) {
        	handleMetaControlsTablesIdTag( (MetaTable)currentObject );
        }
        else if( qName.equalsIgnoreCase("aggregation_level") && currentContext.equals( MetaTable.TYPE ) && parentContext.equals( MetaControlsTables.TYPE ) ) {
        	handleMetaControlsTablesAggregationTag( (MetaTable)currentObject );
        }
        else if( qName.equalsIgnoreCase("type") && currentContext.equals( Database.TYPE ) ) {
            handleDatabaseServerTypeTag( (Database)currentObject );
        }
        else if( qName.equalsIgnoreCase("server") && currentContext.equals( Database.TYPE ) ) {
            handleDatabaseServerTag( (Database)currentObject );
        }
        else if( qName.equalsIgnoreCase("user") && currentContext.equals( Database.TYPE ) ) {
            handleDatabaseUserTag( (Database)currentObject );
        }
        else if( qName.equalsIgnoreCase("password") && currentContext.equals( Database.TYPE ) ) {
            handleDatabasePasswordTag( (Database)currentObject );
        }
        else if( qName.equalsIgnoreCase("dbName") && currentContext.equals( Database.TYPE ) ) {
            handleDatabaseDbNameTag( (Database)currentObject );
        }
        //Wu added for adjustment to fit SANDAG database
        else if( qName.equalsIgnoreCase("lu_version") && currentContext.equals( Database.TYPE ) ) {
            handleDatabaseLuVersionTag( (Database)currentObject );
        }
        else if( qName.equalsIgnoreCase("year") && currentContext.equals( Database.TYPE ) ) {
            handleDatabaseYearTag( (Database)currentObject );
        }
        else if( qName.equalsIgnoreCase("pumafieldname") && currentContext.equals( PumsData.TYPE ) ) {
            handlePumsDataPumaFieldNameTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("metafieldname") && currentContext.equals( PumsData.TYPE ) ) {
            handlePumsDataMetaFieldNameTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("tazfieldname") && currentContext.equals( PumsData.TYPE ) ) {
            handlePumsDataTazFieldNameTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("mazfieldname") && currentContext.equals( PumsData.TYPE ) ) {
            handlePumsDataMazFieldNameTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("hhtable") && currentContext.equals( PumsData.TYPE ) ) {
            handlePumsDataHhTableTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("perstable") && currentContext.equals( PumsData.TYPE ) ) {
            handlePumsDataPersTableTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("maxExpansionFactor") && currentContext.equals( PumsData.TYPE ) ) {
            handlePumsDataMaxExpansionFactorTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("idField") && currentContext.equals( PumsData.TYPE ) ) {
        	handlePumsDataIdFieldTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("pumsHhIdField") && currentContext.equals( PumsData.TYPE ) ) {
        	handlePumsDataPumsHhIdFieldTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("weightField") && currentContext.equals( PumsData.TYPE ) ) {
        	handlePumsDataWeightFieldTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("tazPromotionField") && currentContext.equals( PumsData.TYPE ) ) {
        	handlePumsDataTazPromotionFieldTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("tazPromotionFactor") && currentContext.equals( PumsData.TYPE ) ) {
        	handlePumsDataTazPromotionFactorTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("pumshhtable") && currentContext.equals( PumsData.TYPE ) ) {
            handlePumsDataPumsHhTableTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("pumsperstable") && currentContext.equals( PumsData.TYPE ) ) {
            handlePumsDataPumsPersTableTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("outputhhattributes") && currentContext.equals( PumsData.TYPE ) ) {
            handleOutputHhAttributesTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("outputpersattributes") && currentContext.equals( PumsData.TYPE ) ) {
            handleOutputPersAttributesTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("synpopoutputhhtablename") && currentContext.equals( PumsData.TYPE ) ) {
            handleSynpopOutputHhTableNameTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("synpopoutputperstablename") && currentContext.equals( PumsData.TYPE ) ) {
        	handleSynpopOutputPersTableNameTag( (PumsData)currentObject );
        }
        else if( qName.equalsIgnoreCase("id") && currentContext.equals( Marginal.TYPE ) ) {
            handleMarginalIdTag( (Marginal)currentObject );
        }
        else if( qName.equalsIgnoreCase("description") && currentContext.equals( Marginal.TYPE ) ) {
            handleMarginalDescriptionTag( (Marginal)currentObject );
        }
        else if( qName.equalsIgnoreCase("groupQuartersControl") && currentContext.equals( Marginal.TYPE ) ) {
            handleMarginalGroupQuartersControlTag( (Marginal)currentObject );
        }
        else if( qName.equalsIgnoreCase("majorGroupQuartersIndicatorField") && currentContext.equals( Marginal.TYPE ) ) {
            handleMarginalMajorGroupQuartersIndicatorFieldTag( (Marginal)currentObject );
        }
        else if( qName.equalsIgnoreCase("otherGroupQuartersIndicatorField") && currentContext.equals( Marginal.TYPE ) ) {
            handleMarginalOtherGroupQuartersIndicatorFieldTag( (Marginal)currentObject );
        }
        else if( qName.equalsIgnoreCase("table") && currentContext.equals( Marginal.TYPE ) ) {
            handleMarginalTableTag( (Marginal)currentObject );
        }
        else if( qName.equalsIgnoreCase("controlType") && currentContext.equals( Marginal.TYPE ) ) {
            handleMarginalControlTypeTag( (Marginal)currentObject );
        }
        else if( qName.equalsIgnoreCase("geographyType") && currentContext.equals( Marginal.TYPE ) ) {
            handleMarginalGeographyTypeTag( (Marginal)currentObject );
        }
        else if( qName.equalsIgnoreCase("totalHouseholdsControl") && currentContext.equals( Marginal.TYPE ) ) {
        	handleMarginalIsTotalHouseholdsControlTypeTag( (Marginal)currentObject );
        }
        else if( qName.equalsIgnoreCase("importance") && currentContext.equals( Constraint.TYPE ) && parentContext.equals( Marginal.TYPE ) ) {
            handleMarginalConstraintImportanceTag( (Constraint)currentObject );
        }
        else if( qName.equalsIgnoreCase("field") && currentContext.equals( Constraint.TYPE ) && parentContext.equals( Marginal.TYPE ) ) {
            handleMarginalConstraintFieldTag( (Constraint)currentObject );
        }
        else if( qName.equalsIgnoreCase("controlField") && currentContext.equals( Constraint.TYPE ) && parentContext.equals( Marginal.TYPE ) ) {
            handleMarginalConstraintControlFieldTag( (Constraint)currentObject );
        }
        else if( qName.equalsIgnoreCase("type") && currentContext.equals( Constraint.TYPE ) && parentContext.equals( Marginal.TYPE ) ) {
            handleMarginalConstraintTypeTag( (Constraint)currentObject );
        }
        else if( qName.equalsIgnoreCase("value") && currentContext.equals( Constraint.TYPE ) && parentContext.equals( Marginal.TYPE ) ) {
            handleMarginalConstraintValueTag( (Constraint)currentObject );
        }
        else if( qName.equalsIgnoreCase("lo_type") && currentContext.equals( Constraint.TYPE ) && parentContext.equals( Marginal.TYPE ) ) {
            handleMarginalConstraintLoTypeTag( (Constraint)currentObject );
        }
        else if( qName.equalsIgnoreCase("lo_value") && currentContext.equals( Constraint.TYPE ) && parentContext.equals( Marginal.TYPE ) ) {
            handleMarginalConstraintLoValueTag( (Constraint)currentObject );
        }
        else if( qName.equalsIgnoreCase("hi_type") && currentContext.equals( Constraint.TYPE ) && parentContext.equals( Marginal.TYPE ) ) {
            handleMarginalConstraintHiTypeTag( (Constraint)currentObject );
        }
        else if( qName.equalsIgnoreCase("hi_value") && currentContext.equals( Constraint.TYPE ) && parentContext.equals( Marginal.TYPE ) ) {
            handleMarginalConstraintHiValueTag( (Constraint)currentObject );
        }
        showData ("</"+qName+">");

    }

    
    
    //===========================================================
    // Helpers Methods
    //===========================================================

    private void handlePumsDataPumaFieldNameTag( PumsData pumsdata ) {
    	pumsdata.setPumaFieldName( tempVal );
    }
    
    private void handlePumsDataMetaFieldNameTag( PumsData pumsdata ) {
    	pumsdata.setMetaFieldName( tempVal );
    }
    
    private void handlePumsDataTazFieldNameTag( PumsData pumsdata ) {
    	pumsdata.setTazFieldName( tempVal );
    }
    
    private void handlePumsDataMazFieldNameTag( PumsData pumsdata ) {
    	pumsdata.setMazFieldName( tempVal );
    }
    
    private void handlePumsDataHhTableTag( PumsData pumsdata ) {
    	pumsdata.setHhTableName( tempVal );
    }
    
    private void handlePumsDataPersTableTag( PumsData pumsdata ) {
    	pumsdata.setPersTableName( tempVal );
    }
    
    private void handlePumsDataMaxExpansionFactorTag( PumsData pumsdata ) {
    	pumsdata.setMaxExpansionFactor( tempVal );
    }
    
    private void handlePumsDataPumsHhTableTag( PumsData pumsdata ) {
    	pumsdata.setPumsHhTableName( tempVal );
    }
    
    private void handlePumsDataPumsPersTableTag( PumsData pumsdata ) {
    	pumsdata.setPumsPersTableName( tempVal );
    }
    
    private void handlePumsDataIdFieldTag( PumsData pumsdata ) {
    	pumsdata.setIdFieldName( tempVal );
    }
    
    private void handlePumsDataPumsHhIdFieldTag( PumsData pumsdata ) {
    	pumsdata.setPumsHhIdFieldName( tempVal );
    }
    
    private void handlePumsDataWeightFieldTag( PumsData pumsdata ) {
    	pumsdata.setWeightFieldName( tempVal );
    }
    
    private void handlePumsDataTazPromotionFieldTag( PumsData pumsdata ) {
    	pumsdata.setTazPromotionFieldName( tempVal );
    }
    
    private void handlePumsDataTazPromotionFactorTag( PumsData pumsdata ) {
    	pumsdata.setTazPromotionFactor( tempVal );
    }
    
    private void handleOutputHhAttributesTag( PumsData pumsdata ) {
    	pumsdata.setOutputHhAttributes( tempVal );
    }

    private void handleSynpopOutputHhTableNameTag( PumsData pumsdata ) {
    	pumsdata.setSynpopOutputHhTableName( tempVal );
    }

    private void handleSynpopOutputPersTableNameTag( PumsData pumsdata ) {
    	pumsdata.setSynpopOutputPersTableName( tempVal );
    }

    private void handleOutputPersAttributesTag( PumsData pumsdata ) {
    	pumsdata.setOutputPersAttributes( tempVal );
    }

    private void handleMazControlsTableNameTag( MazTable controlsTable ) {
    	controlsTable.setTableName( tempVal );
    }
    
    private void handleMazControlsTableIdTag( MazTable controlsTable ) {
    	controlsTable.setIdFieldName( tempVal );
    }
    
    private void handleMazControlsTableAggregationTag( MazTable controlsTable ) {
    	controlsTable.setAggregationLevel( tempVal );
    }
    
    private void handleTazControlsTableNameTag( TazTable controlsTable ) {
    	controlsTable.setTableName( tempVal );
    }
    
    private void handleTazControlsTableIdTag( TazTable controlsTable ) {
    	controlsTable.setIdFieldName( tempVal );
    }
    
    private void handleTazControlsTableAggregationTag( TazTable controlsTable ) {
    	controlsTable.setAggregationLevel( tempVal );
    }
    
    private void handleMetaControlsTablesNameTag( MetaTable controlsTable ) {
    	controlsTable.setTableName( tempVal );
    }
    
    private void handleMetaControlsTablesIdTag( MetaTable controlsTable ) {
    	controlsTable.setIdFieldName( tempVal );
    }
    
    private void handleMetaControlsTablesAggregationTag( MetaTable controlsTable ) {
    	controlsTable.setAggregationLevel( tempVal );
    }
    
    private void handleDatabaseServerTag( Database db ) {
        db.setServerAddress( tempVal );
    }

    private void handleDatabaseServerTypeTag( Database db ) {
        db.setServerType( tempVal );
    }

    private void handleDatabaseUserTag( Database db ) {
        db.setUser( tempVal );
    }

    private void handleDatabasePasswordTag( Database db ) {
        db.setPassword( tempVal );
    }

    private void handleDatabaseDbNameTag( Database db ) {
        db.setDbName( tempVal );
    }
    
    //Wu added for adjustment to fit SANDAG database 
    private void handleDatabaseLuVersionTag( Database db ) {
        db.setLu_version( tempIntVal );
    }

    //Wu added for adjustment to fit SANDAG database 
    private void handleDatabaseYearTag( Database db ) {
        db.setYear( tempIntVal );
    }

    private void handleMarginalTableTag( Marginal m ) {
        m.setTable( tempVal );
    }

    private void handleMarginalDescriptionTag( Marginal m ) {
        m.setDescription( tempVal );
    }

    private void handleMarginalIdTag( Marginal m ) {
        m.setId( tempVal );
    }

    private void handleMarginalGroupQuartersControlTag( Marginal m ) {
        m.setGroupQuartersControl( tempVal );
    }

    private void handleMarginalMajorGroupQuartersIndicatorFieldTag( Marginal m ) {
        m.setMajorGroupQuartersIndicatorField( tempVal );
    }

    private void handleMarginalOtherGroupQuartersIndicatorFieldTag( Marginal m ) {
        m.setOtherGroupQuartersIndicatorField( tempVal );
    }

    private void handleMarginalGeographyTypeTag( Marginal m ) {
        m.setGeographyType( tempVal );
    }

    private void handleMarginalControlTypeTag( Marginal m ) {
        m.setControlType( tempVal );
    }

    private void handleMarginalIsTotalHouseholdsControlTypeTag( Marginal m ) {
        m.setIsTotalHouseldsControl( tempVal );
    }

    private void handleMarginalConstraintFieldTag( Constraint c ) {
        c.setField( tempVal );
    }

    private void handleMarginalConstraintImportanceTag( Constraint c ) {
        c.setImportance( tempVal );
    }

    private void handleMarginalConstraintControlFieldTag( Constraint c ) {
        c.setControlField( tempVal );
    }

    private void handleMarginalConstraintTypeTag( Constraint c ) {
        c.setIntervalType( tempVal );
    }

    private void handleMarginalConstraintValueTag( Constraint c ) {
        c.setValue( tempVal );
    }

    private void handleMarginalConstraintLoTypeTag( Constraint c ) {
        c.setLoType( tempVal );
    }

    private void handleMarginalConstraintLoValueTag( Constraint c ) {
        c.setLoValue( tempVal );
    }

    private void handleMarginalConstraintHiTypeTag( Constraint c ) {
        c.setHiType( tempVal );
    }

    private void handleMarginalConstraintHiValueTag( Object parent ) {
        ((Constraint)parent).setHiValue( tempVal );
    }

    
    
    // Wrap I/O exceptions in SAX exceptions, to
    // suit handler signature requirements
    private void showData (String s)
    throws SAXException
    {
//        try {
//            out.write (s);
//            out.flush ();
//        } catch (IOException e) {
//            throw new SAXException ("I/O error", e);
//        }
    }

    // Start a new line
    private void newLine ()
    throws SAXException
    {
//        String lineEnd =  System.getProperty("line.separator");
//        try {
//            out.write (lineEnd);
//        } catch (IOException e) {
//            throw new SAXException ("I/O error", e);
//        }
    }

    
    public Marginal[] getControlSetArray() {
        
        Marginal[] array = new Marginal[targetList.size()];
        
        for ( Object target : targetList ) {
            Marginal m = ((Target)target).getMarginal();
            int index = m.getId();
            array[index] = m;
        }
        
        return array;
    }

    
    public Balance getBalanceObject() {
        return balance;
    }
    
}
