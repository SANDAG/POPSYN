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
 * This enum class defines household and person control categories. The enum values correspond to 
 * the values in the target_category table in the database.
 *
 */
public enum ControlCategoryType implements IHasValue<Integer>
{
    /**
     * The household control category defined by households with no child present. The enum value is 4.
     */
    CHILDREN_0(4),
    /**
     * The household control category defined by households with one or more children present. The enum value is 7.
     */
    CHILDREN_1PLUS(7),
    /**
     * The household control category defined by households with one person. The enum value is 10. 
     */
    HHSIZE_1(10),
    /**
     * The household control category defined by households with two persons. The enum value is 13. 
     */
    HHSIZE_2(13),
    /**
     * The household control category defined by households with three persons. The enum value is 16. 
     */
    HHSIZE_3(16),
    /**
     * The household control category defined by households with four or more persons. The enum value is 19. 
     */
    HHSIZE_4PLUS(19),
    /**
     * The household control category defined by households with no workers. The enum value is 22. 
     */
    WORKERS_0(22),
    /**
     * The household control category defined by households with one worker. The enum value is 25. 
     */
    WORKERS_1(25),
    /**
     * The household control category defined by households with two workers. The enum value is 28. 
     */
    WORKERS_2(28),
    /**
     * The household control category defined by households with three or more workers. The enum value is 31. 
     */
    WORKERS_3PLUS(31),
    /**
     * The group quarters control category defined by institutionalized group quarters. The enum value is 34. 
     */
    GQ_INST(34),
    /**
     * The group quarters control category defined by college group quarters. The enum value is 37. 
     */
    GQ_COLL(37),
    /**
     * The group quarters control category defined by military group quarters. The enum value is 40.
     */     
    GQ_MIL(40),
    /**
     * The group quarters control category defined by non-institutionalized group quarters. The enum value is 43.
     */     
    GQ_OTHER(43),
    /**
     * The household control category defined by households with income below $30,000. The enum value is 49. 
     */
    INCOME_30K(49),
    /**
     * The household control category defined by households with income equal to or greater than $30,000 and less than $60,000. 
     * The enum value is 52. 
     */
    INCOME_60K(52),
    /**
     * The household control category defined by households with income equal to or greater than $60,000 and less than $100,000. 
     * The enum value is 55. 
     */
    INCOME_100K(55),
    /**
     * The household control category defined by households with income equal to or greater than $100,000 and less than $150,000. 
     * The enum value is 58. 
     */
    INCOME_150K(58),
    /**
     * The household control category defined by households with income equal to or greater than $150,000. The enum value is 61. 
     */
    INCOME_150KPLUS(61),
    /**
     * The household control category defined by households in a single-family structure. The enum value is 64.
     */
    SINGLE_FAMILY(64),
    /**
     * The household control category defined by households in a multi-family structure. The enum value is 67.
     */
    MULTI_UNIT(67),
    /**
     * The household control category defined by households in a mobile home structure. The enum value is 70.
     */
    MOBILE_HOME(70),
    /**
     * The total number of households. The enum value is 109.
     */
    TOTAL_HOUSEHOLDS(109),
    /**
     * The total number of persons in group quarters. The enum value is 112.
     */
    TOTAL_GQ(112),
    /**
     * The person control category defined by persons of Hispanic race. The enum value is 115.
     */
    HISPANIC(115),
    /**
     * The person control category defined by persons of White alone race. The enum value is 118.
     */
    WHITE(118),
    /**
     * The person control category defined by persons of Black alone race. The enum value is 121.
     */
    BLACK(121),
    /**
     * The person control category defined by persons of American Indian or Alaska Native race. The enum value is 124.
     */
    NINDIAN(124),
    /**
     * The person control category defined by persons of Asian alone race. The enum value is 127.
     */
    ASIAN(127),
    /**
     * The person control category defined by persons of Native Hawaiian and Other Pacific Islander alone race. The enum value is 130.
     */
    ISLANDER(130),
    /**
     * The person control category defined by persons of other race alone. The enum value is 133.
     */
    RACEOTHER(133),
    /**
     * The person control category defined by persons of two or more races. The enum value is 136.
     */
    MIXED(136),
    /**
     * The person control category defined by persons age 0 to 17. The enum value is 139.
     */
    AGE0TO17(139),
    /**
     * The person control category defined by persons age 18 to 24. The enum value is 142.
     */
    AGE18TO24(142),
    /**
     * The person control category defined by persons age 25 to 34. The enum value is 145.
     */
    AGE25TO34(145),
    /**
     * The person control category defined by persons age 35 to 49. The enum value is 148.
     */
    AGE35TO49(148),
    /**
     * The person control category defined by persons age 50 to 64. The enum value is 151.
     */
    AGE50TO64(151),
    /**
     * The person control category defined by persons age 65 to 79. The enum value is 154.
     */
    AGE65TO79(154),
    /**
     * The person control category defined by persons age 80 and over. The enum value is 157.
     */
    AGE80PLUS(157),
    /**
     * The person control category defined by persons of female gender. The enum value is 160.
     */
    FEMALE(160),
    /**
     * The person control category defined by persons of male gender. The enum value is 163.
     */
    MALE(163),
    /**
     * The total number of persons. The enum value is 166.
     */
    TOTAL_POP(166);
    /**
     * The enum value of the specified control category type. 
     */
    private final Integer enumVal;
    
    private ControlCategoryType()
    {
        enumVal = null;
    }
    
    /**
     * Constructs a newly allocated ControlCategoryType object that represents the enum value indicated by the Integer parameter. 
     * @param anEnumValue the value to be represented by the ControlCategoryType object.
     */
    private ControlCategoryType(Integer anEnumValue)
    {
        this.enumVal = anEnumValue;
    }
    
    /**
     * Returns the integer enum value of the control category type.
     * @return the integer enum value of the control category type
     */
    public Integer getValue()
    {
        return enumVal;
    }
}