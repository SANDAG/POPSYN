/*
 * Copyright 2011 San Diego Association of Governments (SANDAG)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.sandag.popsyn.balancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.controls.IControlDefinition;
import org.sandag.popsyn.domain.Household;

/**
 * This class determines and records the convergence status for a particular TAZ.  
 * It records convergence status, a list of non-converged controls (if applicable), 
 * the proportion differences between PopSyn and observed values by control category, and 
 * the control for which the PopSyn value and the observed value differs to the greatest extent.
 *
 */
public class ConvergenceStat
{
    /**
     * The zone being balanced; the zone to check the convergence status of
     */
    protected int                                              zone;
    
    /**
     * The Household objects (HHs and GQs) that have been selected for balancing
     */
    protected Set<Household>                                   balanceObjs;
    
    /**
     * The zonal control targets that the balancer is trying to match
     */
    protected Hashtable<IControlDefinition<Household>, Double> zTargets;
    
    /**
     * For each control category, this Hashtable stores the zonal sum of PUMS 
     * weights over all balance objects that participate in the control category
     */
    protected Hashtable<IControlDefinition<Household>, Double> zPumsVals;
    
    /**
     * The proportion differences between PopSyn and observed values.  This HashMap maps
     * between control category and the proportion difference that applies to that category.
     */
    protected HashMap<IControlDefinition<Household>, Double>   diffs;

    /**
     * The largest proportion difference between a PopSyn value and an observed value
     */
    protected double                                           maxDiff = -999;
    
    /**
     * The control with the largest proportion difference between PopSyn and observed value
     */
    protected IControlDefinition<Household>                    maxDiffControl;
    
    /**
     * A list of any non-converged controls
     */
    protected List<IControlDefinition<Household>>              nonConvergedControls;
    
    /**
     * A boolean indicating whether the zone is converged
     */
    protected boolean                                          isConverged;
    
    /**
     * Stores parameter/version information for this PopSyn run
     */
    protected Version                                          version;

    /**
     * Creates a new ConvergenceStat object with the specified zone, balance objects,
     * control targets, and version object.
     * @param aZone the zone to record the convergence status of
     * @param theBalanceObjs    the balance objects (households / group quarters)
     * @param theZTargets   the zonal control targets
     * @param aVersion  the version object for this PopSyn run
     */
    public ConvergenceStat(int aZone, Set<Household> theBalanceObjs,
            Hashtable<IControlDefinition<Household>, Double> theZTargets, Version aVersion)
    {
        this.zone = aZone;
        this.balanceObjs = theBalanceObjs;
        this.zTargets = theZTargets;
        this.version = aVersion;
        zPumsVals = new Hashtable<IControlDefinition<Household>, Double>();
    }

    /**
     * Gets the zone being balanced (the zone to check the convergence status of)
     * @return  returns an integer referring to the zone id
     */
    public int getZone()
    {
        return zone;
    }

    /**
     * Sets the zone being balanced (the zone to check the convergence status of)
     * @param zone  an integer referring to the zone id
     */
    public void setZone(int zone)
    {
        this.zone = zone;
    }

    /**
     * Gets the objects that have been selected for balancing
     * @return  returns a Set of Household objects (HHs and GQs)
     */
    public Set<Household> getBalanceObjs()
    {
        return balanceObjs;
    }

    /**
     * Sets the objects that are selected for balancing
     * @param balanceObjs   a Set of Household objects
     */
    public void setBalanceObjs(Set<Household> balanceObjs)
    {
        this.balanceObjs = balanceObjs;
    }

    /**
     * Gets the zonal control targets that the balancer is trying to match
     * @return  the zonal control targets
     */
    public Hashtable<IControlDefinition<Household>, Double> getZTargets()
    {
        return zTargets;
    }

    /**
     * Gets the zonal control target for a particular control definition
     * @param def   the control definition to retrieve the target for
     * @return  returns the zonal target
     */
    public double getZTarget(IControlDefinition<Household> def)
    {
        return zTargets.get(def);
    }

    /**
     * Gets the control definitions
     * @return  Returns a Set of control definitions
     */
    public Set<IControlDefinition<Household>> getControlDefs()
    {
        return zTargets.keySet();
    }

    /**
     * Gets the proportion differences between PopSyn and observed values for 
     * a particular control category
     * @param def   a control definition
     * @return  returns a double equaling the proportion difference
     */
    public double getDiff(IControlDefinition<Household> def)
    {
        return diffs.get(def);
    }

    /**
     * Gets the zonal sum of PUMS weights over all balance object that participate 
     * in a particular control category
     * @param def   a control definition
     * @return  returns a double equaling the zonal sum of PUMS weights
     */
    public double getPumsVal(IControlDefinition<Household> def)
    {
        return zPumsVals.get(def);
    }

    /**
     * Sets the zonal control targets
     * @param targets   the zonal control targets
     */
    public void setZTargets(Hashtable<IControlDefinition<Household>, Double> targets)
    {
        zTargets = targets;
    }

    /**
     * Gets the list of any non-converged controls
     * @return  returns a list of the non-converged controls
     */
    public List<IControlDefinition<Household>> getNonConvergedControls()
    {
        return nonConvergedControls;
    }

    /**
     * Sets the list of non-converged controls
     * @param nonConvergedControls  a list of non-converged controls
     */
    public void setNonConvergedControls(List<IControlDefinition<Household>> nonConvergedControls)
    {
        this.nonConvergedControls = nonConvergedControls;
    }

    /**
     * Checks the convergence status of a particular balance zone
     * @return  returns a boolean indicating whether the zone is converged
     */
    public boolean isConverged()
    {
        updateConvergenceStats();
        return isConverged;
    }

    /**
     * Determines the convergence status of a particular balance zone.  For each 
     * control category, the proportion difference between the zonal target value
     * and the zonal sum of balance object weights (for objects participating in the
     * control category) is calculated.  If the absolute proportion difference for 
     * every control category is less than the convergence criteria, then the zone
     * is considered to be converged.  The proportion differences for all controls
     * are stored in a HashMap.  The maximum proportion difference and the control
     * category that corresponds to this maximum difference are recorded.
     */
    public void updateConvergenceStats()
    {
        diffs = new HashMap<IControlDefinition<Household>, Double>();
        nonConvergedControls = new ArrayList<IControlDefinition<Household>>();

        Set<Entry<IControlDefinition<Household>, Double>> entries = zTargets.entrySet();
        for (Entry<IControlDefinition<Household>, Double> entry : entries)
        {
            IControlDefinition<Household> def = entry.getKey();
            double pumsVal = BalanceObjsSelector.calPumsVal(balanceObjs, def);
            zPumsVals.put(def, pumsVal);
            double targetVal = zTargets.get(def);
            double val = 0.0;
            if (targetVal > 0) val = Math.abs((pumsVal / targetVal - 1.0));
            diffs.put(def, val);

            if (val > version.getConvergenceCriteria())
            {
                nonConvergedControls.add(entry.getKey());
            }

            if (val > maxDiff)
            {
                maxDiff = val;
                maxDiffControl = def;
            }
        }
        isConverged = version.isConverged(maxDiff);
    }
}
