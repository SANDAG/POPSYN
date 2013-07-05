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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.log4j.Logger;
import org.sandag.common.montecarlo.RandomSeedManager;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.controls.Classifier;
import org.sandag.popsyn.controls.ControlList;
import org.sandag.popsyn.controls.IControlDefinition;
import org.sandag.popsyn.domain.Household;

/**
 * The balance object selector chooses balance objects (households and GQs) in a way 
 * that their weights won't subsequently be balanced to very small fractional values. 
 * Households and GQs are selected separately, but at the end they are combined as one 
 * balance object set and used in the subsequent balance step.
 *
 */
public class BalanceObjsSelector
{
    private static final Logger                                LOGGER = Logger
                                                                              .getLogger(BalanceObjsSelector.class);
    /**
     * The zonal control targets that the balancer is trying to match.
     * This field is a HashTable that maps between control definitions 
     * (objects that implement IControlDefinition<Household>) and the target
     * values (in the form of a Double) that correspond to each definition.
     */
    protected Hashtable<IControlDefinition<Household>, Double> zTargets;
   
    /**
     * All selected balance objects (HH and GQ together)
     */
    protected Set<Household>                                   balanceObjs;
   
    /**
     * The selected HH balance objects 
     */
    protected Set<Household>                                   balanceHHs;
   
    /**
     * The selected GQ balance objects
     */
    protected Set<Household>                                   balanceGQs;
    
    /**
     * The zonal GQ total
     */
    protected int                                              zonalGQTotal;
    
    /**
     * The zonal HH total
     */
    protected int                                              zonalHHTotal;
    
    /**
     * Classifies PUMS households/GQ by control category.
     */
    protected Classifier                                       classifier;
    
    /**
     * The PUMA (which contains the balance zone) to select balance objects from
     */
    protected int                                              puma;
   
    /**
     * Stores parameter/version information for this PopSyn run
     */
    protected Version                                          version;
    
    /**
     * The RandomSeedManager
     */
    protected Random                                           rm;
    
    /**
     * The balance zone
     */
    protected int                                              bzone;

    /**
     * Constructs a new BalanceObjsSelector object with the specified balance zone,
     * zonal control targets, Classifier, PUMA ID, Version, zonal household total, 
     * zonal group quarters total, and RandomSeedManager.  
     * @param bzone an integer equaling the balance zone ID
     * @param theZTargets   the zonal control targets that the balancer is trying to match.  
     * This takes the form of a HashTable that maps between control definitions (objects 
     * that implement IControlDefinition<Household>) and the target values (in the form of a Double) 
     * that correspond to each definition
     * @param aClassifier   a Classifier object- classifies PUMS households/GQ by control category
     * @param aPuma PUMA ID of the PUMA (which contains the balance zone) to select balance objects from
     * @param aVersion  a Version object- stores parameter/version information for this PopSyn run
     * @param theZonalHHTotal   a double equaling the zonal household total
     * @param theZonalGQTotal   a double equaling the zonal group quarters total
     * @param rdm   a RandomSeedManager object, which ensures that seeds used for 
     * random number generation are the same across PopSyn runs
     */
    public BalanceObjsSelector(int bzone,
            Hashtable<IControlDefinition<Household>, Double> theZTargets, Classifier aClassifier,
            int aPuma, Version aVersion, double theZonalHHTotal, double theZonalGQTotal,
            RandomSeedManager<Integer> rdm)
    {
        this.zTargets = theZTargets;
        this.classifier = aClassifier;
        this.puma = aPuma;
        this.version = aVersion;
        this.zonalGQTotal = (int) Math.round(theZonalGQTotal);
        this.zonalHHTotal = (int) Math.round(theZonalHHTotal);
        rm = new Random();
        if (version.isFixRandomSeed())
        {
            rm.setSeed(rdm.getSeedByKeyBySetIndex(bzone, 0));
        }
        setBalanceObjs();
    }

    /**
     * Gets the balance objects (households and group quarters) that have been selected
     * @return  Returns a Set of Household objects that represent the chosen balance objects
     */
    public Set<Household> getBalanceObjs()
    {
        return balanceObjs;
    }

    /**
     * Chooses balance objects according to the following steps:  Retrieves the 
     * classified objects from the particular PUMA, selects balance objects from 
     * among the classified objects, then adds the selected objects to the balanceObjs 
     * Set of Household objects.
     */
    private void setBalanceObjs()
    {
        balanceObjs = new HashSet<Household>();
        List<ControlList> classifiedHHs = classifier.getClassifiedHHs(puma);
        List<ControlList> classifiedGQs = classifier.getClassifiedGQs(puma);
        balanceHHs = selectObjs(zonalHHTotal, classifiedHHs);
        balanceGQs = selectObjs(zonalGQTotal, classifiedGQs);
        balanceObjs.addAll(balanceHHs);
        balanceObjs.addAll(balanceGQs);
        // Collections.sort(new ArrayList<Household>(balanceObjs));
    }

    /**
     * Selects balance objects in a way that their weights won't be balanced to 
     * very small fractional values.
     * @param zonalTotal    The zonal household total or the zonal group quarters total
     * @param classifiedObjs    The pool of classified objects from the PUMA to select from
     * @return  Returns a set of Household objects that have been selected
     */
    private Set<Household> selectObjs(int zonalTotal, List<ControlList> classifiedObjs)
    {
        Set<Household> result = selectObjFromEachControl(classifiedObjs);
        int count = result.size();
        Set<Household> current;
        while (count < zonalTotal)
        {
            current = selectObjFromEachControl(classifiedObjs);
            result.addAll(current);
            count = count + current.size();
        }
        return result;
    }

    /**
     * Selects one object for each control category.  This guarantees that all
     * controls are balanced if the control targets are greater than 0.  Without
     * this step, sometimes the program can't find a Household object to balance for
     * some controls due to the randomness of the selection procedure.  The 
     * selection uses a random procedure, assuming that all Household objects are
     * equally weighted.  If no Household object is found for a given control, then
     * select another PUMA randomly and then select an object for the control.
     * @param classifiedObjs    The classified objects to select from
     * @return  Returns a HashSet of selected balance objects
     */
    private Set<Household> selectObjFromEachControl(List<ControlList> classifiedObjs)
    {
        Set<Household> result = new HashSet<Household>();
        for (ControlList control : classifiedObjs)
        {
            Double targetVal = zTargets.get(control.getControlDefinition());
            if (null != targetVal && targetVal > 0)
            {
                // Household obj = selectRandomHousehold(control.getControlObjects());
                Household obj = selectRandomHousehold(control.getSortedControlObjects());
                if (obj == null)
                {
                    obj = selectObjFromOtherPumas(control.getControlDefinition());
                    if (obj == null)
                    {
                        LOGGER
                                .info("!!!!!no PUMAs HH found for "
                                        + control.getControlDefinition().getCategory()
                                        + " in PUMA " + puma);
                    }
                }
                if (obj != null) result.add(obj);
            }
        }
        return result;
    }

    /**
     * Selects another PUMA randomly and then selects a balance object for
     * the given control.  If a qualifying Household object is not found in the 
     * PUMA, then another PUMA is randomly selected.  This search continues until
     * all PUMAs are exhausted.
     * @param def   The control definition that the selected object should meet
     * @return  returns a randomly selected Household object
     */
    private Household selectObjFromOtherPumas(IControlDefinition<Household> def)
    {
        Household result = null;
        // Random prm = new Random();
        // if (version.isFixRandomSeed()) prm.setSeed(pSeed);
        int pumaStart = version.getPumaStart();

        for (int i = 0; i < version.getNumPumas(); i++)
        {
            int rpuma = pumaStart + rm.nextInt(version.getNumPumas());
            ControlList list = classifier.getClassifiedObjs(rpuma, def);
            if (list != null) result = selectRandomHousehold(list.getControlObjects());
        }
        return result;
    }

    /**
     * Selects a random Household object from a Set of Household objects
     * @param set   the Set of Household objects to randomly select from
     * @return  returns a randomly selected Household object
     */
    private Household selectRandomHousehold(Set<Household> set)
    {
        Household result = null;
        if (set != null && set.size() > 0)
        {
            int rNumber = rm.nextInt(set.size());

            int count = 0;
            for (Household hh : set)
            {
                if (count == rNumber)
                {
                    result = hh;
                    break;
                }
                count++;
            }
        }
        return result;
    }

    /**
     * Gets the zonal group quarters total
     * @return  returns an integer that equals the zonal group quarters total
     */
    public int getZonalGQTotal()
    {
        return zonalGQTotal;
    }

    /**
     * Sets the zonal group quarters total
     * @param zonalGQTotal  an integer to set the zonal group quarters total to
     */
    public void setZonalGQTotal(int zonalGQTotal)
    {
        this.zonalGQTotal = zonalGQTotal;
    }

    /**
     * Gets the zonal household total
     * @return  returns an integer that equals the zonal household total
     */
    public int getZonalHHTotal()
    {
        return zonalHHTotal;
    }

    /** Sets the zonal household Total
     * @param zonalHHTotal  an integer to set the zonal household total to
     */
    public void setZonalHHTotal(int zonalHHTotal)
    {
        this.zonalHHTotal = zonalHHTotal;
    }

    /**
     * Sums the weights of balance objects
     * @param hhs   the set of balance objects to sum the weights of
     * @param def   the control category that balance objects must participate in if their weights are to be summed 
     * @return  returns the summation of weights over all balance objects
     */
    public static double calPumsVal(Set<Household> hhs, IControlDefinition<Household> def)
    {
        double sum = 0.0;
        int pf = 1;

        for (Household hh : hhs)
        {
            if (def.isParticipant(hh))
            {
                pf = def.getParticipationFactor(hh);
                sum += pf * hh.getWeight();
            }
        }
        return sum;
    }

    /**
     * Gets the selected household balance objects
     * @return  returns the selected HH balance objects
     */
    public Set<Household> getBalanceHHs()
    {
        return balanceHHs;
    }

    /**
     * Sets the selected household balance objects
     * @param balanceHHs    the balance objects to designate as selected
     */
    public void setBalanceHHs(Set<Household> balanceHHs)
    {
        this.balanceHHs = balanceHHs;
    }

    /**
     * Gets the selected group quarter balance objects
     * @return  returns the selected GQ balance objects
     */
    public Set<Household> getBalanceGQs()
    {
        return balanceGQs;
    }

    /**
     * Sets the selected group quarter balance objects
     * @param balanceGQs    the balance objects to designate as selected
     */
    public void setBalanceGQs(Set<Household> balanceGQs)
    {
        this.balanceGQs = balanceGQs;
    }
}
