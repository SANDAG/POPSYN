/*
 * Copyright 2011 San Diego Association of Governments (SANDAG)
 * 
 * 
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
package org.sandag.popsyn.io.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.sandag.popsyn.domain.Geography;
import org.sandag.popsyn.enums.GeographyType;
import org.sandag.popsyn.io.IGeographyDao;

/**
 * This class implements the IGeographyDao interface.
 * 
 */
public class GeographyDao
        extends SqlSessionDaoSupport
        implements IGeographyDao
{
    private static final String DOMAIN_PATH = "org.sandag.popsyn.io.mybatis";

    @SuppressWarnings("unchecked")
    public List<Geography> getAllZonesByType(GeographyType type)
    {

        return (List<Geography>) getSqlSession().selectList(
                DOMAIN_PATH + ".Geography.getAllZonesByType", type.getValue());
    }

    @SuppressWarnings("unchecked")
    public List<Geography> getAllSubZonesByTypeAndId(GeographyType superZone, int superZoneId,
            GeographyType subZone)
    {
        HashMap<String, Integer> qParams = new HashMap<String, Integer>();

        qParams.put("superZone", superZone.getValue());
        qParams.put("superZoneId", superZoneId);
        qParams.put("subZone", subZone.getValue());

        return (List<Geography>) getSqlSession().selectList(
                DOMAIN_PATH + ".Geography.getAllSubZonesByTypeAndId", qParams);
    }

    public Geography getSuperZoneByTypeAndId(GeographyType subZoneType, int subZoneId,
            GeographyType superZoneType)
    {
        HashMap<String, Integer> qParams = new HashMap<String, Integer>();

        qParams.put("superZone", superZoneType.getValue());
        qParams.put("subZoneId", subZoneId);
        qParams.put("subZone", subZoneType.getValue());

        return (Geography) getSqlSession().selectOne(
                DOMAIN_PATH + ".Geography.getSuperZoneByTypeAndId", qParams);
    }

    public Map<Geography, List<Geography>> getSuperZoneMap(GeographyType superZoneType,
            GeographyType subZoneType)
    {
        List<Geography> superZones = this.getAllZonesByType(superZoneType);

        Map<Geography, List<Geography>> superZoneMap = new HashMap<Geography, List<Geography>>(
                superZones.size());

        for (Geography superZone : superZones)
        {
            List<Geography> subZones = this.getAllSubZonesByTypeAndId(superZoneType,
                    superZone.getZone(), subZoneType);
            System.out.println("superZone=" + superZone.getZone());
            if (null != subZones && subZones.size() > 0) superZoneMap.put(superZone, subZones);
        }

        return superZoneMap;
    }

    public Map<Integer, Integer> getSubZoneMap(GeographyType superZoneType,
            GeographyType subZoneType)
    {
        List<Geography> subZones = this.getAllZonesByType(subZoneType);

        Map<Integer, Integer> subZoneMap = new HashMap<Integer, Integer>(subZones.size());

        for (Geography subZone : subZones)
        {
            Geography superZone = this.getSuperZoneByTypeAndId(subZoneType, subZone.getZone(),
                    superZoneType);
            Integer superZoneId = null;
            if (null != superZone)
            {
                superZoneId = superZone.getZone();
                subZoneMap.put(subZone.getZone(), superZoneId);
            }
        }

        return subZoneMap;

    }
}
