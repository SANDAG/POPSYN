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
import java.util.Map;
import org.sandag.popsyn.domain.Geography;
import org.sandag.popsyn.enums.GeographyType;
/**
 * This interface maps Geography objects to an external Geography data source. 
 * It defines methods to access the data.  
 *
 */
public interface IGeographyDao
{
    /**
     * Returns a list of Geography objects by the given geography type.
     * @param type the geography type
     * @return a list of Geography objects
     * 
     */ 
    List<Geography> getAllZonesByType(GeographyType type);
    
    /**
     * Returns a list of sub-zone Geography objects with centroids contained by the super-zone.
     * @param superZoneType super-zone geography type
     * @param superZoneId super-zone ID
     * @param subZoneType sub-zone geography type 
     * @return a list of Geography objects
     */
    List<Geography> getAllSubZonesByTypeAndId(GeographyType superZoneType, int superZoneId,
            GeographyType subZoneType);
    
    /**
     * Returns a Geography object that contains the centroid of the given sub-zone
     * @param subZoneType sub-zone geography type
     * @param subZoneId sub-zone ID
     * @param superZoneType super-zone geography type
     * @return a Geography object 
     */    
    Geography getSuperZoneByTypeAndId(GeographyType subZoneType, int subZoneId, GeographyType superZoneType);

    /**
     * Returns a hashmap between sub-zone and super-zone IDs. Each sub-zone corresponds to only one super-zone.
     * @param superZone super-zone geography type
     * @param subZone sub-zone geography type
     * @return a hashmap with sub-zone ID as the key and the super-zone ID as the value.
     */
    Map<Integer, Integer> getSubZoneMap(GeographyType superZone, GeographyType subZone);
    
    /**
     * Returns a hashmap between super-zone and sub-zone Geography with the super-zone geography as the key 
     * and a list of sub-zone geography objects as the value.
     * @param superZone super-zone geography type
     * @param subZone sub-zone geography type
     * @return a hashmap with the super-zone geography as the key and a list of sub-zone geography objects as the value.
     */
    Map<Geography, List<Geography>> getSuperZoneMap(GeographyType superZone, GeographyType subZone);
}