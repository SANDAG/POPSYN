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

import org.sandag.popsyn.enums.ControlCategoryType;
import org.sandag.popsyn.enums.GeographyType;

public class TargetGrowthFactor
{
    protected ControlCategoryType category;
    protected GeographyType       geoType;
    protected int                 geoZone;
    protected int                 baseYear;
    protected int                 growthYear;
    protected int                 luRevisionNo;
    protected int                 luMajorVersion;
    protected int                 luMinorVersion;
    protected double              value;

    public ControlCategoryType getCategory()
    {
        return category;
    }

    public void setCategory(ControlCategoryType category)
    {
        this.category = category;
    }

    public GeographyType getGeoType()
    {
        return geoType;
    }

    public void setGeoType(GeographyType geoType)
    {
        this.geoType = geoType;
    }

    public int getGeoZone()
    {
        return geoZone;
    }

    public void setGeoZone(int geoZone)
    {
        this.geoZone = geoZone;
    }

    public int getBaseYear()
    {
        return baseYear;
    }

    public void setBaseYear(int baseYear)
    {
        this.baseYear = baseYear;
    }

    public int getGrowthYear()
    {
        return growthYear;
    }

    public void setGrowthYear(int growthYear)
    {
        this.growthYear = growthYear;
    }

    
    public int getLuRevisionNo()
    {
        return luRevisionNo;
    }

    public void setLuRevisionNo(int luRevisionNo)
    {
        this.luRevisionNo = luRevisionNo;
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

    public void setLuMinorVersion(int luMinorVersion)
    {
        this.luMinorVersion = luMinorVersion;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

}
