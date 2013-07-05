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

import java.util.List;
import org.sandag.popsyn.validator.IPumaValMetrics;

/**
 * This interface accesses observed data for each validation measure and the corresponding PopSyn estimated values.  
 * The number of validation measures depends on the data source.    
 *
 */
public interface IValidatorDao
{
    /**
     * Returns a list of validation measures computed from balanced households for each PUMA from a PopSyn II run. 
     * @param sourceId data source ID
     * @param version the Version of the run
     * @return a list of validation measures
     */    
    List<IPumaValMetrics> getAllFromPopSyn(int sourceId, int version);

    /**
     * Returns a list of validation measures from observed data for each PUMA.
     * @param sourceId data source ID
     * @return a list of validation measures
     */    
    List<IPumaValMetrics> getAllFromObserved(int sourceId);
    
    /**
     * Returns validation measures computed from balanced households for a given PUMA from a PopSyn II run.
     * @param puma PUMA ID
     * @param sourceId data source ID
     * @param version the Version of the run
     * @return validation measures for a given PUMA
     */        
    IPumaValMetrics getByPumaFromPopSyn(int puma, int sourceId, int version);

    /**
     * Returns validation measures from observed data for a given PUMA.
     * @param puma PUMA ID
     * @param sourceId data source ID
     * @return validation measures for a given PUMA
     */
    IPumaValMetrics getByPumaFromObserved(int puma, int sourceId);
}
