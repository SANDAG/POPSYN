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
 * This enum class defines a person's race. 
 *
 */
public enum RaceType implements IHasValue<Integer>
{

    HISPANIC(0),
    WHITE_ALONE(1),
    BLACK_ALONE(2),
    AMERICAN_INDI_ALONE(3),
    ALASKA_NATIVE_ALONE(4),
    AMERICAN_INDI_ALASKA_TRIBES(5),
    ASIAN_ALONE(6),
    NATIVE_PACIFIC_ISLANDER(7),
    OTHER_RACE_ALONE(8),
    TWO_OR_MORE_RACE(9);

    private final Integer enumVal;

    private RaceType()
    {
        enumVal = null;
    }

    private RaceType(Integer anEnumValue)
    {
        this.enumVal = anEnumValue;
    }

    public Integer getValue()
    {
        return enumVal;
    }
}
