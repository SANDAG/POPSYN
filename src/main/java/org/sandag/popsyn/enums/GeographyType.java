/*
 * Copyright 2011 San Diego Association of Governments (SANDAG)
 * 
 * 
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
 * This enum class defines the geography types corresponding to the values from the geography_type table in the database.
 * 
 */
public enum GeographyType implements IHasValue<Integer>
{
    /**
     * Census Tract 2000
     */
    TRACT(1),
    /**
     * Subregional Area
     */
    SRA(2),
    /**
     * Major Statistical Area
     */
    MSA(3),
    /**
     * The entire region for which a synthetic population is being generated
     */
    REGION(4),
    /**
     * Municipality
     */
    CITY(5),
    /**
     * City of San Diego Community Planning Area
     */
    CITY_CPA(6),
    /**
     * Zip code area as of 2009
     */
    ZIP_2009(7),
    /**
     * County Community Planning Area
     */
    COUNTY_CPA(8),
    /**
     * Series 10 Master Geographic Reference Area
     */
    MGRA_10(9),
    /**
     * Series 12 Master Geographic Reference Area
     */
    MGRA_12(10),
    /**
     * Series 13 Master Geographic Reference Area
     */
    MGRA_13(90),
    /**
     * Zip code area as of 2010
     */
    ZIP_2010(11),

    /**
     * Series 11 Traffic Analysis Zone
     */
    TAZ_11(13),
    /**
     * Community College District
     */
    COMM_COLL_DISTRICT(15),
    /**
     * Congressional District
     */
    CONG_DISTRICT(16),
    /**
     * City of San Diego Council District
     */
    COUNCIL_DISTRICT(17),
    /**
     * Elementary school district
     */
    ELEM_DISTRICT(18),
    /**
     * High school district
     */
    HS_DISTRICT(19),
    /**
     * Judicial district
     */
    JUDI_DISTRICT(20),
    /**
     * State Assembly District
     */
    ASS_DISTRICT(21),
    /**
     * State Senatorial District
     */
    SEN_DISTRICT(22),
    /**
     * County of San Diego Supervisorial District
     */
    SUP_DISTRICT(23),
    /**
     * Transit district
     */
    TRN_DISTRICT(24),
    /**
     * Unified school district
     */
    UNI_SCH_DISTRICT(25),
    /**
     * Major Employment Area
     */
    ELEM_AREA(26),
    /**
     * Smart Growth Opportunity Area
     */
    SGOA(27),
    /**
     * Transit Priority Area
     */
    TPA(28),
    /**
     * SB375 Transit Priority Area
     */
    TPA_SB375(29),
    /**
     * Urban Area Transit Plan Transit Priority Areas
     */
    TPA_UATS(30),
    /**
     * SR12 TAZ
     */
    TAZ_12(33),
    /**
     * SR13 TAZ
     */
    TAZ_13(34),
    /**
     * Public Use Microdata Area
     */
    PUMA(69),
    /**
     * Census Transportation Planning Package- Traffic Analysis Zone
     */
    CTPP_TAZ(57);

    private final Integer enumVal;

    private GeographyType()
    {
        enumVal = null;
    }

    private GeographyType(Integer anEnumValue)
    {
        this.enumVal = anEnumValue;
    }

    public Integer getValue()
    {
        return enumVal;
    }
}
