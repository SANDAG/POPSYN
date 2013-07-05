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
package org.sandag.popsyn.io;

import org.sandag.popsyn.Version;
/**
 * This interface maps the Version object to the version table in the database. 
 * 
 */
public interface IVersionManagerDao
{
    /**
     * Returns a boolean value that indicates the PopSyn II run has successfully started.
     * The parameters of each run are inserted into the version table in the database.
     * @param version the Version object
     * @return a boolean value where true indicates a successful start
     */
    
    boolean initializeRun(Version version);
    /**
     * Returns a boolean value that indicates the PopSyn II run has successfully finished. 
     * The end time of each run is inserted into the version table in the database.
     * @param versionId Unique identifier for the PopSyn run
     * @return boolean a boolean value where true indicates a successful run.
     */
    boolean finalizeRun(Integer versionId);
}
