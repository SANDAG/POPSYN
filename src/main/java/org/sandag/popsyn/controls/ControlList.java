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
package org.sandag.popsyn.controls;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.domain.Household;

/**
 * For a given control definition and PUMA, this class creates a list of households that belong to the control and PUMA pair. The list takes the form
 * of a Hashmap that contains the mapping between a Household object (key) and its participation factor in the control.
 */
public class ControlList
{
    private static final Logger           LOGGER     = Logger.getLogger(ControlList.class);

    /**
     * The control category that the list of balance objects belongs to
     */
    private IControlDefinition<Household> controlDef = null;

    /**
     * The PUMA that this ControlList belongs to
     */
    private Integer                       puma       = -1;

    /**
     * The mapping between Household objects and their participation factor for this control
     */
    private HashMap<Household, Integer>   controlMap = new HashMap<Household, Integer>();

    /**
     * Stores parameter/version information for this PopSyn run
     */
    private Version                       version    = null;

    /**
     * Constructs a new ControlList object with the specified control category, PUMA ID, and version object
     * 
     * @param aControlDef
     *            the control category that the list of balance objects belongs to
     * @param aPuma
     *            the PUMA that this ControlList belongs to
     * @param aVersion
     *            the version object for this PopSyn run
     */
    public ControlList(IControlDefinition<Household> aControlDef, int aPuma, Version aVersion)
    {
        this.controlDef = aControlDef;
        this.puma = aPuma;
        this.version = aVersion;
    }

    /**
     * Adds Household object (HH or GQ) to the ControlList. Household only gets added if it belongs to the PUMA corresponding to the ControlList.
     * Household only gets added if the relevant control definition is applicable to it. Household only gets added if it is from an appropriate data
     * source and year.
     * 
     * @param t
     *            the household to be added to the ControlList
     * @return a boolean where true indicates that the household was added
     */
    public boolean add(Household t)
    {
        boolean result = false;
        if (t.getPumaId().equals(puma) && controlDef.isParticipant(t))
        {
            Integer pFactor = controlDef.getParticipationFactor(t);
            if (pFactor > 0 && isHousehold(t))
            {
                controlMap.put(t, pFactor);
                result = true;
            }
        }
        return result;
    }

    /**
     * Checks if Household object is from an appropriate data source and year.
     * 
     * @param hh
     *            the Household object to check
     * @return Returns a boolean where true indicates that the Household object meets the source/year criteria to be added to the ControlList
     */
    private boolean isHousehold(Household hh)
    {
        boolean result = false;
        if (version.getSourceId() == 1)
        {
            result = true;
        } else if (version.getSourceId() == 4 || version.getSourceId() == 7)
        {
            if (isRightACSYear(hh, version.getAcsYear())) result = true;
        } else
        {
            LOGGER.fatal("Invalid source ID, check property file!");
        }
        return result;
    }

    /**
     * Checks if the Household object has the correct ACS year if not all ACS years can be used. When the acsYear property is set to 0, PUMS
     * households from all ACS years can be used.
     * 
     * @param hh
     *            the Household object to check
     * @param year
     *            the acsYear field of the Version object- a value of zero indicates that PUMS households from all ACS years can be used
     * @return Returns a boolean where true indicates that the household is from an acceptable year
     */
    private boolean isRightACSYear(Household hh, int year)
    {
        boolean result = false;
        if (year == 0)
        {
            result = true;
        } else if (hh.getId().toString().startsWith("" + year))
        {
            result = true;
        }
        return result;
    }

    /**
     * Reports the number of objects in the ControlList
     * 
     * @return returns an integer equaling the number of objects in the ControlList
     */
    public int size()
    {
        return controlMap.size();
    }

    /**
     * Clears the ControlList of all objects
     */
    public void clear()
    {
        controlMap.clear();
    }

    /**
     * Removes a specific Household object from the ControlList
     * 
     * @param t
     */
    public void remove(Household t)
    {
        controlMap.remove(t);
    }

    /**
     * Checks whether a specific balance object participates in the control definition. The object must be a participant and have a participation
     * factor greater than 0.
     * 
     * @param t
     *            the Household object to check
     * @return returns a boolean where true indicates the object is a participant
     */
    public boolean isParticipant(Household t)
    {
        boolean result = false;
        if (controlDef.getParticipationFactor(t) > 0 && controlDef.isParticipant(t)) result = true;
        return result;
    }

    /**
     * Gets the control definition for this ControlList
     * 
     * @return returns the control definition
     */
    public IControlDefinition<Household> getControlDefinition()
    {
        return controlDef;
    }

    /**
     * Retrieves all objects in the ControlList
     * 
     * @return returns a set of Household objects
     */
    public Set<Household> getControlObjects()
    {
        return controlMap.keySet();
    }

    /**
     * Looks up the participation factor for an object in the ControlList
     * 
     * @param object
     *            the object to find the participation factor for
     * @return returns an integer equaling the participation factor
     */
    public Integer getParticipationFactor(Household object)
    {
        return controlMap.get(object);
    }

    /**
     * Gets the map between Household objects and their participation factor
     * 
     * @return returns a HashMap object that maps between objects and their participation factor
     */
    public HashMap<Household, Integer> getControlMap()
    {
        return controlMap;
    }

    /**
     * Sets the map between Household objects and their participation factor
     * 
     * @param controlMap
     *            the HashMap to use
     */
    public void setControlMap(HashMap<Household, Integer> controlMap)
    {
        this.controlMap = controlMap;
    }

    /**
     * The PUMA that this ControlList pertains to
     * 
     * @return returns an integer that equals the PUMA ID
     */
    public Integer getPuma()
    {
        return puma;
    }

    /**
     * Sets the PUMA that this ControlList pertains to
     * 
     * @param puma
     *            the PUMA to associate with the ControlList
     */
    public void setPuma(Integer puma)
    {
        this.puma = puma;
    }

    /**
     * Prints the control definition for this ControlList, prints ID and participation factor of all objects in the ControlList, and prints the total
     * number of objects in the ControlList.
     */
    public void print()
    {
        LOGGER.info("control definitions:");
        controlDef.print();
        LOGGER.info("household on the control list:");
        Set<Entry<Household, Integer>> set = getControlMap().entrySet();
        for (Entry<Household, Integer> hh : set)
        {
            LOGGER.info("hhID=" + hh.getKey().getId() + " pf=" + hh.getValue());
        }
        LOGGER.info("Number of households on the list:" + set.size());
    }

    /**
     * Sorts the objects in the ControlMap in ascending order and returns the sorted objects as a Set
     * 
     * @return Returns a Set of sorted Household objects
     */
    public Set<Household> getSortedControlObjects()
    {
        return new TreeMap<Household, Integer>(controlMap).keySet();
    }
}
