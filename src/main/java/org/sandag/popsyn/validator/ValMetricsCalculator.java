/*
 * Copyright 2011 San Diego Association of Governments (SANDAG)


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

import java.util.List;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.io.IValidatorDao;

/**
 * This class calculates the observed and estimated validation attributes. 
 *
 */
public class ValMetricsCalculator
{
    /**
     * The list of observed validation attributes by PUMA.
     */
    protected List<IPumaValMetrics> observedValMetrics = null;
    /**
     * The list of estimated validation attributes by PUMA.
     */
    protected List<IPumaValMetrics> popSynValMetrics   = null;
    /**
     * The array of summed observed values across all PUMAs indexed by validation attribute ID. 
     */
    protected double[]              observedSum        = null;
    /**
     * The array of summed estimated values across all PUMAs indexed by validation attribute ID. 
     */
    protected double[]              popSynSum          = null;
    /**
     * The two dimensional array of estimated percent values with the first dimension as PUMA ID and the second dimension as validation attribute ID.
     */
    protected double[][]            perSynPop          = null;
    /**
     * The two dimensional array of observed percent values with the first dimension as PUMA ID and the second dimension as validation attribute ID.
     */
    protected double[][]            perCensus          = null;
    protected int                   valCount;
    protected int                   pumaCount;

    /**
     * Creates a ValMetricsCalculator object with the specified Version and IValidatorDao objects, 
     * and then calculates the validation attributes for both observed and estimated data. 
     * @param version the Version object
     * @param valDao the IValidatorDao object
     */
    public ValMetricsCalculator(Version version, IValidatorDao valDao)
    {
        observedValMetrics = valDao.getAllFromObserved(version.getSourceId());
        popSynValMetrics = valDao.getAllFromPopSyn(version.getSourceId(), version.getId());
        valCount = version.getValCount();
        pumaCount = version.getNumPumas();
        sum();
        setPercent();
    }

    /**
     * Sums validation attribute values across all PUMAs indexed by validation attribute ID.  
     */
    private void sum()
    {
        observedSum = new double[valCount];
        popSynSum = new double[valCount];
        for (int i = 0; i < pumaCount; i++)
        {
            IPumaValMetrics omByPuma = observedValMetrics.get(i);
            IPumaValMetrics pmByPuma = popSynValMetrics.get(i);
            omByPuma.createMap();
            pmByPuma.createMap();
            for (int j = 0; j < valCount; j++)
            {
                observedSum[j] += omByPuma.getValMetric(j + 1);
                popSynSum[j] += pmByPuma.getValMetric(j + 1);
            }
        }
        replaceWithPct();
    }

    /**
     * Calculates the percentage values for non-universe attributes, then replaces non-universe attributes with percentage values.
     */
    private void replaceWithPct()
    {
        for (int j = 0; j < valCount; j++)
        {
            int universeIndex = observedValMetrics.get(0).getUniverse(j + 1);
            if (universeIndex != (j + 1))
            {
                observedSum[j] = 100 * observedSum[j] / observedSum[universeIndex - 1];
                popSynSum[j] = 100 * popSynSum[j] / popSynSum[universeIndex - 1];
            }
        }
    }

    /**
     * Sets the percentage value of validation attributes by PUMA and by attribute ID for both observed and estimated data.  
     */
    private void setPercent()
    {
        perSynPop = new double[pumaCount][valCount];
        perCensus = new double[pumaCount][valCount];
        for (int i = 0; i < pumaCount; i++)
        {
            IPumaValMetrics omByPuma = observedValMetrics.get(i);
            IPumaValMetrics pmByPuma = popSynValMetrics.get(i);

            for (int j = 0; j < valCount; j++)
            {
                perCensus[i][j] = omByPuma.getValMetricPct(j + 1);
                perSynPop[i][j] = pmByPuma.getValMetricPct(j + 1);
            }
        }
    }

    /**
     * Returns the validation attribute count.
     * @return the number of validation attributes 
     */
    public int getValCount()
    {
        return valCount;
    }

    /**
     * Sets the validation attribute count.
     * @param count the number of validation attributes
     */
    public void setValCount(int count)
    {
        valCount = count;
    }

    /**
     * Returns the estimated two dimensional array of validation attributes (in terms of percentage values) 
     * with the first dimension as PUMA ID and the second dimension as validation attribute ID.
     * @return a two dimensional array of validation attributes (in terms of percentage values) 
     * with the first dimension as PUMA ID and the second dimension as validation attribute ID.
     */
    public double[][] getPerSynPop()
    {
        return perSynPop;
    }

    /**
     * Sets the estimated two dimensional array of validation attributes (in terms of percentage values) 
     * with the first dimension as PUMA ID and the second dimension as validation attribute ID.
     * @param perSynPop a two dimensional array of validation attributes (in terms of percentage values) 
     * with the first dimension as PUMA ID and the second dimension as validation attribute ID.
     */
    public void setPerSynPop(double[][] perSynPop)
    {
        this.perSynPop = perSynPop;
    }

    /**
     * Returns the observed two dimensional array of validation attributes (in terms of percentage values) 
     * with the first dimension as PUMA ID and the second dimension as validation attribute ID.
     * @return a two dimensional array of validation attributes (in terms of percentage values) 
     * with the first dimension as PUMA ID and the second dimension as validation attribute ID.
     */
    public double[][] getPerCensus()
    {
        return perCensus;
    }

    /**
     * Sets the observed two dimensional array of validation attributes (in terms of percentage values) 
     * with the first dimension as PUMA ID and the second dimension as validation attribute ID.
     * @param perCensus a two dimensional array of validation attributes (in terms of percentage values) 
     * with the first dimension as PUMA ID and the second dimension as validation attribute ID.
     */
    public void setPerCensus(double[][] perCensus)
    {
        this.perCensus = perCensus;
    }

    /**
     * Returns the sum of the observed values by validation attributes across all PUMAs indexed by validation attribute ID. 
     * @return a single dimensional array of the sum of the observed values indexed by validation attribute ID
     */
    public double[] getObservedSum()
    {
        return observedSum;
    }

    /**
     * Sets the sum of the observed values across all PUMAs indexed by validation attribute ID. 
     * @param observedSum a single dimensional array of observed values indexed by validation attribute ID
     */
    public void setObservedSum(double[] observedSum)
    {
        this.observedSum = observedSum;
    }

    /**
     * Returns the sum of the estimated values across all PUMAs indexed by validation attribute ID. 
     * @return a single dimensional array of the sum of the observed values indexed by validation attribute ID
     */
    public double[] getPopSynSum()
    {
        return popSynSum;
    }

    /**
     *  Sets the sum of the estimated values across all PUMAs indexed by validation attribute ID.
     * @param popSynSum a single dimensional array of values indexed by validation attribute ID
     */
    public void setPopSynSum(double[] popSynSum)
    {
        this.popSynSum = popSynSum;
    }
}
