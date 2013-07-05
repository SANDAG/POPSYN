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
 * This enum class defines housing unit type by household or group quarters categories.
 *
 */
public enum UnitType implements IHasValue<Integer>
{
   
    HOUSING_UNIT(1),
    /**
     * Institutional group quarters.
     */
    INST_GROUP_QUARTERS(2),
    /**
     * College group quarters.
     */
    NON_INST_COLL(3), 
    /**
     * Military group quarters.
     */
    NON_INST_MIL(4), 
    /**
     * Non-institutional other group quarters such as senior caring center. 
     */
    NON_INST_OTHER(5);
    
   /**
    * The enum value of the specified unit type.
    */
    private final Integer enumVal;
    
    private UnitType()
    {
        enumVal = null;
    }
    
    private UnitType(Integer anEnumValue)
    {
        this.enumVal = anEnumValue;
    }
    
    /**
     * Returns the enum value of the specified unit type.
     * @return the enum value of the specified unit type
     */
    public Integer getValue()
    {
        return enumVal;
    }
}
