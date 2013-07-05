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

import java.util.List;
import org.apache.log4j.Logger;
import org.sandag.common.montecarlo.IWeightedObject;
import org.sandag.popsyn.enums.ControlCategoryType;
import org.sandag.popsyn.enums.GeographyType;

/**
 * Targets are the characteristics of each zone that PopSyn tries to match. Each target involves a single zone and control variable. TargetDao.java
 * and Target.xml map between target records in the database and Target objects.
 * 
 */
public class Target
        implements IGeoWeightedObject
{
    private static final Logger   LOGGER         = Logger.getLogger(Target.class);

    private Long                  id             = null;
    /**
     * The target category
     */
    protected ControlCategoryType category       = null;

    /**
     * Type of geographic unit the target value pertains to (typically TAZ)
     */
    protected GeographyType       geoType        = null;

    /**
     * The zone ID the target pertains to
     */
    private int                   geoZone        = -1;
    private List<Integer>         geoZones       = null;

    /**
     * The year that the target pertains to
     */
    private int                   year           = -1;
    private int                   luMajorVersion = -1;
    private int                   luMinorVersion = -1;

    /**
     * The target value
     */
    private double                value          = -1.0;

    public Long getId()
    {
        return id;
    }

    public void setId(Long anId)
    {
        this.id = anId;
    }

    /**
     * Gets the target category
     * 
     * @return returns a ControlCategoryType object
     */
    public ControlCategoryType getCategory()
    {
        return category;
    }

    /**
     * Sets the target category
     * 
     * @param category
     *            a ControlCategoryType object to set as the target category
     */
    public void setCategory(ControlCategoryType category)
    {
        this.category = category;
    }

    /**
     * Gets the type of geographic unit that the target value pertains to
     * 
     * @return returns a GeographyType object
     */
    public GeographyType getGeoType()
    {
        return geoType;
    }

    /**
     * Sets the type of geographic unit that the target value pertains to
     * 
     * @param geoType
     *            a GeographyType object
     */
    public void setGeoType(GeographyType geoType)
    {
        this.geoType = geoType;
    }

    /**
     * Gets the zone ID of the target zone
     * 
     * @return returns an integer equaling the zone ID
     */
    public int getZone()
    {
        return geoZone;
    }

    /**
     * Sets the zone ID of the target zone
     * 
     * @param geoZone
     *            an integer equaling the zone ID
     */
    public void setZone(int geoZone)
    {
        this.geoZone = geoZone;
    }

    public List<Integer> getZones()
    {
        return geoZones;
    }

    public void setZones(List<Integer> geoZones)
    {
        this.geoZones = geoZones;
    }

    /**
     * Gets the year that the target pertains to
     * 
     * @return an integer referring to the target year
     */
    public int getYear()
    {
        return year;
    }

    /**
     * Sets the year that the target pertains to
     * 
     * @param year
     *            an integer referring to a year
     */
    public void setYear(int year)
    {
        this.year = year;
    }

    public int getLuMajorVersion()
    {
        return luMajorVersion;
    }

    public void setLuMajorVersion(int luMajorVersion)
    {
        this.luMajorVersion = luMajorVersion;
    }

    public int getLuMinorVersion()
    {
        return luMinorVersion;
    }

    /**
     * Gets the target value
     * 
     * @return returns a double equaling the target value
     */
    public double getWeight()
    {
        return value;
    }

    /**
     * Sets the target value
     * 
     * @param value
     *            a double equaling the target value
     */
    public void setWeight(double value)
    {
        this.value = value;
    }

    public void setLuMinorVersion(int luMinorVersion)
    {
        this.luMinorVersion = luMinorVersion;
    }

    /**
     * Compares the target value of this Target object with the target value of another specified Target object. Returns 1 if this target value is
     * greater than the other. Returns 0 if target values are equal. Returns -1 if this target value is less than the other.
     * 
     * @param t
     *            the Target object to compare with
     * @return Returns 1 if this target value is greater than the target value of the specified Target object. Returns 0 if target values are equal.
     *         Returns -1 if this target value is less than the target value of the specified Target object.
     */
    // public int compareTo(Target t)
    // {
    // int result = 0;
    // double diff = this.getValue() - t.getValue();
    // if (diff > 0) result = 1;
    // else if (diff == 0) result = 0;
    // else result = -1;
    // return result;
    // }

    public int compareTo(IWeightedObject o)
    {
        int result = 0;
        double diff = this.getWeight() - o.getWeight();
        if (diff > 0) result = 1;
        else if (diff == 0) result = 0;
        else result = -1;
        return result;
    }

    public void setModifiedWeight(double aModWeight)
    {
        this.value = aModWeight;
    }

    public double getModifiedWeight()
    {
        return this.value;
    }

    public Object clone()
    {
        Object cloned = null;
        try
        {
            cloned = super.clone();
        } catch (CloneNotSupportedException e)
        {
            LOGGER.fatal(e);
        }
        return cloned;
    }
}
