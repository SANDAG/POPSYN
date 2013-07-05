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
import org.sandag.popsyn.domain.Household;

/**
 * This interface maps Household objects to an external household data source.
 * It defines methods to access the data.
 */

public interface IReaderDao
{
    /**
     * Returns a list of Household objects by data source ID
     * @param sourceId data source ID 
     * @return a list of Household objects
     */
    List<Household> getAllBySource(int sourceId);

    /**
     * Returns a Household object
     * @param id household serial no
     * @param sourceId data source ID 
     * @return a Household object
     */
    Household getByIdAndSource(String id, int sourceId);
}
