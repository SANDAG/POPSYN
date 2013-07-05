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
package org.sandag.popsyn.domain;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.sandag.common.montecarlo.IWeightedObject;
import org.sandag.popsyn.enums.DwellingType;
import org.sandag.popsyn.enums.UnitType;

/**
 * This class represents the households and group quarters in PopSyn. Each Household object is mapped to a PUMS record in the database. This mapping
 * is carried out by ReaderDao and Household.xml
 * 
 */
public class Household
        implements IGeoWeightedObject
{
    private static final Logger LOGGER        = Logger.getLogger(Household.class);

    /**
     * The Household object ID
     */
    protected Long              id            = null;

    /**
     * The PUMA ID of the PUMA that the Household object belongs to
     */
    protected Integer           pumaId        = null;

    /**
     * The inflation-adjusted income of the household
     */
    protected int               adjIncome     = -1;

    /**
     * The number of workers in the household
     */
    protected int               numOfWorkers  = -1;

    /**
     * The number of children in the household
     */
    protected int               numOfChildren = -1;

    /**
     * The Household object's unit type (whether it is a housing unit or a type of GQ)
     */
    protected UnitType          unitType      = null;

    /**
     * The dwelling unit type (such as SF, MF, mobile home) of the household
     */
    protected DwellingType      dwellingType  = null;

    /**
     * The initial PUMS sample weight of the Household object
     */
    private double              initWeight    = -1.0;

    /**
     * The modified weight of the Household object (modified during the balancing step)
     */
    private double              modifiedWgt   = -1.0;

    /**
     * A boolean indicating whether the Household object is a GQ
     */
    protected boolean           groupQuarter  = false;

    /**
     * A list of persons in the household
     */
    protected List<Person>      people        = new ArrayList<Person>();

    /**
     * The MGRA that the household resides in
     */
    protected int               mgra          = -1;

    /**
     * Clones the Household object
     */
    public Object clone()
    {
        Household cloned = null;
        try
        {
            cloned = (Household) super.clone();
        } catch (CloneNotSupportedException e)
        {
            LOGGER.fatal(e);
        }
        return cloned;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Integer getPumaId()
    {
        return pumaId;
    }

    public void setPumaId(Integer aPumaId)
    {
        this.pumaId = aPumaId;
    }

    public int getHhSize()
    {
        return people.size();
    }

    public int getAdjIncome()
    {
        return adjIncome;
    }

    public void setAdjIncome(int adjIncome)
    {
        this.adjIncome = adjIncome;
    }

    public int getNumOfWorkers()
    {
        return this.numOfWorkers;
    }

    public void setNumOfWorkers(int numOfWorkers)
    {
        this.numOfWorkers = numOfWorkers;
    }

    public int getNumOfChildren()
    {
        return numOfChildren;
    }

    public void setNumOfChildren(int numOfChildren)
    {
        this.numOfChildren = numOfChildren;
    }

    public UnitType getUnitType()
    {
        return unitType;
    }

    public void setUnitType(UnitType unitType)
    {
        this.unitType = unitType;
    }

    public DwellingType getDwellingType()
    {
        return dwellingType;
    }

    public void setDwellingType(DwellingType dwellingType)
    {
        this.dwellingType = dwellingType;
    }

    /**
     * Gets the initial PUMS weight of the Household object
     * 
     * @return returns a double equaling the initial weight
     */
    public double getInitWeight()
    {
        return initWeight;
    }

    /**
     * Sets the initial PUMS weight of the Household object
     * 
     * @param weight
     *            a double equaling the initial weight to use
     */
    public void setInitWeight(double weight)
    {
        this.initWeight = weight;
    }

    /**
     * Gets the modified weight of the Household object
     * 
     * @return returns a double equaling the modified weight
     */
    public double getModifiedWeight()
    {
        return modifiedWgt;
    }

    /**
     * Sets the modified weight of the Household object
     * 
     * @param modifiedWgt
     *            a double equaling the modified weight to use
     */
    public void setModifiedWeight(double modifiedWgt)
    {
        this.modifiedWgt = modifiedWgt;
    }

    public List<Person> getPeople()
    {
        return people;
    }

    public void setPeople(List<Person> people)
    {
        this.people = people;
    }

    /**
     * Adds a person to the household
     * 
     * @param person
     *            the Person object to add to the Household object's List of Persons
     */
    public void addPerson(Person person)
    {
        if (people != null)
        {
            people.add(person);
        } else
        {
            people = new ArrayList<Person>();
            people.add(person);
        }
    }

    public boolean isGroupQuarter()
    {
        return groupQuarter;
    }

    public void setWeight(double weight)
    {
        modifiedWgt = weight;
    }

    public double getWeight()
    {
        return modifiedWgt;
    }

    public void setZone(int aMgra)
    {
        mgra = aMgra;
    }

    public int getZone()
    {
        return mgra;
    }

    /**
     * Compares this Household object with another Household object based on Household ID
     * 
     * @param hh
     *            the Household object to compare with
     */
    public int compareTo(IWeightedObject anObj)
    {
        return id.compareTo(anObj.getId());
    }

    /**
     * Sets the modified weight to the initial weight
     * 
     * @param useInitWeight
     *            a boolean indicating whether to set the modified weight to the initial weight
     */
    public void initWgt(boolean useInitWeight)
    {
        if (!useInitWeight)
        {
            setModifiedWeight(1);
        } else if (isGroupQuarter())
        {
            setModifiedWeight(1);
        } else
        {
            setModifiedWeight(getInitWeight());
        }
    }

    public String toString()
    {
        StringBuilder core = new StringBuilder("Id: " + id + ";PUMA: " + pumaId + ";AdjIncome: "
                + adjIncome + ";NumWorkers: " + numOfWorkers + ";numChild: " + numOfChildren
                + ";UnitType: " + unitType + ";DwellType: " + dwellingType + ";InitWgt: "
                + initWeight + ";ModWgt: " + modifiedWgt + ";GQ: " + groupQuarter);

        for (Person person : people)
        {
            core.append("\n\tHH: " + id + person.toString());
        }

        return core.toString();
    }

}
