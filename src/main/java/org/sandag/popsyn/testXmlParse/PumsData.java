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

import java.util.StringTokenizer;

public class PumsData
{

    public static final String TYPE = "Incidence";
    
    private Object parentObject;
    
    private String pumaFieldName;
    private String metaFieldName;
    private String tazFieldName;
    private String mazFieldName;
    private String hhTableName;
    private String persTableName;
    private String pumsHhTableName;
    private String pumsPersTableName;
    private String synpopOutputHhTableName;
    private String synpopOutputPersTableName;
    private String idFieldName;
    private String pumsHhIdFieldName;
    private String weightFieldName;
    private String tazPromotionFieldName;
    private String tazPromotionFactor;
    private String[] outputHhAttribues;
    private String[] outputPersAttribues;
    private int maxExpansionFactor;
    
    public PumsData ( Object parent ) {
        parentObject = parent;
    }

        
    public Object getParentObject() {
        return parentObject;
    }
      
    public void setPumaFieldName ( String name ) {    
        this.pumaFieldName = name;
    }
      
    public void setMetaFieldName ( String name ) {    
        this.metaFieldName = name;
    }
      
    public void setTazFieldName ( String name ) {    
        this.tazFieldName = name;
    }
      
    public void setMazFieldName ( String name ) {    
        this.mazFieldName = name;
    }
      
    public void setHhTableName ( String tableName ) {    
        this.hhTableName = tableName;
    }
      
    public void setPersTableName ( String tableName ) {    
        this.persTableName = tableName;
    }
      
    public void setPumsHhTableName ( String tableName ) {    
        this.pumsHhTableName = tableName;
    }
      
    public void setPumsPersTableName ( String tableName ) {    
        this.pumsPersTableName = tableName;
    }
    
    public void setMaxExpansionFactor ( String maxExpansionFactor ) {    
        this.maxExpansionFactor = Integer.parseInt(maxExpansionFactor);
    }
      
    public void setSynpopOutputHhTableName ( String tableName ) {    
        this.synpopOutputHhTableName = tableName;
    }
      
    public void setSynpopOutputPersTableName ( String tableName ) {    
        this.synpopOutputPersTableName = tableName;
    }
      
    public void setIdFieldName ( String idFieldName ) {    
        this.idFieldName = idFieldName;
    }
      
    public void setPumsHhIdFieldName ( String idFieldName ) {    
        this.pumsHhIdFieldName = idFieldName;
    }
      
    public void setWeightFieldName ( String weightFieldName ) {    
        this.weightFieldName = weightFieldName;
    }

    public void setTazPromotionFieldName ( String tazPromotionFieldName ) {    
        this.tazPromotionFieldName = tazPromotionFieldName;
    }

    public void setTazPromotionFactor ( String tazPromotionFactor ) {    
        this.tazPromotionFactor = tazPromotionFactor;
    }

    public void setOutputHhAttributes( String attributesString ) {
        StringTokenizer st = new StringTokenizer ( attributesString, "," );
        int numValues = st.countTokens();
        outputHhAttribues = new String[numValues];
        for ( int i=0; i < numValues; i++ )
            outputHhAttribues[i] = st.nextToken().trim();
    }
    
    public void setOutputPersAttributes( String attributesString ) {
        StringTokenizer st = new StringTokenizer ( attributesString, "," );
        int numValues = st.countTokens();
        outputPersAttribues = new String[numValues];
        for ( int i=0; i < numValues; i++ )
        	outputPersAttribues[i] = st.nextToken().trim();
    }
    
    public String getPumaFieldName() {
        return pumaFieldName;
    }

    public String getMetaFieldName() {
        return metaFieldName;
    }

    public String getTazFieldName() {
        return tazFieldName;
    }

    public String getMazFieldName() {
        return mazFieldName;
    }

    public String getHhTableName() {
        return hhTableName;
    }

    public String getPersTableName() {
        return persTableName;
    }

    public String getPumsHhTableName() {
        return pumsHhTableName;
    }

    public String getPumsPersTableName() {
        return pumsPersTableName;
    }
    
    public int getMaxExpansionFactor() {    
        return maxExpansionFactor;
    }

    public String getSynpopOutputHhTableName() {
        return synpopOutputHhTableName;
    }

    public String getSynpopOutputPersTableName() {
        return synpopOutputPersTableName;
    }

    public String getIdFieldName() {
        return idFieldName;
    }

    public String getPumsHhIdFieldName() {
        return pumsHhIdFieldName;
    }

    public String getWeightFieldName() {
        return weightFieldName;
    }

    public String getTazPromotionFieldName() {
        return tazPromotionFieldName;
    }

    public String getTazPromotionFactor() {
        return tazPromotionFactor;
    }

    public String[] getOutputHhAttributes() {
    	return outputHhAttribues;
    }
    
    public String[] getOutputPersAttributes() {
    	return outputPersAttribues;
    }
    
    
    public String getType() {
        return TYPE;
    }
    
}
