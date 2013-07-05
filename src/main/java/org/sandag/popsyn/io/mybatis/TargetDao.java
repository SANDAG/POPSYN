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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.controls.IControlDefinition;
import org.sandag.popsyn.domain.Geography;
import org.sandag.popsyn.domain.Target;
import org.sandag.popsyn.enums.ControlCategoryType;
import org.sandag.popsyn.io.ITargetDao;

/**
 * This class implements the ITargetDao interface.
 * 
 */
public class TargetDao
        extends SqlSessionDaoSupport
        implements ITargetDao
{
    protected static final Logger LOGGER      = Logger.getLogger(TargetDao.class);
    private static final String   DOMAIN_PATH = "org.sandag.popsyn.io.mybatis.sqlmap";

    public Set<Target> getAllocationSet(ControlCategoryType type, List<Geography> zones,
            Version version)
    {
        HashSet<Target> result = new HashSet<Target>();
        Target params = new Target();
        params.setYear(version.getTargetYear());
        params.setGeoType(version.getAllocationGeography());
        params.setLuMajorVersion(version.getTargetMajorVersion());
        params.setLuMinorVersion(version.getTargetMinorVersion());
        params.setCategory(type);
        for (Geography zone : zones)
        {
            params.setZone(zone.getZone());
            result.add((Target) getSqlSession().selectOne(DOMAIN_PATH + ".Target.getTargetByZone",
                    params));
        }
        return result;
    }

    public double getZonalHHTotal(int zoneId, Version version)
    {
        double result = 0;
        Target param = new Target();

        param.setYear(version.getTargetYear());
        param.setGeoType(version.getBalanceGeography());
        param.setLuMajorVersion(version.getTargetMajorVersion());
        param.setLuMinorVersion(version.getTargetMinorVersion());
        param.setCategory(ControlCategoryType.TOTAL_HOUSEHOLDS);
        param.setZone(zoneId);
        Target t = (Target) getSqlSession().selectOne(DOMAIN_PATH + ".Target.getTargetByZone",
                param);
        if (t != null)
        {
            result = t.getWeight();
        }
        return result;
    }

    public double getZonalGQTotal(int zoneId, Version version)
    {
        double result = 0;
        Target param = new Target();

        param.setYear(version.getTargetYear());
        param.setGeoType(version.getBalanceGeography());
        param.setLuMajorVersion(version.getTargetMajorVersion());
        param.setLuMinorVersion(version.getTargetMinorVersion());
        param.setCategory(ControlCategoryType.TOTAL_GQ);
        param.setZone(zoneId);
        Target t = (Target) getSqlSession().selectOne(DOMAIN_PATH + ".Target.getTargetByZone",
                param);
        if (t != null)
        {
            result = t.getWeight();
        }
        return result;
    }

    public <T> Hashtable<IControlDefinition<T>, Double> getZonalTargets(List<Geography> zones,
            List<IControlDefinition<T>> ctrlDefs, Version version)
    {
        Hashtable<IControlDefinition<T>, Double> resultTable = new Hashtable<IControlDefinition<T>, Double>(
                ctrlDefs.size());
        Target param = new Target();

        param.setYear(version.getTargetYear());
        param.setLuMajorVersion(version.getTargetMajorVersion());
        param.setLuMinorVersion(version.getTargetMinorVersion());
        param.setGeoType(version.getTargetGeography());
        List<Integer> geoZones = new ArrayList<Integer>();
        for (Geography zone : zones)
        {
            geoZones.add(zone.getZone());
        }

        param.setZones(geoZones);

        for (IControlDefinition<T> ctrlDef : ctrlDefs)
        {
            param.setCategory(ctrlDef.getCategory());
            Double returnVal = (Double) getSqlSession().selectOne(
                    DOMAIN_PATH + ".Target.getTargetSummation", param);
            if (null != returnVal && returnVal > 0) resultTable.put(ctrlDef, returnVal);
        }

        return resultTable;
    }

    public <T> Hashtable<IControlDefinition<T>, Double> getTargetsByZone(int zone,
            List<IControlDefinition<T>> ctrlDefs, Version version)
    {
        Hashtable<IControlDefinition<T>, Double> resultTable = new Hashtable<IControlDefinition<T>, Double>(
                ctrlDefs.size());
        Target param = new Target();

        param.setYear(version.getTargetYear());
        param.setLuMajorVersion(version.getTargetMajorVersion());
        param.setLuMinorVersion(version.getTargetMinorVersion());
        param.setGeoType(version.getTargetGeography());
        param.setZone(zone);

        for (IControlDefinition<T> ctrlDef : ctrlDefs)
        {
            param.setCategory(ctrlDef.getCategory());
            Double val = (Double) getSqlSession().selectOne(
                    DOMAIN_PATH + ".Target.getTargetByZone2", param);
            if (val != null && val > 0) resultTable.put(ctrlDef, val);
        }

        return resultTable;
    }

    public void regenerateTargets(int tyear)
    {
        getSqlSession().selectOne(DOMAIN_PATH + ".Target.regenerateTargets", tyear);
    }

}
