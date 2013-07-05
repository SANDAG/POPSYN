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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.domain.Geography;
import org.sandag.popsyn.enums.GeographyType;
import org.sandag.popsyn.io.IGeographyDao;

/**
 * This class stores and records information related to balance geographies and related geographies (target, allocation, PUMA).
 * 
 */
public class BalanceGeographies
{
    /**
     * Balance geography type
     */
    protected GeographyType                   bGeoType;

    /**
     * Target geography type
     */
    protected GeographyType                   tGeoType;

    /**
     * Allocation geography type
     */
    protected GeographyType                   aGeoType;

    /**
     * Balance zone to PUMA Xref
     */
    protected Map<Integer, Integer>           bZonePumaXref;

    /**
     * Balance zone to Target Xref
     */
    protected Map<Geography, List<Geography>> bZoneTargetZoneXref;

    /**
     * Balance zones
     */
    protected Set<Geography>                  balanceZones;

    /**
     * Stores parameter/version information for this PopSyn run
     */
    protected Version                         version;

    /**
     * The data access object for Geography
     */
    private IGeographyDao                     geographyDao;

    /**
     * Constructs a new BalanceGeographies objects with the specified IGeograph Dao and version object
     * 
     * @param aGeographyDao
     *            The data access object for Geography
     * @param aVersion
     *            the version object for this PopSyn run
     */
    public BalanceGeographies(IGeographyDao aGeographyDao, Version aVersion)
    {
        this.version = aVersion;
        this.geographyDao = aGeographyDao;
        bGeoType = version.getBalanceGeography();
        tGeoType = version.getTargetGeography();
        aGeoType = version.getAllocationGeography();
    }

    /**
     * Gets the balance geography type
     * 
     * @return Returns the GeographyType object that represents the balance geography type
     */
    public GeographyType getBGeoType()
    {
        return bGeoType;
    }

    /**
     * Gets the target geography type
     * 
     * @return Returns the GeographyType object that represents the target geography type
     */
    public GeographyType getTGeoType()
    {
        return tGeoType;
    }

    /**
     * Gets the balance zone to PUMA Xref
     * 
     * @return Returns the Map that represents the Balance zone to PUMA Xref
     */
    public Map<Integer, Integer> getBZonePumaXref()
    {
        bZonePumaXref = geographyDao.getSubZoneMap(GeographyType.PUMA, bGeoType);
        return bZonePumaXref;
    }

    /**
     * Gets the balance zone to target Xref
     * 
     * @return Returns the Map that represents the balance zone to target Xref
     */
    public Map<Geography, List<Geography>> getBZoneTargetZoneXref()
    {
        bZoneTargetZoneXref = geographyDao.getSuperZoneMap(bGeoType, tGeoType);
        return bZoneTargetZoneXref;
    }

    /**
     * Gets the allocation zones that correspond to a specific balance zone id
     * 
     * @param superZoneId
     *            the balance zone id to find allocation zones for
     * @return Returns a List of Geography objects that represent the allocation zones
     */
    public List<Geography> getAllocationZones(int superZoneId)
    {
        return geographyDao.getAllSubZonesByTypeAndId(bGeoType, superZoneId, aGeoType);
    }

    /**
     * Gets the allocation zones that correspond to a specific balance zone Geography object
     * 
     * @param zone
     *            the balance zone Geography object to find allocation zones for
     * @return Returns a List of Geography objects that represent the allocation zones
     */
    public List<Geography> getAllocationZones(Geography zone)
    {
        return geographyDao.getAllSubZonesByTypeAndId(bGeoType, zone.getZone(), aGeoType);
    }

    /**
     * Gets the balance zones
     * 
     * @return Returns a Set of Geography objects that represent the balance zones
     */
    public Set<Geography> getBalanceZones()
    {
        return bZoneTargetZoneXref.keySet();
    }

    /**
     * Gets the balance zones in terms of an ArrayList of integers
     * 
     * @return Returns an ArrayList of integers that correspond to the balance zone IDs
     */
    public ArrayList<Integer> getBalanceZonesAsIntList()
    {
        ArrayList<Integer> result = new ArrayList<Integer>();
        /*
         * Set<Geography> set = bZoneTargetZoneXref.keySet(); for (Geography zone : set) { result.add(zone.getZone()); }
         */
        Set<Integer> set = bZonePumaXref.keySet();
        for (Integer zone : set)
        {
            result.add(zone);
        }

        return result;
    }
}
