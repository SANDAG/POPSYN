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
package org.sandag.popsyn.validator;

import java.util.HashMap;

/**
 * This class implements IPumaValMetrics for Census 2000 data source with 96 validation attributes.
 * 
 */
public class CensusPumaValMetrics
        implements IPumaValMetrics
{
    protected String                    puma                             = null;

    protected int                       hhsUnivs                         = -1;
    protected int                       famHhs                           = -1;
    protected int                       nonFamHhs                        = -1;

    protected int                       hhsbyHolderAge15to64             = -1;
    protected int                       hhsbyHolderAge65Plus             = -1;
    protected int                       famHhsbyHolderAge15to64          = -1;
    protected int                       famHhsbyHolderAge65Plus          = -1;
    protected int                       nonFamHhsbyHolderAge15to64       = -1;
    protected int                       nonFamHhsbyHolderAge65Plus       = -1;

    protected int                       hh1Person                        = -1;
    protected int                       hh2Persons                       = -1;
    protected int                       hh3Persons                       = -1;
    protected int                       hh4PlusPersons                   = -1;

    protected int                       famHhsOwnChildUnder18            = -1;
    protected int                       famHhsNoOwnChildUnder18          = -1;

    protected int                       famHhsAge18UnderPres             = -1;
    protected int                       nonFamHhsAge18UnderPres          = -1;
    protected int                       famHhsNoAge18UnderPres           = -1;
    protected int                       nonFamHhsNoAge18UnderPres        = -1;

    protected int                       hhs1PlusAge65Plus                = -1;
    protected int                       hhsNoAge65Plus                   = -1;
    protected int                       hhs1PersonAge65Plus              = -1;
    protected int                       fam2PlusPersons1PlusAge65Plus    = -1;
    protected int                       nonFam2PlusPersons1PlusAge65Plus = -1;
    protected int                       hhs1PersonAge65Under             = -1;
    protected int                       fam2PlusPersonsNoAge65Plus       = -1;
    protected int                       nonFam2PlusPersonsNoAge65Plus    = -1;

    protected int                       numOfWorkers0                    = -1;
    protected int                       numOfWorkers1                    = -1;
    protected int                       numOfWorkers2                    = -1;
    protected int                       numOfWorkers3Plus                = -1;

    protected int                       incomeUnder30k                   = -1;
    protected int                       income30kto60k                   = -1;
    protected int                       income60kto100k                  = -1;
    protected int                       income100kto150k                 = -1;
    protected int                       income150kPlus                   = -1;

    protected int                       incomeUnder10k                   = -1;
    protected int                       income10kto20k                   = -1;
    protected int                       income20kto30k                   = -1;
    protected int                       income30kto40k                   = -1;
    protected int                       income40kto50k                   = -1;
    protected int                       income50kto60k                   = -1;
    protected int                       income60kto75k                   = -1;
    protected int                       income75kto100k                  = -1;
    protected int                       income60kUnder                   = -1;

    protected int                       singleFamily                     = -1;
    protected int                       multiUnit                        = -1;
    protected int                       mobileHome                       = -1;
    protected int                       numOfOwned                       = -1;
    protected int                       numOfRent                        = -1;

    protected int                       hh1per0wkr                       = -1;
    protected int                       hh1per1wkr                       = -1;
    protected int                       hh2per0wkr                       = -1;
    protected int                       hh2per1wkr                       = -1;
    protected int                       hh2per2wkr                       = -1;
    protected int                       hh3per0wkr                       = -1;
    protected int                       hh3per1wkr                       = -1;
    protected int                       hh3per2wkr                       = -1;
    protected int                       hh3per3wkr                       = -1;
    protected int                       hh4plusper0wkr                   = -1;
    protected int                       hh4plusper1wkr                   = -1;
    protected int                       hh4plusper2wkr                   = -1;
    protected int                       hh4plusper3wkr                   = -1;
    protected int                       hh1perHhinc0to30k                = -1;
    protected int                       hh1perHhinc30kto60k              = -1;
    protected int                       hh1perHhinc60kto100k             = -1;
    protected int                       hh1perHhinc100kplus              = -1;
    protected int                       hh2perHhinc0to30k                = -1;
    protected int                       hh2perHhinc30kto60k              = -1;
    protected int                       hh2perHhinc60kto100k             = -1;
    protected int                       hh2perHhinc100kplus              = -1;
    protected int                       hh3perHhinc0to30k                = -1;
    protected int                       hh3perHhinc30kto60k              = -1;
    protected int                       hh3perHhinc60kto100k             = -1;
    protected int                       hh3perHhinc100kplus              = -1;
    protected int                       hh4plusperHhinc0to30k            = -1;
    protected int                       hh4plusperHhinc30kto60k          = -1;
    protected int                       hh4plusperHhinc60kto100k         = -1;
    protected int                       hh4plusperHhinc100kplus          = -1;
    protected int                       hh0wkrHhinc0kto30k               = -1;
    protected int                       hh0wkrHhinc30kto60k              = -1;
    protected int                       hh0wkrHhinc60kto100k             = -1;
    protected int                       hh0wkrHhinc100kPlus              = -1;
    protected int                       hh1wkrHhinc0kto30k               = -1;
    protected int                       hh1wkrHhinc30kto60k              = -1;
    protected int                       hh1wkrHhinc60kto100k             = -1;
    protected int                       hh1wkrHhinc100kPlus              = -1;
    protected int                       hh2wkrHhinc0kto30k               = -1;
    protected int                       hh2wkrHhinc30kto60k              = -1;
    protected int                       hh2wkrHhinc60kto100k             = -1;
    protected int                       hh2wkrHhinc100kPlus              = -1;
    protected int                       hh3wkrHhinc0kto30k               = -1;
    protected int                       hh3wkrHhinc30kto60k              = -1;
    protected int                       hh3wkrHhinc60kto100k             = -1;
    protected int                       hh3wkrHhinc100kPlus              = -1;

    protected int                       numPersonsHhUnivs                = -1;
    protected int                       numPersonsFamilyHh               = -1;
    protected int                       numPersonsNonFamilyHh            = -1;
    protected int                       numPersonsIncomeBelowPoverty     = -1;

    protected int                       numPersonsFamilyHhUnivs          = -1;
    protected int                       numOfPersonsAsHolders            = -1;
    protected int                       numOfPersonsAsSpouse             = -1;
    protected int                       numOfPersonsAsChild              = -1;
    protected int                       numOfPersonsAsOthers             = -1;
    protected int                       numOfPersonsNotRelatedToHolder   = -1;

    protected int                       numOfPersonsGQIncludedUnivs      = -1;

    protected int                       male                             = -1;
    protected int                       female                           = -1;

    protected int                       age0to5                          = -1;
    protected int                       age3PlusUnivs                    = -1;
    protected int                       age6to11                         = -1;
    protected int                       age12to15                        = -1;
    protected int                       age16PlusUnivs                   = -1;
    protected int                       age16to17                        = -1;
    protected int                       age18to24                        = -1;
    protected int                       age25to34                        = -1;
    protected int                       age35to49                        = -1;
    protected int                       age50to64                        = -1;
    protected int                       age65to79                        = -1;
    protected int                       age80Plus                        = -1;
    protected int                       age17AndUnder                    = -1;
    protected int                       age18to64                        = -1;
    protected int                       age65Plus                        = -1;

    protected int                       marriedSpousePres                = -1;

    protected int                       hispanic                         = -1;
    protected int                       whiteAlone                       = -1;
    protected int                       blackAlone                       = -1;
    protected int                       indiAlaskaAlone                  = -1;
    protected int                       asianAlone                       = -1;
    protected int                       islanderAlone                    = -1;
    protected int                       otherRaceAloneOrTwoPlusRaces     = -1;

    protected int                       worked27plusWksUnivs             = -1;
    protected int                       worked27plusWks35PlusHrsPerWk    = -1;
    protected int                       worked27plusWks15to34HrsPerWk    = -1;
    protected int                       worked27plusWks1to14HrsPerWk     = -1;

    protected int                       civilianEmployed                 = -1;
    protected int                       militaryEmployed                 = -1;
    protected int                       unemployed                       = -1;
    protected int                       notInLaborForce                  = -1;

    protected int                       occpMng                         = -1;
    protected int                       occpService                     = -1;
    protected int                       occpSales                       = -1;
    protected int                       occpConstruct                   = -1;
    protected int                       occpTransport                   = -1;

    protected int                       enrolledNurseyToGrade12          = -1;
    protected int                       enrollPostSecondary              = -1;

    // puma level census statistics, Key attribute ID, value
    protected HashMap<Integer, Integer> statsMap                         = null;
    protected static int[]              universeIndex                    = {1, 96, 100, 106, 130, 134,
           144, 147                                                      };

    public void createMap()
    {
        statsMap = new HashMap<Integer, Integer>();
        statsMap.put(1, hhsUnivs);
        statsMap.put(2, famHhs);
        statsMap.put(3, nonFamHhs);
        statsMap.put(4, hhsbyHolderAge15to64);
        statsMap.put(5, hhsbyHolderAge65Plus);
        statsMap.put(6, hh1Person);
        statsMap.put(7, hh2Persons);
        statsMap.put(8, hh3Persons);
        statsMap.put(9, hh4PlusPersons);
        statsMap.put(10, famHhsbyHolderAge15to64);
        statsMap.put(11, famHhsbyHolderAge65Plus);
        statsMap.put(12, nonFamHhsbyHolderAge15to64);
        statsMap.put(13, nonFamHhsbyHolderAge65Plus);
        statsMap.put(14, famHhsOwnChildUnder18);
        statsMap.put(15, famHhsNoOwnChildUnder18);
        statsMap.put(16, famHhsAge18UnderPres);
        statsMap.put(17, famHhsNoAge18UnderPres);
        statsMap.put(18, nonFamHhsAge18UnderPres);
        statsMap.put(19, nonFamHhsNoAge18UnderPres);
        statsMap.put(20, hhs1PlusAge65Plus);
        statsMap.put(21, hhsNoAge65Plus);
        statsMap.put(22, hhs1PersonAge65Plus);
        statsMap.put(23, fam2PlusPersons1PlusAge65Plus);
        statsMap.put(24, nonFam2PlusPersons1PlusAge65Plus);
        statsMap.put(25, hhs1PersonAge65Under);
        statsMap.put(26, fam2PlusPersonsNoAge65Plus);
        statsMap.put(27, nonFam2PlusPersonsNoAge65Plus);
        statsMap.put(28, numOfWorkers0);
        statsMap.put(29, numOfWorkers1);
        statsMap.put(30, numOfWorkers2);
        statsMap.put(31, numOfWorkers3Plus);
        statsMap.put(32, incomeUnder30k);
        statsMap.put(33, income30kto60k);
        statsMap.put(34, income60kto100k);
        statsMap.put(35, income100kto150k);
        statsMap.put(36, income150kPlus);
        statsMap.put(37, incomeUnder10k);
        statsMap.put(38, income10kto20k);
        statsMap.put(39, income20kto30k);
        statsMap.put(40, income30kto40k);
        statsMap.put(41, income40kto50k);
        statsMap.put(42, income50kto60k);
        statsMap.put(43, income60kto75k);
        statsMap.put(44, income75kto100k);
        statsMap.put(45, income60kUnder);
        statsMap.put(46, singleFamily);
        statsMap.put(47, multiUnit);
        statsMap.put(48, mobileHome);
        statsMap.put(49, numOfOwned);
        statsMap.put(50, numOfRent);

        statsMap.put(51, hh1per0wkr);
        statsMap.put(52, hh1per1wkr);
        statsMap.put(53, hh2per0wkr);
        statsMap.put(54, hh2per1wkr);
        statsMap.put(55, hh2per2wkr);
        statsMap.put(56, hh3per0wkr);
        statsMap.put(57, hh3per1wkr);
        statsMap.put(58, hh3per2wkr);
        statsMap.put(59, hh3per3wkr);
        statsMap.put(60, hh4plusper0wkr);
        statsMap.put(61, hh4plusper1wkr);
        statsMap.put(62, hh4plusper2wkr);
        statsMap.put(63, hh4plusper3wkr);
        statsMap.put(64, hh1perHhinc0to30k);
        statsMap.put(65, hh1perHhinc30kto60k);
        statsMap.put(66, hh1perHhinc60kto100k);
        statsMap.put(67, hh1perHhinc100kplus);
        statsMap.put(68, hh2perHhinc0to30k);
        statsMap.put(69, hh2perHhinc30kto60k);
        statsMap.put(70, hh2perHhinc60kto100k);
        statsMap.put(71, hh2perHhinc100kplus);
        statsMap.put(72, hh3perHhinc0to30k);
        statsMap.put(73, hh3perHhinc30kto60k);
        statsMap.put(74, hh3perHhinc60kto100k);
        statsMap.put(75, hh3perHhinc100kplus);
        statsMap.put(76, hh4plusperHhinc0to30k);
        statsMap.put(77, hh4plusperHhinc30kto60k);
        statsMap.put(78, hh4plusperHhinc60kto100k);
        statsMap.put(79, hh4plusperHhinc100kplus);
        statsMap.put(80, hh0wkrHhinc0kto30k);
        statsMap.put(81, hh0wkrHhinc30kto60k);
        statsMap.put(82, hh0wkrHhinc60kto100k);
        statsMap.put(83, hh0wkrHhinc100kPlus);
        statsMap.put(84, hh1wkrHhinc0kto30k);
        statsMap.put(85, hh1wkrHhinc30kto60k);
        statsMap.put(86, hh1wkrHhinc60kto100k);
        statsMap.put(87, hh1wkrHhinc100kPlus);
        statsMap.put(88, hh2wkrHhinc0kto30k);
        statsMap.put(89, hh2wkrHhinc30kto60k);
        statsMap.put(90, hh2wkrHhinc60kto100k);
        statsMap.put(91, hh2wkrHhinc100kPlus);
        statsMap.put(92, hh3wkrHhinc0kto30k);
        statsMap.put(93, hh3wkrHhinc30kto60k);
        statsMap.put(94, hh3wkrHhinc60kto100k);
        statsMap.put(95, hh3wkrHhinc100kPlus);

        
        statsMap.put(96, numPersonsHhUnivs);
        statsMap.put(97, numPersonsFamilyHh);
        statsMap.put(98, numPersonsNonFamilyHh);
        statsMap.put(99, numPersonsIncomeBelowPoverty);
        statsMap.put(100, numPersonsFamilyHhUnivs);
        statsMap.put(101, numOfPersonsAsHolders);
        statsMap.put(102, numOfPersonsAsSpouse);
        statsMap.put(103, numOfPersonsAsChild);
        statsMap.put(104, numOfPersonsAsOthers);
        statsMap.put(105, numOfPersonsNotRelatedToHolder);
        statsMap.put(106, numOfPersonsGQIncludedUnivs);
        statsMap.put(107, male);
        statsMap.put(108, female);
        statsMap.put(109, age0to5);
        statsMap.put(110, age6to11);
        statsMap.put(111, age12to15);
        statsMap.put(112, age16to17);
        statsMap.put(113, age18to24);
        statsMap.put(114, age25to34);
        statsMap.put(115, age35to49);
        statsMap.put(116, age50to64);
        statsMap.put(117, age65to79);
        statsMap.put(118, age80Plus);
        statsMap.put(119, age17AndUnder);
        statsMap.put(120, age18to64);
        statsMap.put(121, age65Plus);
        statsMap.put(122, marriedSpousePres);
        statsMap.put(123, hispanic);
        statsMap.put(124, whiteAlone);
        statsMap.put(125, blackAlone);
        statsMap.put(126, indiAlaskaAlone);
        statsMap.put(127, asianAlone);
        statsMap.put(128, islanderAlone);
        statsMap.put(129, otherRaceAloneOrTwoPlusRaces);
        statsMap.put(130, worked27plusWksUnivs);
        statsMap.put(131, worked27plusWks35PlusHrsPerWk);
        statsMap.put(132, worked27plusWks15to34HrsPerWk);
        statsMap.put(133, worked27plusWks1to14HrsPerWk);
        statsMap.put(134, age16PlusUnivs);
        statsMap.put(135, civilianEmployed);
        statsMap.put(136, militaryEmployed);
        statsMap.put(137, unemployed);
        statsMap.put(138, notInLaborForce);
        
        statsMap.put(139, occpMng);
        statsMap.put(140, occpService);
        statsMap.put(141, occpSales);
        statsMap.put(142, occpConstruct);
        statsMap.put(143, occpTransport);
        
        statsMap.put(144, age3PlusUnivs);
        statsMap.put(145, enrolledNurseyToGrade12);
        statsMap.put(146, enrollPostSecondary);

    }

    public HashMap<Integer, Integer> getStatsMap()
    {
        return statsMap;
    }

    public void setStatsMap(HashMap<Integer, Integer> statsMap)
    {
        this.statsMap = statsMap;
    }

    public String getPuma()
    {
        return puma;
    }

    public void setPuma(String puma)
    {
        this.puma = puma;
    }

    public int getHhsUnivs()
    {
        return hhsUnivs;
    }

    public void setHhsUnivs(int hhsUnivs)
    {
        this.hhsUnivs = hhsUnivs;
    }

    public int getFamHhs()
    {
        return famHhs;
    }

    public void setFamHhs(int famHhs)
    {
        this.famHhs = famHhs;
    }

    public int getNonFamHhs()
    {
        return nonFamHhs;
    }

    public void setNonFamHhs(int nonFamHhs)
    {
        this.nonFamHhs = nonFamHhs;
    }

    public int getHhsbyHolderAge15to64()
    {
        return hhsbyHolderAge15to64;
    }

    public void setHhsbyHolderAge15to64(int hhsbyHolderAge15to64)
    {
        this.hhsbyHolderAge15to64 = hhsbyHolderAge15to64;
    }

    public int getHhsbyHolderAge65Plus()
    {
        return hhsbyHolderAge65Plus;
    }

    public void setHhsbyHolderAge65Plus(int hhsbyHolderAge65Plus)
    {
        this.hhsbyHolderAge65Plus = hhsbyHolderAge65Plus;
    }

    public int getFamHhsbyHolderAge15to64()
    {
        return famHhsbyHolderAge15to64;
    }

    public void setFamHhsbyHolderAge15to64(int famHhsbyHolderAge15to64)
    {
        this.famHhsbyHolderAge15to64 = famHhsbyHolderAge15to64;
    }

    public int getFamHhsbyHolderAge65Plus()
    {
        return famHhsbyHolderAge65Plus;
    }

    public void setFamHhsbyHolderAge65Plus(int famHhsbyHolderAge65Plus)
    {
        this.famHhsbyHolderAge65Plus = famHhsbyHolderAge65Plus;
    }

    public int getNonFamHhsbyHolderAge15to64()
    {
        return nonFamHhsbyHolderAge15to64;
    }

    public void setNonFamHhsbyHolderAge15to64(int nonFamHhsbyHolderAge15to64)
    {
        this.nonFamHhsbyHolderAge15to64 = nonFamHhsbyHolderAge15to64;
    }

    public int getNonFamHhsbyHolderAge65Plus()
    {
        return nonFamHhsbyHolderAge65Plus;
    }

    public void setNonFamHhsbyHolderAge65Plus(int nonFamHhsbyHolderAge65Plus)
    {
        this.nonFamHhsbyHolderAge65Plus = nonFamHhsbyHolderAge65Plus;
    }

    public int getHh1Person()
    {
        return hh1Person;
    }

    public void setHh1Person(int hh1Person)
    {
        this.hh1Person = hh1Person;
    }

    public int getHh2Persons()
    {
        return hh2Persons;
    }

    public void setHh2Persons(int hh2Persons)
    {
        this.hh2Persons = hh2Persons;
    }

    public int getHh3Persons()
    {
        return hh3Persons;
    }

    public void setHh3Persons(int hh3Persons)
    {
        this.hh3Persons = hh3Persons;
    }

    public int getHh4PlusPersons()
    {
        return hh4PlusPersons;
    }

    public void setHh4PlusPersons(int hh4PlusPersons)
    {
        this.hh4PlusPersons = hh4PlusPersons;
    }

    public int getFamHhsOwnChildUnder18()
    {
        return famHhsOwnChildUnder18;
    }

    public void setFamHhsOwnChildUnder18(int famHhsOwnChildUnder18)
    {
        this.famHhsOwnChildUnder18 = famHhsOwnChildUnder18;
    }

    public int getFamHhsNoOwnChildUnder18()
    {
        return famHhsNoOwnChildUnder18;
    }

    public void setFamHhsNoOwnChildUnder18(int famHhsNoOwnChildUnder18)
    {
        this.famHhsNoOwnChildUnder18 = famHhsNoOwnChildUnder18;
    }

    public int getFamHhsAge18UnderPres()
    {
        return famHhsAge18UnderPres;
    }

    public void setFamHhsAge18UnderPres(int famHhsAge18UnderPres)
    {
        this.famHhsAge18UnderPres = famHhsAge18UnderPres;
    }

    public int getNonFamHhsAge18UnderPres()
    {
        return nonFamHhsAge18UnderPres;
    }

    public void setNonFamHhsAge18UnderPres(int nonFamHhsAge18UnderPres)
    {
        this.nonFamHhsAge18UnderPres = nonFamHhsAge18UnderPres;
    }

    public int getFamHhsNoAge18UnderPres()
    {
        return famHhsNoAge18UnderPres;
    }

    public void setFamHhsNoAge18UnderPres(int famHhsNoAge18UnderPres)
    {
        this.famHhsNoAge18UnderPres = famHhsNoAge18UnderPres;
    }

    public int getNonFamHhsNoAge18UnderPres()
    {
        return nonFamHhsNoAge18UnderPres;
    }

    public void setNonFamHhsNoAge18UnderPres(int nonFamHhsNoAge18UnderPres)
    {
        this.nonFamHhsNoAge18UnderPres = nonFamHhsNoAge18UnderPres;
    }

    public int getHhs1PlusAge65Plus()
    {
        return hhs1PlusAge65Plus;
    }

    public void setHhs1PlusAge65Plus(int hhs1PlusAge65Plus)
    {
        this.hhs1PlusAge65Plus = hhs1PlusAge65Plus;
    }

    public int getHhsNoAge65Plus()
    {
        return hhsNoAge65Plus;
    }

    public void setHhsNoAge65Plus(int hhsNoAge65Plus)
    {
        this.hhsNoAge65Plus = hhsNoAge65Plus;
    }

    public int getHhs1PersonAge65Plus()
    {
        return hhs1PersonAge65Plus;
    }

    public void setHhs1PersonAge65Plus(int hhs1PersonAge65Plus)
    {
        this.hhs1PersonAge65Plus = hhs1PersonAge65Plus;
    }

    public int getFam2PlusPersons1PlusAge65Plus()
    {
        return fam2PlusPersons1PlusAge65Plus;
    }

    public void setFam2PlusPersons1PlusAge65Plus(int fam2PlusPersons1PlusAge65Plus)
    {
        this.fam2PlusPersons1PlusAge65Plus = fam2PlusPersons1PlusAge65Plus;
    }

    public int getNonFam2PlusPersons1PlusAge65Plus()
    {
        return nonFam2PlusPersons1PlusAge65Plus;
    }

    public void setNonFam2PlusPersons1PlusAge65Plus(int nonFam2PlusPersons1PlusAge65Plus)
    {
        this.nonFam2PlusPersons1PlusAge65Plus = nonFam2PlusPersons1PlusAge65Plus;
    }

    public int getHhs1PersonAge65Under()
    {
        return hhs1PersonAge65Under;
    }

    public void setHhs1PersonAge65Under(int hhs1PersonAge65Under)
    {
        this.hhs1PersonAge65Under = hhs1PersonAge65Under;
    }

    public int getFam2PlusPersonsNoAge65Plus()
    {
        return fam2PlusPersonsNoAge65Plus;
    }

    public void setFam2PlusPersonsNoAge65Plus(int fam2PlusPersonsNoAge65Plus)
    {
        this.fam2PlusPersonsNoAge65Plus = fam2PlusPersonsNoAge65Plus;
    }

    public int getNonFam2PlusPersonsNoAge65Plus()
    {
        return nonFam2PlusPersonsNoAge65Plus;
    }

    public void setNonFam2PlusPersonsNoAge65Plus(int nonFam2PlusPersonsNoAge65Plus)
    {
        this.nonFam2PlusPersonsNoAge65Plus = nonFam2PlusPersonsNoAge65Plus;
    }

    public int getNumOfWorkers0()
    {
        return numOfWorkers0;
    }

    public void setNumOfWorkers0(int numOfWorkers0)
    {
        this.numOfWorkers0 = numOfWorkers0;
    }

    public int getNumOfWorkers1()
    {
        return numOfWorkers1;
    }

    public void setNumOfWorkers1(int numOfWorkers1)
    {
        this.numOfWorkers1 = numOfWorkers1;
    }

    public int getNumOfWorkers2()
    {
        return numOfWorkers2;
    }

    public void setNumOfWorkers2(int numOfWorkers2)
    {
        this.numOfWorkers2 = numOfWorkers2;
    }

    public int getNumOfWorkers3Plus()
    {
        return numOfWorkers3Plus;
    }

    public void setNumOfWorkers3Plus(int numOfWorkers3Plus)
    {
        this.numOfWorkers3Plus = numOfWorkers3Plus;
    }

    public int getIncomeUnder30k()
    {
        return incomeUnder30k;
    }

    public void setIncomeUnder30k(int incomeUnder30k)
    {
        this.incomeUnder30k = incomeUnder30k;
    }

    public int getIncome30kto60k()
    {
        return income30kto60k;
    }

    public void setIncome30kto60k(int income30kto60k)
    {
        this.income30kto60k = income30kto60k;
    }

    public int getIncome60kto100k()
    {
        return income60kto100k;
    }

    public void setIncome60kto100k(int income60kto100k)
    {
        this.income60kto100k = income60kto100k;
    }

    public int getIncome100kto150k()
    {
        return income100kto150k;
    }

    public void setIncome100kto150k(int income100kto150k)
    {
        this.income100kto150k = income100kto150k;
    }

    public int getIncome150kPlus()
    {
        return income150kPlus;
    }

    public void setIncome150kPlus(int income150kPlus)
    {
        this.income150kPlus = income150kPlus;
    }

    public int getIncomeUnder10k()
    {
        return incomeUnder10k;
    }

    public void setIncomeUnder10k(int incomeUnder10k)
    {
        this.incomeUnder10k = incomeUnder10k;
    }

    public int getIncome10kto20k()
    {
        return income10kto20k;
    }

    public void setIncome10kto20k(int income10kto20k)
    {
        this.income10kto20k = income10kto20k;
    }

    public int getIncome20kto30k()
    {
        return income20kto30k;
    }

    public void setIncome20kto30k(int income20kto30k)
    {
        this.income20kto30k = income20kto30k;
    }

    public int getIncome30kto40k()
    {
        return income30kto40k;
    }

    public void setIncome30kto40k(int income30kto40k)
    {
        this.income30kto40k = income30kto40k;
    }

    public int getIncome40kto50k()
    {
        return income40kto50k;
    }

    public void setIncome40kto50k(int income40kto50k)
    {
        this.income40kto50k = income40kto50k;
    }

    public int getIncome50kto60k()
    {
        return income50kto60k;
    }

    public void setIncome50kto60k(int income50kto60k)
    {
        this.income50kto60k = income50kto60k;
    }

    public int getIncome60kto75k()
    {
        return income60kto75k;
    }

    public void setIncome60kto75k(int income60kto75k)
    {
        this.income60kto75k = income60kto75k;
    }

    public int getIncome75kto100k()
    {
        return income75kto100k;
    }

    public void setIncome75kto100k(int income75kto100k)
    {
        this.income75kto100k = income75kto100k;
    }

    public int getIncome60kUnder()
    {
        return income60kUnder;
    }

    public void setIncome60kUnder(int income60kUnder)
    {
        this.income60kUnder = income60kUnder;
    }

    public int getSingleFamily()
    {
        return singleFamily;
    }

    public void setSingleFamily(int singleFamily)
    {
        this.singleFamily = singleFamily;
    }

    public int getMultiUnit()
    {
        return multiUnit;
    }

    public void setMultiUnit(int multiUnit)
    {
        this.multiUnit = multiUnit;
    }

    public int getMobileHome()
    {
        return mobileHome;
    }

    public void setMobileHome(int mobileHome)
    {
        this.mobileHome = mobileHome;
    }

    public int getNumOfOwned()
    {
        return numOfOwned;
    }

    public void setNumOfOwned(int numOfOwned)
    {
        this.numOfOwned = numOfOwned;
    }

    public int getNumOfRent()
    {
        return numOfRent;
    }

    public void setNumOfRent(int numOfRent)
    {
        this.numOfRent = numOfRent;
    }

    public int getHh1per0wkr()
    {
        return hh1per0wkr;
    }

    public void setHh1per0wkr(int hh1per0wkr)
    {
        this.hh1per0wkr = hh1per0wkr;
    }

    public int getHh1per1wkr()
    {
        return hh1per1wkr;
    }

    public void setHh1per1wkr(int hh1per1wkr)
    {
        this.hh1per1wkr = hh1per1wkr;
    }

    public int getHh2per0wkr()
    {
        return hh2per0wkr;
    }

    public void setHh2per0wkr(int hh2per0wkr)
    {
        this.hh2per0wkr = hh2per0wkr;
    }

    public int getHh2per1wkr()
    {
        return hh2per1wkr;
    }

    public void setHh2per1wkr(int hh2per1wkr)
    {
        this.hh2per1wkr = hh2per1wkr;
    }

    public int getHh2per2wkr()
    {
        return hh2per2wkr;
    }

    public void setHh2per2wkr(int hh2per2wkr)
    {
        this.hh2per2wkr = hh2per2wkr;
    }

    public int getHh3per0wkr()
    {
        return hh3per0wkr;
    }

    public void setHh3per0wkr(int hh3per0wkr)
    {
        this.hh3per0wkr = hh3per0wkr;
    }

    public int getHh3per1wkr()
    {
        return hh3per1wkr;
    }

    public void setHh3per1wkr(int hh3per1wkr)
    {
        this.hh3per1wkr = hh3per1wkr;
    }

    public int getHh3per2wkr()
    {
        return hh3per2wkr;
    }

    public void setHh3per2wkr(int hh3per2wkr)
    {
        this.hh3per2wkr = hh3per2wkr;
    }

    public int getHh3per3wkr()
    {
        return hh3per3wkr;
    }

    public void setHh3per3wkr(int hh3per3wkr)
    {
        this.hh3per3wkr = hh3per3wkr;
    }

    public int getHh4plusper0wkr()
    {
        return hh4plusper0wkr;
    }

    public void setHh4plusper0wkr(int hh4plusper0wkr)
    {
        this.hh4plusper0wkr = hh4plusper0wkr;
    }

    public int getHh4plusper1wkr()
    {
        return hh4plusper1wkr;
    }

    public void setHh4plusper1wkr(int hh4plusper1wkr)
    {
        this.hh4plusper1wkr = hh4plusper1wkr;
    }

    public int getHh4plusper2wkr()
    {
        return hh4plusper2wkr;
    }

    public void setHh4plusper2wkr(int hh4plusper2wkr)
    {
        this.hh4plusper2wkr = hh4plusper2wkr;
    }

    public int getHh4plusper3wkr()
    {
        return hh4plusper3wkr;
    }

    public void setHh4plusper3wkr(int hh4plusper3wkr)
    {
        this.hh4plusper3wkr = hh4plusper3wkr;
    }

    public int getHh1perHhinc0to30k()
    {
        return hh1perHhinc0to30k;
    }

    public void setHh1perHhinc0to30k(int hh1perHhinc0to30k)
    {
        this.hh1perHhinc0to30k = hh1perHhinc0to30k;
    }

    public int getHh1perHhinc30kto60k()
    {
        return hh1perHhinc30kto60k;
    }

    public void setHh1perHhinc30kto60k(int hh1perHhinc30kto60k)
    {
        this.hh1perHhinc30kto60k = hh1perHhinc30kto60k;
    }

    public int getHh1perHhinc60kto100k()
    {
        return hh1perHhinc60kto100k;
    }

    public void setHh1perHhinc60kto100k(int hh1perHhinc60kto100k)
    {
        this.hh1perHhinc60kto100k = hh1perHhinc60kto100k;
    }

    public int getHh1perHhinc100kplus()
    {
        return hh1perHhinc100kplus;
    }

    public void setHh1perHhinc100kplus(int hh1perHhinc100kplus)
    {
        this.hh1perHhinc100kplus = hh1perHhinc100kplus;
    }

    public int getHh2perHhinc0to30k()
    {
        return hh2perHhinc0to30k;
    }

    public void setHh2perHhinc0to30k(int hh2perHhinc0to30k)
    {
        this.hh2perHhinc0to30k = hh2perHhinc0to30k;
    }

    public int getHh2perHhinc30kto60k()
    {
        return hh2perHhinc30kto60k;
    }

    public void setHh2perHhinc30kto60k(int hh2perHhinc30kto60k)
    {
        this.hh2perHhinc30kto60k = hh2perHhinc30kto60k;
    }

    public int getHh2perHhinc60kto100k()
    {
        return hh2perHhinc60kto100k;
    }

    public void setHh2perHhinc60kto100k(int hh2perHhinc60kto100k)
    {
        this.hh2perHhinc60kto100k = hh2perHhinc60kto100k;
    }

    public int getHh2perHhinc100kplus()
    {
        return hh2perHhinc100kplus;
    }

    public void setHh2perHhinc100kplus(int hh2perHhinc100kplus)
    {
        this.hh2perHhinc100kplus = hh2perHhinc100kplus;
    }

    public int getHh3perHhinc0to30k()
    {
        return hh3perHhinc0to30k;
    }

    public void setHh3perHhinc0to30k(int hh3perHhinc0to30k)
    {
        this.hh3perHhinc0to30k = hh3perHhinc0to30k;
    }

    public int getHh3perHhinc30kto60k()
    {
        return hh3perHhinc30kto60k;
    }

    public void setHh3perHhinc30kto60k(int hh3perHhinc30kto60k)
    {
        this.hh3perHhinc30kto60k = hh3perHhinc30kto60k;
    }

    public int getHh3perHhinc60kto100k()
    {
        return hh3perHhinc60kto100k;
    }

    public void setHh3perHhinc60kto100k(int hh3perHhinc60kto100k)
    {
        this.hh3perHhinc60kto100k = hh3perHhinc60kto100k;
    }

    public int getHh3perHhinc100kplus()
    {
        return hh3perHhinc100kplus;
    }

    public void setHh3perHhinc100kplus(int hh3perHhinc100kplus)
    {
        this.hh3perHhinc100kplus = hh3perHhinc100kplus;
    }

    public int getHh4plusperHhinc0to30k()
    {
        return hh4plusperHhinc0to30k;
    }

    public void setHh4plusperHhinc0to30k(int hh4plusperHhinc0to30k)
    {
        this.hh4plusperHhinc0to30k = hh4plusperHhinc0to30k;
    }

    public int getHh4plusperHhinc30kto60k()
    {
        return hh4plusperHhinc30kto60k;
    }

    public void setHh4plusperHhinc30kto60k(int hh4plusperHhinc30kto60k)
    {
        this.hh4plusperHhinc30kto60k = hh4plusperHhinc30kto60k;
    }

    public int getHh4plusperHhinc60kto100k()
    {
        return hh4plusperHhinc60kto100k;
    }

    public void setHh4plusperHhinc60kto100k(int hh4plusperHhinc60kto100k)
    {
        this.hh4plusperHhinc60kto100k = hh4plusperHhinc60kto100k;
    }

    public int getHh4plusperHhinc100kplus()
    {
        return hh4plusperHhinc100kplus;
    }

    public void setHh4plusperHhinc100kplus(int hh4plusperHhinc100kplus)
    {
        this.hh4plusperHhinc100kplus = hh4plusperHhinc100kplus;
    }

    public int getHh0wkrHhinc0kto30k()
    {
        return hh0wkrHhinc0kto30k;
    }

    public void setHh0wkrHhinc0kto30k(int hh0wkrHhinc0kto30k)
    {
        this.hh0wkrHhinc0kto30k = hh0wkrHhinc0kto30k;
    }

    public int getHh0wkrHhinc30kto60k()
    {
        return hh0wkrHhinc30kto60k;
    }

    public void setHh0wkrHhinc30kto60k(int hh0wkrHhinc30kto60k)
    {
        this.hh0wkrHhinc30kto60k = hh0wkrHhinc30kto60k;
    }

    public int getHh0wkrHhinc60kto100k()
    {
        return hh0wkrHhinc60kto100k;
    }

    public void setHh0wkrHhinc60kto100k(int hh0wkrHhinc60kto100k)
    {
        this.hh0wkrHhinc60kto100k = hh0wkrHhinc60kto100k;
    }

    public int getHh0wkrHhinc100kPlus()
    {
        return hh0wkrHhinc100kPlus;
    }

    public void setHh0wkrHhinc100kPlus(int hh0wkrHhinc100kPlus)
    {
        this.hh0wkrHhinc100kPlus = hh0wkrHhinc100kPlus;
    }

    public int getHh1wkrHhinc0kto30k()
    {
        return hh1wkrHhinc0kto30k;
    }

    public void setHh1wkrHhinc0kto30k(int hh1wkrHhinc0kto30k)
    {
        this.hh1wkrHhinc0kto30k = hh1wkrHhinc0kto30k;
    }

    public int getHh1wkrHhinc30kto60k()
    {
        return hh1wkrHhinc30kto60k;
    }

    public void setHh1wkrHhinc30kto60k(int hh1wkrHhinc30kto60k)
    {
        this.hh1wkrHhinc30kto60k = hh1wkrHhinc30kto60k;
    }

    public int getHh1wkrHhinc60kto100k()
    {
        return hh1wkrHhinc60kto100k;
    }

    public void setHh1wkrHhinc60kto100k(int hh1wkrHhinc60kto100k)
    {
        this.hh1wkrHhinc60kto100k = hh1wkrHhinc60kto100k;
    }

    public int getHh1wkrHhinc100kPlus()
    {
        return hh1wkrHhinc100kPlus;
    }

    public void setHh1wkrHhinc100kPlus(int hh1wkrHhinc100kPlus)
    {
        this.hh1wkrHhinc100kPlus = hh1wkrHhinc100kPlus;
    }

    public int getHh2wkrHhinc0kto30k()
    {
        return hh2wkrHhinc0kto30k;
    }

    public void setHh2wkrHhinc0kto30k(int hh2wkrHhinc0kto30k)
    {
        this.hh2wkrHhinc0kto30k = hh2wkrHhinc0kto30k;
    }

    public int getHh2wkrHhinc30kto60k()
    {
        return hh2wkrHhinc30kto60k;
    }

    public void setHh2wkrHhinc30kto60k(int hh2wkrHhinc30kto60k)
    {
        this.hh2wkrHhinc30kto60k = hh2wkrHhinc30kto60k;
    }

    public int getHh2wkrHhinc60kto100k()
    {
        return hh2wkrHhinc60kto100k;
    }

    public void setHh2wkrHhinc60kto100k(int hh2wkrHhinc60kto100k)
    {
        this.hh2wkrHhinc60kto100k = hh2wkrHhinc60kto100k;
    }

    public int getHh2wkrHhinc100kPlus()
    {
        return hh2wkrHhinc100kPlus;
    }

    public void setHh2wkrHhinc100kPlus(int hh2wkrHhinc100kPlus)
    {
        this.hh2wkrHhinc100kPlus = hh2wkrHhinc100kPlus;
    }

    public int getHh3wkrHhinc0kto30k()
    {
        return hh3wkrHhinc0kto30k;
    }

    public void setHh3wkrHhinc0kto30k(int hh3wkrHhinc0kto30k)
    {
        this.hh3wkrHhinc0kto30k = hh3wkrHhinc0kto30k;
    }

    public int getHh3wkrHhinc30kto60k()
    {
        return hh3wkrHhinc30kto60k;
    }

    public void setHh3wkrHhinc30kto60k(int hh3wkrHhinc30kto60k)
    {
        this.hh3wkrHhinc30kto60k = hh3wkrHhinc30kto60k;
    }

    public int getHh3wkrHhinc60kto100k()
    {
        return hh3wkrHhinc60kto100k;
    }

    public void setHh3wkrHhinc60kto100k(int hh3wkrHhinc60kto100k)
    {
        this.hh3wkrHhinc60kto100k = hh3wkrHhinc60kto100k;
    }

    public int getHh3wkrHhinc100kPlus()
    {
        return hh3wkrHhinc100kPlus;
    }

    public void setHh3wkrHhinc100kPlus(int hh3wkrHhinc100kPlus)
    {
        this.hh3wkrHhinc100kPlus = hh3wkrHhinc100kPlus;
    }

    public int getNumPersonsHhUnivs()
    {
        return numPersonsHhUnivs;
    }

    public void setNumPersonsHhUnivs(int numPersonsHhUnivs)
    {
        this.numPersonsHhUnivs = numPersonsHhUnivs;
    }

    public int getNumPersonsFamilyHh()
    {
        return numPersonsFamilyHh;
    }

    public void setNumPersonsFamilyHh(int numPersonsFamilyHh)
    {
        this.numPersonsFamilyHh = numPersonsFamilyHh;
    }

    public int getNumPersonsNonFamilyHh()
    {
        return numPersonsNonFamilyHh;
    }

    public void setNumPersonsNonFamilyHh(int numPersonsNonFamilyHh)
    {
        this.numPersonsNonFamilyHh = numPersonsNonFamilyHh;
    }

    public int getNumPersonsIncomeBelowPoverty()
    {
        return numPersonsIncomeBelowPoverty;
    }

    public void setNumPersonsIncomeBelowPoverty(int numPersonsIncomeBelowPoverty)
    {
        this.numPersonsIncomeBelowPoverty = numPersonsIncomeBelowPoverty;
    }

    public int getNumPersonsFamilyHhUnivs()
    {
        return numPersonsFamilyHhUnivs;
    }

    public void setNumPersonsFamilyHhUnivs(int numPersonsFamilyHhUnivs)
    {
        this.numPersonsFamilyHhUnivs = numPersonsFamilyHhUnivs;
    }

    public int getNumOfPersonsAsHolders()
    {
        return numOfPersonsAsHolders;
    }

    public void setNumOfPersonsAsHolders(int numOfPersonsAsHolders)
    {
        this.numOfPersonsAsHolders = numOfPersonsAsHolders;
    }

    public int getNumOfPersonsAsSpouse()
    {
        return numOfPersonsAsSpouse;
    }

    public void setNumOfPersonsAsSpouse(int numOfPersonsAsSpouse)
    {
        this.numOfPersonsAsSpouse = numOfPersonsAsSpouse;
    }

    public int getNumOfPersonsAsChild()
    {
        return numOfPersonsAsChild;
    }

    public void setNumOfPersonsAsChild(int numOfPersonsAsChild)
    {
        this.numOfPersonsAsChild = numOfPersonsAsChild;
    }

    public int getNumOfPersonsAsOthers()
    {
        return numOfPersonsAsOthers;
    }

    public void setNumOfPersonsAsOthers(int numOfPersonsAsOthers)
    {
        this.numOfPersonsAsOthers = numOfPersonsAsOthers;
    }

    public int getNumOfPersonsNotRelatedToHolder()
    {
        return numOfPersonsNotRelatedToHolder;
    }

    public void setNumOfPersonsNotRelatedToHolder(int numOfPersonsNotRelatedToHolder)
    {
        this.numOfPersonsNotRelatedToHolder = numOfPersonsNotRelatedToHolder;
    }

    public int getNumOfPersonsGQIncludedUnivs()
    {
        return numOfPersonsGQIncludedUnivs;
    }

    public void setNumOfPersonsGQIncludedUnivs(int numOfPersonsGQIncludedUnivs)
    {
        this.numOfPersonsGQIncludedUnivs = numOfPersonsGQIncludedUnivs;
    }

    public int getMale()
    {
        return male;
    }

    public void setMale(int male)
    {
        this.male = male;
    }

    public int getFemale()
    {
        return female;
    }

    public void setFemale(int female)
    {
        this.female = female;
    }

    public int getAge0to5()
    {
        return age0to5;
    }

    public void setAge0to5(int age0to5)
    {
        this.age0to5 = age0to5;
    }

    public int getAge3PlusUnivs()
    {
        return age3PlusUnivs;
    }

    public void setAge3PlusUnivs(int age3PlusUnivs)
    {
        this.age3PlusUnivs = age3PlusUnivs;
    }

    public int getAge6to11()
    {
        return age6to11;
    }

    public void setAge6to11(int age6to11)
    {
        this.age6to11 = age6to11;
    }

    public int getAge12to15()
    {
        return age12to15;
    }

    public void setAge12to15(int age12to15)
    {
        this.age12to15 = age12to15;
    }

    public int getAge16PlusUnivs()
    {
        return age16PlusUnivs;
    }

    public void setAge16PlusUnivs(int age16PlusUnivs)
    {
        this.age16PlusUnivs = age16PlusUnivs;
    }

    public int getAge16to17()
    {
        return age16to17;
    }

    public void setAge16to17(int age16to17)
    {
        this.age16to17 = age16to17;
    }

    public int getAge18to24()
    {
        return age18to24;
    }

    public void setAge18to24(int age18to24)
    {
        this.age18to24 = age18to24;
    }

    public int getAge25to34()
    {
        return age25to34;
    }

    public void setAge25to34(int age25to34)
    {
        this.age25to34 = age25to34;
    }

    public int getAge35to49()
    {
        return age35to49;
    }

    public void setAge35to49(int age35to49)
    {
        this.age35to49 = age35to49;
    }

    public int getAge50to64()
    {
        return age50to64;
    }

    public void setAge50to64(int age50to64)
    {
        this.age50to64 = age50to64;
    }

    public int getAge65to79()
    {
        return age65to79;
    }

    public void setAge65to79(int age65to79)
    {
        this.age65to79 = age65to79;
    }

    public int getAge80Plus()
    {
        return age80Plus;
    }

    public void setAge80Plus(int age80Plus)
    {
        this.age80Plus = age80Plus;
    }

    public int getAge17AndUnder()
    {
        return age17AndUnder;
    }

    public void setAge17AndUnder(int age17AndUnder)
    {
        this.age17AndUnder = age17AndUnder;
    }

    public int getAge18to64()
    {
        return age18to64;
    }

    public void setAge18to64(int age18to64)
    {
        this.age18to64 = age18to64;
    }

    public int getAge65Plus()
    {
        return age65Plus;
    }

    public void setAge65Plus(int age65Plus)
    {
        this.age65Plus = age65Plus;
    }

    public int getMarriedSpousePres()
    {
        return marriedSpousePres;
    }

    public void setMarriedSpousePres(int marriedSpousePres)
    {
        this.marriedSpousePres = marriedSpousePres;
    }

    public int getHispanic()
    {
        return hispanic;
    }

    public void setHispanic(int hispanic)
    {
        this.hispanic = hispanic;
    }

    public int getWhiteAlone()
    {
        return whiteAlone;
    }

    public void setWhiteAlone(int whiteAlone)
    {
        this.whiteAlone = whiteAlone;
    }

    public int getBlackAlone()
    {
        return blackAlone;
    }

    public void setBlackAlone(int blackAlone)
    {
        this.blackAlone = blackAlone;
    }

    public int getIndiAlaskaAlone()
    {
        return indiAlaskaAlone;
    }

    public void setIndiAlaskaAlone(int indiAlaskaAlone)
    {
        this.indiAlaskaAlone = indiAlaskaAlone;
    }

    public int getAsianAlone()
    {
        return asianAlone;
    }

    public void setAsianAlone(int asianAlone)
    {
        this.asianAlone = asianAlone;
    }

    public int getIslanderAlone()
    {
        return islanderAlone;
    }

    public void setIslanderAlone(int islanderAlone)
    {
        this.islanderAlone = islanderAlone;
    }

    public int getOtherRaceAloneOrTwoPlusRaces()
    {
        return otherRaceAloneOrTwoPlusRaces;
    }

    public void setOtherRaceAloneOrTwoPlusRaces(int otherRaceAloneOrTwoPlusRaces)
    {
        this.otherRaceAloneOrTwoPlusRaces = otherRaceAloneOrTwoPlusRaces;
    }

    public int getWorked27plusWksUnivs()
    {
        return worked27plusWksUnivs;
    }

    public void setWorked27plusWksUnivs(int worked27plusWksUnivs)
    {
        this.worked27plusWksUnivs = worked27plusWksUnivs;
    }

    public int getWorked27plusWks35PlusHrsPerWk()
    {
        return worked27plusWks35PlusHrsPerWk;
    }

    public void setWorked27plusWks35PlusHrsPerWk(int worked27plusWks35PlusHrsPerWk)
    {
        this.worked27plusWks35PlusHrsPerWk = worked27plusWks35PlusHrsPerWk;
    }

    public int getWorked27plusWks15to34HrsPerWk()
    {
        return worked27plusWks15to34HrsPerWk;
    }

    public void setWorked27plusWks15to34HrsPerWk(int worked27plusWks15to34HrsPerWk)
    {
        this.worked27plusWks15to34HrsPerWk = worked27plusWks15to34HrsPerWk;
    }

    public int getWorked27plusWks1to14HrsPerWk()
    {
        return worked27plusWks1to14HrsPerWk;
    }

    public void setWorked27plusWks1to14HrsPerWk(int worked27plusWks1to14HrsPerWk)
    {
        this.worked27plusWks1to14HrsPerWk = worked27plusWks1to14HrsPerWk;
    }

    public int getCivilianEmployed()
    {
        return civilianEmployed;
    }

    public void setCivilianEmployed(int civilianEmployed)
    {
        this.civilianEmployed = civilianEmployed;
    }

    public int getMilitaryEmployed()
    {
        return militaryEmployed;
    }

    public void setMilitaryEmployed(int militaryEmployed)
    {
        this.militaryEmployed = militaryEmployed;
    }

    public int getUnemployed()
    {
        return unemployed;
    }

    public void setUnemployed(int unemployed)
    {
        this.unemployed = unemployed;
    }

    public int getNotInLaborForce()
    {
        return notInLaborForce;
    }

    public void setNotInLaborForce(int notInLaborForce)
    {
        this.notInLaborForce = notInLaborForce;
    }
  

    public int getOccpMng()
    {
        return occpMng;
    }

    public void setOccpMng(int occpMng)
    {
        this.occpMng = occpMng;
    }

    public int getOccpService()
    {
        return occpService;
    }

    public void setOccpService(int occpService)
    {
        this.occpService = occpService;
    }

    public int getOccpSales()
    {
        return occpSales;
    }

    public void setOccpSales(int occpSales)
    {
        this.occpSales = occpSales;
    }

    public int getOccpConstruct()
    {
        return occpConstruct;
    }

    public void setOccpConstruct(int occpConstruct)
    {
        this.occpConstruct = occpConstruct;
    }

    public int getOccpTransport()
    {
        return occpTransport;
    }

    public void setOccpTransport(int occpTransport)
    {
        this.occpTransport = occpTransport;
    }

    public int getEnrolledNurseyToGrade12()
    {
        return enrolledNurseyToGrade12;
    }

    public void setEnrolledNurseyToGrade12(int enrolledNurseyToGrade12)
    {
        this.enrolledNurseyToGrade12 = enrolledNurseyToGrade12;
    }

    public int getEnrollPostSecondary()
    {
        return enrollPostSecondary;
    }

    public void setEnrollPostSecondary(int enrollPostSecondary)
    {
        this.enrollPostSecondary = enrollPostSecondary;
    }

    public double getValMetric(int index)
    {
        return statsMap.get(index);
    }

    public double getValMetricPct(int index)
    {
        double result = 0;
        double absoluteVal = statsMap.get(index);
        int uIndex = getUniverse(index);
        double universeVal = statsMap.get(uIndex);
        if (index != uIndex)
        {
            if (universeVal > 0) result = absoluteVal / universeVal;
        } else
        {
            result = absoluteVal;
        }
        return result;
    }

    public int getUniverse(int index)
    {
        int result = -1;
        for (int i = 0; i < universeIndex.length; i++)
        {
            if (index >= universeIndex[i] && index < universeIndex[i + 1])
            {
                result = universeIndex[i];
                break;
            }
        }
        return result;
    }

}
