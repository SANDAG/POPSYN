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
package org.sandag.popsyn.domain;

import org.sandag.popsyn.enums.RaceType;

/**
 * This class represents individual persons in PopSyn. Each Person object is mapped to a PUMS record in the database. As with for the Household class,
 * this mapping is carried out by ReaderDao and Household.xml
 * 
 */
public class Person
{
    /**
     * An long integer that stores the household serial number
     */
    private Long       hhSerialNo = null;

    /**
     * A integer of sequential person number within a household
     */
    private int        id         = -1;

    /**
     * An integer that corresponds to the person's age
     */
    protected int      age        = -1;

    /**
     * The person's race
     */
    protected RaceType race       = null;

    /**
     * The person's gender in terms of a boolean where true indicates male
     */
    protected boolean  male       = false;

    /**
     * The person's hispanic status in terms of a boolean where true indicates hispanic
     */
    protected boolean  hispanic   = false;

    /**
     * The initial PUMS sample weight of the person
     */
    protected double   initWeight = -1.0;

    public Long getHhSerialNo()
    {
        return hhSerialNo;
    }

    public void setHhSerialNo(Long aHhSerialNo)
    {
        this.hhSerialNo = aHhSerialNo;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int anId)
    {
        this.id = anId;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public boolean isMale()
    {
        return male;
    }

    public void setMale(boolean male)
    {
        this.male = male;
    }

    public RaceType getRace()
    {
        return race;
    }

    public void setRace(RaceType race)
    {
        this.race = race;
    }

    public boolean isHispanic()
    {
        return hispanic;
    }

    public void setHispanic(boolean hispanic)
    {
        this.hispanic = hispanic;
    }

    public String toString()
    {
        return "Id: " + id + ";Age: " + age + ";Race: " + race + ";Male: " + male + ";Hisp"
                + hispanic;
    }

}
