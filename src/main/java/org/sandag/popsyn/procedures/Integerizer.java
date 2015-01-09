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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.*;

public class Integerizer
{

    private static Logger logger = Logger.getLogger(Integerizer.class);
    
    // set timeout to 100 seconds
    //private static final long TIME_LIMIT = 360000L;
    
    private static final double MAX_EXP = 999;    
    private static final double EXP_999 = Math.exp( -MAX_EXP );    
    //private static final String CBC_LB_TYPE = "GLPK_MIXED_INTEGER_PROGRAMMING";
    //private static final String CBC_LB_TYPE = "CBC_MIXED_INTEGER_PROGRAMMING";

    private boolean debugControlSet;

    private int[] householdControls;

    private double[] initialWeights;
    private double[] importanceWeights;
    private double[] relaxationFactors;
    private double[] balancedWeights;
    private int[][] incidenceTable;
    private int[] controlTotals;

    private int[] lpRightHandSide;
    private int[] residualIntegerWeights;
    private int[] lpSolution;
    
    private int[] maxIncidenceValue;
    
    private int totalHouseholdsControlIndex;

    
    
    static {
    	try {
            Map<String,String> envMap = System.getenv();
            
            Properties sysProps = System.getProperties();
            Enumeration<?> names = sysProps.propertyNames();
            while (names.hasMoreElements()) {
                String key = (String) names.nextElement();
                String value = (String) sysProps.get(key);
                if ( key.equalsIgnoreCase("java.library.path") )
                	logger.info( "java.library.path" + " = " + value );
                else if ( key.equalsIgnoreCase("java.class.path") )
                	logger.info( "java.class.path" + " = " + value );
                else if ( key.equalsIgnoreCase("user.dir") )
                	logger.info( "user.dir" + " = " + value );
            }
                        	
    		logger.info( "loading ortools-3795.dll ");
    		System.loadLibrary("ortools-3795");
    	}
    	catch ( Exception e ) {
    		System.out.println ( "Exception caught loading jnilinearsolver.dll" );
    		throw new RuntimeException( e );
    	}
    }


    public void setHouseholdBasedControls( int[] householdBasedControls ) {
    	householdControls = householdBasedControls;
    }
    
    
    public void setControlTotals( int[] controlTotals ) {
        this.controlTotals = controlTotals;
        lpRightHandSide = new int[controlTotals.length];
    }
    
    
    public void setIncidenceTable( int[][] incidenceTable ) {
        this.incidenceTable = incidenceTable;
    }
    
    
    public void setTotalHouseholdsControlIndex( int totalHouseholdsControlIndex ) {
        this.totalHouseholdsControlIndex = totalHouseholdsControlIndex;
    }
    
    
    public void setInitialWeights( double[] initialWeights ) {
        this.initialWeights = initialWeights;
    }
    
    
    public void setImportanceWeights( double[] importanceWeights ) {
        this.importanceWeights = importanceWeights;
    }
    
    
    public void setFinalWeights( double[] finalWeights ) {
        this.balancedWeights = finalWeights;
        residualIntegerWeights = new int[finalWeights.length];
        lpSolution = new int[finalWeights.length];
    }
    
    
    public void setRelaxationFactors( double[] relaxationFactors ) {
        this.relaxationFactors = relaxationFactors;
    }
    
    
    public void integerizeReport( double[][] constraintCoeffs, double[] ofCoeffs ) {

        if ( ! debugControlSet )
            return;
        
        // summarize the weighted incidence table for camparing to contraint totals for the current iteration
        double[] constraintCeoffTotals = new double[controlTotals.length];
        for ( int i=0; i < constraintCoeffs.length; i++ )
            for ( int n=0; n < constraintCoeffs[i].length; n++ )
                constraintCeoffTotals[i] += constraintCoeffs[i][n];
        

        logger.info( "Integerize Residual Report" );
        logger.info( String.format( "%10s %10s %16s %16s", "control", "target", "constCoeffs", "constIntegers" ) );
        for ( int i=0; i < constraintCoeffs.length; i++ ) {
            logger.info( String.format( "%10d %10d %16.6e %16d", (i+1), controlTotals[i], constraintCeoffTotals[i], lpRightHandSide[i] ) );
        }
        logger.info("");
        logger.info("");
        logger.info("");
        
    }
        
        
    private double[][] getConstraintCoefficientArray() {
        
    	maxIncidenceValue = new int[controlTotals.length];
        double[][] coeffs = new double[controlTotals.length][];
        for ( int i=0; i < controlTotals.length; i++ ) {
            
            lpRightHandSide[i] = (int)( controlTotals[i] * relaxationFactors[i] + 0.5 );
            coeffs[i] = new double[incidenceTable.length];
            
            for ( int n=0; n < incidenceTable.length; n++ ) {
                int intWeight = (int)balancedWeights[n];
                lpRightHandSide[i] -= ( intWeight * incidenceTable[n][i] );
                if ( lpRightHandSide[i] < 0 )
                    lpRightHandSide[i] = 0;
                
                coeffs[i][n] = incidenceTable[n][i];
                residualIntegerWeights[n] = intWeight;
                
                if( incidenceTable[n][i] > maxIncidenceValue[i] )
                	maxIncidenceValue[i] = incidenceTable[n][i];
            }
            
        }        
        return coeffs;
    }
    
    
    
    
    private double[] getObjectiveFunctionCoefficients() {

        double[] coeffs = new double[balancedWeights.length];

        for ( int n=0; n < balancedWeights.length; n++ ) {
            int intWeight = (int)balancedWeights[n];
            double residWeight = balancedWeights[n] - intWeight;
            if ( residWeight > EXP_999 ) {
                // use negative for coefficients since solver is minimizing
                coeffs[n] = -Math.log( residWeight );
            }
            else {
                // use positive for big coefficient since solver is minimizing
                coeffs[n] = MAX_EXP;
            }
        }            
        
        
        return coeffs;
    }
    
    public void setDebugControlSet( boolean flag ) {
        debugControlSet = flag;
    }
    

    public double[] integerizeCbc( String label, int id, String solverString, boolean isContinuous, double[] previousXValues, long timeoutInSeconds ) {
        
        MPSolver solver = null;
        try {
            //solver = new MPSolver( "IntegerizeCbc", MPSolver.getSolverEnum( solverString ) );
           solver = new MPSolver( "IntegerizeCbc", MPSolver.OptimizationProblemType.valueOf(solverString));
        }
        catch (java.lang.Exception e) {
            throw new Error(e);
        }
        
        
        // calculate residual integer weights and set boolean values for cases where balanced weight is an integer value
        boolean[] balancedWeightIsIntegerValue = new boolean[balancedWeights.length];
        for ( int n=0; n < balancedWeights.length; n++ ) {
            int intWeight = (int)balancedWeights[n];
            residualIntegerWeights[n] = intWeight;
            
            if ( balancedWeights[n] - intWeight == 0 )
            	balancedWeightIsIntegerValue[n] = true;
        }
            

        
        int numVariables = balancedWeights.length;
        int numControls = controlTotals.length;
        
        if ( debugControlSet ) {
            logger.info( "Solving LP with CBC solver:" );
            logger.info( "     " + numVariables + " integer binary variables, " + (numControls-1) + " controls." );
            logger.info( "     " + numControls + " controls, including total household control." );
            logger.info( "     " + (numControls-1) + " GE inequality constraints." );
            logger.info( "     " + (numControls-1) + " LE inequality constraints." );
            logger.info( "     " + "1 equality constraint for total households control." );
        }

        
        // create the inequality constraints
        double[][] constraintCoeffs = getConstraintCoefficientArray();


        // Create binary integer variables
        MPVariable[] x = new MPVariable[numVariables]; 
        for ( int n=0; n < numVariables; n++ ) {
        	
        	// isContinuous is true in order to solve problem as simple continuous LP
        	// used after an attempt to solve discrete MIP timed out
        	if ( isContinuous ) {
        		
        		if ( balancedWeightIsIntegerValue[n] )
            		x[n] = solver.makeNumVar( 0.0, 0.0, "X_"+n );
        		else
            		x[n] = solver.makeNumVar( 0.0, 1.0, "X_"+n );
        			
        	}
        	else {
	        	// isContinuous is false and previousXValues is null for first attempt to solve as discrete MIP
	        	if ( previousXValues == null )
	        		if ( balancedWeightIsIntegerValue[n] )
	            		x[n] = solver.makeIntVar( 0.0, 0.0, "X_"+n );
	        		else
	            		x[n] = solver.makeIntVar( 0.0, 1.0, "X_"+n );
	        	// following timeout, previousXValues consists of 0s, 1s, and some fractions,
	        	// so fix previous 0 and 1 values and create new binary values for discrete MIP
	        	else if ( previousXValues[n] == 0 )
	        		x[n] = solver.makeIntVar( 0.0, 0.0, "X_"+n );
	        	else if ( previousXValues[n] == 1 )
	        		x[n] = solver.makeIntVar( 1.0, 1.0, "X_"+n );
	        	else
	         		x[n] = solver.makeIntVar( 0.0, 1.0, "X_"+n );
        	}
        	
        }
    
        
        int numHHs = (int)( controlTotals[totalHouseholdsControlIndex] * relaxationFactors[totalHouseholdsControlIndex] + 0.5 );
        int[] maxGtConstraintUpperBound = new int[numControls];
        for ( int i=0; i < numControls; i++ ) {
            
            // for person controls only
            if ( householdControls[i] == 0 ) {            	
            	maxGtConstraintUpperBound[i] = Math.max( maxIncidenceValue[i]*numHHs - lpRightHandSide[i], 0 );
            }
            // for household controls only
            else {
            	maxGtConstraintUpperBound[i] = Math.max( numHHs - lpRightHandSide[i], 0 );
            }
            
        }
        
                
        // Create positive continuous constraint relaxation variables
        MPVariable[] y = new MPVariable[numControls]; 
        MPVariable[] z = new MPVariable[numControls]; 
        for ( int i=0; i < numControls; i++ ) {
            
            // don't create variables for total households control
            if ( i == totalHouseholdsControlIndex )
                continue;
                
        	if ( isContinuous ) {
	            y[i] = solver.makeNumVar( 0.0, lpRightHandSide[i], "Y_"+i );
	            z[i] = solver.makeNumVar( 0.0, maxGtConstraintUpperBound[i], "Z_"+i );
        	}
        	else {
	            y[i] = solver.makeIntVar( 0.0, lpRightHandSide[i], "Y_"+i );
	            z[i] = solver.makeIntVar( 0.0, maxGtConstraintUpperBound[i], "Z_"+i );
        	}
        	
        }


        
        // Set objective: min sum{c(n)*x(n)} + 999*y(i) - 999*z(i)}
        double[] ofCoeffs = getObjectiveFunctionCoefficients();
        
        for ( int n=0; n < numVariables; n++ )
            solver.objective().setCoefficient( x[n], ofCoeffs[n] );
        
        for ( int i=0; i < numControls; i++ ) {

            // don't add variables for total households control to objective function
            if ( i == totalHouseholdsControlIndex )
                continue;
                
            solver.objective().setCoefficient( y[i], importanceWeights[i] );
            solver.objective().setCoefficient( z[i], importanceWeights[i] );

        }

        
        double[] ct_ge_bound = new double[numControls];
        
        MPConstraint[] ct_ge = new MPConstraint[numControls];
        MPConstraint[] ct_le = new MPConstraint[numControls];
        for ( int i=0; i < numControls; i++ ) {
            
            // don't add inequality constraints for total households control
            if ( i == totalHouseholdsControlIndex )
                continue;
                
            ct_ge_bound[i] = Math.max( controlTotals[totalHouseholdsControlIndex]*maxIncidenceValue[i], lpRightHandSide[i] );
            
            // add the lower bound relaxation inequality constraint
            ct_ge[i] = solver.makeConstraint( lpRightHandSide[i], ct_ge_bound[i] );
            for ( int n=0; n < numVariables; n++ )
                ct_ge[i].setCoefficient( x[n], constraintCoeffs[i][n] );
            for ( int j=0; j < numControls; j++ )
                if ( i == j && j != totalHouseholdsControlIndex )
                    ct_ge[i].setCoefficient( y[j], 1.0 );
            
            // add the upper bound relaxation inequality constraint
            ct_le[i] = solver.makeConstraint( 0, lpRightHandSide[i] );
            for ( int n=0; n < numVariables; n++ )
                ct_le[i].setCoefficient( x[n], constraintCoeffs[i][n] );
            for ( int j=0; j < numControls; j++ )
                if ( i == j && j != totalHouseholdsControlIndex )
                    ct_le[i].setCoefficient( z[j], -1.0 );

        }
        

        // add an equality constraint for the total households control
        MPConstraint ct_eq = solver.makeConstraint( lpRightHandSide[totalHouseholdsControlIndex], lpRightHandSide[totalHouseholdsControlIndex] );
        for ( int n=0; n < numVariables; n++ )
            ct_eq.setCoefficient( x[n], constraintCoeffs[totalHouseholdsControlIndex][n] );

        if ( debugControlSet ) {
        	dumpLPInfo( "outputs/lp_info_" + label + "_" + id + "_" + (isContinuous ? "continuous" : "discrete" ) + ".csv" );		
    		logger.info( "lp for:" + label + "_" + id );
    	}
		//logger.info( "lp for:" + label + "_" + id );
        
    	solver.setTimeLimit( timeoutInSeconds*1000 );
        ResultStatus resultStatus = solver.solve();

        double totValues = 0;
        double[] continuousSolution = new double[numVariables];
        for ( int n=0; n < numVariables; n++ ) {
            double value = x[n].solutionValue();            
            lpSolution[n] = (int)(value + 0.5);
            continuousSolution[n] = value;
            totValues += value;
        }

        // Check that the problem has an optimal solution.
        if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
          logger.error("The problem with " + label + "=" + id + " did not find an optimal " + (isContinuous ? "continuous" : "discrete" ) + " solution in " + timeoutInSeconds + " seconds!");          
          writeFinalWeights(  "outputs/debugWeights_" + label + "_" + id + "_no_soln " + "_" + timeoutInSeconds + ".csv", ofCoeffs, constraintCoeffs, continuousSolution );
          dumpLPInfo( "outputs/failed_lp_info_" + label + "_" + id + "_" + timeoutInSeconds + ".csv" );
          return null;
        }

        if ( debugControlSet )
        	writeFinalWeights(  "outputs/debugWeights_" + label + "_" + id + "_with_soln.csv", ofCoeffs, constraintCoeffs, continuousSolution );
        

        double[] xSolution = new double[numVariables];
        double[] ySolution = new double[numControls];
        double[] zSolution = new double[numControls];
        for ( int i=0; i < numControls; i++ ) {
            
            if ( i == totalHouseholdsControlIndex )
                continue;
            
            double value = y[i].solutionValue();            
            ySolution[i] = value;
            value = z[i].solutionValue();            
            zSolution[i] = value;
        }

        for ( int i=0; i < numVariables; i++ ) {            
            double value = x[i].solutionValue();            
            xSolution[i] = value;
        }

        if ( debugControlSet ) {
        
            logger.info("Problem solved in " + solver.wallTime() + " milliseconds");

            // The objective value of the solution.
            logger.info("Optimal objective value = " + solver.objective().value());

            logger.info( String.format("%-20s  %20s  %20s  %20s", "control", "LP RHS", "y solution", "z solution" ) );
            for ( int i=0; i < numControls; i++ )
                if ( i != totalHouseholdsControlIndex )
                    logger.info( String.format("%-20d  %20d  %20.13e  %20.13e", i, lpRightHandSide[i], ySolution[i], zSolution[i] ) );
    
            logger.info( String.format("%-10s  %10s  %10s", "control", "x solution", "resid int" ) );
            for ( int i=0; i < numVariables; i++ )
                    logger.info( String.format("%-20d  %20.13e  %20d", i, xSolution[i], residualIntegerWeights[i] ) );
    
            System.out.println("Advanced usage:");
            System.out.println("Problem solved in " + solver.nodes() + " branch-and-bound nodes");

        
            writeFinalWeights(  "outputs/debugWeights_" + numControls + ".csv", ofCoeffs, constraintCoeffs,  continuousSolution );
        }
        
        return continuousSolution;

    }


    private void writeFinalWeights( String fileName, double[] ofCoeffs, double[][] constraintCoeffs, double[] continuousSolution ) {
        
        try {

            PrintWriter outStream = new PrintWriter( new BufferedWriter( new FileWriter( fileName ) ) );

            //write household file header record
            String header = "hhid, tot, hh, gq, h1, h2, h3, h4, i1, i2, i3, i4, i5, a1, a2, a3, a4, ft1, ft2, c1, c2, c3, c4, u1, u2, u3, u4, u5, u6, u7, u8, u9, u10, u11, u12, u13, u14, u15, u16, u17, u18, u19, u20, u21, u22, u23, u24, u25, u26, u27, u28, u29, u30, u31, u32, u33, u34, u35, u36, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17, i18, i19, i20, i21, i22, i23, i24, OF_Coeff, HH_WT, Balanced_WT, LP_Int_Soln, LP_Fractional_Soln, Resid_Ints";
            outStream.println( header );

            for ( int n=0; n < balancedWeights.length; n++ ) {
                outStream.print( (n+1) + "," );
                for ( int j=0; j < constraintCoeffs.length; j++ )
                    outStream.print( constraintCoeffs[j][n] + "," );
                outStream.println( ofCoeffs[n] + ","
                        + initialWeights[n] + ","
                        + (balancedWeights[n] < 1.0e-200 ? 0 : balancedWeights[n] ) + ","
                        + lpSolution[n] + ","
                        + continuousSolution[n] + ","
                        + residualIntegerWeights[n]
                );
            }

            outStream.close();

        }
        catch (IOException e) {
            logger.fatal("IO Exception writing file for checking integerizing procedure: " + fileName, e );
        }
        
    }
    
    
    private void dumpLPInfo( String fileName ) {
        
        try {

            PrintWriter outStream = new PrintWriter( new BufferedWriter( new FileWriter( fileName ) ) );

            //write household file header record
            String header = "incidenceTable";
            outStream.println( header );
            for ( int n=0; n < incidenceTable.length; n++ ) {
            	for ( int i=0; i < incidenceTable[n].length-1; i++ )
                    outStream.print( incidenceTable[n][i] + "," );
                outStream.println( incidenceTable[n][incidenceTable[n].length-1] );
            }                    
            outStream.println( "" );

            
            header = "initialWeights";
            outStream.println( header );
            for ( int n=0; n < initialWeights.length-1; n++ )
                outStream.print(  initialWeights[n] + "," );
            outStream.println( initialWeights[initialWeights.length-1] );
            outStream.println( "" );
            

            header = "balancedWeights";
            outStream.println( header );
            for ( int n=0; n < balancedWeights.length-1; n++ )
                outStream.print(  balancedWeights[n] + "," );
            outStream.println( balancedWeights[balancedWeights.length-1] );
            outStream.println( "" );
            

            header = "residualIntegerWeights";
            outStream.println( header );
            for ( int n=0; n < residualIntegerWeights.length-1; n++ )
                outStream.print(  residualIntegerWeights[n] + "," );
            outStream.println( residualIntegerWeights[residualIntegerWeights.length-1] );
            outStream.println( "" );
            

            header = "importanceWeights";
            outStream.println( header );
            for ( int n=0; n < importanceWeights.length-1; n++ )
                outStream.print(  importanceWeights[n] + "," );
            outStream.println( importanceWeights[importanceWeights.length-1] );
            outStream.println( "" );
            

            header = "relaxationFactors";
            outStream.println( header );
            for ( int n=0; n < relaxationFactors.length-1; n++ )
                outStream.print(  relaxationFactors[n] + "," );
            outStream.println( relaxationFactors[relaxationFactors.length-1] );
            outStream.println( "" );
            

            header = "controlTotals";
            outStream.println( header );
            for ( int n=0; n < controlTotals.length-1; n++ )
                outStream.print(  controlTotals[n] + "," );
            outStream.println( controlTotals[controlTotals.length-1] );
            outStream.println( "" );
            

            header = "lpRightHandSide";
            outStream.println( header );
            for ( int n=0; n < lpRightHandSide.length-1; n++ )
                outStream.print(  lpRightHandSide[n] + "," );
            outStream.println( lpRightHandSide[lpRightHandSide.length-1] );
            outStream.println( "" );            

            outStream.close();

        }
        catch (IOException e) {
            logger.fatal("IO Exception writing file for checking integerizing procedure: " + fileName, e );
        }
        
    }
    
    
    public int[] getresidualIntegerizedWeights() {
        return residualIntegerWeights;
    }
    
    public int[] getFinalIntegerizedWeights() {
        
        int[] finalSolution = new int[lpSolution.length];
        
        for ( int n=0; n < lpSolution.length; n++ )
            finalSolution[n] = lpSolution[n] + residualIntegerWeights[n];
            
        return finalSolution;
        
    }
    
    public int[] getFinalIntegerizedWeights( int[] tempWeights ) {
        
        int[] finalSolution = new int[tempWeights.length];
        
        for ( int n=0; n < tempWeights.length; n++ )
            finalSolution[n] = tempWeights[n] + residualIntegerWeights[n];
            
        return finalSolution;
        
    }
    
}
