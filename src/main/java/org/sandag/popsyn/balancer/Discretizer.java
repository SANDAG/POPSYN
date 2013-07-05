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
package org.sandag.popsyn.balancer;

import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import org.sandag.common.montecarlo.Distribution;
import org.sandag.common.montecarlo.IWeightedObject;
import org.sandag.common.montecarlo.MonteCarloChoice;
import org.sandag.common.montecarlo.RandomSeedManager;

/**
 * The discretizer integerizes the fractional portion of weights while maintaining the marginal totals. This is necessary because the balanced weights
 * of PUMS sample households and group quarters are non-integer values.
 * 
 * @param <T>
 *            takes a generic type argument such as Household
 */
public class Discretizer<T extends IWeightedObject>
{
    /**
     * Maps between balance objects and the integer portion of their weight
     */
    protected HashMap<Long, Double> baseWeightMap;

    /**
     * The Monte Carlo sampling procedure used to select objects to integerize
     */
    protected MonteCarloChoice<T>   mcc;

    /**
     * Constructs a new Discretizer object with the given balance zone, version object, and random seed manager.
     * 
     * @param bzone
     *            the balance zone (typically, TAZ)
     * @param aVersion
     *            the version object for this PopSyn run
     * @param rdm
     *            the random seed manager
     */
    public Discretizer(int bzone, boolean isFixRandomSeed, RandomSeedManager<Integer> rdm)

    {
        mcc = new MonteCarloChoice<T>();
        
        if (isFixRandomSeed)
            mcc.setSeed(rdm.getSeedByKeyBySetIndex(bzone, 1));
    }

    /**
     * Calculates a distribution using the fractional part of balanced weights
     * 
     * @param discretizeObjs
     * @return returns a Distribution object
     */
    private Distribution<T> setFractionDistribution(Set<T> discretizeObjs)
    {
        Distribution<T> result = new Distribution<T>();
        double weight = -1;

        for (T obj : discretizeObjs)
        {

            weight = obj.getWeight();
            obj.setModifiedWeight(weight - Math.floor(weight));
        }
        result.setObjs(discretizeObjs);
        return result;
    }

    /**
     * Calculates the sum of the integer part of the weights of objects
     * 
     * @param objs
     *            the balance objects
     * @return returns a double that equals the sum of the integer part of the weights of objects
     */
    private double calBaseVal(Set<T> objs)
    {
        double result = 0;
        double baseWeight = -1;
        baseWeightMap = new HashMap<Long, Double>();
        for (T obj : objs)
        {
            baseWeight = Math.floor(obj.getWeight());
            baseWeightMap.put(obj.getId(), baseWeight);
            result += baseWeight;
        }
        return result;
    }

    /**
     * Determines whether an object is one of the objects that has been selected to be integerized
     * 
     * @param thisObj
     *            the object to check
     * @param disObjs
     *            the set of objects selected for integerization
     * @return returns a boolean where true indicates that the object has been selected
     */
    private boolean isDiscretizeObj(T thisObj, Set<T> disObjs)
    {
        boolean result = false;
        for (T obj : disObjs)
        {
            if (thisObj.getId() == obj.getId())
            {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Integerizes the fractional portion of weights while maintaining the marginal totals. First, the sum of the integer part of the weights of
     * objects is calculated. Next, the number of objects to be integerized is calculated. A distribution using the fractional parts of balanced
     * weights is calculated. The method then calculates the accumulative distribution and randomly selects objects to integerize according to that
     * distribution.
     * 
     * @param objs
     *            The balance objects (HH or GQ) to discretize
     * @param targetVal
     *            the zonal HH or GQ total
     * @return returns a set of discretized objects
     */
    public SortedSet<T> discretize(SortedSet<T> objs, double targetVal)
    {
        double baseVal = calBaseVal(objs);
        int allocateAmount = (int) Math.round(targetVal - baseVal);
        Distribution<T> fractionDist = setFractionDistribution(objs);
        TreeMap<Double, T> accDist = fractionDist.getAccDist();
        mcc.setAccDist(accDist);
        Set<T> discretizeObjs = mcc.getAlts(allocateAmount);

        double newWeight = -1;
        for (T obj : objs)
        {
            if (isDiscretizeObj(obj, discretizeObjs))
            {
                newWeight = 1 + baseWeightMap.get(obj.getId());
            } else
            {
                newWeight = baseWeightMap.get(obj.getId());
            }
            obj.setModifiedWeight(newWeight);
        }

        return objs;
    }
}
