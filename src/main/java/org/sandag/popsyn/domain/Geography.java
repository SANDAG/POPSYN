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

import java.util.List;
import org.sandag.popsyn.enums.GeographyType;

/**
 * This class represents the geographic units used by PopSyn.
 * Each Geography object is mapped to a specific geography zone
 * in the database.  This mapping is carried out by GeographyDAO
 * and geography.xml
 *
 */
public class Geography
{
    /**
     * The geography zone ID
     */
    protected int             zone;
    
    /**
     * The geography type (such as PUMA, MGRA_10, TAZ_11)
     */
    protected GeographyType   geoType;
    
    /**
     * The geography alias, if applicable
     */
    protected String          alias;
    
    /**
     * A list of any child geographies, if applicable
     */
    protected List<Geography> children;

    /**
     * Gets the list of child geographies
     * @return  returns a List of Geography objects
     */
    public List<Geography> getChildren()
    {
        return children;
    }

    /**
     * Sets the list of child geographies
     * @param children  a List of Geography objects
     */
    public void setChildren(List<Geography> children)
    {
        this.children = children;
    }

    /**
     * Gets the Geography object's alias
     * @return  returns the Geography object's alias in the form of a String
     */
    public String getAlias()
    {
        return alias;
    }

    /**
     * Sets the Geography object's alias
     * @param alias the alias to use
     */
    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    /**
     * Gets the Geography object's zone ID
     * @return  an integer equaling the zone ID
     */
    public int getZone()
    {
        return zone;
    }

    /**
     * Sets the Geography object's zone ID
     * @param zone  the zone ID to use
     */
    public void setZone(int zone)
    {
        this.zone = zone;
    }

    /**
     * Gets the GeographyType associated with this Geography object
     * @return  returns a GeographyType object
     */
    public GeographyType getGeoType()
    {
        return geoType;
    }

    /**
     * Sets the GeographyType associated with this Geography object
     * @param geoType   the GeographyType to use
     */
    public void setGeoType(GeographyType geoType)
    {
        this.geoType = geoType;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof Geography)) return false;

        Geography geography = (Geography) obj;
        return geography.getZone() == zone && geoType.equals(geography.getGeoType());
    }

    @Override
    public int hashCode()
    {
        final int multiplier = 37;
        int code = 23;

        code = multiplier * code + zone;
        code = multiplier * code + geoType.getValue();

        return code;
    }
}
