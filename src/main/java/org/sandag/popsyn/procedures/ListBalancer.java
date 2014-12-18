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

package org.sandag.popsyn.procedures;

import java.util.Arrays;
import org.apache.log4j.Logger;
import org.sandag.popsyn.methods.ListBalanceIf;

public class ListBalancer
        implements ListBalanceIf
{

    private Logger logger = Logger.getLogger(ListBalancer.class);
    
    private static final int MAX_ITERATION = 100000;
    private static final double MAX_GAP = 1.0e-9;
    private static final double IMPORTANCE_ADJUST = 2;
    private static final int IMPORTANCE_ADJUST_COUNT = 100;
    private static final double MINIMUM_IMPORTANCE = 1.0;
    private static final double MAXIMUM_RELAXATION_FACTOR = 1000000;    
    private static final double MIN_CONTROL_VALUE = 0.1;
    
    
    private int tazTotalHouseholdsControlIndex;

    private boolean debugControlSet;
    private double[] initialWeights;
    private int[] controlTotals;
    private int[][] incidenceTable;

    private double[] importanceWeights;
    private double[] relaxationFactors;
    private double[] finalWeights;
    private int[] lbWeights;
    private int[] ubWeights;
    
    
    /**
     * Construct the ListBalancer object.  Initial weights, incidence table, and constraint totals are required
     * elements, and must be passed as arguments for object creation.
     * 
     * An additional array of importance weights, with the same dimension as initial weights, may be optionally specified
     * by calling: setImportanceWeights(double[] weights).  If this optional method is not called, importance weights
     * retain their default values of 1.0.
     * 
     * @param initialWeights initial variable weights for the list of elements to be balanced.
     * @param incidenceTable incidence table - rows are elements for which factors are calculated, columns are constraints to be satisfied
     * @param constraintTotals column totals which the final weighted incidence table sums must satisfy
     */
    public ListBalancer( double[] initialWeights, double[] importanceWeights, int[][] incidenceTable, int[] constraintTotals, int tazTotalHouseholdsControlIndex ) {
        
        this.initialWeights = initialWeights;
        this.importanceWeights = importanceWeights;
        this.incidenceTable = incidenceTable;
        this.controlTotals = constraintTotals;
        this.tazTotalHouseholdsControlIndex = tazTotalHouseholdsControlIndex;
        
        
        // dimension final weights array from the incidence table dimensions.
        finalWeights = new double[incidenceTable.length];
        
        // dimension and initialize default values for relaxation weights.
        relaxationFactors = new double[constraintTotals.length];
        Arrays.fill( relaxationFactors, 1.0 );

        lbWeights = new int[incidenceTable.length];
        Arrays.fill( lbWeights, 0 );

        ubWeights = new int[incidenceTable.length];
        Arrays.fill( ubWeights, Integer.MAX_VALUE );
        
    }
    
    
    
    public void doBalancing() {
    	doBalancing( null );
    }
    
    
   	public void doBalancing( Logger pumaLogger ) {

        	
        double[] gamma = new double[controlTotals.length];        
        double[] previousIterationWeights = new double[finalWeights.length];
        
        for( int n=0; n < finalWeights.length; n++ ) {
            finalWeights[n] = initialWeights[n];
            previousIterationWeights[n] = initialWeights[n];
        }
        
        
        int[][] incidenceTransposed = new int[controlTotals.length][];
        for ( int i=0; i < controlTotals.length; i++ ) {
            incidenceTransposed[i] = new int[finalWeights.length];
            for ( int n=0; n < finalWeights.length; n++ )                
                incidenceTransposed[i][n] = incidenceTable[n][i];
        }
    
        int[][] incidenceTransposedSquared = new int[controlTotals.length][];
        for ( int i=0; i < controlTotals.length; i++ ) {
            incidenceTransposedSquared[i] = new int[finalWeights.length];
            for ( int n=0; n < finalWeights.length; n++ )                
                incidenceTransposedSquared[i][n] = incidenceTransposed[i][n] * incidenceTransposed[i][n];
        }
    
        
        int nexp = 1;
        double adjust = 1;
        double oldAdjust = 1;
        int finalIter = 0;
        double finalDelta = 0;
        for ( int iter=0; iter < MAX_ITERATION; iter++ ) {

            if ( iter > 0 && iter % IMPORTANCE_ADJUST_COUNT == 0 ) {
                adjust = oldAdjust / IMPORTANCE_ADJUST;
                oldAdjust = adjust;
            }
            
            for ( int i=0; i < controlTotals.length; i++ ) {

                double xx = 0;
                double yy = 0;        
                for ( int n=0; n < finalWeights.length; n++ ) {                
                    xx += finalWeights[n] * incidenceTable[n][i];
                    yy += finalWeights[n] * incidenceTransposedSquared[i][n];
                }
            

                if ( i == tazTotalHouseholdsControlIndex )
                    adjust = 1.0;
                else
                    adjust = oldAdjust;

                
                // calculate constraint balancing factors, gamma[]
                if ( xx > 0 ) {          
            		double tempControl = Math.max( controlTotals[i], MIN_CONTROL_VALUE );
            		gamma[i] = 1.0 - ( xx - ( tempControl * relaxationFactors[i] ) ) / ( yy + ( tempControl * relaxationFactors[i] ) * ( 1.0/( Math.max(importanceWeights[i]*adjust, MINIMUM_IMPORTANCE) ) ) );
                }
                else {
                    gamma[i] = 1.0;
                }
                
                
                for ( int n=0; n < finalWeights.length; n++ ) {                
                    if ( incidenceTable[n][i] > 0 ) {
                        finalWeights[n] *= Math.pow( gamma[i], incidenceTable[n][i] );
                        finalWeights[n] = Math.min( Math.max( finalWeights[n], lbWeights[n] ), ubWeights[n] );
                    }
                }

                double tempFactor = relaxationFactors[i] * ( Math.pow( 1.0/gamma[i], ( 1.0/( Math.max(importanceWeights[i]*adjust, MINIMUM_IMPORTANCE) ) ) ) );
                relaxationFactors[i] = Math.min( MAXIMUM_RELAXATION_FACTOR, tempFactor );
                
            }
            

            
            double maxGammaDiff = 0;
            for ( int i=0; i < gamma.length; i++ ) {
            	double diff = Math.abs( gamma[i] - 1.0 ); 
            	if ( diff > maxGammaDiff )
            		maxGammaDiff = diff;
            }

            
            double delta = 0;
            for ( int n=0; n < finalWeights.length; n++ )
                delta += Math.abs( finalWeights[n] - previousIterationWeights[n] );
            delta /= finalWeights.length;
            
            
            for( int n=0; n < finalWeights.length; n++ )
                previousIterationWeights[n] = finalWeights[n];
            
                
            if ( debugControlSet ) {
                if ( (iter+1) % (Math.pow(2,nexp)) == 0 ) {
                    nexp++;
                    summaryReport( iter, delta, gamma );
                }
                if ( ( delta < MAX_GAP && maxGammaDiff < MAX_GAP) || iter == MAX_ITERATION-1 ) {
                    break;
                }
            }
            else {
                if ( ( delta < MAX_GAP && maxGammaDiff < MAX_GAP) || iter == MAX_ITERATION-1 ) {
                	finalIter = iter; 
                	finalDelta = delta;
                    break;
                }
            }
            
        }
        
    
        int i = tazTotalHouseholdsControlIndex;            
        double xx = 0;
        double yy = 0;        
        for ( int n=0; n < finalWeights.length; n++ ) {                
            xx += finalWeights[n] * incidenceTable[n][i];
            yy += finalWeights[n] * incidenceTransposedSquared[i][n];
        }
    

        if ( i == tazTotalHouseholdsControlIndex )
            adjust = 1.0;
        else
            adjust = oldAdjust;

        
        // calculate constraint balancing factors, gamma[]
        if ( xx > 0 ) {          
    		double tempControl = Math.max( controlTotals[i], MIN_CONTROL_VALUE );
    		gamma[i] = 1.0 - ( xx - ( tempControl * relaxationFactors[i] ) ) / ( yy + ( tempControl * relaxationFactors[i] ) * ( 1.0/( Math.max(importanceWeights[i]*adjust, MINIMUM_IMPORTANCE) ) ) );
        }
        else {
            gamma[i] = 1.0;
        }
        
        
        for ( int n=0; n < finalWeights.length; n++ ) {                
            if ( incidenceTable[n][i] > 0 ) {
                finalWeights[n] *= Math.pow( gamma[i], incidenceTable[n][i] );
                finalWeights[n] = Math.min( Math.max( finalWeights[n], lbWeights[n] ), ubWeights[n] );
            }
        }

        double tempFactor = relaxationFactors[i] * ( Math.pow( 1.0/gamma[i], ( 1.0/( Math.max(importanceWeights[i]*adjust, MINIMUM_IMPORTANCE) ) ) ) );
        relaxationFactors[i] = Math.min( MAXIMUM_RELAXATION_FACTOR, tempFactor );  
        
        if ( pumaLogger != null )
        	summaryReport( pumaLogger, finalIter, finalDelta, gamma );
        if ( debugControlSet )
        	summaryReport( logger, finalIter, finalDelta, gamma );
        
        if ( debugControlSet )
        	logger.info( "i= " + i + ", gamma[i]=" + gamma[i] + ", relaxationFactors[i]=" + relaxationFactors[i] + ", controlTotals[i]=" + controlTotals[i]);

    }
    

    
    private void summaryReport( int iter, double delta, double[] gamma ) {
    	summaryReport( logger, iter, delta, gamma );
    }

    private synchronized void summaryReport( Logger reportLogger, int iter, double delta, double[] gamma ) {
        
        // summarize the weighted incidence table for camparing to contraint totals for the current iteration
        double[] weightedIncidenceTotals = new double[controlTotals.length];
        double[] incidenceTotals = new double[controlTotals.length];
        for ( int n=0; n < incidenceTable.length; n++ ) {
            for ( int i=0; i < controlTotals.length; i++ ) {
                incidenceTotals[i] += ( incidenceTable[n][i] * finalWeights[n] );
                weightedIncidenceTotals[i] += ( incidenceTable[n][i] * finalWeights[n] * (1/relaxationFactors[i]) );
            }
        }
        

        reportLogger.info( " Summary Report for Iteration " + (iter+1) + ", Delta=" + delta );
        reportLogger.info( String.format( "%10s %10s %10s %16s %16s %16s", "control", "target", "current", "currentWtd", "relaxWts", "gamma" ) );
        for ( int i=0; i < controlTotals.length; i++ ) {
            reportLogger.info( String.format( "%10d %10d %16.6f %16.6e %16.6e %16.6e", (i+1), controlTotals[i], incidenceTotals[i], weightedIncidenceTotals[i], relaxationFactors[i], gamma[i] ) );
        }
        reportLogger.info("");
        reportLogger.info("");
        reportLogger.info("");
        
    }
        
    
    public double[] getRelaxedControls() {
        
        double[] relaxedControls = new double[controlTotals.length];
        for ( int i=0; i < controlTotals.length; i++ )
            relaxedControls[i] = controlTotals[i] * relaxationFactors[i];
        
        return relaxedControls;
        
    }
        
    public void setDebugControlSet( boolean flag ) {
        debugControlSet = flag;
    }
    

        
    // These must be implemented for the interface
    
    //@Override
    public double[] getFinalWeights()
    {
        return finalWeights;
    }

    public double[] getRelaxationFactors()
    {
        return relaxationFactors;
    }

    //@Override
    public void setConstraintTotals(int[] total)
    {
        controlTotals = total;
    }

    //@Override
    public void setIncidenceTable(int[][] incidence)
    {
        incidenceTable = incidence;
    }

    //@Override
    public void setInitialWeights(double[] weights)
    {
        initialWeights = weights;
    }

    public void setLbWeights(int[] weights)
    {
        lbWeights = weights;
    }

    public void setUbWeights(int[] weights)
    {
        ubWeights = weights;
    }

}
