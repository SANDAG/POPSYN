/*
 * Copyright 2011 San Diego Association of Governments (SANDAG)
 * 
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package org.sandag.popsyn.validator;

import org.sandag.common.datafile.TableDataManager;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.io.IValidatorDao;
import com.pb.common.datafile.TableDataSet;

/**
 * This class calculates validation measures by comparing the observed data and estimated PopSyn II results. It also writes out the outputs.
 * 
 */
public class Validator
{
    /**
     * The valMetricsCalculator object.
     */
    protected ValMetricsCalculator vMetrics  = null;
    /**
     * The Version object
     */
    protected Version              version   = null;
    /**
     * The mean difference between estimated and observed validation attributes across all PUMAs indexed by validation attribute ID.
     */
    protected double[]             meanDiff  = null;
    /**
     * The standard deviation of the percentage difference between estimated and observed validation attributes across all PUMAs indexed by validation
     * attribute ID.
     */
    protected double[]             stdDev    = null;
    /**
     * The maximum percentage difference between estimated and observed validation attributes across all PUMAs indexed by validation attribute ID.
     */
    protected double[]             maxDiff   = null;
    /**
     * The minimum percentage difference between estimated and observed validation attributes across all PUMAs indexed by validation attribute ID.
     */
    protected double[]             minDiff   = null;
    /**
     * The percentage difference between estimated and observed validation attributes where the first dimension is the PUMA ID and the second
     * dimension is the validation attribute ID.
     */
    protected double[][]           diff      = null;
    /**
     * The validation measures based on results from a PopSyn II run. The first dimension is PUMA ID and the second dimension is validation attribute
     * ID.
     */
    protected double[][]           perSynPop = null;
    /**
     * The validation measures based on observed data tables. The first dimension is PUMA ID and the second dimension is validation attribute ID.
     */
    protected double[][]           perCensus = null;
    /**
     * validation attribute count.
     */
    protected int                  valCount;
    /**
     * PUMA count
     */
    protected int                  pumaCount;

    /**
     * Creates a Validator object with a specified Version object and ValidatorDao object
     * 
     * @param aVersion
     *            the Version object for this PopSyn II run
     * @param vDao
     *            the ValidatorDao object
     */
    public Validator(Version aVersion, IValidatorDao vDao)
    {
        this.version = aVersion;
        vMetrics = new ValMetricsCalculator(version, vDao);
        valCount = vMetrics.getValCount();
        pumaCount = version.getNumPumas();
        perSynPop = vMetrics.getPerSynPop();
        perCensus = vMetrics.getPerCensus();
    }

    /**
     * Calculates validation measures and writes results to output tables.
     * 
     * @param vDao
     *            the ValidatorDao object
     */
    public void reportOutput(IValidatorDao vDao)
    {
        calculate();
        writeResult();
        writeResultByPuma();
    }

    /**
     * Calculates the validation measures by comparing observed and estimated PopSyn II results. The measures include mean, minimum, maximum
     * differences and standard deviation.
     */
    private void calculate()
    {
        // initialize
        meanDiff = new double[valCount];
        stdDev = new double[valCount];
        maxDiff = new double[valCount];
        minDiff = new double[valCount];
        for (int i = 0; i < valCount; i++)
        {
            maxDiff[i] = -9999999f;
            minDiff[i] = 9999999f;
        }

        // start computing
        diff = new double[pumaCount][valCount];
        double[] tempSum = new double[valCount];

        for (int j = 0; j < valCount; j++)
        {
            for (int i = 0; i < pumaCount; i++)
            {
                // calculate diffs
                if (perCensus[i][j] != 0)
                {
                    diff[i][j] = ((perSynPop[i][j] - perCensus[i][j]) / perCensus[i][j]) * 100.0;
                }

                // set diff sum for each validation metric
                tempSum[j] += diff[i][j];

                // set min and max diffs for each validation metric
                if (diff[i][j] > maxDiff[j])
                {
                    maxDiff[j] = diff[i][j];
                }
                if (diff[i][j] < minDiff[j])
                {
                    minDiff[j] = diff[i][j];
                }
            }
            // set mean diff for each validation metric
            meanDiff[j] = tempSum[j] / pumaCount;
        }

        // calculate standard deviations for each validation metric
        for (int j = 0; j < valCount; j++)
        {
            tempSum[j] = 0;
            for (int i = 0; i < pumaCount; i++)
            {
                tempSum[j] += Math.pow(diff[i][j] - meanDiff[j], 2.0);
            }
            stdDev[j] = (double) Math.sqrt(tempSum[j] / (pumaCount - 1));
        }

    }

    /**
     * Writes the validation measures to a CSV file.
     */
    private void writeResult()
    {
        TableDataSet table = new TableDataSet();
        table.appendColumn(setLabels(valCount), "Label");
        table.appendColumn((new Labels(version)).getLabels(), "Description");
        table.appendColumn(vMetrics.getPopSynSum(), "popSynSum");
        table.appendColumn(vMetrics.getObservedSum(), "censusSum");
        table.appendColumn(meanDiff, "meanDiff");
        table.appendColumn(stdDev, "stdDev");
        table.appendColumn(minDiff, "minDiff");
        table.appendColumn(maxDiff, "maxDiff");

        TableDataManager.writeTable(version.getWorkDir(), "validationStats_v" + version.getId()
                + ".csv", table);

    }

    /**
     * Writes the validation measures by PUMA to a CSV file.
     */
    private void writeResultByPuma()
    {
        TableDataSet table = new TableDataSet();
        String[] labels = (new Labels(version)).getLabels();
        table.appendColumn(setLabels(pumaCount), "PUMA");
        double[][] pumaDiff = pivotArray(diff);
        for (int i = 0; i < labels.length; i++)
        {
            table.appendColumn(pumaDiff[i], (i + 1) + "-" + labels[i]);
        }

        TableDataManager.writeTable(version.getWorkDir(),
                "validationStatsByPuma_v" + version.getId() + ".csv", table);
    }

    /**
     * Pivots a two dimensional array.
     * 
     * @param array
     *            the two dimensional array
     * @return the pivoted two dimensional array
     */
    private double[][] pivotArray(double[][] array)
    {
        int dim1 = array.length;
        int dim2 = array[0].length;
        double[][] result = new double[dim2][dim1];
        for (int i = 0; i < dim1; i++)
        {
            for (int j = 0; j < dim2; j++)
            {
                result[j][i] = array[i][j];
            }
        }
        return result;
    }

    /**
     * Sets the sequential IDs of the validation attributes
     * 
     * @param count
     *            number of validation attributes
     * @return the single dimensional array of validation attribute IDs in String format
     */
    private String[] setLabels(int count)
    {
        String[] labels = new String[count];
        for (int i = 0; i < count; i++)
        {
            labels[i] = (i + 1) + "";
        }
        return labels;
    }

    /**
     * Gets the Version object.
     * 
     * @return the Version object
     */
    public Version getVersion()
    {
        return version;
    }

    /**
     * Returns the observed validation attributes.
     * 
     * @return a two dimensional array of percentage values with the first dimension as PUMA ID and the second dimension as the validation attribute
     *         ID.
     */
    public double[][] getPerCensus()
    {
        return perCensus;
    }

    /**
     * Returns the estimated validation attributes.
     * 
     * @return a two dimensional array of percentage values with the first dimension as PUMA ID and the second dimension as the validation attribute
     *         ID.
     */
    public double[][] getPerSynPop()
    {
        return perSynPop;
    }

    /**
     * Returns the mean percentage difference between observed and estimated values by validation attributes across all PUMAs.
     * 
     * @return a single dimensional array of mean percentage differences indexed by validation attribute ID.
     */
    public double[] getMeanDiff()
    {
        return meanDiff;
    }

    /**
     * Gets the standard deviation of percentage value differences between observed and estimated by validation attributes across all PUMAs.
     * 
     * @return a single dimensional array of standard deviations indexed by validation attribute ID.
     */
    public double[] getStdDev()
    {
        return stdDev;
    }

    /**
     * Gets the maximum percentage difference between observed and estimated values by validation attributes across all PUMAs.
     * 
     * @return a single dimensional array of maximum percentage values indexed by validation attribute ID.
     */
    public double[] getMaxDiff()
    {
        return maxDiff;
    }

    /**
     * Returns the minimum percentage difference between observed and estimated values by validation attributes across all PUMAs.
     * 
     * @return a single dimensional array of minimum percentage values indexed by validation attribute ID.
     */
    public double[] getMinDiff()
    {
        return minDiff;
    }

}
