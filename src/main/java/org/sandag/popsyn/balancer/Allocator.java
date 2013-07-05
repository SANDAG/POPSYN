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

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import org.sandag.common.montecarlo.Distribution;
import org.sandag.common.montecarlo.MonteCarloChoice;
import org.sandag.common.montecarlo.RandomSeedManager;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.domain.IGeoWeightedObject;
import org.sandag.popsyn.domain.Target;

/**
 * Allocates households and GQs from TAZs to the MGRAs that are within the TAZ. A Monte Carlo procedure is used to select an MGRA for each GQ and
 * household according to the distribution of HH/GQ.
 * 
 * @param <T>
 *            takes a generic type argument such as Target
 */
public class Allocator
{
    /**
     * The distribution object is used to calculate the distribution (and cumulative distribution) across MGRAs of households and group quarters (or
     * some other variable) in the TAZ.
     */
    protected Distribution<Target>     dis;

    /**
     * The Monte Carlo sampling used to select an MGRA based on HH/GQ distributions
     */
    protected MonteCarloChoice<Target> mcc;

    /**
     * Stores parameter/version information for this PopSyn run
     */
    protected Version                  version;

    /**
     * Constructs a new Allocator object with the specified balance zone, version object, and random seed manager.
     * 
     * @param bzone
     *            the balance zone (typically, TAZ)
     * @param aVersion
     *            the version object for this PopSyn run
     * @param rdm
     *            the random seed manager
     */
    public Allocator(int bzone, Version aVersion, RandomSeedManager<Integer> rdm)
    {
        dis = new Distribution<Target>();
        mcc = new MonteCarloChoice<Target>();
        version = aVersion;
        if (version.isFixRandomSeed()) mcc.setSeed(rdm.getSeedByKeyBySetIndex(bzone, 2));
    }

    /**
     * Assigns an allocation geography (typically MGRA) to balance objects using a Monte Carlo procedure. Balance objects are randomly allocated to an
     * MGRA contained in a TAZ according to the distribution of a variable across MGRAs. This variable can be the number of HHs or GQs in each MGRA.
     * The more HHs or GQs there are in a MGRA, the higher the chance that MGRA will be selected for a HH or GQ. The method first builds a
     * distribution of the allocation variable for MGRAs contained in the TAZ and also calculates the accumulative distribution. The method then loops
     * through the discretized balance objects in the TAZ and selects an MGRA for the object using a Monte Carlo procedure.
     * 
     * @param hhs
     *            a Set of Households with discretized weights
     * @param alSet
     *            a Set of Targets to calculate a distribution to allocate according to variable to allocate according to (for example, number of
     *            households by MGRA)
     * @return Returns a set of Households that have been assigned an MGRA
     */
    public ArrayList<IGeoWeightedObject> allocate(SortedSet<IGeoWeightedObject> wgtObjs,
            Set<Target> alSet)
    {
        ArrayList<IGeoWeightedObject> result = new ArrayList<IGeoWeightedObject>();
        dis.setObjs(alSet);
        mcc.setRedraw(true);
        mcc.setAccDist(dis.getAccDist());

        IGeoWeightedObject chh = null;
        for (IGeoWeightedObject wgtObj : wgtObjs)
        {
            int wgt = (int) wgtObj.getWeight();
            for (int i = 0; i < wgt; i++)
            {
                Target alt = mcc.getAlt();
                if (alt != null)
                {
                    chh = (IGeoWeightedObject) wgtObj.clone();
                    chh.setZone(alt.getZone());
                    result.add(chh);
                }
            }
        }
        return result;
    }
}
