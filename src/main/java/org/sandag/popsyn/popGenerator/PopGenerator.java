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

package org.sandag.popsyn.popGenerator;

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
import java.util.HashMap;
import java.util.List;
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

import org.sandag.popsyn.accessibility.GeographyManager;

import com.pb.common.util.IndexSort;


public class PopGenerator implements Serializable
{
    
	private static final long versionNumber = 1L;
	
	private static final long serialVersionUID = versionNumber;
	
    private transient Logger logger = Logger.getLogger(PopGenerator.class);


    /**
     * The JPPF client, handles all communications with the server.
     * It is recommended to only use one JPPF client per JVM, so it
     * should generally be created and used as a singleton.
     */
    private static JPPFClient jppfClient =  null;

    
    private static final String TEMP_INCIDENCE_TABLE_NAME = "dbo.incidence";
    private static final String TEMP_HHIDS_TABLE_NAME = "dbo.hhIds";
    private static final String SIMPLE_TYPE = "simple";
    private static final String COUNT_TYPE = "count";
    

    private static final int RUN_THIS_PUMA_ONLY = 0;
    private static final int RUN_THIS_TAZ_ONLY = 0;
    private static final int REPORT_TAZ_INDEX = 0;
    private static final int REPORT_MAZ_INDEX = 0;

    private static final boolean USE_JPPF = true;    
    private static final int NUM_THREADS =  10;
    
    private int tazPromotionFactor = -1;
    private int max_expansion_factor;
    private static final int INITIAL_TIMEOUT_IN_SECONDS = 300;
    
    GeographyManager geogManager;

    // metaPumaControls are meta control totals that are aggregates of puma values
    // metaTazControls are meta control totals that are aggregates of taz values
    private int[][] metaControls;
    
    private int[][] pumaControls;
    private int[][] tazControls;
    private int[][] mazControls;
    private String[][] controlSetFieldNames;

	private double[] importanceWeights;

    // tazPromotion allows household records with the specified TAZ to be weighted more heavily for balancing for that TAZ
	private int[][] tazPromotion;
	
	private double[][] pumaInitialWeights;
	private int[][] pumaHhIds;
	private int[][] pumaHhIdIndex;
	private int[][][] pumaIncidenceTables;
	
    private int[] householdBasedControls;

    private int totalHouseholdsTazBalancingIncidenceIndex;    


    public PopGenerator() {
    	
    	if ( USE_JPPF ) {

        	try {
                // create the JPPFClient. This constructor call causes JPPF to read the configuration file
                // and connect with one or multiple JPPF drivers.
                //jppfClient = new JPPFClient( Long.toString( serialVersionUID ) );
                jppfClient = new JPPFClient();
            }
        	catch(Exception e) {
                e.printStackTrace();
            }
    		
    	}

    }

    private void runPopulationGenerator( String xmlFileName ) throws Exception {

		logger.info( "start of runPopulationGenerator: " + "parsing xml file" );
		TargetsSAXParser saxParser = new TargetsSAXParser();
		saxParser.parseConditions( xmlFileName );
		
		Marginal[] controlSetArray = saxParser.getControlSetArray();
		Balance balanceObject = saxParser.getBalanceObject();
		Database db = balanceObject.getDatabase();               
		
		// create a helper object for forming and submitting SQL commands to the database server
		ListBalancingSqlHelper sqlHelper = new ListBalancingSqlHelper( db.getDbType(), db.getDbName(), db.getDbUser(), db.getDbPassword(), db.getDbHost() );
		
		// create the sql table in which household records will be stored
		sqlHelper.createHouseholdIdTable( TEMP_HHIDS_TABLE_NAME, balanceObject.getPumsData().getPumaFieldName(), balanceObject.getPumsData().getMetaFieldName(), balanceObject.getPumsData().getWeightFieldName() );
		
		String mazControlTotalsTableName = balanceObject.getMazControlsTable().getControlsTableName();

		//get max expansion factor
		max_expansion_factor = balanceObject.getPumsData().getMaxExpansionFactor();
				
		
		// read the geographic correspondence information, then get a handle to the geogManager.
		geogManager = GeographyManager.getInstance();
		geogManager.createGeogCorrespondence( db.getDbType(), db.getDbName(), db.getDbUser(), db.getDbPassword(), db.getDbHost(), mazControlTotalsTableName,
				balanceObject.getPumsData().getMetaFieldName(), balanceObject.getPumsData().getPumaFieldName(), balanceObject.getPumsData().getTazFieldName(), balanceObject.getPumsData().getMazFieldName() );

		
        List<Integer>[] tazMazsList = geogManager.getTazMazsList();
        List<Integer>[] pumaTazsList = geogManager.getPumaTazsList();
        int[] mazIndex = geogManager.getMazIndices();
        int[] tazIndex = geogManager.getTazIndices();
        int[] pumaIndex = geogManager.getPumaIndices();
        int[] metaIndex = geogManager.getMetaIndices();
        int[] pumaValues = geogManager.getPumaValues();
        int[] mazMetaValues = geogManager.getMazMetaValues();
        int[] mazPumaValues = geogManager.getMazPumaValues();
        int[] pumaMetaCorresp = geogManager.getPumaMetaCorresp();
        
		createControlSetArrays( sqlHelper, balanceObject, controlSetArray ); 						
		
		        
		long start = System.nanoTime();
		logger.info( "balancing household weights over initial PUMA controls ..." );
		
		
		getIncidenceData( pumaValues, balanceObject, sqlHelper, controlSetArray );		
		
		double[][][] pumaListBalancingResults = runListBalancing( "initial PUMA", pumaValues, pumaIndex, pumaControls );		
		double[][] summedTotalMetaWeightsInitial = sumTotalWeights( pumaValues, pumaMetaCorresp, metaIndex, metaControls, pumaListBalancingResults[3] );
		//dumpPumaBalancingInfo( "outputs/initialPumaBalancing.txt", pumaListBalancingResults[1], pumaControls, pumaListBalancingResults[2], pumaListBalancingResults[0], summedTotalMetaWeightsInitial );
    	if ( RUN_THIS_PUMA_ONLY > 0 ) {		
    		dumpPumaBalancingWeights( "outputs/weightsCompare_" + max_expansion_factor + ".csv", pumaListBalancingResults[1][pumaIndex[RUN_THIS_PUMA_ONLY]], pumaInitialWeights[pumaIndex[RUN_THIS_PUMA_ONLY]] ); 
	        dumpBalancingInfo( "outputs/balancingInfoInitial.txt", pumaIncidenceTables[pumaIndex[RUN_THIS_PUMA_ONLY]], pumaHhIds[pumaIndex[RUN_THIS_PUMA_ONLY]], pumaInitialWeights[pumaIndex[RUN_THIS_PUMA_ONLY]], pumaListBalancingResults[1][pumaIndex[RUN_THIS_PUMA_ONLY]] );		        
    	}        
		int[][] factoredPumaMetaControls = metaControlFactoring( "PUMA", pumaValues, pumaMetaCorresp, metaIndex, pumaListBalancingResults[3] );    	    	
		int[][] finalPumaControls = createFinalBalancingControls( "PUMA", pumaControls, factoredPumaMetaControls );
		int[][] summedTotalControls = sumTotalControls( pumaValues, pumaMetaCorresp, metaIndex, metaControls, finalPumaControls );
		
		
		pumaListBalancingResults = runListBalancing( "final PUMA", pumaValues, pumaIndex, finalPumaControls );
		double[][] pumaFinalRelaxedControls = pumaListBalancingResults[0];
		double[][] pumaFinalWeights = pumaListBalancingResults[1];
		double[][] pumaFinalRelaxationFactors = pumaListBalancingResults[2];
		double[][] summedTotalMetaWeightsFinal = sumTotalWeights( pumaValues, pumaMetaCorresp, metaIndex, metaControls, pumaListBalancingResults[3] );
		//dumpPumaBalancingInfo( "outputs/finalPumaBalancing.txt", pumaFinalWeights, finalPumaControls, pumaFinalRelaxationFactors, pumaFinalRelaxedControls, summedTotalMetaWeightsFinal );		
    	if ( RUN_THIS_PUMA_ONLY > 0 ) {		
		//for ( int p=0; p < pumaValues.length; p++ )
		//	dumpBalancingInfo( "outputs/balancingInfoFinal_" + pumaValues[p] + ".txt", pumaIncidenceTables[pumaIndex[pumaValues[p]]], pumaHhIds[pumaIndex[pumaValues[p]]], pumaInitialWeights[pumaIndex[pumaValues[p]]], pumaListBalancingResults[1][pumaIndex[pumaValues[p]]] );		        
    		dumpBalancingInfo( "outputs/balancingInfoFinal.txt", pumaIncidenceTables[pumaIndex[RUN_THIS_PUMA_ONLY]], pumaHhIds[pumaIndex[RUN_THIS_PUMA_ONLY]], pumaInitialWeights[pumaIndex[RUN_THIS_PUMA_ONLY]], pumaListBalancingResults[1][pumaIndex[RUN_THIS_PUMA_ONLY]] );		        
    	}        
		logger.info( "finished with PUMA balancing" );


    	// create a list of tazs in each puma and an array of TAZ controls for the TAZs in the pumas.
		int[][] pumaTazs = new int[pumaValues.length][];
		int[][][] pumaTazControls = new int[pumaValues.length][][];
		for ( int i=0; i < pumaValues.length; i++ ) {
			int puma = pumaValues[i];
			pumaTazs[i] = new int[pumaTazsList[puma].size()];
			pumaTazControls[i] = new int[pumaTazsList[puma].size()][];
			for ( int k=0; k < pumaTazs[i].length; k++ ) {
				int taz = pumaTazsList[puma].get(k);
				pumaTazs[i][k] = taz;
				pumaTazControls[i][k] = tazControls[tazIndex[taz]];
			}
		}

		

	    logger.info( "Allocating household records to TAZs and MAZs ..." );        

		if ( USE_JPPF ) {
			 
			runTazAllocationMt( "TAZ", pumaValues, tazPromotion, finalPumaControls, pumaTazControls, pumaFinalWeights, pumaFinalRelaxationFactors, pumaTazs, pumaIncidenceTables, pumaInitialWeights, pumaHhIds, sqlHelper );				 
			
		}
		else {
			 
			for ( int p=0; p < pumaValues.length; p++ ) {
				
				int puma = pumaValues[p];
				
		    	// don't need to allocate if no households
		    	if ( finalPumaControls[p][totalHouseholdsTazBalancingIncidenceIndex] == 0 )
		    		continue;
		    	
		    	if ( RUN_THIS_PUMA_ONLY > 0 && puma != RUN_THIS_PUMA_ONLY )
		    		continue;


				int[] finalHouseholdIntegerWeights = doIntegerizing( "PUMA", puma, finalPumaControls[p], pumaIncidenceTables[p], pumaInitialWeights[p], importanceWeights, pumaFinalWeights[p], pumaFinalRelaxationFactors[p], totalHouseholdsTazBalancingIncidenceIndex, RUN_THIS_PUMA_ONLY > 0 && puma == RUN_THIS_PUMA_ONLY );                               
		        int[] finalIntegerWeightTotals = getIntegerWeightTotals( pumaIncidenceTables[p], finalHouseholdIntegerWeights );

		        //dumpPumaBalancingWeights( "outputs/weightsCompare.csv", finalHouseholdIntegerWeights, pumaInitialWeights[p] ); 
		        ReturnData rd = tazAllocation( puma, pumaTazs[p], tazPromotion[p], pumaTazControls[p], finalHouseholdIntegerWeights, pumaIncidenceTables[p], pumaInitialWeights[p], pumaHhIds[p], totalHouseholdsTazBalancingIncidenceIndex );

		        // create a tazFinalWeights array and a tazFinalIds array from the results of TAZ allocation, for use in MAZ allocation
		        // only the household records with a nonzero weight from TAZ allocation will have a nonzero weight and hhid.
		        // MAZ allocation will strip out the zero weight households.
		        int[][] tempIndices = rd.getIndices();
		        int[][] tempWeights = rd.getWeights();
		        int[][] tempIds = rd.getIds();

		        
		    	// create a list of MAZs in each TAZ and an array of MAZ controls for the MAZs in the TAZ.
				int[] tazMazs = new int[pumaTazs[p].length];
				int[][] tazMazControls = new int[pumaTazs[p].length][];
				for ( int t=0; t < pumaTazs[p].length; t++ ) {
					
					if ( pumaTazControls[p][t][totalHouseholdsTazBalancingIncidenceIndex] == 0 )
						continue;

					int taz = pumaTazs[p][t];
			    	if ( RUN_THIS_TAZ_ONLY > 0 && taz != RUN_THIS_TAZ_ONLY )
			    		continue;

			        int[] tazFinalWeights = new int[pumaFinalWeights[p].length];
			        int[] tazFinalIds = new int[pumaFinalWeights[p].length];
					for ( int i=0; i < tempIndices[t].length; i++ ) {
						int index = tempIndices[t][i];
						tazFinalWeights[index] = tempWeights[t][i];
						tazFinalIds[index] = tempIds[t][i];
					}
					
					tazMazs = new int[tazMazsList[taz].size()];
					tazMazControls = new int[tazMazsList[taz].size()][];
					for ( int m=0; m < tazMazs.length; m++ ) {
						int maz = tazMazsList[taz].get(m);
						tazMazs[m] = maz;
						tazMazControls[m] = mazControls[mazIndex[maz]];
					}
			        
					rd = mazAllocation( tazMazs, tazMazControls, tazFinalWeights, pumaIncidenceTables[p], pumaInitialWeights[p], tazFinalIds, totalHouseholdsTazBalancingIncidenceIndex );
			    	
					// make an array of MAZ control totals for MAZs in the TAZ
			        int[] mazMetas = new int[tazMazs.length];
			        int[] mazPumas = new int[tazMazs.length];
					for ( int kk=0; kk < tazMazs.length; kk++ ) {
						int maz = tazMazs[kk];
						int mIndex = mazIndex[maz];
						mazMetas[kk] = mazMetaValues[mIndex];
						mazPumas[kk] = mazPumaValues[mIndex];
					}
					
			        sqlHelper.insertIntoHouseholdIdTable( rd.getWeights(), rd.getIds(), taz, tazMazs, mazPumas, mazMetas, rd.getPumsWts() );

				}
					        
		        logger.info( "done allocating TAZs and MAZs for puma " + puma );

			}
			
		}
				 
		PumsData pumsData = balanceObject.getPumsData();
		String pumsRecordIdFieldName = pumsData.getPumsHhIdFieldName();
		String pumsHhAttributesTableName = pumsData.getPumsHhTableName();
		String pumsPersAttributesTableName = pumsData.getPumsPersTableName();
		String synpopHhOutputTableName = pumsData.getSynpopOutputHhTableName();
		String synpopPersOutputTableName = pumsData.getSynpopOutputPersTableName();
		String[] pumsHhAttributesList = pumsData.getOutputHhAttributes();
		String[] pumsPersAttributesList = pumsData.getOutputPersAttributes();
		
	    sqlHelper.createSyntheticPopulationTables( pumsRecordIdFieldName, synpopHhOutputTableName, pumsHhAttributesTableName, pumsHhAttributesList, synpopPersOutputTableName, pumsPersAttributesTableName, pumsPersAttributesList );
		
		
	}
    

    private int[][]  createFinalBalancingControls( String label, int[][] geogControls, int[][] geogMetaControls ) {
    	
		logger.info( "appending new " + label + " meta controls to original " + label + " controls ..." );

		int[][] finalControls = new int[geogControls.length][geogControls[0].length + geogMetaControls[0].length];
		
        // create a new expanded array of controls by the geog index with met controls added to th end of original controls
		for ( int i=0; i < geogControls.length; i++ ) {

			for ( int m=0; m < geogControls[i].length; m++ )
		    	finalControls[i][m] = geogControls[i][m];
		    
		    for ( int m=0; m < geogMetaControls[i].length; m++ )
		    	finalControls[i][geogControls[i].length + m] = geogMetaControls[i][m];
		}		
		
		return finalControls;
		
    }
    

    private void getIncidenceData( int[] pumaValues, Balance balanceObject, ListBalancingSqlHelper sqlHelper, Marginal[] controlSetArray ) {
    	
		logger.info( "getting incidence table data for pums ..." );

		tazPromotion = new int[pumaValues.length][];
		pumaInitialWeights = new double[pumaValues.length][];
		pumaHhIds = new int[pumaValues.length][];
		pumaHhIdIndex = new int[pumaValues.length][];
		pumaIncidenceTables = new int[pumaValues.length][][];
		
		PumsData pumsData = balanceObject.getPumsData();
		String pumsRecordIdFieldName = pumsData.getIdFieldName();
		String pumsRecordWeightFieldName = pumsData.getWeightFieldName();
		String pumsTazPromotionFieldName = pumsData.getTazPromotionFieldName();
		String pumsTazPromotionFactor = pumsData.getTazPromotionFactor();
		String pumsRecordHhTableName = pumsData.getHhTableName();
		
		if(pumsTazPromotionFieldName==null||pumsTazPromotionFactor==null){
			tazPromotionFactor = 1;
		}else{
			tazPromotionFactor = Integer.parseInt( pumsTazPromotionFactor );
		}
		
		
		// loop over the set of PUMAs for the region
		for ( int i=0; i < pumaValues.length; i++ ) {
			
			int puma = pumaValues[i];
			
			pumaHhIds[i] = sqlHelper.submitGetIdsQuery( pumsRecordIdFieldName, pumsRecordHhTableName, puma );
			
			int maxId = 0;
			for ( int k=0; k < pumaHhIds[i].length; k++ )
				if ( pumaHhIds[i][k] > maxId )
					maxId = pumaHhIds[i][k];
			
			pumaHhIdIndex[i] = new int[maxId+1]; 
			for ( int k=0; k < pumaHhIds[i].length; k++ )
				pumaHhIdIndex[i][pumaHhIds[i][k]] = k;

			if(pumsTazPromotionFieldName==null||pumsTazPromotionFactor==null){
				tazPromotion[i] = new int[pumaHhIds[i].length];
				for ( int k=0; k < pumaHhIds[i].length; k++ )
					tazPromotion[i][k] = 0;
			}else{
				double[] tempArray = sqlHelper.submitGetTableFieldQuery( pumsTazPromotionFieldName, pumsRecordHhTableName, pumsRecordIdFieldName, puma );
				tazPromotion[i] = new int[tempArray.length];
				for ( int k=0; k < tempArray.length; k++ )
					tazPromotion[i][k] = (int)tempArray[k];
			}

			pumaInitialWeights[i] = sqlHelper.submitGetTableFieldQuery( pumsRecordWeightFieldName, pumsRecordHhTableName, pumsRecordIdFieldName, puma );
			pumaIncidenceTables[i] = createIncidenceTablesForPuma( puma, sqlHelper, balanceObject, controlSetArray, pumaHhIdIndex[i] );
		}

    }
    
    
    private double[][][] runListBalancing( String label, int[] geogValues, int[] geogIndices, int[][] geogControls ) {
    	
		double[][] geogRelaxedControls = new double[geogControls.length][];
		double[][] geogFractionalWeights = new double[geogControls.length][];
		double[][] geogRelaxationFactors = new double[geogControls.length][];
		double[][] geogFractionalMetaWeights = new double[geogControls.length][];
		
		long start = System.nanoTime();
		logger.info( "balancing household weights over " + label + " controls ..." );
		
        ExecutorService exec = Executors.newFixedThreadPool( NUM_THREADS );
        ArrayList<Future<List<Object>>> taskList = new ArrayList<Future<List<Object>>>();
		
		// loop over the set of PUMAs for the region
		for ( int i=0; i < geogControls.length; i++ ) {
			
			int geog = geogValues[i];

			ListBalancingTask task = new ListBalancingTask ( (i+1), label + " " + geog, geogIndices[geog], geog, geogControls[i], pumaIncidenceTables[i], RUN_THIS_PUMA_ONLY > 0 && geog == RUN_THIS_PUMA_ONLY );
    		taskList.add( exec.submit(task) );			
			
	    }

		
	    // process the results
        for ( Future<List<Object>> fs : taskList ) {

            try {
                List<Object> resultBundle = fs.get();
                processExecutionResults( resultBundle, geogRelaxedControls, geogFractionalWeights, geogRelaxationFactors, geogFractionalMetaWeights );
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
            catch (ExecutionException e) {
                logger.error("Exception returned in place of result object.", e);
                throw new RuntimeException();
            }
        	finally {
                exec.shutdown();
            }

        } // future

		logger.info( String.format( "%s%.2f%s", "Time for " + label + " balancing for all pumas = ", (( System.nanoTime() - start )/1000000.0), " millisecs." ) );
    	
		double[][][] returnArray = new double[4][][];
		returnArray[0] = geogRelaxedControls;
		returnArray[1] = geogFractionalWeights;
		returnArray[2] = geogRelaxationFactors;
		returnArray[3] = geogFractionalMetaWeights;
		
		return returnArray;
		
    }
    
    
    private void runTazAllocationMt( String label, int[] pumaValues, int[][] tazPromotion, int[][] finalPumaControls, int[][][] pumaTazControls, double[][] pumaFinalWeights, double[][] pumaRelaxationFactors, int[][] pumaTazList, int[][][] pumaIncidenceTable, double[][] pumaInitialWeights, int[][] pumaHhids, ListBalancingSqlHelper sqlHelper ) {
    	
    	
    	try {

	    	DataProvider tazDataProvider = new MemoryMapDataProvider();
	        
	        tazDataProvider.setValue( "geogManager", geogManager );
	        tazDataProvider.setValue( "finalPumaControls", finalPumaControls );
			tazDataProvider.setValue( "tazPromotion", tazPromotion );
			tazDataProvider.setValue( "pumaTazControls", pumaTazControls );
			tazDataProvider.setValue( "pumaFinalWeights", pumaFinalWeights );
			tazDataProvider.setValue( "pumaRelaxationFactors", pumaRelaxationFactors );
			tazDataProvider.setValue( "pumaTazList", pumaTazList );
			tazDataProvider.setValue( "pumaIncidenceTable", pumaIncidenceTable );
			tazDataProvider.setValue( "pumaInitialWeights", pumaInitialWeights );
			tazDataProvider.setValue( "pumaHhids", pumaHhids );
			tazDataProvider.setValue( "totalHouseholdsControlIndex", totalHouseholdsTazBalancingIncidenceIndex );
			tazDataProvider.setValue( "sqlHelper", sqlHelper );
			
	        // create a JPPF job
	        JPPFJob tazJob = new JPPFJob( tazDataProvider );
	
	        // give this job a readable unique id that we can use to monitor and manage it.
	        tazJob.setName( "TAZ Allocation Job" );
	
	        for ( int p=0; p < pumaValues.length; p++ ) {
				
				int puma = pumaValues[p];
				
		    	if ( RUN_THIS_PUMA_ONLY > 0 && puma != RUN_THIS_PUMA_ONLY )
		    		continue;

				if ( finalPumaControls[p][totalHouseholdsTazBalancingIncidenceIndex] == 0 )
		    		continue;
	
	
		        // add a task to the job.
		        TazAllocationTask tazTask = new TazAllocationTask( label, puma, p );
				tazTask.setId( "Taz Allocation Task: puma=" + puma );
				tazJob.addTask( tazTask );	        
		    	
	        }
	        
			MyResultListener myTazResultListener = new MyResultListener( tazJob );
			tazJob.setResultListener( myTazResultListener );
			tazJob.setBlocking( false );

		        
		    jppfClient.submit(tazJob);
		    List<JPPFTask> tazResults = myTazResultListener.waitForResults();
		    
		    // process the results
		    for ( JPPFTask task: tazResults ) {
	
		    	// if the task execution resulted in an exception
		    	Exception e = task.getException();
				if ( e != null ) {
					logger.error( "Exception returned instead of result object for " + task.getId(), e );
					throw new RuntimeException();
				}
				else {
					
			        // process the result here ...
					List<Object> resultBundle = (List<Object>)task.getResult();
					ArrayList<Object> resultList = (ArrayList<Object>)resultBundle.get( 3 );
					for ( int i=0; i < resultList.size(); i++ ) {
						Object[] results = (Object[])resultList.get( i );
				        int[][] weights = (int[][])results[0];
				        int[][] ids = (int[][])results[1];
				        int taz = (Integer)results[2];
				        int[] tazMazs = (int[])results[3];
				        int[] mazPumas = (int[])results[4];
				        int[] mazMetas = (int[])results[5];
				        double[][] pumsWeights = (double[][])results[6];
				        sqlHelper.insertIntoHouseholdIdTable( weights, ids, taz, tazMazs, mazPumas, mazMetas, pumsWeights );
					}
			    }
			    
	        }
        
        }
        catch ( Exception e ) {
			logger.error( "Exception caught in client running JPPF job", e );
			throw new RuntimeException();
        }


        logger.info( "done allocating TAZs and MAZs." );

    }
    
    

    private int[][] metaControlFactoring( String label, int[] geogValues, int[] geogMetaCorresp, int[] metaIndex, double[][] factoredGeogWeights ) {
    	
		// apply simple factoring to summed household fractional weights based on original meta control values relative to summed household fractional weights by meta zone.
		// the resulting factored meta control weights will be new meta controls, to be appended to the original controls, for final balancing.
		logger.info( "calculating new " + label + " meta controls ..." );
		
		int numMetaControls = metaControls[0].length;
		int numControls = factoredGeogWeights[0].length;
		int firstMetaControlIndex = numControls - numMetaControls;
		
        // sum the factored meta weights over meta controls to get total household fractional weights for each meta control
        double[][] totalFactoredMetaWeights = new double[metaControls.length][numMetaControls];
		for ( int i=0; i < geogValues.length; i++ ) {
			// get the actual geographic identifier (puma number, taz number, etc...)
			
			int geog = geogValues[i];
			
	    	// set the value of the meta zone
	    	int meta = geogMetaCorresp[geog];
		    	
	    	// set the value of the meta zone index for the meta zone
	    	int mi = metaIndex[meta];
					        	
	        for ( int m=0; m < numMetaControls; m++ )
	        	totalFactoredMetaWeights[mi][m] += factoredGeogWeights[i][firstMetaControlIndex + m];

		}
		
		
		int[][] newMetaControls = new int[geogValues.length][numMetaControls];
		int[][] totalNewMetaControls = new int[metaControls.length][numMetaControls];

		for ( int i=0; i < geogValues.length; i++ ) {
			
			// get the actual geographic identifier (puma number, taz number, etc...)
			int geog = geogValues[i];
			
	    	// set the value of the meta zone
	    	int meta = geogMetaCorresp[geog];
		    	
	    	// set the value of the meta zone index for the meta zone
	    	int mi = metaIndex[meta];
					        	
		    for ( int m=0; m < metaControls[mi].length; m++ ) {
		        double factor = metaControls[mi][m] / totalFactoredMetaWeights[mi][m];
		        newMetaControls[i][m] = (int)( factoredGeogWeights[i][firstMetaControlIndex + m] * factor + 0.5 );
		        totalNewMetaControls[mi][m] += newMetaControls[i][m]; 
		    }

		}
    
		return newMetaControls;
    }
    
    
    private double[][] sumTotalWeights( int[] geogValues, int[] geogMetaCorresp, int[] metaIndex, int[][] metaControls, double[][] weights ) {
        
        double[][] totalWeights = new double[metaControls.length][];
		for ( int i=0; i < geogValues.length; i++ ) {
			// get the actual geographic identifier (puma number, taz number, etc...)
			
			int geog = geogValues[i];
			
	    	// set the value of the meta zone
	    	int meta = geogMetaCorresp[geog];
		    	
	    	// set the value of the meta zone index for the meta zone
	    	int mi = metaIndex[meta];
				
	    	if ( totalWeights[mi] == null )
	    		totalWeights[mi] = new double[weights[i].length];

	        for ( int m=0; m < weights[i].length; m++ ) {
	        	totalWeights[mi][m] += weights[i][m];
	        }
		}
    	
		return totalWeights;
    }
    
    
    private int[][] sumTotalControls( int[] geogValues, int[] geogMetaCorresp, int[] metaIndex, int[][] metaControls, int[][] controls ) {
        
        int[][] totalControls = new int[metaControls.length][];
		for ( int i=0; i < geogValues.length; i++ ) {
			// get the actual geographic identifier (puma number, taz number, etc...)
			
			int geog = geogValues[i];
			
	    	// set the value of the meta zone
	    	int meta = geogMetaCorresp[geog];
		    	
	    	// set the value of the meta zone index for the meta zone
	    	int mi = metaIndex[meta];
				
	    	if ( totalControls[mi] == null )
	    		totalControls[mi] = new int[controls[i].length];

	        for ( int m=0; m < controls[i].length; m++ ) {
	        	totalControls[mi][m] += controls[i][m];
	        }
		}
    	
		return totalControls;
    }
    

    private ReturnData tazAllocation( int puma, int[] pumaTazs, int[] tazPromotion, int[][] tazControls, int[] finalHouseholdIntegerWeights, int[][] incidenceTable, double[] initialWeights, int[] hhIds, int totalHouseholdsControlIndex ) {    	
        
    	long lbTime = 0;
    	long intTime = 0;
    	long totTime = 0;
    	
    	long startTime = System.currentTimeMillis();
    	
	    // get an array of indices so that TAZs can be processed in order of increasing number of households
	    // the array values are indices into the pumaTazs array, which is a list of TAZs in the PUMA. 
	    int[] sortedIndices = getSortedIndexArray( pumaTazs, tazControls, totalHouseholdsControlIndex );

	    // dimension the returned arrays for the number of MAZs in this TAZ
        int[][] returnIntegerWeights = new int[sortedIndices.length][];
	    int[][] returnPumsRecords = new int[sortedIndices.length][];
	    double[][] returnPumsWeights = new double[sortedIndices.length][];
	    int[][] returnHhIndices = new int[sortedIndices.length][];
	    

	    int[] tempIndices = new int[finalHouseholdIntegerWeights.length];
	    for ( int i=0; i< tempIndices.length; i++ )
	    	tempIndices[i] = i;
	    
        // get the hhids, incidence table, and initial weights for the non-zero weight household records for this TAZ
        int[] nzHhIndices = getNonZeroWeightHouseholdIndices( tempIndices, finalHouseholdIntegerWeights );
        int[][] nzIncidenceTable = getNonZeroWeightIncidenceTableForMaz( nzHhIndices, finalHouseholdIntegerWeights, incidenceTable );
        int[] nzInitialWeights = getNonZeroWeights( nzHhIndices, finalHouseholdIntegerWeights );                
        

        boolean hhsHaveBeenAllocated = false;
        
        // if this was the next to last MAZ, then all the remaining weights get allocated to the last MAZ
        if ( sortedIndices.length == 1 ) {

	    	int m = sortedIndices[0];
	    	int taz = pumaTazs[m];

		    // dimension the returned arrays for the MAZ to the number of final non-zero weights (should be number of total households in MAZ)
	        returnIntegerWeights[m] = new int[nzInitialWeights.length];
	        returnPumsRecords[m] = new int[nzInitialWeights.length];
	        returnPumsWeights[m] = new double[nzInitialWeights.length];
	        returnHhIndices[m] = new int[nzInitialWeights.length];
	        
	    	int mCount = 0;
	        for ( int k=0; k < nzInitialWeights.length; k++ ) {
	            
                // get pums hh record number for this index
                int n = nzHhIndices[k];
                
                // store puma record numbers and weights for each puma record in the maz
                returnIntegerWeights[m][mCount] = nzInitialWeights[k];
                returnPumsRecords[m][mCount] = hhIds[n];
                returnPumsWeights[m][mCount] = initialWeights[n];
                returnHhIndices[m][mCount] = n;
                
                mCount++;
	        }
		        
        }
        else {
        	
    	    // loop over TAZs in increasing order of total number of households in the MAZ
    	    for ( int p=0; p < sortedIndices.length; p++ ) {
    	    	
    	    	int m = sortedIndices[p];
    	    	int taz = pumaTazs[m];
    	    
    	    	if ( tazControls[m][totalHouseholdsControlIndex] == 0 )
    	    		continue;
    	    	
    	    	// if the first taz with non zero households is the last taz, 
    	        if ( p == (sortedIndices.length - 1) && ! hhsHaveBeenAllocated ) {

    			    // dimension the returned arrays for the MAZ to the number of final non-zero weights (should be number of total households in MAZ)
    		        returnIntegerWeights[m] = new int[nzInitialWeights.length];
    		        returnPumsRecords[m] = new int[nzInitialWeights.length];
    		        returnPumsWeights[m] = new double[nzInitialWeights.length];
    		        returnHhIndices[m] = new int[nzInitialWeights.length];
    		        
    		    	int mCount = 0;
        	        int totalIntWeights = 0;
    		        for ( int k=0; k < nzInitialWeights.length; k++ ) {
    		            
    	                // get puma record number for this index
    	                int n = nzHhIndices[k];
    	                
    	                // store puma record numbers and weights for each puma record in the maz
    	                returnIntegerWeights[m][mCount] = nzInitialWeights[k];
    	                returnPumsRecords[m][mCount] = hhIds[n];
    	                returnPumsWeights[m][mCount] = initialWeights[n];
    	                returnHhIndices[m][mCount] = n;

                        totalIntWeights += nzInitialWeights[k];

                        mCount++;
    		        }
    		        
    		        break;

    	        }

    	        // increase the balancing weight if the tazPromotion value is the TAZ being processed
    	        int[] tempWeights = new int[nzInitialWeights.length];
    	        for ( int i=0; i < nzInitialWeights.length; i++ )
    	        	if( taz == tazPromotion[nzHhIndices[i]] )
    	        		tempWeights[i] = nzInitialWeights[i] * tazPromotionFactor;
    	        	else
    	        		tempWeights[i] = nzInitialWeights[i];
    	        		
    	        double[] lbInitialWeights = getNonZeroWeightIntValuesAsDouble( nzHhIndices, tempWeights );
    	        ListBalancer lb = new ListBalancer( lbInitialWeights, importanceWeights, nzIncidenceTable, tazControls[m], totalHouseholdsControlIndex );
    	
    	        // set bounds for weights for MAZ balancing
    	        int[] lbWeights = new int[nzInitialWeights.length];
    	        Arrays.fill( lbWeights, 0 );                
    	        lb.setLbWeights( lbWeights );
    	        lb.setUbWeights( nzInitialWeights );                

    	        if ( taz == REPORT_TAZ_INDEX )
    	        	lb.setDebugControlSet( true );
    	        else
    	        	lb.setDebugControlSet( false );
    	        
    	    	long check = System.currentTimeMillis();
    	        lb.doBalancing();
    	    	lbTime += ( System.currentTimeMillis() - check );

    	    	double[] balancedSolution = lb.getFinalWeights();
    	        double totalWeights = 0;
    	        for (int i=0; i < balancedSolution.length; i++)
    	        	totalWeights += balancedSolution[i]; 
    	        
    	    	check = System.currentTimeMillis();
    	        int[] finalIntegerWeights = doIntegerizing( "TAZ", taz, tazControls[m], nzIncidenceTable, getNonZeroWeightIntValuesAsDouble(nzHhIndices, nzInitialWeights), importanceWeights, balancedSolution, lb.getRelaxationFactors(), totalHouseholdsControlIndex, ( taz == REPORT_TAZ_INDEX ) );
    	    	intTime += ( System.currentTimeMillis() - check );
    	
    	        
    	        int nonZeroCount = 0;
    	        for ( int k=0; k < finalIntegerWeights.length; k++ )
    	            if ( finalIntegerWeights[k] > 0 )
    	                nonZeroCount++;
    	
    	        int[] nonZeroHhIds = new int[nonZeroCount];
    	        int count = 0;
    	        for ( int k=0; k < finalIntegerWeights.length; k++ )
    	            if ( finalIntegerWeights[k] > 0 )
    	            	nonZeroHhIds[count++] = k; 
    	        
    	        
    		    // dimension the returned arrays for the MAZ to the number of final non-zer weights (should be number of total households in MAZ)
    	        returnIntegerWeights[m] = new int[nonZeroCount];
    	        returnPumsRecords[m] = new int[nonZeroCount];
    	        returnPumsWeights[m] = new double[nonZeroCount];
    	        returnHhIndices[m] = new int[nonZeroCount];
    	        
    	        int mCount = 0;
    	        int totalIntWeights = 0;
    	        for ( int kk=0; kk < nonZeroHhIds.length; kk++ ) {
    	            
    	        	int k = nonZeroHhIds[kk];
    	        	
                    // get puma record number for this index
                    int n = nzHhIndices[k];
                    
                    // store puma record numbers and weights for each puma record in the maz
                    returnIntegerWeights[m][mCount] = finalIntegerWeights[k];
                    returnPumsRecords[m][mCount] = hhIds[n];
	                returnPumsWeights[m][mCount] = initialWeights[n];
	                returnHhIndices[m][mCount] = n;

                    // reduce the taz integer weights by the pums record weight for the maz
                    nzInitialWeights[k] = Math.max( (nzInitialWeights[k] - finalIntegerWeights[k]), 0 );
                    
                    totalIntWeights += finalIntegerWeights[k];
                    
                    mCount++;
    	            
    	        }

    	        int totalRemainingWeights = 0;
    	        for ( int k=0; k < nzInitialWeights.length; k++ )
                    totalRemainingWeights += nzInitialWeights[k];
    	        
    	        hhsHaveBeenAllocated = true;
    	        
    	        nzHhIndices = getNonZeroWeightHouseholdIndices( nzHhIndices, nzInitialWeights );
    	        nzIncidenceTable = getNonZeroWeightIncidenceTableForMaz( nzHhIndices, nzInitialWeights, nzIncidenceTable );
    	        nzInitialWeights = getNonZeroWeights( nzHhIndices, nzInitialWeights );        
    	        
    	        
    	        // for the next to last MAZ, when more than one MAZ has HHs, the remaining weights get allocated to the last MAZ
    	        if ( p == (sortedIndices.length - 2) && hhsHaveBeenAllocated ) {

    		    	m = sortedIndices[p+1];
    		    	taz = pumaTazs[m];

    			    // dimension the returned arrays for the MAZ to the number of final non-zero weights (should be number of total households in MAZ)
    		        returnIntegerWeights[m] = new int[nzInitialWeights.length];
    		        returnPumsRecords[m] = new int[nzInitialWeights.length];
        	        returnPumsWeights[m] = new double[nzInitialWeights.length];
    		        returnHhIndices[m] = new int[nzInitialWeights.length];
    		        
    		    	mCount = 0;
    		        for ( int k=0; k < nzInitialWeights.length; k++ ) {
    		            
    	                // get puma record number for this index
    	                int n = nzHhIndices[k];
    	                
    	                // store puma record numbers and weights for each puma record in the maz
    	                returnIntegerWeights[m][mCount] = nzInitialWeights[k];
    	                returnPumsRecords[m][mCount] = hhIds[n];
    	                returnPumsWeights[m][mCount] = initialWeights[n];
    	                returnHhIndices[m][mCount] = n;

    	                mCount++;
    		        }
    		        
       		        break;
        		        
    	        }
    	        
        	}

        }

    	totTime = ( System.currentTimeMillis() - startTime );        
    	long[] returnTimes = new long[] { lbTime, intTime, totTime };
    	
	    ReturnData rd = new ReturnData();
	    rd.setWeights( returnIntegerWeights );
	    rd.setIds( returnPumsRecords );
	    rd.setPumsWeights( returnPumsWeights );
	    rd.setIndices( returnHhIndices );
	    rd.setRunTimes( returnTimes );
	    
	    return rd;
	    
    }

    
    
    private ReturnData mazAllocation( int[] tazMazs, int[][] mazControls, int[] finalHouseholdIntegerWeights, int[][] tazIncidenceTable, double[] initialWeights, int[] hhIds, int totalHouseholdsControlIndex ) {    	
        
    	long lbTime = 0;
    	long intTime = 0;
    	long totTime = 0;
    	
    	long startTime = System.currentTimeMillis();
    	
	    // get an array of indices so that MAZs can be processed in order of increasing number of households
	    // the array values are indices into the tazMazsList[taz] List, which is a list of MAZs in the TAZ. 
	    int[] sortedIndices = getSortedIndexArray( tazMazs, mazControls, totalHouseholdsControlIndex );

	    // dimension the returned arrays for the number of MAZs in this TAZ
        int[][] returnIntegerWeights = new int[sortedIndices.length][];
	    int[][] returnPumsRecords = new int[sortedIndices.length][];
	    double[][] returnPumsWeights = new double[sortedIndices.length][];
	    

	    int[] tempIndices = new int[finalHouseholdIntegerWeights.length];
	    for ( int i=0; i< tempIndices.length; i++ )
	    	tempIndices[i] = i;
	    
        // get the hhids, incidence table, and initial weights for the non-zero weight household records for this TAZ
        int[] mazHhIndices = getNonZeroWeightHouseholdIndices( tempIndices, finalHouseholdIntegerWeights );
        int[][] mazIncidenceTable = getNonZeroWeightIncidenceTableForMaz( mazHhIndices, finalHouseholdIntegerWeights, tazIncidenceTable );
        int[] mazInitialWeights = getNonZeroWeights( mazHhIndices, finalHouseholdIntegerWeights );                
        

        boolean hhsHaveBeenAllocated = false;
        
        // if this was the next to last MAZ, then all the remaining weights get allocated to the last MAZ
        if ( sortedIndices.length == 1 ) {

	    	int m = sortedIndices[0];
	    	int maz = tazMazs[m];

		    // dimension the returned arrays for the MAZ to the number of final non-zero weights (should be number of total households in MAZ)
	        returnIntegerWeights[m] = new int[mazInitialWeights.length];
	        returnPumsRecords[m] = new int[mazInitialWeights.length];
	        returnPumsWeights[m] = new double[mazInitialWeights.length];
	        
	    	int mCount = 0;
	        for ( int k=0; k < mazInitialWeights.length; k++ ) {
	            
                // get puma record number for this index
                int n = mazHhIndices[k];
                
                // store puma record numbers and weights for each puma record in the maz
                returnIntegerWeights[m][mCount] = mazInitialWeights[k];
                returnPumsRecords[m][mCount] = hhIds[n];
                returnPumsWeights[m][mCount] = initialWeights[n];
                
                mCount++;
	        }
	        
        }
        else {
        	
    	    // loop over MAZs in increasing order of total number of households in the MAZ
    	    for ( int p=0; p < sortedIndices.length; p++ ) {
    	    	
    	    	int m = sortedIndices[p];
    	    	int maz = tazMazs[m];
    	    
    	    	if ( mazControls[m][totalHouseholdsControlIndex] == 0 )
    	    		continue;
    	    	
    	    	// if the first maz with non zero is the last maz, 
    	        if ( p == (sortedIndices.length - 1) && ! hhsHaveBeenAllocated ) {

    			    // dimension the returned arrays for the MAZ to the number of final non-zero weights (should be number of total households in MAZ)
    		        returnIntegerWeights[m] = new int[mazInitialWeights.length];
    		        returnPumsRecords[m] = new int[mazInitialWeights.length];
    		        returnPumsWeights[m] = new double[mazInitialWeights.length];
    		        
    		    	int mCount = 0;
        	        int totalIntWeights = 0;
    		        for ( int k=0; k < mazInitialWeights.length; k++ ) {
    		            
    	                // get puma record number for this index
    	                int n = mazHhIndices[k];
    	                
    	                // store puma record numbers and weights for each puma record in the maz
    	                returnIntegerWeights[m][mCount] = mazInitialWeights[k];
    	                returnPumsRecords[m][mCount] = hhIds[n];
    	                returnPumsWeights[m][mCount] = initialWeights[n];

                        totalIntWeights += mazInitialWeights[k];

                        mCount++;
    		        }
    		        
    		        break;

    	        }

    	        double[] lbInitialWeights = getNonZeroWeightIntValuesAsDouble(mazHhIndices, mazInitialWeights);
    	        ListBalancer lb = new ListBalancer( lbInitialWeights, importanceWeights, mazIncidenceTable, mazControls[m], totalHouseholdsControlIndex );
    	
    	        // set bounds for weights for MAZ balancing
    	        int[] lbWeights = new int[mazInitialWeights.length];
    	        Arrays.fill( lbWeights, 0 );                
    	        lb.setLbWeights( lbWeights );
    	        lb.setUbWeights( mazInitialWeights );                

    	        if ( maz == REPORT_MAZ_INDEX )
    	        	lb.setDebugControlSet( true );
    	        else
    	        	lb.setDebugControlSet( false );
    	        
    	    	long check = System.currentTimeMillis();
    	        lb.doBalancing();
    	    	lbTime += ( System.currentTimeMillis() - check );

    	    	double[] balancedSolution = lb.getFinalWeights();
    	        double totalWeights = 0;
    	        for (int i=0; i < balancedSolution.length; i++)
    	        	totalWeights += balancedSolution[i]; 
    	        
    	    	check = System.currentTimeMillis();
    	        int[] finalMazIntegerWeights = doIntegerizing( "MAZ", maz, mazControls[m], mazIncidenceTable, getNonZeroWeightIntValuesAsDouble(mazHhIndices, mazInitialWeights), importanceWeights, balancedSolution, lb.getRelaxationFactors(), totalHouseholdsControlIndex, ( maz == REPORT_MAZ_INDEX ) );
    	    	intTime += ( System.currentTimeMillis() - check );
    	
    	        
    	        int nonZeroCount = 0;
    	        for ( int k=0; k < finalMazIntegerWeights.length; k++ )
    	            if ( finalMazIntegerWeights[k] > 0 )
    	                nonZeroCount++;
    	
    	        int[] nonZeroHhIds = new int[nonZeroCount];
    	        int count = 0;
    	        for ( int k=0; k < finalMazIntegerWeights.length; k++ )
    	            if ( finalMazIntegerWeights[k] > 0 )
    	            	nonZeroHhIds[count++] = k; 
    	        
    	        
    		    // dimension the returned arrays for the MAZ to the number of final non-zer weights (should be number of total households in MAZ)
    	        returnIntegerWeights[m] = new int[nonZeroCount];
    	        returnPumsRecords[m] = new int[nonZeroCount];
    	        returnPumsWeights[m] = new double[nonZeroCount];
    	        
    	        int mCount = 0;
    	        int totalIntWeights = 0;
    	        for ( int kk=0; kk < nonZeroHhIds.length; kk++ ) {
    	            
    	        	int k = nonZeroHhIds[kk];
    	        	
                    // get puma record number for this index
                    int n = mazHhIndices[k];
                    
                    // store puma record numbers and weights for each puma record in the maz
                    returnIntegerWeights[m][mCount] = finalMazIntegerWeights[k];
                    returnPumsRecords[m][mCount] = hhIds[n];
	                returnPumsWeights[m][mCount] = initialWeights[n];

                    // reduce the taz integer weights by the pums record weight for the maz
                    mazInitialWeights[k] = Math.max( (mazInitialWeights[k] - finalMazIntegerWeights[k]), 0 );
                    
                    totalIntWeights += finalMazIntegerWeights[k];
                    
                    mCount++;
    	            
    	        }

    	        int totalRemainingWeights = 0;
    	        for ( int k=0; k < mazInitialWeights.length; k++ )
                    totalRemainingWeights += mazInitialWeights[k];
    	        
    	        hhsHaveBeenAllocated = true;
    	        
    	        mazHhIndices = getNonZeroWeightHouseholdIndices( mazHhIndices, mazInitialWeights );
    	        mazIncidenceTable = getNonZeroWeightIncidenceTableForMaz( mazHhIndices, mazInitialWeights, mazIncidenceTable );
    	        mazInitialWeights = getNonZeroWeights( mazHhIndices, mazInitialWeights );        
    	        
    	        
    	        // for the next to last MAZ, when more than one MAZ has HHs, the remaining weights get allocated to the last MAZ
    	        if ( p == (sortedIndices.length - 2) && hhsHaveBeenAllocated ) {

    		    	m = sortedIndices[p+1];
    		    	maz = tazMazs[m];

    			    // dimension the returned arrays for the MAZ to the number of final non-zero weights (should be number of total households in MAZ)
    		        returnIntegerWeights[m] = new int[mazInitialWeights.length];
    		        returnPumsRecords[m] = new int[mazInitialWeights.length];
        	        returnPumsWeights[m] = new double[mazInitialWeights.length];
    		        
    		    	mCount = 0;
    		        for ( int k=0; k < mazInitialWeights.length; k++ ) {
    		            
    	                // get puma record number for this index
    	                int n = mazHhIndices[k];
    	                
    	                // store puma record numbers and weights for each puma record in the maz
    	                returnIntegerWeights[m][mCount] = mazInitialWeights[k];
    	                returnPumsRecords[m][mCount] = hhIds[n];
    	                returnPumsWeights[m][mCount] = initialWeights[n];

    	                mCount++;
    		        }
    		        
    		        break;
    		        
    	        }
    	        
        	}

        }
        
    	totTime = ( System.currentTimeMillis() - startTime );        
    	long[] returnTimes = new long[] { lbTime, intTime, totTime };
    	
	    ReturnData rd = new ReturnData();
	    rd.setWeights( returnIntegerWeights );
	    rd.setIds( returnPumsRecords );
	    rd.setPumsWeights( returnPumsWeights );
	    rd.setRunTimes( returnTimes );
	    return rd;
	    
    }

    
    private void createControlSetArrays( ListBalancingSqlHelper sqlHelper, Balance balanceObject, Marginal[] controlSetArray ) {

        logger.info( "creating the controls arrays.");
        int[][][] mazControlTotals = new int[controlSetArray.length][][];
        int[][][] mazTazControlTotals = new int[controlSetArray.length][][];
        int[][][] mazPumaControlTotals = new int[controlSetArray.length][][];
        int[][][] tazControlTotals = new int[controlSetArray.length][][];
        int[][][] tazPumaControlTotals = new int[controlSetArray.length][][];
        

        // table names for the control totals tables: maz controls, meta controls, additional taz controls
        String mazControlTotalsTableName = "";
		String tazControlTotalsTableName = "";
		String[] metaControlTotalsTableNames = null;
		String[] metaAggregationLevels = null;
        if ( balanceObject.getMazControlsTable() != null )
			mazControlTotalsTableName = balanceObject.getMazControlsTable().getControlsTableName();
		if ( balanceObject.getTazControlsTable() != null )
			tazControlTotalsTableName = balanceObject.getTazControlsTable().getControlsTableName();
		if ( balanceObject.getMetaControlsTables() != null ) {
			metaControlTotalsTableNames = balanceObject.getMetaControlsTables().getControlsTableNames();
			metaAggregationLevels = balanceObject.getMetaControlsTables().getControlsTableAggregations();
		}
        

		int[][][] pumaMetaControlTotals = new int[controlSetArray.length][][];
		int[][][] tazMetaControlTotals = new int[controlSetArray.length][][];

        controlSetFieldNames = new String[controlSetArray.length][];

		geogManager = GeographyManager.getInstance();
		
        int[] mazIndex = geogManager.getMazIndices();
        int[] tazIndex = geogManager.getTazIndices();
        int[] pumaIndex = geogManager.getPumaIndices();
        int[] metaIndex = geogManager.getMetaIndices();
        int[] mazValues = geogManager.getMazValues();
        int[] tazValues = geogManager.getTazValues();
        int[] metaValues = geogManager.getMetaValues();
        int[] pumaValues = geogManager.getPumaValues();
        
        
        ArrayList<Integer >householdBasedControlList = new ArrayList<Integer>();
        
        // loop over control sets defined in the xml config file
        for ( int c=0; c < controlSetArray.length; c++ ) {
        	
        	 Marginal controlSet = controlSetArray[c];
        	 
        	 String tableValue = controlSet.getTable();
        	 if ( tableValue.equalsIgnoreCase( balanceObject.getPumsData().getHhTableName() ) ) {
        		 for ( int i=0; i < controlSet.getConstraintMap().size(); i++ )
        			 householdBasedControlList.add( 1 );
        	 }
        	 else if ( tableValue.equalsIgnoreCase( balanceObject.getPumsData().getPersTableName() ) ) {
        		 for ( int i=0; i < controlSet.getConstraintMap().size(); i++ )
        			 householdBasedControlList.add( 0 );
        	 }
        	 else {
        		 for ( int i=0; i < controlSet.getConstraintMap().size(); i++ )
        			 householdBasedControlList.add( -1 );
        	 }
        			 
        	 // get the HashMap of conditions for this control set
        	 HashMap<Integer, Constraint> conditionsMap = controlSet.getConstraintMap();
        	 
        	 String controlGeography = controlSet.getGeographyType();
        	 String controlType = controlSet.getControlType();
        	 
        	 // get the control set incidence type - simple or count, and create the incidence table.
        	 if ( controlType.equalsIgnoreCase( SIMPLE_TYPE ) ) {

                 // determine whether the incidence table indices should be updated for TAZ balancing table or Meta balancing table
                 if ( controlGeography.equalsIgnoreCase( balanceObject.getPumsData().getMazFieldName() ) ) {
                     mazControlTotals[c] = sqlHelper.submitGetControlsTableFieldsQuery( mazControlTotalsTableName, balanceObject.getPumsData().getMazFieldName(), conditionsMap );
                     mazTazControlTotals[c] = sqlHelper.submitGetAggregateControlsTableFieldsQuery( mazControlTotalsTableName, balanceObject.getPumsData().getTazFieldName(), conditionsMap );
                     mazPumaControlTotals[c] = sqlHelper.submitGetAggregateControlsTableFieldsQuery( mazControlTotalsTableName, balanceObject.getPumsData().getPumaFieldName(), conditionsMap );
                 }
                 else if ( controlGeography.equalsIgnoreCase( balanceObject.getPumsData().getTazFieldName() ) ) {
                     tazControlTotals[c] = sqlHelper.submitGetControlsTableFieldsQuery( tazControlTotalsTableName, balanceObject.getPumsData().getTazFieldName(), conditionsMap );
                     tazPumaControlTotals[c] = sqlHelper.submitGetAggregateControlsTableFieldsQuery( tazControlTotalsTableName, balanceObject.getPumsData().getPumaFieldName(), conditionsMap );
                 }
                 else {
		        	 for ( int i=0; i < metaAggregationLevels.length; i++ ) {
		        		 if ( metaAggregationLevels[i].equalsIgnoreCase( balanceObject.getPumsData().getPumaFieldName() ) )
	                		 pumaMetaControlTotals[c] = sqlHelper.submitGetControlsTableFieldsQuery( metaControlTotalsTableNames[i], balanceObject.getPumsData().getMetaFieldName(), conditionsMap );
		        		 else if ( metaAggregationLevels[i].equalsIgnoreCase( balanceObject.getPumsData().getTazFieldName() ) )
		        			 tazMetaControlTotals[c] = sqlHelper.submitGetControlsTableFieldsQuery( metaControlTotalsTableNames[i], balanceObject.getPumsData().getMetaFieldName(), conditionsMap );
		        	 }
                 }
                	 
        	 }
        	 else if ( controlType.equalsIgnoreCase( COUNT_TYPE ) ) {

                 // determine whether the incidence table indices should be updated for TAZ balancing table or Meta balancing table
                 if ( controlGeography.equalsIgnoreCase( balanceObject.getPumsData().getMazFieldName() ) ) {
                     mazControlTotals[c] = sqlHelper.submitGetControlsTableFieldsQuery( mazControlTotalsTableName, balanceObject.getPumsData().getMazFieldName(), conditionsMap );
                     mazTazControlTotals[c] = sqlHelper.submitGetAggregateControlsTableFieldsQuery( mazControlTotalsTableName, balanceObject.getPumsData().getTazFieldName(), conditionsMap );
                     mazPumaControlTotals[c] = sqlHelper.submitGetAggregateControlsTableFieldsQuery( mazControlTotalsTableName, balanceObject.getPumsData().getPumaFieldName(), conditionsMap );
                 }
                 else if ( controlGeography.equalsIgnoreCase( balanceObject.getPumsData().getTazFieldName() ) ) {
                     tazControlTotals[c] = sqlHelper.submitGetControlsTableFieldsQuery( tazControlTotalsTableName, balanceObject.getPumsData().getTazFieldName(), conditionsMap );
                     tazPumaControlTotals[c] = sqlHelper.submitGetAggregateControlsTableFieldsQuery( tazControlTotalsTableName, balanceObject.getPumsData().getPumaFieldName(), conditionsMap );
                 }
                 else {
		        	 for ( int i=0; i < metaAggregationLevels.length; i++ ) {
		        		 if ( metaAggregationLevels[i].equalsIgnoreCase( balanceObject.getPumsData().getPumaFieldName() ) )
	                		 pumaMetaControlTotals[c] = sqlHelper.submitGetControlsTableFieldsQuery( metaControlTotalsTableNames[i], balanceObject.getPumsData().getMetaFieldName(), conditionsMap );
		        		 else if ( metaAggregationLevels[i].equalsIgnoreCase( balanceObject.getPumsData().getTazFieldName() ) )
		        			 tazMetaControlTotals[c] = sqlHelper.submitGetControlsTableFieldsQuery( metaControlTotalsTableNames[i], balanceObject.getPumsData().getMetaFieldName(), conditionsMap );
		        	 }
                 }
        	 }
        	 
             controlSetFieldNames[c] = getControlSetFieldNames( conditionsMap );
        	         	
        }

        
                              
        // create tazControls array
        // get the taz aggregated maz controls and the taz additional controls and combine them into a single array
        int[][] mazTazControls = getCombinedControls( tazIndex, tazValues, mazTazControlTotals );
        int[][] addTazControls = getCombinedControls( tazIndex, tazValues, tazControlTotals );
        if ( mazTazControls.length > 0 && addTazControls.length > 0 ) {
            tazControls = new int[mazTazControls.length][mazTazControls[0].length + addTazControls[0].length];
            for ( int i=0; i < tazControls.length; i++ ) {
                for ( int j=0; j < mazTazControls[0].length; j++ )
                	tazControls[i][j] = mazTazControls[i][j];
                for ( int j=0; j < addTazControls[0].length; j++ )
                	tazControls[i][j + mazTazControls[0].length] = addTazControls[i][j];
            }
        }
        else if ( mazTazControls.length > 0 && addTazControls.length == 0 ) {
        	tazControls = new int[mazTazControls.length][mazTazControls[0].length];
            for ( int i=0; i < tazControls.length; i++ ) {
                for ( int j=0; j < mazTazControls[0].length; j++ )
                	tazControls[i][j] = mazTazControls[i][j];
            }
        }
        else if ( mazTazControls.length == 0 && addTazControls.length > 0 ) {
        	tazControls = new int[addTazControls.length][addTazControls[0].length];
            for ( int i=0; i < tazControls.length; i++ ) {
                for ( int j=0; j < addTazControls[0].length; j++ )
                	tazControls[i][j] = addTazControls[i][j];
            }
        }

        
        // create pumaControls array
        // get the puma aggregated maz controls and the puma aggregated additional taz controls and combine them into a single array
        int[][] mazPumaControls = getCombinedControls( pumaIndex, pumaValues, mazPumaControlTotals );
        int[][] addTazPumaControls = getCombinedControls( pumaIndex, pumaValues, tazPumaControlTotals );
        if ( mazPumaControls.length > 0 && addTazPumaControls.length > 0 ) {
            pumaControls = new int[mazPumaControls.length][mazPumaControls[0].length + addTazPumaControls[0].length];
            for ( int i=0; i < pumaControls.length; i++ ) {
                for ( int j=0; j < mazPumaControls[0].length; j++ )
                	pumaControls[i][j] = mazPumaControls[i][j];
                for ( int j=0; j < addTazPumaControls[0].length; j++ )
                	pumaControls[i][j + mazPumaControls[0].length] = addTazPumaControls[i][j];
            }
        }
        else if ( mazPumaControls.length > 0 && addTazPumaControls.length == 0 ) {
        	pumaControls = new int[mazPumaControls.length][mazPumaControls[0].length];
            for ( int i=0; i < pumaControls.length; i++ ) {
                for ( int j=0; j < mazPumaControls[0].length; j++ )
                	pumaControls[i][j] = mazPumaControls[i][j];
            }
        }
        else if ( mazPumaControls.length == 0 && addTazPumaControls.length > 0 ) {
        	pumaControls = new int[addTazPumaControls.length][addTazPumaControls[0].length];
            for ( int i=0; i < pumaControls.length; i++ ) {
                for ( int j=0; j < addTazPumaControls[0].length; j++ )
                	pumaControls[i][j] = addTazPumaControls[i][j];
            }
        }
        
        //pumaControls =  getCombinedControls( pumaIndex, pumaValues, mazPumaControlTotals );
        
        // get the meta zone and maz controls
        metaControls = getCombinedControls( metaIndex, metaValues, pumaMetaControlTotals );
        mazControls = getCombinedControls( mazIndex, mazValues, mazControlTotals );
        
        // copy the values from the list into an array
        householdBasedControls = new int[householdBasedControlList.size()];
        for ( int i=0; i < householdBasedControls.length; i++ )
        	householdBasedControls[i] = householdBasedControlList.get( i );
        
    }    

    

	private int[][] createIncidenceTablesForPuma( int puma, ListBalancingSqlHelper sqlHelper, Balance balanceObject, Marginal[] controlSetArray, int[] hhIdIndex ) {
		
		Set<Integer> tazBalanceIncidenceIndices = new TreeSet<Integer>();
		Set<Integer> tazMetaBalanceIncidenceIndices = new TreeSet<Integer>();
		Set<Integer> pumaMetaBalanceIncidenceIndices = new TreeSet<Integer>();
		Set<Integer> finalTazBalanceIncidenceIndices = new TreeSet<Integer>();
		Set<Integer> finalPumaBalanceIncidenceIndices = new TreeSet<Integer>();

		List<Double> controlImportanceWeights = new ArrayList<Double>();
		
		String[] metaAggregationLevels = balanceObject.getMetaControlsTables().getControlsTableAggregations();
		
		int[][][] incidenceTables = new int[controlSetArray.length][][];
		
		int[] majorGroupQuarterHhs = null;
		int[] otherGroupQuarterHhs = null;

		String idFieldName = balanceObject.getPumsData().getIdFieldName();
		
		
		
		// loop over control sets defined in the xml config file
		int totalHouseholdsControlIndex = -1;
		int majorUnivGroupQuartersIndicatorControlIndex = -1;
		int otherUnivGroupQuartersIndicatorControlIndex = -1;
		int nextControlIndex = 0;
		int finalPumaControlIndex = 0;
		int finalTazControlIndex = 0;
		for ( int c=0; c < controlSetArray.length; c++ ) {
			
			 Marginal controlSet = controlSetArray[c];
			 
			 // get the HashMap of conditions for this control set
			 HashMap<Integer, Constraint> conditionsMap = controlSet.getConstraintMap();
			 
			 String controlSetDescription = controlSet.getDescription();
			 String controlGeography = controlSet.getGeographyType();
			 String controlType = controlSet.getControlType();
			 
	         // set the index if control set is the group quarters indicator control
	         if ( controlSet.getIsMajorGroupQuartersIndicatorField() )
	        	 majorUnivGroupQuartersIndicatorControlIndex = nextControlIndex;
	         else if ( controlSet.getIsOtherGroupQuartersIndicatorField() )
	        	 otherUnivGroupQuartersIndicatorControlIndex = nextControlIndex;
	
			 boolean groupQuartersControl = controlSet.getIsGroupQuartersControl();
			 
			 // get the control set incidence type - simple or count, and create the incidence table.
			 if ( controlType.equalsIgnoreCase( SIMPLE_TYPE ) ) {
		         logger.info( "getting " + controlSetDescription + " " + SIMPLE_TYPE + " incidence table.");
		         sqlHelper.createSimpleIncidenceTable( idFieldName, TEMP_INCIDENCE_TABLE_NAME, conditionsMap, puma );
		         incidenceTables[c] = sqlHelper.submitGetSimpleIncidenceTableQuery( TEMP_INCIDENCE_TABLE_NAME, idFieldName, conditionsMap, hhIdIndex );
		
		         // check if control set is the total households control
		         if ( controlSet.getIsTotalHouseldsControl() )
		        	 totalHouseholdsControlIndex = nextControlIndex;
		
		         // determine whether the incidence table indices should be updated for TAZ balancing table or Meta balancing table
		         if ( controlGeography.equalsIgnoreCase( balanceObject.getPumsData().getMazFieldName() ) ) {
		             nextControlIndex = setControlIndices( tazBalanceIncidenceIndices, nextControlIndex, conditionsMap.size() );
		         }
		         else if ( controlGeography.equalsIgnoreCase( balanceObject.getPumsData().getTazFieldName() ) ) {
		             nextControlIndex = setControlIndices( tazBalanceIncidenceIndices, nextControlIndex, conditionsMap.size() );
		         }
		         else {
		        	 for ( int i=0; i < metaAggregationLevels.length; i++ ) {
		        		 if ( metaAggregationLevels[i].equalsIgnoreCase( balanceObject.getPumsData().getPumaFieldName() ) )
		        			 nextControlIndex = setControlIndices( pumaMetaBalanceIncidenceIndices, nextControlIndex, conditionsMap.size() );
		        		 else if ( metaAggregationLevels[i].equalsIgnoreCase( balanceObject.getPumsData().getTazFieldName() ) )
		        			 nextControlIndex = setControlIndices( tazMetaBalanceIncidenceIndices, nextControlIndex, conditionsMap.size() );
		        	 }
		         }
		        	 
			 }
			 else if ( controlType.equalsIgnoreCase( COUNT_TYPE ) ) {
		         logger.info( "getting " + controlSetDescription + " " + COUNT_TYPE + " incidence table.");
		         sqlHelper.createCountIncidenceTable( idFieldName, TEMP_INCIDENCE_TABLE_NAME, conditionsMap, puma );
		         incidenceTables[c] = sqlHelper.submitGetCountIncidenceTableQuery( TEMP_INCIDENCE_TABLE_NAME, idFieldName, conditionsMap, hhIdIndex );
		
		         // determine whether the incidence table indices should be updated for TAZ balancing table or Meta balancing table
		         if ( controlGeography.equalsIgnoreCase( balanceObject.getPumsData().getMazFieldName() ) ) {
		             nextControlIndex = setControlIndices( tazBalanceIncidenceIndices, nextControlIndex, conditionsMap.size() );
		         }
		         else if ( controlGeography.equalsIgnoreCase( balanceObject.getPumsData().getTazFieldName() ) ) {
		             nextControlIndex = setControlIndices( tazBalanceIncidenceIndices, nextControlIndex, conditionsMap.size() );
		         }
		         else {
		        	 for ( int i=0; i < metaAggregationLevels.length; i++ ) {
		        		 if ( metaAggregationLevels[i].equalsIgnoreCase( balanceObject.getPumsData().getPumaFieldName() ) )
		        			 nextControlIndex = setControlIndices( pumaMetaBalanceIncidenceIndices, nextControlIndex, conditionsMap.size() );
		        		 else if ( metaAggregationLevels[i].equalsIgnoreCase( balanceObject.getPumsData().getTazFieldName() ) )
		        			 nextControlIndex = setControlIndices( tazMetaBalanceIncidenceIndices, nextControlIndex, conditionsMap.size() );
		        	 }
		         }
			 }


			 
	         // the group quarters indicator index should be 2, defined after the total households and family/non-family households controls
	         // if the group quarters indicator index is set and this is not a group quarters control, then all the incidence values should be set to 0
	         // for all group quarters household records for the control set.
			 int controlSetId = controlSet.getId();
	         if ( c == majorUnivGroupQuartersIndicatorControlIndex ) {
	        	 
	        	 majorGroupQuarterHhs = new int[incidenceTables[majorUnivGroupQuartersIndicatorControlIndex].length];
	        	 for ( int i=0; i < incidenceTables[majorUnivGroupQuartersIndicatorControlIndex].length; i++ ) {
	        		 if ( incidenceTables[majorUnivGroupQuartersIndicatorControlIndex][i][0] == 1 )
	        			 majorGroupQuarterHhs[i] = 1;
	        	 }
	        	 
	         }
	         else if ( c == otherUnivGroupQuartersIndicatorControlIndex ) {
	        	 
	        	 otherGroupQuarterHhs = new int[incidenceTables[otherUnivGroupQuartersIndicatorControlIndex].length];
	        	 for ( int i=0; i < incidenceTables[otherUnivGroupQuartersIndicatorControlIndex].length; i++ ) {
	        		 if ( incidenceTables[otherUnivGroupQuartersIndicatorControlIndex][i][0] == 1 )
	        			 otherGroupQuarterHhs[i] = 1;
	        	 }
	        	 
	         }		         
	         
	         if ( ! groupQuartersControl && majorUnivGroupQuartersIndicatorControlIndex > 0 && otherUnivGroupQuartersIndicatorControlIndex > 0 && c > otherUnivGroupQuartersIndicatorControlIndex ) {
	        	 
	        	 for ( int i=0; i < incidenceTables[c].length; i++ ) {
	        		 
	        		 try {
		        		 // check if the incidence table row is a group quarters household
		        		 if ( majorGroupQuarterHhs[i] == 1 ) {
			        		 // set incidence column values for group quarter household records to 0
		        			 for ( int j=0; j < conditionsMap.size(); j++ )
		        				 incidenceTables[c][i][j] = 0; 
		        		 }		
	        		 }
	        		 catch ( Exception e ) {
	        			 throw new RuntimeException( "i="+i );
	        		 }
	        	 }
	        	 
	         }
	         
	         if ( ! groupQuartersControl && otherUnivGroupQuartersIndicatorControlIndex > 0 && otherUnivGroupQuartersIndicatorControlIndex > 0 && c > otherUnivGroupQuartersIndicatorControlIndex ) {
	        	 
	        	 for ( int i=0; i < incidenceTables[c].length; i++ ) {
	        		 
	        		 try {
		        		 // check if the incidence table row is a group quarters household
		        		 if ( otherGroupQuarterHhs[i] == 1 ) {
			        		 // set incidence column values for group quarter household records to 0
		        			 for ( int j=0; j < conditionsMap.size(); j++ )
		        				 incidenceTables[c][i][j] = 0; 
		        		 }		
	        		 }
	        		 catch ( Exception e ) {
	        			 throw new RuntimeException( "i="+i );
	        		 }
	        	 }
	        	 
	         }
			 
	         
	         
		     finalPumaControlIndex = setControlIndices( finalPumaBalanceIncidenceIndices, finalPumaControlIndex, conditionsMap.size() );
		     finalTazControlIndex = setControlIndices( finalTazBalanceIncidenceIndices, finalTazControlIndex, conditionsMap.size() );
		
  	         for ( int id=1; id <= conditionsMap.size(); id++ ) {
  	        	 if ( ! conditionsMap.containsKey(id) )
  	        		continue;

  			     // get the importance weights for each control so they can be saved in an array for later use
   	        	 Constraint constraint = conditionsMap.get( id );
		    	 double importance = Double.parseDouble( constraint.getImportance() );
		    	 controlImportanceWeights.add( importance );
		    	 
		     }
		     
		}
		
		importanceWeights = new double[controlImportanceWeights.size()];
		for ( int i=0; i < importanceWeights.length; i++ )
			importanceWeights[i] = controlImportanceWeights.get(i);
		
		
		logger.info( "combining final incidence tables.");
		int[][] returnTables = getBalancingIncidenceTable( incidenceTables, finalPumaBalanceIncidenceIndices, totalHouseholdsControlIndex );
		
		return returnTables;
		
	}
	

        
    /**
     * get the total households field values from the mazControls array for MAZs in the specified TAZ 
     * and return an index array to the MAZs in ascending order of number of MAZ households.
     * 
     * @param mazControls is an int[][] array of controls by MAZ.
     * @param totalHouseholdsControlIndex is the field id for the total households in the MAZ.
     */
    private int[] getSortedIndexArray( int[] geogs, int[][] geogControls, int totalHouseholdsControlIndex ) { 

    	// create an array with a value for each maz in the list of mazs for the taz.
    	// assign the number of households in the maz to the array.    	
    	int[] geogHHs = new int[geogs.length];
    	for ( int i=0; i < geogs.length; i++ ) {
    		// store the number of maz households in the array to be sorted
    		geogHHs[i] = geogs[i] + geogControls[i][totalHouseholdsControlIndex]*100000;
    	}

    	// return an array of indices for the list of mazs in the taz, sorted from lowest number of households to highest.
    	int[] sortedIndices = IndexSort.indexSort( geogHHs );
    	return sortedIndices;
    	
    }
    

    /**
     * Write a debug report for the complete taz and meta balancing procedure
     * 
     * @param controlSetFieldNames is a String[][] array with the first dimension being control sets and the second being field names for each control set.
     * @param tazMetaZones is an int[] array dimensioned for TAZ ids with the meta zone id to which each TAZ belongs.
     * @param inputTazControls is an int[][] array with the first dimension being control sets and the second being input TAZ control values for each control set.
     * @param tazRelaxedControls is a double[][] array with the first dimension being control sets and the second being balanced, relaxed TAZ control values 
     * 		for each control set resulting from the TAZ list balancing procedure.
     * @param tazRelaxedControls is a double[][] array with the first dimension being control sets and the second being balanced, relaxed TAZ control values 
     * 		for each control set resulting from the TAZ list balancing procedure.
     * @param tazFractionalWeightSummaries is a double[][] array with the first dimension being control sets and the second being household fractional weights
     * 		resulting from TAZ list balancing summarized over TAZs for TAZ and Meta controls.
     * @param tazRelaxedMetaControls is a double[][] array with the first dimension being control sets and the second being balanced, relaxed TAZ control values
     * 		for each TAZ and Meta control resulting from list balancing of households on final meta controls.
     * @param finalTazIntegerWeightSummaries is an int[][] array with the first dimension being control sets and the second being summaries by TAZ
     * 		of the final integer household weights for each TAZ and Meta control.
     * 
     */
    private void debugMetaBalancingReport( String[][] controlSetFieldNames, int[] tazMetaZones, int[][] inputTazControls, double[][] tazRelaxedControls,
    		double[][] tazFractionalWeightSummaries, int[][] finalMetaControls, double[][] tazRelaxedMetaControls, int[][] finalTazIntegerWeightSummaries ) {
    	
    	// for debugging
    	int RUN_THIS_DISTRICT_ONLY = 1;
    	
        try {

            PrintWriter outStream1 = new PrintWriter( new BufferedWriter( new FileWriter( "outputs/DebugMetaBalancing_Meta_" + RUN_THIS_DISTRICT_ONLY + ".csv" ) ) );

    		geogManager = GeographyManager.getInstance();
            int[] tazValues = geogManager.getTazValues();
            
            // Step 1 - Report input control values for TAZs
            String header = "taz, metaZone";
            for ( String[] controlSetNames : controlSetFieldNames ) {
                for ( String name : controlSetNames )
                    header += ", " + name;
            }
            outStream1.println( header );

            outStream1.println( "" );
            outStream1.println( "Input Controls" );

            for ( int z=0; z < inputTazControls.length; z++ ) {

            	int t = tazValues[z];
                int d = tazMetaZones[t];
                if ( d != RUN_THIS_DISTRICT_ONLY )
                    continue;

                String outputString = t + "," + d;
                for ( int j=0; j < inputTazControls[z].length; j++ )
                	outputString += "," + inputTazControls[z][j];
                outStream1.println( outputString );
            }

            
            
            // Step 2 - Report balanced, relaxed, fractional control values for TAZs
            outStream1.println( "" );
            outStream1.println( "Balanced, Relaxed TAZ Controls" );

            for ( int z=0; z < tazRelaxedControls.length; z++ ) {

            	int t = tazValues[z];
                int d = tazMetaZones[t];
                if ( d != RUN_THIS_DISTRICT_ONLY )
                    continue;
                
                String outputString = t + "," + d;
                for ( int j=0; j < tazRelaxedControls[z].length; j++ )
                	outputString += "," + tazRelaxedControls[z][j];
                outStream1.println( outputString );
            }

            
            
            // Step 3 - Report total Household fractional weights for TAZ and Meta controls summarized at by TAZs
            outStream1.println( "" );
            outStream1.println( "Fractional HH Weights Summarized by TAZ for TAZ and Meta controls" );

            for ( int z=0; z < tazFractionalWeightSummaries.length; z++ ) {

            	int t = tazValues[z];
                int d = tazMetaZones[t];
                if ( d != RUN_THIS_DISTRICT_ONLY )
                    continue;
                
                String outputString = t + "," + d;
                for ( int j=0; j < tazFractionalWeightSummaries[z].length; j++ )
                	outputString += "," + tazFractionalWeightSummaries[z][j];
                outStream1.println( outputString );
            }

            
            
            // Step 4 - Report new Meta Balancing control values for TAZs after Meta list balancing and integerizing
            outStream1.println( "" );
            outStream1.println( "Final Integer Meta Balancing Controls" );

            for ( int z=0; z < finalMetaControls.length; z++ ) {

            	int t = tazValues[z];
                int d = tazMetaZones[t];
                if ( d != RUN_THIS_DISTRICT_ONLY )
                    continue;
                
                String outputString = t + "," + d;
                for ( int j=0; j < finalMetaControls[z].length; j++ )
                	outputString += "," + finalMetaControls[z][j];
                outStream1.println( outputString );
            }

            
            
            // Step 5 - Report balanced, relaxed Meta control values for TAZs after meta balancing
            outStream1.println( "" );
            outStream1.println( "Meta Balancing Relaxed Controls" );

            for ( int z=0; z < tazRelaxedMetaControls.length; z++ ) {

            	int t = tazValues[z];
                int d = tazMetaZones[t];
                if ( d != RUN_THIS_DISTRICT_ONLY )
                    continue;
                
                String outputString = t + "," + d;
                for ( int j=0; j < tazRelaxedMetaControls[z].length; j++ )
                	outputString += "," + tazRelaxedMetaControls[z][j];
                outStream1.println( outputString );
            }

            
            
            // Step 6 - Household final integer weight summaries by TAZ
            outStream1.println( "" );
            outStream1.println( "Final Integer HH Weights Summmarized by TAZ" );

            for ( int z=0; z < finalTazIntegerWeightSummaries.length; z++ ) {

            	int t = tazValues[z];
                int d = tazMetaZones[t];
                if ( d != RUN_THIS_DISTRICT_ONLY )
                    continue;
                
                String outputString = t + "," + d;
                for ( int j=0; j < finalTazIntegerWeightSummaries[z].length; j++ )
                	outputString += "," + finalTazIntegerWeightSummaries[z][j];
                outStream1.println( outputString );
            }
            
            outStream1.close();

        }
        catch (IOException e) {
            logger.fatal("IO Exception writing file for checking integerizing procedure: ", e );
        }
        
        
    }
    
    private int[][] getNonZeroWeightIncidenceTableForMaz( int[] mazHhIndices, int[] hhWeights, int[][] finalIncidenceTable ) {
        
        int numHHs = 0;
        for ( int n=0; n < hhWeights.length; n++ )
            if ( hhWeights[n] > 0 )
                numHHs++;
        
        int[][] mazIncidenceTable = new int[numHHs][];
        
        // loop over PUMS household records with  non-zero weight and store full incidence table records
        int count = 0;
        for ( int k=0; k < hhWeights.length; k++ ) {
        	if ( hhWeights[k] > 0 ) {
        		mazIncidenceTable[count++] = finalIncidenceTable[k];
        	}
        }
        
        return mazIncidenceTable;
    }
    
    

    private int[] getNonZeroWeights( int[] mazHhIndices, int[] hhWeights ) {
        
        int numHHs = 0;
        for ( int n=0; n < hhWeights.length; n++ )
            if ( hhWeights[n] > 0 )
                numHHs++;
        
        int[] nonZeroWeights = new int[numHHs];
        
        // loop over PUMS households and store values of weights > 0 for records with non-zero weight
        int count = 0;
        for ( int k=0; k < hhWeights.length; k++ ) {
        	if ( hhWeights[k] > 0 ) {
                int value = hhWeights[k];
                nonZeroWeights[count++] = value;
        	}
        }
        
        return nonZeroWeights;
    }
    
    

    private double[] getNonZeroWeightIntValuesAsDouble( int[] nonZeroWeightIndices, int[] values ) {
        
        int numHHs = 0;
        for ( int n=0; n < values.length; n++ )
            if ( values[n] > 0 )
                numHHs++;
        
        double[] nonZeroWeightValues = new double[numHHs];
        
        // loop over PUMS households and store values of weights > 0 for records with non-zero weight
        int count = 0;
        for ( int k=0; k < values.length; k++ ) {
        	if ( values[k] > 0 ) {
                double value = values[k];
            	nonZeroWeightValues[count++] = value;
        	}
        }
        
        return nonZeroWeightValues;
    }
    
    

    private int[] getNonZeroWeightHouseholdIndices( int[] hhIndices, int[] hhWeights ) {
        
        int numHHs = 0;
        for ( int n=0; n < hhWeights.length; n++ )
            if ( hhWeights[n] > 0 )
                numHHs++;
        
        int[] indices = new int[numHHs];
        
        // loop over PUMS households and store household record index for households with weights > 0
        int count = 0;
        for ( int n=0; n < hhWeights.length; n++ ) {
            if ( hhWeights[n] > 0 ) {
            	int index = hhIndices[n];
                indices[count++] = index;
            }
        }
        
        return indices;
    }
    
    

    public int[] doIntegerizing( String label, int id, int[] controlTotals, int[][] incidenceTable, double[] initialWeights, double[] importanceWeights, double[] finalWeights, double[] relaxationFactors, int tazTotalHouseholdsControlIndex, boolean debugControlSet ) {
        
        // if the incidence table has only one record, then the final integer weights should be just an array with 1 element equal to the total number of households;
        // otherwise, solve for the integer weights using the Mixed Integer Programming solver.
        int[] integerWeights = { controlTotals[tazTotalHouseholdsControlIndex] };
        if ( incidenceTable.length > 1 ) {
        	
            Integerizer integerizer = new Integerizer();
            integerizer.setHouseholdBasedControls( householdBasedControls );
            integerizer.setControlTotals( controlTotals );
            integerizer.setIncidenceTable( incidenceTable );
            integerizer.setInitialWeights( initialWeights );
            integerizer.setImportanceWeights( importanceWeights );
            integerizer.setFinalWeights( finalWeights );
            integerizer.setRelaxationFactors( relaxationFactors );
            integerizer.setTotalHouseholdsControlIndex( tazTotalHouseholdsControlIndex );        
            integerizer.setDebugControlSet( debugControlSet );
            
        	double[] continuousSolution = integerizer.integerizeCbc( label, id, "CBC_MIXED_INTEGER_PROGRAMMING", true, null, INITIAL_TIMEOUT_IN_SECONDS );
        	
        	// set the weights to be negative integers so sorting returns descending order indices
        	double weightTotal = 0.0;
        	int[] weights = new int[continuousSolution.length];
        	for ( int i=0; i < continuousSolution.length; i++ ) {
        		weightTotal += continuousSolution[i];
        		weights[i] = (int)( -1000000000 * continuousSolution[i] );
        	}

        	// return an array of indices for the list of weights, sorted from highest to lowest.
        	int[] sortedIndices = IndexSort.indexSort( weights );
        	
        	int totalHhs = (int)( weightTotal + 0.5 );
        	int[] tempWeights = new int[continuousSolution.length];
        	for ( int i=0; i < totalHhs; i++ ) {
        		int k = sortedIndices[i];
        		int wt = weights[k];
        		tempWeights[k] = 1;
        	}
        	
        	int[] residualWeights = integerizer.getresidualIntegerizedWeights();
        	integerWeights = integerizer.getFinalIntegerizedWeights( tempWeights );
        	
        	totalHhs = 0;
        	for ( int i=0; i < integerWeights.length; i++ )
        		totalHhs += integerWeights[i];
        	
        	int totalResid = 0;
        	for ( int i=0; i < residualWeights.length; i++ )
        		totalResid += residualWeights[i];
        	
        	double totalFractionalHhs = 0;
        	for ( int i=0; i < continuousSolution.length; i++ )
        		totalFractionalHhs += continuousSolution[i];
        	
        	int totalHouseholdsControl = (int)(controlTotals[tazTotalHouseholdsControlIndex] * relaxationFactors[tazTotalHouseholdsControlIndex] + 0.5); 
        	if ( totalHhs != totalHouseholdsControl ) {
        		logger.error( "sum of integer weights = " + totalHhs +  " not equal to total households control = " + totalHouseholdsControl + ": " + label + " " + id );
        		throw new RuntimeException();
        	}
        	
        }
            
        return integerWeights;
        
    }
    
    
    private int[][] getBalancingIncidenceTable( int[][][] inputTables, Set<Integer> controlIndexSet, int totalHouseholdsControlIndex ) {
        
        // get the number of balancing incidence table columns from the number of controls in the index set.
        int numNewColumns = controlIndexSet.size();
        
        // use the first table to get the number of rows in all tables
        // create an outputTable with the same number of rows as all the incidence tables, and the number of combined columns
        int numIncidenceRows = inputTables[0].length;
        int[] totalHousehldsField = new int[numIncidenceRows];
        int[][] outputTables = new int[numIncidenceRows][numNewColumns];
        
        // copy incidence values from the separate incidence tables for each control set index to the combined table.
        int columnOffset = 0;
        int inputTablesColumn = 0;
        for ( int t=0; t < inputTables.length; t++ ) {
            int[][] table = inputTables[t];
            String columnsUsed = "";
            for ( int j=0; j < table[0].length; j++ ) {
                if ( controlIndexSet.contains( inputTablesColumn ) ) {

                    if ( inputTablesColumn == totalHouseholdsControlIndex )
                        totalHouseholdsTazBalancingIncidenceIndex = columnOffset;

                    columnsUsed += " " + j;
                    for ( int i=0; i < numIncidenceRows; i++ )
                        outputTables[i][columnOffset] = table[i][j];            
                    columnOffset++;
                }

                inputTablesColumn++;
            }
            
            logger.info( "balancing incidence table " + t + " has " + table.length + " rows and " + table[0].length + " columns.  Input table columns [" + columnsUsed + " ] were added to final incidence.");
        }
        
        logger.info( "balancing incidence table total households control index = " + totalHouseholdsTazBalancingIncidenceIndex + "." );

        return outputTables;
    
    }
    
    

    private double[] getFractionalWeightTotals( int[][] incidenceTable, double[] finalBalancedWeights ) {
        
        double[] returnValues = new double[incidenceTable[0].length];
        for ( int i=0; i < incidenceTable.length; i++ )
            for ( int j=0; j < incidenceTable[i].length; j++ )
                returnValues[j] += incidenceTable[i][j] * finalBalancedWeights[i];
        
        return returnValues;
    }
    
    
    private int[] getIntegerWeightTotals( int[][] incidenceTable, int[] finalIntegerWeights ) {
        
        int[] returnValues = new int[incidenceTable[0].length];
        for ( int i=0; i < incidenceTable.length; i++ )
            for ( int j=0; j < incidenceTable[i].length; j++ )
                returnValues[j] += incidenceTable[i][j] * finalIntegerWeights[i];
        
        return returnValues;
    }

    
    private String[] getControlSetFieldNames( HashMap<Integer, Constraint> constraints ) {

        String[] fieldNames = new String[constraints.size()];
        int count = 0;
        for ( int id=0; id < constraints.size(); id++ ) {
        	if ( ! constraints.containsKey(id) )
        		continue;
            Constraint constraint = constraints.get(id);
            fieldNames[count] = constraint.getField() + "_" + (++count);
        }

        return fieldNames;
    }
    
    
    private int setControlIndices( Set<Integer> controlIndexSet, int nextControlIndex, int numControlIndices ) {
        
        int nextIndex = -1;
        for ( int i=0; i < numControlIndices; i++ ) {
            nextIndex = nextControlIndex + i;
            controlIndexSet.add( nextIndex );
        }
        
        return nextIndex + 1;
    }
    

    private int[][] getCombinedControls( int[] geogIndex, int[] geogValues, int[][][] controlTotals ) {
    
        // get the total number of controls table columns from the number of controls in each array element (ignore first column of each table which is geographic index).
        int numColumns = 0;
        int numRows = 0;
        for ( int i=0; i < controlTotals.length; i++ ) {
        	if ( controlTotals[i] != null ) {
        		numColumns += ( controlTotals[i][0].length - 1 );

        		// use the length of the first found table to get the number of rows in all tables
        		if ( numRows == 0 )
        			numRows = controlTotals[i].length;
        	}
        }
        

        // create an outputTable with the same number of rows as all the incidence tables,
        // and one less than the number of combined columns - the first column is the geographic index
        int[][] outputTables = new int[numRows][numColumns];
        
        
        // copy table values from the separate control totals tables for each control set index to the combined table.
        int columnOffset = 0;
        for ( int t=0; t < controlTotals.length; t++ ) {
        	
        	if ( controlTotals[t] == null )
        		continue;

    		int tSaved = t;
    		int jSaved = -1;
    		int iSaved = -1;
    		int geogSaved = -1;
    		int indexSaved = -1;
    		
        	try {
        		
                int[][] table = controlTotals[t];
            	// save the table with the rows in the order indicated by the index array passed in for each control
                // column indices start at 1 since the 0 index holods the geographic index
                for ( int j=1; j < table[0].length; j++ ) {
            		jSaved = j;

                	for ( int i=0; i < numRows; i++ ) {
                		iSaved = i;
        	        	int geog = table[i][0];
                		geogSaved = geog;
        	        	
        	        	if ( geog < geogIndex.length ) {
        	        		int index = geogIndex[geog];
                    		indexSaved = index;
        	        		outputTables[index][columnOffset] = table[i][j];
        	        	}
        	        	
                    }
                    columnOffset++;
                }

        	}
        	catch (Exception e) {
        		logger.error( "exception caught", e );
        	}
            
        }
        
        
		return outputTables;

    }


    private void dumpMetaBalancingInfo( String fileName, int[][] finalIncidenceTable, int[][] newTazFinalIntegerWeightTotals, int[][] newTazFinalControls, double[] initialWeights, double[][] metaFinalWeights, double[][] metaRelaxationFactors ) {
        
        try {

            PrintWriter outStream = new PrintWriter( new BufferedWriter( new FileWriter( fileName ) ) );

            
            //write household file header record
            String header = "newTazFinalIntegerWeightTotals";
            outStream.println( header );
            outStream.println( newTazFinalIntegerWeightTotals.length );
            for ( int n=0; n < newTazFinalIntegerWeightTotals.length; n++ ) {
            	if ( newTazFinalIntegerWeightTotals[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < newTazFinalIntegerWeightTotals[n].length-1; i++ )
                    outStream.print( newTazFinalIntegerWeightTotals[n][i] + "," );
                outStream.println( newTazFinalIntegerWeightTotals[n][newTazFinalIntegerWeightTotals[n].length-1] );
            }                    
            outStream.println( "" );

            
            header = "newTazFinalControls";
            outStream.println( header );
            outStream.println( newTazFinalControls.length );
            for ( int n=0; n < newTazFinalControls.length; n++ ) {
            	if ( newTazFinalControls[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < newTazFinalControls[n].length-1; i++ )
                    outStream.print( newTazFinalControls[n][i] + "," );
                outStream.println( newTazFinalControls[n][newTazFinalControls[n].length-1] );
            }                    
            outStream.println( "" );

            
            header = "finalIncidenceTable";
            outStream.println( header );
            outStream.println( finalIncidenceTable.length );
            for ( int n=0; n < finalIncidenceTable.length; n++ ) {
            	if ( finalIncidenceTable[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < finalIncidenceTable[n].length-1; i++ )
                    outStream.print( finalIncidenceTable[n][i] + "," );
                outStream.println( finalIncidenceTable[n][finalIncidenceTable[n].length-1] );
            }                    
            outStream.println( "" );

            
            header = "initialWeights";
            outStream.println( header );
            for ( int n=0; n < initialWeights.length-1; n++ )
                outStream.print(  initialWeights[n] + "," );
            outStream.println( initialWeights[initialWeights.length-1] );
            outStream.println( "" );
            

            header = "metaFinalWeights";
            outStream.println( header );
            outStream.println( metaFinalWeights.length );
            for ( int n=0; n < metaFinalWeights.length; n++ ) {
            	if ( metaFinalWeights[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < metaFinalWeights[n].length-1; i++ )
                    outStream.print( metaFinalWeights[n][i] + "," );
                outStream.println( metaFinalWeights[n][metaFinalWeights[n].length-1] );
            }                    
            outStream.println( "" );

            
            header = "metaRelaxationFactors";
            outStream.println( header );
            outStream.println( metaRelaxationFactors.length );
            for ( int n=0; n < metaRelaxationFactors.length; n++ ) {
            	if ( metaRelaxationFactors[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < metaRelaxationFactors[n].length-1; i++ )
                    outStream.print( metaRelaxationFactors[n][i] + "," );
                outStream.println( metaRelaxationFactors[n][metaRelaxationFactors[n].length-1] );
            }                    
            outStream.println( "" );


            outStream.close();

        }
        catch (IOException e) {
            logger.fatal("IO Exception writing file for checking integerizing procedure: " + fileName, e );
        }
        
    }
    
    
    private void dumpBalancingInfo( String fileName, int[][] finalIncidenceTable, int[] pumaHhIds, double[] initialWeights, double[] balancedWeights ) {
        
        try {

            PrintWriter outStream = new PrintWriter( new BufferedWriter( new FileWriter( fileName ) ) );

            
            String header = "finalIncidenceTable";
            outStream.println( header );
            outStream.println( finalIncidenceTable.length );
            for ( int n=0; n < finalIncidenceTable.length; n++ ) {
            	if ( finalIncidenceTable[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < finalIncidenceTable[n].length-1; i++ )
                    outStream.print( finalIncidenceTable[n][i] + "," );
                outStream.println( finalIncidenceTable[n][finalIncidenceTable[n].length-1] );
            }                    
            outStream.println( "" );

            header = "pumaHhIds";
            outStream.println( header );
            outStream.println( pumaHhIds.length );
            for ( int n=0; n < pumaHhIds.length; n++ ) {
                outStream.println( n + "," + pumaHhIds[n]);
            }                    

            header = "initialWeights";
            outStream.println( header );
            outStream.println( initialWeights.length );
            for ( int n=0; n < initialWeights.length; n++ ) {
                outStream.println( n + "," + initialWeights[n]);
            }                    

            header = "balancedWeights";
            outStream.println( header );
            outStream.println( balancedWeights.length );
            for ( int n=0; n < balancedWeights.length; n++ ) {
                outStream.println( n + "," + balancedWeights[n]);
            }                    


            
            outStream.close();

        }
        catch (IOException e) {
            logger.fatal("IO Exception writing file for checking integerizing procedure: " + fileName, e );
        }
        
    }
    
    
    private void dumpPumaBalancingInfo( String fileName, double[][] weightTotals, int[][] originalControls, double[][] relaxationFactors, double[][] relaxedControls, double[][] totalMetaWeights ) {
        
        try {

            PrintWriter outStream = new PrintWriter( new BufferedWriter( new FileWriter( fileName ) ) );

            
            //write household file header record
            String header = "weightTotals";
            outStream.println( header );
            outStream.println( weightTotals.length );
            for ( int n=0; n < weightTotals.length; n++ ) {
            	if ( weightTotals[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < weightTotals[n].length-1; i++ )
                    outStream.print( weightTotals[n][i] + "," );
                outStream.println( weightTotals[n][weightTotals[n].length-1] );
            }                    
            outStream.println( "" );

            
            header = "originalControls";
            outStream.println( header );
            outStream.println( originalControls.length );
            for ( int n=0; n < originalControls.length; n++ ) {
            	if ( originalControls[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < originalControls[n].length-1; i++ )
                    outStream.print( originalControls[n][i] + "," );
                outStream.println( originalControls[n][originalControls[n].length-1] );
            }                    
            outStream.println( "" );

            
            header = "relaxationFactors";
            outStream.println( header );
            outStream.println( totalMetaWeights.length );
            for ( int n=0; n < relaxationFactors.length; n++ ) {
            	if ( relaxationFactors[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < relaxationFactors[n].length-1; i++ )
                    outStream.print( relaxationFactors[n][i] + "," );
                outStream.println( relaxationFactors[n][relaxationFactors[n].length-1] );
            }                    
            outStream.println( "" );

            
            header = "relaxedControls" +
            		"";
            outStream.println( header );
            outStream.println( relaxedControls.length );
            for ( int n=0; n < relaxedControls.length; n++ ) {
            	if ( relaxedControls[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < relaxedControls[n].length-1; i++ )
                    outStream.print( relaxedControls[n][i] + "," );
                outStream.println( relaxedControls[n][relaxedControls[n].length-1] );
            }                    
            outStream.println( "" );

            
            header = "totalMetaWeights";
            outStream.println( header );
            outStream.println( totalMetaWeights.length );
            for ( int n=0; n < totalMetaWeights.length; n++ ) {
            	if ( totalMetaWeights[n] == null )
            		continue;
                outStream.print( n + "," );
            	for ( int i=0; i < totalMetaWeights[n].length-1; i++ )
                    outStream.print( totalMetaWeights[n][i] + "," );
                outStream.println( totalMetaWeights[n][totalMetaWeights[n].length-1] );
            }                    
            outStream.println( "" );

            
            outStream.close();

        }
        catch (IOException e) {
            logger.fatal("IO Exception writing file for checking puma balancing procedure: " + fileName, e );
        }
        
    }
    
    
    private void dumpPumaBalancingWeights( String fileName, double[] balancedWeights, double[] pumsInitialWeights ) {
        
        try {

            PrintWriter outStream = new PrintWriter( new BufferedWriter( new FileWriter( fileName ) ) );

            
            //write household file header record
            String header = "N, balancedWeights, pumsInitialWeights";
            outStream.println( header );
            for ( int n=0; n < balancedWeights.length; n++ )
                outStream.println( n + "," + balancedWeights[n] + "," + pumsInitialWeights[n] );
            
            outStream.close();

        }
        catch (IOException e) {
            logger.fatal("IO Exception writing file for comparing balanced and original weights procedure: " + fileName, e );
        }
        
    }
    
    
    private List<Object> restoreMetaBalancing( String filename ) {

    	List<Object> returnList = new ArrayList<Object>();
    	
    	try {
    		
	        // open the input stream
	        String delimSet = ",";
	        BufferedReader inStream = null;
	        try {
	            inStream = new BufferedReader(new FileReader(new File(filename)));
	        }
	        catch (FileNotFoundException e) {
	            e.printStackTrace();
	            System.exit(-1);
	        }
	
	        
	        
	        int[][] newTazFinalIntegerWeightTotals = null;
	        
	        String label = inStream.readLine();            
	        if ( label.equals("newTazFinalIntegerWeightTotals")) {
	
	            ArrayList<ArrayList<Integer>> rows = new ArrayList<ArrayList<Integer>>();
	            String line = inStream.readLine();
	
	            while ( ! line.equals( "" ) ) {
	                ArrayList<Integer> columnValues = new ArrayList<Integer>();
	                StringTokenizer st = new StringTokenizer(line, delimSet);
	                while ( st.hasMoreTokens() )
	                    columnValues.add( Integer.parseInt(st.nextToken()) );
	                rows.add(columnValues);
	                line = inStream.readLine();
	            }
	            
	            newTazFinalIntegerWeightTotals = new int[rows.size()][];
	            for ( int i=0; i < rows.size(); i++ ) {
	            	ArrayList<Integer> columnValues = rows.get(i);
	            	newTazFinalIntegerWeightTotals[i] = new int[columnValues.size()];
	            	for ( int j=0; j < columnValues.size(); j++ )
	            		newTazFinalIntegerWeightTotals[i][j] = columnValues.get(j);
	            }
	            
	            returnList.add( newTazFinalIntegerWeightTotals );
	            	
	        }
	        else {
	        	logger.error ( "newTazFinalIntegerWeightTotals table label was expected but wasn't found" );
	        }
	
	        
	        
	        int[][] newTazFinalControls = null;
	        
	        label = inStream.readLine();            
	        if ( label.equals("newTazFinalControls")) {
	
	            ArrayList<ArrayList<Integer>> rows = new ArrayList<ArrayList<Integer>>();
	            String line = inStream.readLine();
	
	            while ( ! line.equals( "" ) ) {
	                ArrayList<Integer> columnValues = new ArrayList<Integer>();
	                StringTokenizer st = new StringTokenizer(line, delimSet);
	                while ( st.hasMoreTokens() )
	                    columnValues.add( Integer.parseInt(st.nextToken()) );
	                rows.add(columnValues);
	                line = inStream.readLine();
	            }
	            
	            newTazFinalControls = new int[rows.size()][];
	            for ( int i=0; i < rows.size(); i++ ) {
	            	ArrayList<Integer> columnValues = rows.get(i);
	            	newTazFinalControls[i] = new int[columnValues.size()];
	            	for ( int j=0; j < columnValues.size(); j++ )
	            		newTazFinalControls[i][j] = columnValues.get(j);
	            }
	            
	            returnList.add( newTazFinalControls );
	            	
	        }
	        else {
	        	logger.error ( "newTazFinalControls table label was expected but wasn't found" );
	        }
	
	        
	        
	        int[][] finalIncidenceTable = null;
	        
	        label = inStream.readLine();            
	        if ( label.equals("finalIncidenceTable")) {
	
	            ArrayList<ArrayList<Integer>> rows = new ArrayList<ArrayList<Integer>>();
	            String line = inStream.readLine();
	
	            while ( ! line.equals( "" ) ) {
	                ArrayList<Integer> columnValues = new ArrayList<Integer>();
	                StringTokenizer st = new StringTokenizer(line, delimSet);
	                while ( st.hasMoreTokens() )
	                    columnValues.add( Integer.parseInt(st.nextToken()) );
	                rows.add(columnValues);
	                line = inStream.readLine();
	            }
	            
	            finalIncidenceTable = new int[rows.size()][];
	            for ( int i=0; i < rows.size(); i++ ) {
	            	ArrayList<Integer> columnValues = rows.get(i);
	            	finalIncidenceTable[i] = new int[columnValues.size()];
	            	for ( int j=0; j < columnValues.size(); j++ )
	            		finalIncidenceTable[i][j] = columnValues.get(j);
	            }
	            
	            returnList.add( finalIncidenceTable );
	            	
	        }
	        else {
	        	logger.error ( "finalIncidenceTable table label was expected but wasn't found" );
	        }
            

	        
	        double[] initialWeights = null;
	        
	        label = inStream.readLine();            
	        if ( label.equals("initialWeights")) {
	
	            ArrayList<Double> columnValues = new ArrayList<Double>();
	            String line = inStream.readLine();
	
	            while ( ! line.equals( "" ) ) {
	                StringTokenizer st = new StringTokenizer(line, delimSet);
	                while ( st.hasMoreTokens() )
	                    columnValues.add( Double.parseDouble(st.nextToken()) );
	                line = inStream.readLine();
	            }
	            
	            initialWeights = new double[columnValues.size()];
            	for ( int i=0; i < columnValues.size(); i++ )
            		initialWeights[i] = columnValues.get(i);
	            	
	            returnList.add( initialWeights );
	        }
	        else {
	        	logger.error ( "initialWeights label was expected but wasn't found" );
	        }
            

	        
	        double[][] metaFinalWeights = null;
	        
	        label = inStream.readLine();            
	        if ( label.equals("metaFinalWeights")) {
	
	            ArrayList<ArrayList<Double>> rows = new ArrayList<ArrayList<Double>>();
	            String line = inStream.readLine();
	
	            while ( ! line.equals( "" ) ) {
	                ArrayList<Double> columnValues = new ArrayList<Double>();
	                StringTokenizer st = new StringTokenizer(line, delimSet);
	                while ( st.hasMoreTokens() )
	                    columnValues.add( Double.parseDouble(st.nextToken()) );
	                rows.add(columnValues);
	                line = inStream.readLine();
	            }
	            
	            metaFinalWeights = new double[rows.size()][];
	            for ( int i=0; i < rows.size(); i++ ) {
	            	ArrayList<Double> columnValues = rows.get(i);
	            	metaFinalWeights[i] = new double[columnValues.size()];
	            	for ( int j=0; j < columnValues.size(); j++ )
	            		metaFinalWeights[i][j] = columnValues.get(j);
	            }
	            
	            returnList.add( metaFinalWeights );
	            	
	        }
	        else {
	        	logger.error ( "metaFinalWeights table label was expected but wasn't found" );
	        }
            

	        
	        double[][] metaRelaxationFactors = null;
	        
	        label = inStream.readLine();            
	        if ( label.equals("metaRelaxationFactors")) {
	
	            ArrayList<ArrayList<Double>> rows = new ArrayList<ArrayList<Double>>();
	            String line = inStream.readLine();
	
	            while ( ! line.equals( "" ) ) {
	                ArrayList<Double> columnValues = new ArrayList<Double>();
	                StringTokenizer st = new StringTokenizer(line, delimSet);
	                while ( st.hasMoreTokens() )
	                    columnValues.add( Double.parseDouble(st.nextToken()) );
	                rows.add(columnValues);
	                line = inStream.readLine();
	            }
	            
	            metaRelaxationFactors = new double[rows.size()][];
	            for ( int i=0; i < rows.size(); i++ ) {
	            	ArrayList<Double> columnValues = rows.get(i);
	            	metaRelaxationFactors[i] = new double[columnValues.size()];
	            	for ( int j=0; j < columnValues.size(); j++ )
	            		metaRelaxationFactors[i][j] = columnValues.get(j);
	            }
	            
	            returnList.add( metaRelaxationFactors );
	            	
	        }
	        else {
	        	logger.error ( "metaRelaxationFactors table label was expected but wasn't found" );
	        }
            
	        
    	} catch (NumberFormatException e) {
        	e.printStackTrace();
        	System.exit(-1);
        } catch (IOException e) {
        	e.printStackTrace();
        	System.exit(-1);
        }

        return returnList;
    }



    /**
     * @param args
     */
    public static void main(String[] args)
    {

    	Long start = System.nanoTime();
    	
    	PopGenerator test = new PopGenerator();
        try {        	
            test.runPopulationGenerator( args[0] );        	
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        test.logger.info( String.format( "%s%.2f%s", "Time for full model run = ", (( System.nanoTime() - start )/60000000000.0), " minutes." ) );
        
        System.exit(0);
        
        //System.out.println("---- Integer programming example with CBC ----");
        //Integerizer test = new Integerizer();        
        //test.runIntegerProgrammingExample();
    }
    
    

    /**
     * Process the execution results of each submitted task. 
     * @param results the tasks results after execution on the grid.
     */
    public void processExecutionResults( List<Object> results, double[][] relaxedControls, double[][] fractionalWeights, double[][] relaxationFactors, double[][] factoredPumaMetaWeights ) {
		
		try {
			int id = (Integer)results.get(0);
			logger.info( "Task " + results.get(0) + ": " + results.get(1) + " completed." );
			relaxedControls[id-1] = (double[])results.get(2);
			fractionalWeights[id-1] = (double[])results.get(3);
			relaxationFactors[id-1] = (double[])results.get(4);
			factoredPumaMetaWeights[id-1] = (double[])results.get(5);
		}
		catch ( ClassCastException e ) {
			logger.error("An exception was raised: " + e.getMessage());
		}

    }

    
    
    private class ReturnData implements Serializable {
    	
    	private static final long serialVersionUID = 1L;
    	
        int[][] finalIntegerWeights;
	    int[][] finalPumsRecords;
        double[][] finalPumsWeights;
        int[][] finalHhIndices;
        long[] runTimes;

	    private void setWeights( int[][] weights ) {
	    	finalIntegerWeights = weights;
	    }

	    private void setIds( int[][] ids ) {
	    	finalPumsRecords = ids;
	    }
	    
	    private void setPumsWeights( double[][] weights ) {
	    	finalPumsWeights = weights;
	    }
	    
	    private void setIndices( int[][] indices ) {
	    	finalHhIndices = indices;
	    }

	    private void setRunTimes( long[] times ) {
	    	runTimes = times;
	    }

	    private int[][] getWeights() {
	    	return finalIntegerWeights;
	    }

	    private int[][] getIds() {
	    	return finalPumsRecords;
	    }

	    private double[][] getPumsWts() {
	    	return finalPumsWeights;
	    }
	    
	    private int[][] getIndices() {
	    	return finalHhIndices;
	    }
	    
	    private long[] getRunTimes() {
	    	return runTimes;
	    }

    }

    
    class TazAllocationTask extends JPPFTask
    {
    
    	private static final long serialVersionUID = versionNumber;

    	private String label;
    	private int puma;
    	private int p;
    	private String taskName;
    	private String runtime;
    	
        public TazAllocationTask( String label, int puma, int p ) {
        	this.label = label;
        	this.puma = puma;
        	this.p = p;
        }
        
        public void run() {

            Logger logger = Logger.getLogger(TazAllocationTask.class);
        	
    		taskName = "NULL";
            try {
    			taskName = label + "[ " + java.net.InetAddress.getLocalHost().getHostName() + "_" + Thread.currentThread().getName() + "_" + p + "_" + puma + " ]";
    			setId( taskName );
    		}
    		catch (UnknownHostException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

            
    		DataProvider dataProvider = getDataProvider();

    		int[][] finalPumaControls = null;
    		int[][] tazPromotion = null;
    		int[][][] pumaTazControls = null;
    		double[][] pumaFinalWeights = null;
    		double[][] pumaRelaxationFactors = null;
    		int[][] pumaTazList = null;
    		int[][][] pumaIncidenceTable = null;
    		double[][] pumaInitialWeights = null;
    		int[][] pumaHhids = null;
    		GeographyManager geogManager = null;
    		
    		int totalHouseholdsControlIndex = -1;
			try {
				geogManager = (GeographyManager)dataProvider.getValue( "geogManager" );
				finalPumaControls = (int[][])dataProvider.getValue( "finalPumaControls" );
				tazPromotion = (int[][])dataProvider.getValue( "tazPromotion" );
				pumaTazControls = (int[][][])dataProvider.getValue( "pumaTazControls" );
				pumaFinalWeights = (double[][])dataProvider.getValue( "pumaFinalWeights" );
				pumaRelaxationFactors = (double[][])dataProvider.getValue( "pumaRelaxationFactors" );
				pumaTazList = (int[][])dataProvider.getValue( "pumaTazList" );
				pumaIncidenceTable = (int[][][])dataProvider.getValue( "pumaIncidenceTable" );
				pumaInitialWeights = (double[][])dataProvider.getValue( "pumaInitialWeights" );
				pumaHhids = (int[][])dataProvider.getValue( "pumaHhids" );
	    		totalHouseholdsControlIndex = (Integer)dataProvider.getValue( "totalHouseholdsControlIndex" );
	    	}
			catch (Exception e) {
				logger.error ( "Exception caught assigning run() data members from DataProvider.", e );
				throw new RuntimeException();
			}

			
	    	int[] finalHouseholdIntegerWeights = doIntegerizing( label, puma, finalPumaControls[p], pumaIncidenceTable[p], pumaInitialWeights[p], importanceWeights, pumaFinalWeights[p], pumaRelaxationFactors[p], totalHouseholdsControlIndex, RUN_THIS_PUMA_ONLY > 0 && puma == RUN_THIS_PUMA_ONLY );                               
	        int[] finalIntegerWeightTotals = getIntegerWeightTotals( pumaIncidenceTable[p], finalHouseholdIntegerWeights );

	        ReturnData rd = tazAllocation( puma, pumaTazList[p], tazPromotion[p], pumaTazControls[p], finalHouseholdIntegerWeights, pumaIncidenceTable[p], pumaInitialWeights[p], pumaHhids[p], totalHouseholdsControlIndex );

	        int[] mazIndex = geogManager.getMazIndices();
	        List<Integer>[] tazMazsList = geogManager.getTazMazsList();
	        int[] mazMetaValues = geogManager.getMazMetaValues();
	        int[] mazPumaValues = geogManager.getMazPumaValues();
	        
	        
	        int[][] tempIndices = rd.getIndices();
	        int[][] tempWeights = rd.getWeights();
	        int[][] tempIds = rd.getIds();
	        long[] runTimes = rd.getRunTimes();	        
	        runtime = String.format( "TAZ: lb=%.1f, int=%.1f, tot=%.1f", (runTimes[0]/1000.0), (runTimes[1]/1000.0),  (runTimes[2]/1000.0) );

	        ArrayList<Object> resultsList = new ArrayList<Object>();
	        
	    	// create a list of MAZs in each TAZ and an array of MAZ controls for the MAZs in the TAZ.
	        long[] mazRunTimes = new long[4];
			int[] tazMazs = new int[pumaTazList[p].length];
			int[][] tazMazControls = new int[pumaTazList[p].length][];
			for ( int t=0; t < pumaTazList[p].length; t++ ) {

				int taz = pumaTazList[p][t];
				
		    	if ( RUN_THIS_TAZ_ONLY > 0 && taz != RUN_THIS_TAZ_ONLY )
		    		continue;

				if ( pumaTazControls[p][t][totalHouseholdsTazBalancingIncidenceIndex] == 0 )
					continue;

		        int[] tazFinalWeights = new int[pumaFinalWeights[p].length];
		        int[] tazFinalIds = new int[pumaFinalWeights[p].length];
				for ( int i=0; i < tempIndices[t].length; i++ ) {
					int index = tempIndices[t][i];
					tazFinalWeights[index] = tempWeights[t][i];
					tazFinalIds[index] = tempIds[t][i];
				}
				
				tazMazs = new int[tazMazsList[taz].size()];
				tazMazControls = new int[tazMazsList[taz].size()][];
				for ( int m=0; m < tazMazs.length; m++ ) {
					int maz = tazMazsList[taz].get(m);
					tazMazs[m] = maz;
					tazMazControls[m] = mazControls[mazIndex[maz]];
				}
		        
	    		rd = mazAllocation( tazMazs, tazMazControls, tazFinalWeights, pumaIncidenceTable[p], pumaInitialWeights[p], tazFinalIds, totalHouseholdsControlIndex );
		        runTimes = rd.getRunTimes();	        
		        for ( int i=0; i < runTimes.length; i++ )
		        	mazRunTimes[i] += runTimes[i];
	    		
				// make an array of MAZ control totals for MAZs in the TAZ
		        int[] mazMetas = new int[tazMazs.length];
		        int[] mazPumas = new int[tazMazs.length];
				for ( int kk=0; kk < tazMazs.length; kk++ ) {
					int maz = tazMazs[kk];
					int mIndex = mazIndex[maz];
					mazMetas[kk] = mazMetaValues[mIndex];
					mazPumas[kk] = mazPumaValues[mIndex];
				}

				long check = System.currentTimeMillis();
				resultsList.add ( new Object[]{ rd.getWeights(), rd.getIds(), taz, tazMazs, mazPumas, mazMetas, rd.getPumsWts() } );
		        mazRunTimes[3] += ( System.currentTimeMillis() - check );
				
		    }


	        runtime += String.format( ", MAZ: lb=%.1f, int=%.1f, sql=%.1f, tot=%.1f", (mazRunTimes[0]/1000.0), (mazRunTimes[1]/1000.0), (mazRunTimes[3]/1000.0), (mazRunTimes[2]/1000.0) );

	        List<Object> resultBundle = new ArrayList<Object>(4);

            resultBundle.add( p );
            resultBundle.add( taskName );
            resultBundle.add( runtime );
            resultBundle.add( resultsList );

			logger.info( "completed " + taskName );
			
            setResult( resultBundle );
    		
        }
		
    }
        
    
    class ListBalancingTask implements Callable<List<Object>>
    {
		private int taskId;
		private String label;
		private int pi;
		private int id;
		private int[] balancingControls;
		private int[][] incidenceTable;
		private boolean debug;
		
    	public ListBalancingTask ( int taskId, String label, int pi, int id, int[] balancingControls, int[][] incidenceTable, boolean debug ) {
			this.taskId = taskId;
			this.label = label;
			this.pi = pi;
			this.id = id;
			this.balancingControls = balancingControls;
			this.incidenceTable = incidenceTable;
			this.debug = debug;
    	}

    	
    	public List<Object> call()
    	{
    		
    		Logger logger = Logger.getLogger( "lb_" + id );
    		
    		String taskName = "NULL";
            try {
    			taskName = label + "[ " + java.net.InetAddress.getLocalHost().getHostName() + "_" + Thread.currentThread().getName() + "_" + id + " ]"; 
    		}
    		catch (UnknownHostException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            
            
    		List<Object> resultBundle = new ArrayList<Object>(6);

	    	ListBalancer lb = new ListBalancer( pumaInitialWeights[pi], importanceWeights, incidenceTable, balancingControls, totalHouseholdsTazBalancingIncidenceIndex );
	        lb.setDebugControlSet( debug );
	        
	        // set upper bounds for weights to control uniformity during list balancing
	        int numHouseholdRecords = incidenceTable.length;

	        // sum the total pums weights
	        double totalPumsWeights = 0;
	        for ( int n=0; n < numHouseholdRecords; n++ )
	        	totalPumsWeights += pumaInitialWeights[pi][n];
	        
	        int numHouseholds = balancingControls[totalHouseholdsTazBalancingIncidenceIndex];
	        int[] ubWeights = new int[numHouseholdRecords];
	        for ( int n=0; n < numHouseholdRecords; n++ )
	        	ubWeights[n] = Math.max( (int)( (max_expansion_factor * pumaInitialWeights[pi][n] * numHouseholds / totalPumsWeights) + 0.5 ), 1 );
	        lb.setUbWeights( ubWeights );

	        lb.doBalancing( logger );

	        // get the sum of household fractional weights over meta controls
	        double[] metaWeights = getFractionalWeightTotals( incidenceTable, lb.getFinalWeights() );
	        
            resultBundle.add( taskId );
            resultBundle.add( taskName );
			resultBundle.add( lb.getRelaxedControls() );
			resultBundle.add( lb.getFinalWeights() );
			resultBundle.add( lb.getRelaxationFactors() );
			resultBundle.add( metaWeights );

            return resultBundle;
    		
    	}
 
    }
    
}
