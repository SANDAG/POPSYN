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

// import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.sandag.common.montecarlo.RandomSeedManager;
import org.sandag.common.utilities.StringParser;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.controls.Classifier;
import org.sandag.popsyn.controls.IControlDefinition;
import org.sandag.popsyn.controls.Strainer;
import org.sandag.popsyn.domain.Geography;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.domain.IGeoWeightedObject;
import org.sandag.popsyn.domain.Target;
import org.sandag.popsyn.enums.ControlCategoryType;
import org.sandag.popsyn.io.IGeographyDao;
import org.sandag.popsyn.io.ITargetDao;
import org.sandag.popsyn.io.ITargetGrowthFactorDao;
import org.sandag.popsyn.io.IWriterDao;

/**
 * This class handles the balancing of PUMS sample household/GQ weights to control targets by TAZ. It also discretizes weights and writes output to
 * the database.
 * 
 */
public class Balancer
{
    private static final Logger    LOGGER = Logger.getLogger(Balancer.class);

    /**
     * The data access object used to write balance objects to the database
     */
    private IWriterDao             writerDao;

    /**
     * The data access object for the Target class of objects
     */
    private ITargetDao             targetDao;

    /**
     * This data access object maps the targetGrowthFactor object to the growthFactor table in the database
     */
    private ITargetGrowthFactorDao growthFactorDao;

    /**
     * The data access object for the Geography class of objects
     */
    private IGeographyDao          geographyDao;

    /**
     * Stores parameter/version information for this PopSyn run
     */
    private Version                version;

    /**
     * Set of Household objects used to store households after they have been balanced and separated from GQs
     */
    private SortedSet<Household>   balanceHhs;

    /**
     * Set of Household objects used to store GQs after they have been balanced and separated from households
     */
    private SortedSet<Household>   balanceGqs;

    /**
     * Constructs a new Balancer object with the specified Version object, TargetDao, GrowthFactorDao, WriterDao, and Geography Dao.
     * 
     * @param aVersion
     *            Stores parameter/version information for this PopSyn run
     * @param aTargetDao
     *            The data access object for the Target class of objects
     * @param aGrowthFactorDao
     *            This data access object maps the targetGrowthFactor object to the growthFactor table in the database
     * @param aWriterDao
     *            The data access object used to write balance objects to the database
     * @param aGeographyDao
     *            The data access object for the Geography class of objects
     */
    public Balancer(Version aVersion, ITargetDao aTargetDao,
            ITargetGrowthFactorDao aGrowthFactorDao, IWriterDao aWriterDao,
            IGeographyDao aGeographyDao)
    {
        this.version = aVersion;
        this.targetDao = aTargetDao;
        this.growthFactorDao = aGrowthFactorDao;
        this.writerDao = aWriterDao;
        this.geographyDao = aGeographyDao;
    }

    /**
     * Balances PUMS sample household/GQ weights. Weights are balanced to control targets by balance geography (typically TAZ). A balance selector
     * chooses households and GQs to be balanced in such a way that weights won't be balanced to small fractional values. After balance object
     * selection, households and GQs are balanced together. A ConvergenceStat object is created to record the convergence status of each TAZ. This
     * method finishes by calling a method to begin post-balancing steps, which include the integerization of weights and the allocation of Household
     * objects from the TAZ level to the MGRA level.
     * 
     * @param classifier
     *            the Classifier object
     * @throws Exception
     */
    public void balance(Classifier classifier) throws Exception
    {
        List<IControlDefinition<Household>> controlDefs = classifier.getControlDefs();
        BalanceGeographies bg = new BalanceGeographies(geographyDao, version);

        // set up geographies
        Map<Integer, Integer> bZonePumaXref = bg.getBZonePumaXref();

        // uncommend next line if balance and target geographies are different
        // Map<Geography, List<Geography>> bZoneTargetZoneXref = bg.getBZoneTargetZoneXref();

        // set balance zones
        ArrayList<Integer> balanceZones = null;
        if (version.getSelectZones().equals("NA"))
        {
            balanceZones = bg.getBalanceZonesAsIntList();
        } else
        {
            balanceZones = StringParser.parseRangeAsIntList(version.getSelectZones(), "'", "-");
            LOGGER.info("Running select zones!");
        }

        // set random seeds
        RandomSeedManager<Integer> rdm = new RandomSeedManager<Integer>(version.getRandomSeed(),
                balanceZones, 3);

        // set balance settings
        double pumsVal, bf;
        Double targetVal = null;
        ConvergenceManager cm = new ConvergenceManager(controlDefs);
        int count = 0;

        // Regenerate ACS targets
        if (version.getRegenerateTargets() != null
                && version.getRegenerateTargets().equalsIgnoreCase("true"))
        {
            LOGGER.info("Regenerating ACS targets!");
            targetDao.regenerateTargets(version.getTargetYear());
        }

        // target generation method
        boolean growthFactor = false;
        if (version.getTargetMethod() != null
                && version.getTargetMethod().equalsIgnoreCase("growthFactor"))
        {
            growthFactor = true;
            LOGGER.info("Running growth factor target generation method!");
        }

        // loop over balance zones
        for (int zone : balanceZones)
        {
            count++;
            double zHHTotal = targetDao.getZonalHHTotal(zone, version);
            double zGQTotal = targetDao.getZonalGQTotal(zone, version);
            Hashtable<IControlDefinition<Household>, Double> zTargets;
            LOGGER.info("balancing zone count: " + count + " zone=" + zone);

            // only balance internal zones and TAZs with households or GQs
            if (isBalanceZone(zone, zHHTotal, zGQTotal))
            {
                int puma = bZonePumaXref.get(zone);
                // get targets for this zone
                if (version.getBalanceGeography().equals(version.getTargetGeography())) zTargets = targetDao
                        .getTargetsByZone(zone, classifier.getControlDefs(), version);
                // uncomment next two lines if balance and target geographies are different
                // else zTargets = targetDao.getZonalTargets(bZoneTargetZoneXref.get(zone), classifier
                // .getControlDefs(), version);
                else zTargets = targetDao.getTargetsByZone(zone, classifier.getControlDefs(),
                        version);

                // get growth factors for this zone
                HashMap<Integer, Double> gfs = null;
                if (growthFactor)
                {
                    gfs = growthFactorDao.getGrowthFactor(zone, version);
                }

                // select balance objs
                Set<Household> balanceObjs;
                if (version.isBalanceAllPumaSamples())
                {
                    balanceObjs = useAllSamplesInPums(classifier, puma, version);
                } else
                {
                    double bsf = version.getBalanceSelectionFactor();
                    BalanceObjsSelector bs = new BalanceObjsSelector(zone, zTargets, classifier,
                            puma, version, bsf * zHHTotal, bsf * zGQTotal, rdm);
                    balanceObjs = bs.getBalanceObjs();
                }

                if (balanceObjs.size() > 0)
                {
                    // start balancing
                    ConvergenceStat cstat = new ConvergenceStat(zone, balanceObjs, zTargets,
                            version);

                    // max loop
                    for (int i = 0; i < version.getMaxLoop(); i++)
                    {
                        for (IControlDefinition<Household> def : controlDefs)
                        {
                            targetVal = zTargets.get(def);
                            bf = 1.0;
                            if (null != targetVal && targetVal > 0)
                            {
                                pumsVal = BalanceObjsSelector.calPumsVal(balanceObjs, def);
                                // check if apply growth factor on targets
                                if (gfs != null && gfs.size() > 0)
                                {
                                    Double df = gfs.get(def.getCategory().getValue());
                                    if (df != null) targetVal = targetVal * df;
                                }
                                bf = calBalanceFactor(pumsVal, targetVal);
                                // apply bf to HHs, weights updated
                                applyBalanceFactor(balanceObjs, bf, def, zHHTotal, zGQTotal);
                            }
                        }
                        // check convergence
                        if (cstat.isConverged()) break;
                    }

                    // add convergence stat to manager
                    cm.add(cstat);
                } else
                {
                    LOGGER.warn("No balance objects selectd for zone " + zone);
                }

                // post balancing steps
                postBalance(zone, balanceObjs, zHHTotal, zGQTotal, bg, rdm);
            }
        }
        cm.printNonConvergedZones(version);
        cm.printBalanceDetails(version);
    }

    /**
     * Determines whether a zone is subject to balancing. Only internal zones and TAZs with households or GQs can be balanced. This method returns a
     * boolean indicating whether the zone qualifies as a balance zone.
     * 
     * @param zone
     *            the zone to be evaluated
     * @param hhTotal
     *            the zonal HH total
     * @param gqTotal
     *            the zonal GQ total
     * @return Returns a boolean value where true means a zone is a balance zone
     */
    private boolean isBalanceZone(int zone, double hhTotal, double gqTotal)
    {
        boolean result = false;
        if (zone > version.getNumExtTaz())
        {
            if (hhTotal > 0 || gqTotal > 0)
            {
                result = true;
            }
        }
        return result;
    }

    /**
     * Calculates balancing factor for a given control category that will be used to update the initial HH/GQ weight. Balancing factors are calculated
     * at the TAZ level for each control category by dividing the zonal target by a summation of weights over all balance objects.
     * 
     * @param pumsVal
     *            the zonal sum of PUMS weights for this control category over all (participating) balance objects
     * @param targetVal
     *            the zonal target value for this control category
     * @return returns the calculated balancing factor
     */
    private double calBalanceFactor(double pumsVal, double targetVal)
    {
        double result = 1.0;
        if (pumsVal > 0 && targetVal > 0)
        {
            result = targetVal / pumsVal;
        }
        return result;
    }

    /**
     * Updates individual HH/GQ weights using the calculated balancing factor (which is specific to the given control category and balance iteration).
     * The new weight is calculated as a product of the existing weight and the balancing factor exponentiated by the participation factor. Balance
     * factors are only applied to HHs/GQs with a non-zero weight and non-zero participation factor.
     * 
     * @param controlObjs
     *            the balance objects (HHs and GQs)
     * @param bf
     *            the calculated balancing factor
     * @param def
     *            the control category for which the balancing factor was calculated
     * @param zHHTotal
     *            the zonal HH total
     * @param zGQTotal
     *            the zonal GQ total
     */
    private void applyBalanceFactor(Set<Household> controlObjs, double bf,
            IControlDefinition<Household> def, double zHHTotal, double zGQTotal)
    {
        int pf = 1;
        double newWeight = 1.0;
        for (Household hh : controlObjs)
        {
            if (def.isParticipant(hh))
            {
                pf = def.getParticipationFactor(hh);
                // only apply balance factor to non zero weight hh
                // only apply when pf!=0, if pf=0, then weight doesn't change
                if (hh.getWeight() > 0 && pf > 0)
                {
                    newWeight = Math.pow(bf, pf) * hh.getWeight();
                    // new weight is bounded to pf*(zonal HH+ GQ totals), otherwise new weight could be too large
                    newWeight = Math.min(newWeight, (zHHTotal + zGQTotal) * pf);
                    hh.setWeight(newWeight);
                }
            }
        }
    }

    /**
     * Undertakes a number of necessary post-balancing steps. HHs and GQs are separated so they can be discretized separately to zonal HH/GQ totals
     * from the target table. Balance objects are discretized, allocated to MGRAs, and written to output tables in the database.
     * 
     * @param bzone
     *            the balance zone (typically, TAZ)
     * @param balancedObjs
     *            the balance objects (HHs and GQs)
     * @param zHHTotal
     *            the zonal HH total
     * @param zGQTotal
     *            the zonal GQ total
     * @param bg
     *            the BalanceGeographies object
     * @param rdm
     *            the RandomSeedManager object, which ensures that seeds used for random number generation are the same across PopSyn runs
     */
    private void postBalance(int bzone, Set<Household> balancedObjs, // Targets targets,
            double zHHTotal, double zGQTotal, BalanceGeographies bg, RandomSeedManager<Integer> rdm)
    {
        // separate HHs and GQs
        separate(balancedObjs);

        Discretizer<Household> disc = new Discretizer<Household>(bzone, version.isFixRandomSeed(),
                rdm);

        disc.discretize(balanceHhs, zHHTotal);
        disc.discretize(balanceGqs, zGQTotal);

        // discretize
        // SortedSet<IGeoWeightedObject> discretizedHhs = discretize(balanceHhs, bzone, zHHTotal, rdm);
        // SortedSet<IGeoWeightedObject> discretizedGqs = discretize(balanceGqs, bzone, zGQTotal, rdm);

        // write out discretized objs
        write(balanceHhs, balanceGqs, bzone, version, false);

        // allocate
        List<Geography> alZones = bg.getAllocationZones(bzone);
        ArrayList<IGeoWeightedObject> allcoatedHhs = allocate("hh", bzone, alZones, rdm, balanceHhs);
        ArrayList<IGeoWeightedObject> allcoatedGqs = allocate("gq", bzone, alZones, rdm, balanceGqs);

        // write out allocated households
        write(allcoatedHhs, allcoatedGqs, bzone, version, true);
    }

    /**
     * Discretizes HH/GQ weights. The balanced weights of PUMS sample households and group quarters are non-integer values. These values are
     * integerized while maintaining the marginal totals.
     * 
     * @param objs
     *            the balance objects (hh/gq)
     * @param bzone
     *            the balance zone (TAZ)
     * @param zTotal
     *            the zonal HH or GQ total
     * @param rdm
     *            the RandomSeedManager object, which ensures that seeds used for random number generation are the same across PopSyn runs
     * @return Returns a set of Household objects with discretized weights
     */
    // private SortedSet<IWeightedObject> discretize(SortedSet<IWeightedObject> objs, int bzone, double zTotal,
    // RandomSeedManager<Integer> rdm)
    // {
    // Discretizer dis = new Discretizer(bzone, version, rdm);
    // dis.discretize(objs, zTotal);

    // return objs;
    // }

    /**
     * Allocates households and GQs from TAZs to the MGRAs that are within the TAZ. A Monte Carlo procedure is used to select an MGRA for each GQ and
     * household according to the distribution of HH/GQ.
     * 
     * @param type
     *            a String indicating HH or GQ
     * @param bzone
     *            the balance zone (typically TAZ)
     * @param alZones
     *            a list of allocation zones (typically MGRA)
     * @param rdm
     *            the RandomSeedManager object, which ensures that seeds used for random number generation are the same across PopSyn runs
     * @param discretizedObjs
     *            a set of Household objects with discretized weights
     * @return Returns a set of Household objects, each of which has been assigned to an MGRA
     */
    @SuppressWarnings("unchecked")
    private ArrayList<IGeoWeightedObject> allocate(String type, int bzone, List<Geography> alZones,
            RandomSeedManager<Integer> rdm, SortedSet<? extends IGeoWeightedObject> discretizedObjs)
    {
        ControlCategoryType alAttType = null;
        if (type.equalsIgnoreCase("hh"))
        {
            alAttType = version.getHhAlAtt();
        } else
        {
            alAttType = version.getGqAlAtt();
        }

        Allocator al = new Allocator(bzone, version, rdm);
        Set<Target> alSet = targetDao.getAllocationSet(alAttType, alZones, version);
        return al.allocate((SortedSet<IGeoWeightedObject>) discretizedObjs, alSet);
    }

    /**
     * Writes balance objects to a database table.
     * 
     * @param hhObjs
     *            the households to be written out
     * @param gqObjs
     *            the GQs to be written out
     * @param bzone
     *            the balance zone
     * @param aVersion
     *            the version object associated with this PopSyn run
     * @param writeAllocated
     *            a boolean indicating whether to write discretized objects (before allocation) to the database or whether to write allocated objects.
     *            True indicates writing pre-allocation discretized objects. False indicates writing allocated objects.
     */
    private void write(SortedSet<? extends IGeoWeightedObject> hhObjs,
            SortedSet<? extends IGeoWeightedObject> gqObjs, int bzone, Version aVersion,
            boolean writeAllocated)
    {
        Set<IGeoWeightedObject> objs = new HashSet<IGeoWeightedObject>();
        objs.addAll(hhObjs);
        objs.addAll(gqObjs);
        writerDao.persistHouseholds(bzone, aVersion.getTargetGeography(), objs, aVersion,
                writeAllocated);
    }

    private void write(ArrayList<IGeoWeightedObject> hhObjs, ArrayList<IGeoWeightedObject> gqObjs,
            int bzone, Version aVersion, boolean writeAllocated)
    {
        ArrayList<IGeoWeightedObject> objs = new ArrayList<IGeoWeightedObject>();
        objs.addAll(hhObjs);
        objs.addAll(gqObjs);
        writerDao.persistHouseholds(bzone, aVersion.getTargetGeography(), objs, aVersion,
                writeAllocated);
    }

    /**
     * Separates households and GQs.
     * 
     * @param objs
     *            the set of balance objects to be separated
     */
    private void separate(Set<Household> objs)
    {
        balanceHhs = new TreeSet<Household>();
        balanceGqs = new TreeSet<Household>();
        for (Household obj : objs)
        {
            if (obj.isGroupQuarter()) balanceGqs.add(obj);
            else balanceHhs.add(obj);
        }
    }

    /**
     * An option that increases the sample of HH/GQ used for balancing in each TAZ to include all HH/GQ in the corresponding PUMA
     * 
     * @param classifier
     *            the Classifier object
     * @param puma
     *            an int denoting the PUMA that contains the balance zone
     * @param aVersion
     *            the Version object associated with this PopSyn run
     * @return Returns a set with the entire PUMA sample of HH and GQ
     */
    private Set<Household> useAllSamplesInPums(Classifier classifier, int puma, Version aVersion)
    {
        Set<Household> bhhs = Strainer
                .strainHouseholds(classifier.getClassifiedHHs(puma), aVersion);
        Set<Household> bgqs = Strainer
                .strainHouseholds(classifier.getClassifiedGQs(puma), aVersion);
        Set<Household> balanceObjs = new HashSet<Household>();
        balanceObjs.addAll(bhhs);
        balanceObjs.addAll(bgqs);
        return balanceObjs;
    }
}