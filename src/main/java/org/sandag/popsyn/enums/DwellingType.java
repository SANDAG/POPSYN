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
package org.sandag.popsyn.enums;

import org.sandag.popsyn.typehandler.IHasValue;

/**
 * This enum class defines a household's dwelling type.
 *
 */
public enum DwellingType implements IHasValue<Integer>
{
    /**
     * Other dwelling type. The enum value is 0.
     */
    OTHER(0),
    /**
     * Single-family dwelling type. The enum value is 1.
     */
    SINGLE_FAMILY(1),
    /**
     * Multi-family dwelling type. The enum value is 2.
     */
    MULTI_UNIT(2),
    /**
     * Mobile home dwelling type. The enum value is 3.
     */
    MOBILE_HOME(3);
   
   /**
    * The enum value of the specified dwelling type.
    */
    private final Integer enumVal;
    
    private DwellingType()
    {
        enumVal = null;
    }
    
    private DwellingType(Integer anEnumValue)
    {
        this.enumVal = anEnumValue;
    }
    
    /**
     * Returns the integer enum value of the dwelling type.
     * @return the integer enum value of the dwelling type
     */
    public Integer getValue()
    {
        return enumVal;
    }
}
