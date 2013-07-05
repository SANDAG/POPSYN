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
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.domain.TargetGrowthFactor;
import org.sandag.popsyn.io.ITargetGrowthFactorDao;

/**
 * This class maps targetGrowthFactor objects to the growthFactor table in the database. It defines methods to access the table.
 * 
 */
public class TargetGrowthFactorDao
        extends SqlSessionDaoSupport
        implements ITargetGrowthFactorDao
{
    protected static final Logger LOGGER     = Logger.getLogger(TargetGrowthFactorDao.class);

    private static final String   DOMAIN_PATH = "org.sandag.popsyn.io.mybatis.sqlmap";

    /**
     * Returns a hashmap with control category as the key and growth factor as the value.
     * 
     * @param zone
     *            zone ID
     * @param version
     *            the Version object for this PopSynII run
     * @return a hashmap with control category as the key and growth factor as the value, for a given zone
     */
    @SuppressWarnings("unchecked")
    public HashMap<Integer, Double> getGrowthFactor(int zone, Version version)
    {
        HashMap<Integer, Double> result = new HashMap<Integer, Double>();
        HashMap<String, Integer> param = new HashMap<String, Integer>();

        param.put("growthYear", version.getTargetYear());
        param.put("geoZone", zone);
        param.put("geoType", version.getTargetGeography().getValue());
        param.put("luMajorVersion", version.getTargetMajorVersion());
        param.put("luMinorVersion", version.getTargetMinorVersion());

        List<TargetGrowthFactor> list = (List<TargetGrowthFactor>) getSqlSession().selectList(
                DOMAIN_PATH + ".TargetGrowthFactor.getGrowthFactor", param);

        for (TargetGrowthFactor gf : list)
        {
            if (gf != null) result.put(gf.getCategory().getValue(), gf.getValue());
        }
        return result;
    }
}
