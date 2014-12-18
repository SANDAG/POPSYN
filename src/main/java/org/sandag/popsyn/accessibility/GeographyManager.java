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

package org.sandag.popsyn.accessibility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.server.protocol.JPPFTask;
import org.jppf.task.storage.DataProvider;
import org.jppf.task.storage.MemoryMapDataProvider;

import org.sandag.popsyn.procedures.Integerizer;
import org.sandag.popsyn.procedures.ListBalancer;
import org.sandag.popsyn.sql.ListBalancingSqlHelper;
import org.sandag.popsyn.testXmlParse.Balance;
import org.sandag.popsyn.testXmlParse.Constraint;
import org.sandag.popsyn.testXmlParse.Database;
import org.sandag.popsyn.testXmlParse.Marginal;
import org.sandag.popsyn.testXmlParse.PumsData;
import org.sandag.popsyn.testXmlParse.TargetsSAXParser;

import com.pb.common.util.IndexSort;


public class GeographyManager implements Serializable
{
    
	private static final long versionNumber = 1L;	
	private static final long serialVersionUID = versionNumber;
	
    private transient Logger logger = Logger.getLogger(GeographyManager.class);

	public static final GeographyManager INSTANCE = new GeographyManager();
	
    
    private int maxMaz = 0;
    private int maxTaz = 0;
    private int maxMeta = 0;
    private int maxPuma = 0;
        
    // given an MAZ, get the associated TAZ, Meta zone, or puma
    private int[] mazTazValues;
    private int[] mazMetaValues;
    private int[] mazPumaValues;

    // unique values of MAZ, TAZ, Meta zone, and pumas
    private int[] mazValues;
    private int[] tazValues;
    private int[] metaValues;
    private int[] pumaValues;
    
    // given a value, e.g. TAZ, get the index value into the tazValues array.  tazValues[tazIndex[TAZ]] = TAZ
    private int[] mazIndex;
    private int[] tazIndex;
    private int[] metaIndex;
    private int[] pumaIndex;
    
    private int[] tazMetaCorresp;
    private int[] pumaMetaCorresp;
    
    private List<Integer>[] tazMazsList;
    private List<Integer>[] pumaTazsList;
    private List<Integer>[] metaTazsList;

    private boolean isInitialized = false;

    
    private GeographyManager() {
    }

    
	public static GeographyManager getInstance() {
		return INSTANCE;
	}
	
    

    public synchronized void createGeogCorrespondence( String dbType, String dbName, String dbUser, String dbPwd, String dbAddress, String mazTableName,
    		String metaFieldName, String pumaFieldName, String tazFieldName, String mazFieldName ) {

        if ( isInitialized )
        	return;

    	
    	// get the values for the MAZ, TAZ, and META fields
		ListBalancingSqlHelper sqlHelper = new ListBalancingSqlHelper( dbType, dbName, dbUser, dbPwd, dbAddress );
        int[][] values = sqlHelper.submitGetTableFieldsQuery( mazTableName, mazFieldName, new String[]{ mazFieldName, tazFieldName, metaFieldName, pumaFieldName } );

        // allocate separate arrays to hold the geographic field values
        mazValues = new int[values.length];
        mazTazValues = new int[values.length];
        mazMetaValues = new int[values.length];
        mazPumaValues = new int[values.length];
        
        // get the maximum MAZ, TAZ, META, and PUMA geography values - used for dimensioning indexing arrays on geographic fields        
        for ( int i=0; i < mazValues.length; i++ ) {
        	
        	int maz = values[i][0];
        	int taz = values[i][1];
        	int meta = values[i][2];
        	int puma = values[i][3];
        	

        	mazValues[i] = maz;
        	mazTazValues[i] = taz;
        	mazMetaValues[i] = meta;
        	mazPumaValues[i] = puma;
        	
        }
            

        ArrayList<Integer> tazValuesList = new ArrayList<Integer>();
        ArrayList<Integer> metaValuesList = new ArrayList<Integer>();
        ArrayList<Integer> pumaValuesList = new ArrayList<Integer>();

                
        // get the maximum MAZ, TAZ, META, and PUMA geography values - used for dimensioning indexing arrays on geographic fields        
        for ( int i=0; i < mazValues.length; i++ ) {
        	
        	int maz = mazValues[i];
        	int taz = mazTazValues[i];
        	int meta = mazMetaValues[i];
        	int puma = mazPumaValues[i];
        	

        	if ( maz > maxMaz )
        		maxMaz = maz; 
        	if ( taz > maxTaz )
        		maxTaz = taz; 
        	if ( meta > maxMeta )
        		maxMeta = meta;
        	if ( puma > maxPuma )
        		maxPuma = puma;
        	
        	if ( ! tazValuesList.contains( taz ) )
        		tazValuesList.add( taz );
        	
        	if ( ! metaValuesList.contains( meta ) )
        		metaValuesList.add( meta );
        	
        	if ( ! pumaValuesList.contains( puma ) )
        		pumaValuesList.add( puma );
        	
        }

        
        // create array of unique taz values
        tazValues = new int[tazValuesList.size()];
        for ( int i=0; i < tazValues.length; i++ )
      		tazValues[i] = tazValuesList.get( i );
        
        // create array of unique meta zone values
        metaValues = new int[metaValuesList.size()];
        for ( int i=0; i < metaValues.length; i++ )
        	metaValues[i] = metaValuesList.get( i );
        
        // create array of unique puma values
        pumaValues = new int[pumaValuesList.size()];
        for ( int i=0; i < pumaValues.length; i++ )
        	pumaValues[i] = pumaValuesList.get( i );
        
        
        // Allocate space for the mazIndex array. This array holds the 0-based array index for the mazValues array.
        // This relationship holds:  MAZ == mazValues[mazIndex[MAZ]].
        mazIndex = new int[maxMaz+1];
        Arrays.fill( mazIndex, -1 );
        
        // Allocate space for the tazIndex array. This array holds the 0-based array index for the tazValues array.
        // This relationship holds:  TAZ == tazValues[tazIndex[TAZ]].
        tazIndex = new int[maxTaz+1];
        Arrays.fill( tazIndex, -1 );
        
        // Allocate space for the metaIndex array. This array holds the 0-based array index for the metaValues array.
        // This relationship holds:  META == metaValues[metaIndex[META]].
        metaIndex = new int[maxMeta+1];
        Arrays.fill( metaIndex, -1 );
        
        // Allocate space for the tazIndex array. This array holds the 0-based array index for the tazValues array.
        // This relationship holds:  PUMA == pumaValues[pumaIndex[PUMA]].
        pumaIndex = new int[maxPuma+1];
        Arrays.fill( pumaIndex, -1 );
        

        
        // Allocate space for the TAZ to META correspondence array. This array holds the META geog value in which the TAZ is located.
        tazMetaCorresp = new int[maxTaz+1];
        Arrays.fill( tazMetaCorresp, -1 );

        pumaMetaCorresp = new int[maxPuma+1];
        Arrays.fill( pumaMetaCorresp, -1 );


        
        
        // Allocate space for an ArrayLists of MAZs for each TAZ.
        tazMazsList = new ArrayList[maxTaz+1];
        
        // Allocate space for an ArrayList of Meta zones for each PUMA geog.
        pumaTazsList = new ArrayList[maxPuma+1];
        
        // Allocate space for an ArrayList of TAZs for each Meta geog.
        metaTazsList = new ArrayList[maxMeta+1];
        
        
        // populate the correspondence values
        for ( int i=0; i < mazValues.length; i++ ) {
        	
        	int maz = mazValues[i];
        	int taz = mazTazValues[i];
        	int meta = mazMetaValues[i];
        	int puma = mazPumaValues[i];
        	
        	mazIndex[maz] = i;
        	
        	if ( tazIndex[taz] < 0 ) {
        		for ( int k=0; k < tazValues.length; k++ ) {
        			if ( taz == tazValues[k] ) {
        				tazIndex[taz] = k;
        				break;
        			}
        		}
        	}
        	
        	if ( metaIndex[meta] < 0 ) {
        		for ( int k=0; k < metaValues.length; k++ ) {
        			if ( meta == metaValues[k] ) {
        				metaIndex[meta] = k;
        				break;
        			}
        		}
        	}
        	
        	if ( pumaIndex[puma] < 0 ) {
        		for ( int k=0; k < pumaValues.length; k++ ) {
        			if ( puma == pumaValues[k] ) {
        				pumaIndex[puma] = k;
        				break;
        			}
        		}
        	}

        	
            tazMetaCorresp[taz] = meta;
            pumaMetaCorresp[puma] = meta;

            
        	if ( tazMazsList[taz] == null ) {
        		tazMazsList[taz] = new ArrayList<Integer>();
        	}
    		tazMazsList[taz].add( maz );
    		
    		
        	if ( pumaTazsList[puma] == null ) {
        		pumaTazsList[puma] = new ArrayList<Integer>();
        	}
        	if ( ! pumaTazsList[puma].contains(taz) )
        		pumaTazsList[puma].add( taz );

        	
        	if ( metaTazsList[meta] == null ) {
        		metaTazsList[meta] = new ArrayList<Integer>();
        	}
        	if ( ! metaTazsList[meta].contains(taz) )
        		metaTazsList[meta].add( taz );
        }
        
        isInitialized = true;

    }

    

    
    public void createGeogCorrespondenceInitial( int[] mazValues, int[] mazTazValues, int[] mazMetaValues, int[] mazPumaValues ) {

    	this.mazValues = mazValues;
    	this.mazTazValues = mazTazValues;
    	this.mazMetaValues = mazMetaValues;
    	this.mazPumaValues = mazPumaValues;
    	
        ArrayList<Integer> tazValuesList = new ArrayList<Integer>();
        ArrayList<Integer> metaValuesList = new ArrayList<Integer>();
        ArrayList<Integer> pumaValuesList = new ArrayList<Integer>();
        
        
        // get the maximum MAZ, TAZ, META, and PUMA geography values - used for dimensioning indexing arrays on geographic fields        
        for ( int i=0; i < mazValues.length; i++ ) {
        	
        	int maz = mazValues[i];
        	int taz = mazTazValues[i];
        	int meta = mazMetaValues[i];
        	int puma = mazPumaValues[i];
        	

        	if ( maz > maxMaz )
        		maxMaz = maz; 
        	if ( taz > maxTaz )
        		maxTaz = taz; 
        	if ( meta > maxMeta )
        		maxMeta = meta;
        	if ( puma > maxPuma )
        		maxPuma = puma;
        	
        	if ( ! tazValuesList.contains( taz ) )
        		tazValuesList.add( taz );
        	
        	if ( ! metaValuesList.contains( meta ) )
        		metaValuesList.add( meta );
        	
        	if ( ! pumaValuesList.contains( puma ) )
        		pumaValuesList.add( puma );
        	
        }

        
        // create array of unique taz values
        tazValues = new int[tazValuesList.size()];
        for ( int i=0; i < tazValues.length; i++ )
      		tazValues[i] = tazValuesList.get( i );
        
        // create array of unique meta zone values
        metaValues = new int[metaValuesList.size()];
        for ( int i=0; i < metaValues.length; i++ )
        	metaValues[i] = metaValuesList.get( i );
        
        // create array of unique puma values
        pumaValues = new int[pumaValuesList.size()];
        for ( int i=0; i < pumaValues.length; i++ )
        	pumaValues[i] = pumaValuesList.get( i );
        
        
        // Allocate space for the mazIndex array. This array holds the 0-based array index for the mazValues array.
        // This relationship holds:  MAZ == mazValues[mazIndex[MAZ]].
        mazIndex = new int[maxMaz+1];
        Arrays.fill( mazIndex, -1 );
        
        // Allocate space for the tazIndex array. This array holds the 0-based array index for the tazValues array.
        // This relationship holds:  TAZ == tazValues[tazIndex[TAZ]].
        tazIndex = new int[maxTaz+1];
        Arrays.fill( tazIndex, -1 );
        
        // Allocate space for the metaIndex array. This array holds the 0-based array index for the metaValues array.
        // This relationship holds:  META == metaValues[metaIndex[META]].
        metaIndex = new int[maxMeta+1];
        Arrays.fill( metaIndex, -1 );
        
        // Allocate space for the tazIndex array. This array holds the 0-based array index for the tazValues array.
        // This relationship holds:  PUMA == pumaValues[pumaIndex[PUMA]].
        pumaIndex = new int[maxPuma+1];
        Arrays.fill( pumaIndex, -1 );
        

        
        // Allocate space for the TAZ to META correspondence array. This array holds the META geog value in which the TAZ is located.
        tazMetaCorresp = new int[maxTaz+1];
        Arrays.fill( tazMetaCorresp, -1 );

        pumaMetaCorresp = new int[maxPuma+1];
        Arrays.fill( pumaMetaCorresp, -1 );


        
        
        // Allocate space for an ArrayLists of MAZs for each TAZ.
        tazMazsList = new ArrayList[maxTaz+1];
        
        // Allocate space for an ArrayList of Meta zones for each PUMA geog.
        pumaTazsList = new ArrayList[maxPuma+1];
        
        // Allocate space for an ArrayList of TAZs for each Meta geog.
        metaTazsList = new ArrayList[maxMeta+1];
        
        
        // populate the correspondence values
        for ( int i=0; i < mazValues.length; i++ ) {
        	
        	int maz = mazValues[i];
        	int taz = mazTazValues[i];
        	int meta = mazMetaValues[i];
        	int puma = mazPumaValues[i];
        	
        	mazIndex[maz] = i;
        	
        	if ( tazIndex[taz] < 0 ) {
        		for ( int k=0; k < tazValues.length; k++ ) {
        			if ( taz == tazValues[k] ) {
        				tazIndex[taz] = k;
        				break;
        			}
        		}
        	}
        	
        	if ( metaIndex[meta] < 0 ) {
        		for ( int k=0; k < metaValues.length; k++ ) {
        			if ( meta == metaValues[k] ) {
        				metaIndex[meta] = k;
        				break;
        			}
        		}
        	}
        	
        	if ( pumaIndex[puma] < 0 ) {
        		for ( int k=0; k < pumaValues.length; k++ ) {
        			if ( puma == pumaValues[k] ) {
        				pumaIndex[puma] = k;
        				break;
        			}
        		}
        	}

        	
            tazMetaCorresp[taz] = meta;
            pumaMetaCorresp[puma] = meta;

            
        	if ( tazMazsList[taz] == null ) {
        		tazMazsList[taz] = new ArrayList<Integer>();
        	}
    		tazMazsList[taz].add( maz );
    		
    		
        	if ( pumaTazsList[puma] == null ) {
        		pumaTazsList[puma] = new ArrayList<Integer>();
        	}
        	if ( ! pumaTazsList[puma].contains(taz) )
        		pumaTazsList[puma].add( taz );

        	
        	if ( metaTazsList[meta] == null ) {
        		metaTazsList[meta] = new ArrayList<Integer>();
        	}
        	if ( ! metaTazsList[meta].contains(taz) )
        		metaTazsList[meta].add( taz );
        }
        
        isInitialized = true;

    }

    
    
    
    public boolean getIsInitialized() {
    	return isInitialized;    	
    }
    

    public int[] getMazIndices() {
    	return mazIndex;
    }
    
    public int[] getTazIndices() {
    	return tazIndex;
    }
    
    public int[] getPumaIndices() {
    	return pumaIndex;
    }
    
    public int[] getMetaIndices() {
    	return metaIndex;
    }
    
    public int[] getMazValues() {
    	return mazValues;
    }
    
    public int[] getTazValues() {
    	return tazValues;
    }
    
    public int[] getMetaValues() {
    	return metaValues;
    }
    
    public int[] getPumaValues() {
    	return pumaValues;
    }
    
    public List<Integer>[] getTazMazsList() {
    	return tazMazsList;
    }

    public List<Integer>[] getPumaTazsList() {
    	return pumaTazsList;
    }

    public int[] getPumaMetaCorresp() {
    	return pumaMetaCorresp;
    }
    
    public int[] getMazMetaValues() {
    	return mazMetaValues;
    }
    
    public int[] getMazPumaValues() {
    	return mazPumaValues;
    }
    
    public int getMazTazValue( int maz ) {
    	int index = mazIndex[maz];
    	return mazTazValues[index];
    }

    public int getMaxMaz() {
    	return maxMaz;
    }
    
}
