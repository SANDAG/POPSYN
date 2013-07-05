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
package org.sandag.popsyn.io.ibatis;

import org.sandag.popsyn.Version;
import org.sandag.popsyn.io.IVersionManagerDao;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
/**
 * This class implements the IVersionManagerDao interface.
 *
 */
public class VersionManagerDao
        extends SqlMapClientDaoSupport
        implements IVersionManagerDao
{
    public boolean initializeRun(Version version)
    {
        getSqlMapClientTemplate().insert("VersionManager.initPersist", version);        
        return (null != version.getId());
    }

    public boolean finalizeRun(Integer versionId)
    {
        return 1 == getSqlMapClientTemplate().update("VersionManager.finalizeRun", versionId);
    }
}
