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
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import org.sandag.common.datafile.TableDataManager;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.controls.IControlDefinition;
import org.sandag.popsyn.domain.Household;
import com.pb.common.datafile.TableDataSet;

/**
 * This class records the convergence status after balancing each TAZ as well 
 * as the overall convergence statistics after all TAZs are balanced.  The
 * convergence status and performance statistics are printed out as a CSV file.
 * The number of non-converged TAZs is recorded, along with the number of
 * non-converged TAZs by control category.
 *
 */
public class ConvergenceManager
{
    /**
     * A list of ConvergenceStat objects for all balance zones
     * 
     */
    protected List<ConvergenceStat>                           cStats;
    
    /**
     * The number of non-converged zones
     */
    protected int                                             nonConvergedZones;
    
    /**
     * A list of the control definitions
     */
    protected List<IControlDefinition<Household>>             controlDefs;
    
    /**
     * Records the number of non-converged TAZs, by control category
     */
    protected HashMap<IControlDefinition<Household>, Integer> nonConvergedControlCounts;

    /**
     * Constructs a new ConvergenceManager object with the specified control definitions.
     * @param theControlDefs    a list of control definitions
     */
    public ConvergenceManager(List<IControlDefinition<Household>> theControlDefs)
    {
        this.controlDefs = theControlDefs;
        cStats = new ArrayList<ConvergenceStat>();
        nonConvergedZones = 0;
        initNonConvergedControlCounts();
    }

    /**
     * Gets the list of ConvergenceStat objects for all balance zones
     * @return  Returns a List of ConvergenceStat objects
     */
    public List<ConvergenceStat> getCStats()
    {
        return cStats;
    }

    /**
     * Sets the list of ConvergenceStat objects
     * @param stats a list of ConvergenceStat objects
     */
    public void setCStat(List<ConvergenceStat> stats)
    {
        cStats = stats;
    }

    /**
     * Gets the number of non-converged zones
     * @return  Returns an integer equaling the number of non-converged zones
     */
    public int getNonConvergedZones()
    {
        return nonConvergedZones;
    }

    /**
     * Sets the number of non-converged zones
     * @param nonConvergedZones an integer equaling the number of non-converged zones
     */
    public void setNonConvergedZones(int nonConvergedZones)
    {
        this.nonConvergedZones = nonConvergedZones;
    }

    /**
     * Adds a ConvergenceStat object for a zone to cStats, the list of all
     * ConvergenceStat objects.  If the added object is converged, the number
     * of non-converged zones is increased by 1.
     * @param stat
     */
    public void add(ConvergenceStat stat)
    {
        cStats.add(stat);
        if (!stat.isConverged) nonConvergedZones++;
    }

    /**
     * Counts the number of non-converged zones for each control category.
     * The results are stored in the nonConvergedControlCounts HashMap.
     */
    public void setNonConvergedControlCounts()
    {
        int count = 0;
        List<IControlDefinition<Household>> nonConvergedControls;
        for (ConvergenceStat stat : cStats)
        {
            nonConvergedControls = stat.getNonConvergedControls();
            if (nonConvergedControls != null)
            {
                for (IControlDefinition<Household> def : controlDefs)
                    for (IControlDefinition<Household> ncControl : nonConvergedControls)
                    {
                        if (def.equals(ncControl))
                        {
                            count = nonConvergedControlCounts.get(def);
                            count++;
                            nonConvergedControlCounts.put(def, count);
                        }
                    }
            }
        }
    }

    /**
     * Initializes the HashMap that records the number of non-converged zones
     * by control category.  Each control category is initially given 0 
     * non-converged zones.
     */
    private void initNonConvergedControlCounts()
    {
        nonConvergedControlCounts = new HashMap<IControlDefinition<Household>, Integer>();
        for (IControlDefinition<Household> def : controlDefs)
        {
            nonConvergedControlCounts.put(def, 0);
        }
    }

    /**
     * Outputs a CSV file containing the number of non-converged zones by 
     * control category.  
     * @param version   the Version object for this PopSyn run so that the version ID can be appended to the file name
     */
    public void printNonConvergedZones(Version version)
    {
        setNonConvergedControlCounts();
        int dim = nonConvergedControlCounts.size();
        String[] ids = new String[dim];
        String[] counts = new String[dim];
        String[] cats = new String[dim];

        // populate contents
        int i = 0;
        Set<Entry<IControlDefinition<Household>, Integer>> set = nonConvergedControlCounts
                .entrySet();
        for (Entry<IControlDefinition<Household>, Integer> entry : set)
        {
            cats[i] = entry.getKey().getCategory().toString();
            ids[i] = entry.getKey().getCategory().getValue() + "";
            counts[i] = entry.getValue() + "";
            i++;
        }

        TableDataSet table = new TableDataSet();
        table.appendColumn(ids, "ID");
        table.appendColumn(cats, "Category");
        table.appendColumn(counts, "NonConvergedZoneCount");

        TableDataManager.writeTable(version.getWorkDir(), "NonConvergedZone_n" + nonConvergedZones
                + "_v" + version.getId() + ".csv", table);
    }

    /**
     * Outputs a CSV file containing convergence details for each zone and 
     * control category.  For each zone-control pair, the target value is 
     * shown next to the synthesized value.  Whether the zone-control pair
     * has converged is also shown.
     * @param version
     */
    public void printBalanceDetails(Version version)
    {
        int nRows = controlDefs.size() * cStats.size();
        String[] colLabels = {"Zone", "ControlID", "ControlCat", "Target", "PUMSVal", "ConStat"};
        int nCols = colLabels.length;
        String[][] bDetails = new String[nCols][nRows];

        int count = 0;
        for (ConvergenceStat stat : cStats)
        {
            Set<IControlDefinition<Household>> defs = stat.getControlDefs();
            List<IControlDefinition<Household>> nonConDefs = stat.getNonConvergedControls();
            for (IControlDefinition<Household> def : defs)
            {
                bDetails[0][count] = stat.getZone() + "";
                bDetails[1][count] = def.getCategory().getValue() + "";
                bDetails[2][count] = def.getCategory().toString();
                bDetails[3][count] = stat.getZTarget(def) + "";
                bDetails[4][count] = stat.getPumsVal(def) + "";
                if (isInControlDefList(def, nonConDefs)) bDetails[5][count] = "no";
                else bDetails[5][count] = "yes";
                count++;
            }
        }

        TableDataSet table = new TableDataSet();
        for (int i = 0; i < nCols; i++)
        {
            table.appendColumn(bDetails[i], colLabels[i]);

        }

        TableDataManager.writeTable(version.getWorkDir(), "BalanceDetails" + "_v" + version.getId()
                + ".csv", table);
    }

    /**
     * Checks whether a particular control definition is in a list of control definitions
     * @param def   The specific control definition to check for
     * @param defList   The list of control definitions to search through
     * @return  Returns a boolean where true indicates that the control definition is in the list
     */
    private boolean isInControlDefList(IControlDefinition<Household> def,
            List<IControlDefinition<Household>> defList)
    {
        boolean result = false;
        for (IControlDefinition<Household> cdef : defList)
        {
            if (cdef.getCategory().getValue() == def.getCategory().getValue())
            {
                result = true;
                break;
            }
        }
        return result;
    }
}
