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
import org.sandag.popsyn.io.IValidatorDao;
import org.sandag.popsyn.validator.IPumaValMetrics;

/**
 * This class implements the IValidatorDao interface.
 * 
 */
public class ValidatorDao
        extends SqlSessionDaoSupport
        implements IValidatorDao
{
    private static final Logger LOGGER      = Logger.getLogger(ValidatorDao.class);
    private static final String DOMAIN_PATH = "org.sandag.popsyn.io.mybatis.sqlmap";

    @SuppressWarnings("unchecked")
    public List<IPumaValMetrics> getAllFromPopSyn(int sourceId, int version)
    {
        HashMap<String, Integer> paramMap = new HashMap<String, Integer>();

        paramMap.put("sourceId", sourceId);
        paramMap.put("versionId", version);
        return getSqlSession().selectList(
                DOMAIN_PATH + ".Validator.getFromPopSyn" + getType(sourceId) + "All", paramMap);
    }

    @SuppressWarnings("unchecked")
    public List<IPumaValMetrics> getAllFromObserved(int sourceId)
    {
        HashMap<String, Integer> paramMap = new HashMap<String, Integer>();

        paramMap.put("sourceId", sourceId);
        return getSqlSession().selectList(
                DOMAIN_PATH + ".Validator.getFromObserved" + getType(sourceId) + "All", paramMap);
    }

    public IPumaValMetrics getByPumaFromPopSyn(int puma, int sourceId, int version)
    {
        HashMap<String, Integer> paramMap = new HashMap<String, Integer>();

        paramMap.put("puma", puma);
        paramMap.put("versionId", version);
        paramMap.put("sourceId", sourceId);

        return (IPumaValMetrics) getSqlSession().selectOne(
                DOMAIN_PATH + ".Validator.getFromPopSyn" + getType(sourceId) + "ByPuma", paramMap);
    }

    public IPumaValMetrics getByPumaFromObserved(int puma, int sourceId)
    {

        return (IPumaValMetrics) getSqlSession().selectOne(
                DOMAIN_PATH + ".Validator.getFromObserved" + getType(sourceId) + "ByPuma", puma);
    }

    /**
     * Returns the string of the data source
     * 
     * @param sourceId
     *            data source id
     * @return string represents the data source
     */
    private String getType(int sourceId)
    {
        String result = null;
        if (sourceId == 1)
        {
            result = "Census";
        } else if (sourceId == 4 || sourceId == 7)
        {
            result = "Acs";
        } else
        {
            LOGGER.fatal("Invalid sourceId=" + sourceId);
        }
        return result;
    }
}
