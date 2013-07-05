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

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.controls.IControlDefinition;
import org.sandag.popsyn.domain.Geography;
import org.sandag.popsyn.domain.Target;
import org.sandag.popsyn.enums.ControlCategoryType;

/**
 * This interface maps Target objects to an external target data source.
 * It defines methods to access the data.
 */
public interface ITargetDao
{
    /**
     * Returns a set of allocation Target objects by a control type and a list of allocation zones.
     * @param type the given control category
     * @param zones a list of given allocation zones
     * @param version the Version of the PopSyn II run
     * @return a set of allocation Target objects.
     */
    Set<Target> getAllocationSet(ControlCategoryType type, List<Geography> zones, Version version);

    /**
     * Returns the total number of households in a given zone. 
     * @param zoneId zone ID 
     * @param version the Version of this PopSyn II run
     * @return the total number of households
     */
    double getZonalHHTotal(int zoneId, Version version);

    /**
     * Returns the total number of group quarters in a given zone. 
     * @param zoneId zone ID
     * @param version the Version of this PopSyn II run
     * @return the total number of group quarters
     */
    double getZonalGQTotal(int zoneId, Version version);

    /**
     * Returns a hashtable where the key is a control definition and the value is a summation of values across the given list of zones.
     * @param <T> a specified type 
     * @param zones a list of geography objects
     * @param ctrlDefs a list of control definitions
     * @param version the Version object for this PopSynII run 
     * @return a hashtable where the key is a control definition and the value is a summation of values across the given list of zones
     */
    <T> Hashtable<IControlDefinition<T>, Double> getZonalTargets(List<Geography> zones,
            List<IControlDefinition<T>> ctrlDefs, Version version);

    /**
     * Returns a hashtable where the key is a control definition and the value is the zonal target.
     * @param <T> a specified type
     * @param zone zone ID
     * @param ctrlDefs  a list of control definitions
     * @param version the Version object for this PopSyn II run
     * @return a hashtable where the key is a control definition and the value is the zonal target.
     */
    <T> Hashtable<IControlDefinition<T>, Double> getTargetsByZone(int zone,
            List<IControlDefinition<T>> ctrlDefs, Version version);

    /**
     * Regenerates the target based on ACS data source. It recreates the targets generated based on ACS data source.
     */
    void regenerateTargets(int tyear);
    
}
