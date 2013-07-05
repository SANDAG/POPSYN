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
package org.sandag.popsyn.domain;

import org.sandag.popsyn.enums.GeographyType;

public class WrittenHousehold
{
    /**
     * An Integer that refers to the version for this PopSyn run.
     * Unique identifier for this PopSyn run.
     */
    private Integer       version       = null;
    
    /**
     * The zonal ID of the zone that the HH/GQ is contained in
     */
    private Integer       zoneId        = null;
    
    /**
     * The type of geographic unit that the zonal ID pertains to
     */
    private GeographyType geographyType = null;
    
    /**
     * The household serial number
     */
    private Integer       hhSerialNo    = null;
    
    /**
     * The final Household object weight (after balancing)
     */
    private Double        hhWeight      = null;

    /**
     * Gets the Integer that refers to the version for this PopSyn run
     * @return  returns an Integer that is a unique identifier for this PopSyn run
     */
    public Integer getVersion()
    {
        return version;
    }

    /**
     * Sets the Integer that refers to the version for this PopSyn run
     * @param version   an Integer that is a unique identifier for this PopSyn run
     */
    public void setVersion(Integer version)
    {
        this.version = version;
    }

    /**
     * Gets the zone ID of the zone that the HH/GQ is contained in
     * @return  returns an Integer referring to the zone ID
     */
    public Integer getZoneId()
    {
        return zoneId;
    }

    /**
     * Sets the zone ID of the zone that the HH/GQ is contained in
     * @param zoneId    
     */
    public void setZoneId(Integer zoneId)
    {
        this.zoneId = zoneId;
    }

    /**
     * Gets the type of geographic unit that the zone ID refers to
     * @return  returns a GeographyType object 
     */
    public GeographyType getGeographyType()
    {
        return geographyType;
    }

    /**
     * Sets the type of geographic unit that the zone ID pertains to
     * @param geographyType   a GeographyType object
     */
    public void setGeographyType(GeographyType geographyType)
    {
        this.geographyType = geographyType;
    }

    /**
     * Gets the Household serial number
     * @return  returns an Integer that corresponds to the Household serial number
     */
    public Integer getHhSerialNo()
    {
        return hhSerialNo;
    }

    /**
     * Sets the Household serial number
     * @param hhSerialNo    an Integer that corresponds to the Household serial number
     */
    public void setHhSerialNo(Integer hhSerialNo)
    {
        this.hhSerialNo = hhSerialNo;
    }

    /**
     * Gets the weight of the Household object
     * @return  returns a Double whose value equals the Household weight
     */
    public Double getHhWeight()
    {
        return hhWeight;
    }

    /**
     * Sets the weight of the Household object
     * @param hhWeight  a Double whose value equals the Household weight
     */
    public void setHhWeight(Double hhWeight)
    {
        this.hhWeight = hhWeight;
    }

}
