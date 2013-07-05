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
 * This interface defines the validation attributes. It is primarily a metadata of the validation data table.
 * 
 */
public interface IPumaValMetrics
{
    /**
     * Creates the hashmap of validation statistics with validation attributes as the key for each PUMA. The size of the hashmap is data source
     * dependent.
     */
    void createMap();;

    /**
     * Returns the hashmap of validation statistics with validation attributes as the key.
     * 
     * @return the hashmap of validation statistics with validation attributes as the key.
     */
    HashMap<Integer, Integer> getStatsMap();;

    /**
     * Sets the hashmap of validation statistics
     * 
     * @param statsMap
     *            the hashmap of validation statistics with validation attributes as the key.
     */
    void setStatsMap(HashMap<Integer, Integer> statsMap);;

    /**
     * Returns the unique identifier of the PUMA
     * 
     * @return the unique identifier String of the PUMA
     */
    String getPuma();;

    /**
     * Sets the unique identifier of the PUMA
     * 
     * @param puma
     *            the unique identifier String of the PUMA
     */
    void setPuma(String puma);;

    /**
     * Returns the number of the household universe.
     * 
     * @return the number of the household universe
     */
    int getHhsUnivs();;

    /**
     * Sets the number of the household universe.
     * 
     * @param hhsUnivs
     *            the number of the household universe
     */
    void setHhsUnivs(int hhsUnivs);;

    /**
     * Returns the number of the family households.
     * 
     * @return the number of the family households
     */
    int getFamHhs();;

    /**
     * Sets the number of the family households.
     * 
     * @param famHhs
     *            the number of the family households
     */
    void setFamHhs(int famHhs);;

    /**
     * Returns the number of the non-family households.
     * 
     * @return the number of the non-family households
     */
    int getNonFamHhs();;

    /**
     * Sets the number of the non-family households.
     * 
     * @param nonFamHhs
     *            the number of the non-family households
     */
    void setNonFamHhs(int nonFamHhs);;

    /**
     * Returns the number of the non-family households with householder age 15 to 64.
     * 
     * @return the number of the non-family households with householder age 15 to 64
     */
    int getNonFamHhsbyHolderAge15to64();;

    /**
     * Sets the number of the non-family households with householder age 15 to 64.
     * 
     * @param nonFamHhsbyHolderAge15to64
     *            the number of the non-family households with householder age 15 to 64
     */
    void setNonFamHhsbyHolderAge15to64(int nonFamHhsbyHolderAge15to64);;

    /**
     * Returns the number of the non-family households with householder age 65 and over.
     * 
     * @return the number of the non-family households with householder age 65 and over
     */
    int getNonFamHhsbyHolderAge65Plus();;

    /**
     * Sets the number of the non-family households with householder age 65 and over.
     * 
     * @param nonFamHhsbyHolderAge65Plus
     *            the number of the non-family households with householder age 65 and over
     */
    void setNonFamHhsbyHolderAge65Plus(int nonFamHhsbyHolderAge65Plus);;

    /**
     * Returns the number of households with 1 person.
     * 
     * @return the number of households with 1 person.
     */
    int getHh1Person();

    /**
     * Sets the number of households with 1 person.
     * 
     * @param hh1Person
     *            the number of households with 1 person.
     */
    void setHh1Person(int hh1Person);

    /**
     * Returns the number of households with 2 persons.
     * 
     * @return the number of households with 2 persons.
     */
    int getHh2Persons();

    /**
     * Sets the number of households with 2 persons.
     * 
     * @param hh2Persons
     *            the number of households with 2 persons
     */
    void setHh2Persons(int hh2Persons);

    /**
     * Returns the number of households with 3 persons.
     * 
     * @return the number of households with 3 persons
     */
    int getHh3Persons();

    /**
     * Sets the number of households with 3 persons.
     * 
     * @param hh3Persons
     *            the number of households with 3 persons
     */
    void setHh3Persons(int hh3Persons);

    /**
     * Returns the number of households with 4 and plus persons.
     * 
     * @return the number of households with 4 and plus persons
     */
    int getHh4PlusPersons();

    /**
     * Sets the number of households with 4 and plus persons.
     * 
     * @param hh4PlusPersons
     *            the number of households with 4 and plus persons
     */
    void setHh4PlusPersons(int hh4PlusPersons);

    /**
     * Returns the number of family households with own child age under 18.
     * 
     * @return the number of family households with own child age under 18
     */
    int getFamHhsOwnChildUnder18();

    /**
     * Sets the number of family households with own child age under 18.
     * 
     * @param famHhsOwnChildUnder18
     *            the number of family households with own child age under 18
     */
    void setFamHhsOwnChildUnder18(int famHhsOwnChildUnder18);

    /**
     * Returns the number of family households without own child age under 18.
     * 
     * @return the number of family households without own child age under 18
     */
    int getFamHhsNoOwnChildUnder18();

    /**
     * Sets the number of family households without own child age under 18.
     * 
     * @param famHhsNoOwnChildUnder18
     *            the number of family households without own child age under 18
     */
    void setFamHhsNoOwnChildUnder18(int famHhsNoOwnChildUnder18);

    /**
     * Returns the number of family households with people age under 18 present.
     * 
     * @return the number of family households with people age under 18 present
     */
    int getFamHhsAge18UnderPres();

    /**
     * Sets the number of family households with people age under 18 present.
     * 
     * @param famHhsAge18UnderPres
     *            the number of family households with people age under 18 present.
     */
    void setFamHhsAge18UnderPres(int famHhsAge18UnderPres);

    /**
     * Returns the number of non-family households with people age under 18 present.
     * 
     * @return the number of non-family households with people age under 18 present
     */
    int getNonFamHhsAge18UnderPres();

    /**
     * Sets the number of non-family households with people age under 18 present.
     * 
     * @param nonFamHhsAge18UnderPres
     *            the number of non-family households with people age under 18 present
     */
    void setNonFamHhsAge18UnderPres(int nonFamHhsAge18UnderPres);

    /**
     * Returns the number of family households without people age under 18 present.
     * 
     * @return the number of family households without people age under 18 present
     */
    int getFamHhsNoAge18UnderPres();

    /**
     * Sets the number of family households without people age under 18 present.
     * 
     * @param famHhsNoAge18UnderPres
     *            the number of family households without people age under 18 present
     */
    void setFamHhsNoAge18UnderPres(int famHhsNoAge18UnderPres);

    /**
     * Returns the number of non-family households without people age under 18 present.
     * 
     * @return the number of non-family households without people age under 18 present
     */
    int getNonFamHhsNoAge18UnderPres();

    /**
     * Sets the number of non-family households without people age under 18 present.
     * 
     * @param nonFamHhsNoAge18UnderPres
     *            the number of non-family households without people age under 18 present
     */
    void setNonFamHhsNoAge18UnderPres(int nonFamHhsNoAge18UnderPres);

    /**
     * Returns the number of households with one or more people age 65 and over.
     * 
     * @return the number of households with one or more people age 65 and over
     */
    int getHhs1PlusAge65Plus();

    /**
     * Sets the number of households with one or more people age 65 and over.
     * 
     * @param hhs1PlusAge65Plus
     *            the number of households with one or more people age 65 and over
     */
    void setHhs1PlusAge65Plus(int hhs1PlusAge65Plus);

    /**
     * Returns the number of households with no people age 65 and over.
     * 
     * @return the number of households with no people age 65 and over
     */
    int getHhsNoAge65Plus();

    /**
     * Sets the number of households with no people age 65 and over.
     * 
     * @param hhsNoAge65Plus
     *            the number of households with no people age 65 and over
     */
    void setHhsNoAge65Plus(int hhsNoAge65Plus);

    /**
     * Returns the number of 1-person households with one person age 65 and over.
     * 
     * @return the number of 1-person households with one person age 65 and over
     */
    int getHhs1PersonAge65Plus();

    /**
     * Sets the number of 1-person households with one person age 65 and over.
     * 
     * @param hhs1PersonAge65Plus
     *            the number of 1-person households with one person age 65 and over
     */
    void setHhs1PersonAge65Plus(int hhs1PersonAge65Plus);

    /**
     * Returns the number of 2-or-more person family with one or more people age 65 and over.
     * 
     * @return the number of 2-or-more person family with one or more people age 65 and over
     */
    int getFam2PlusPersons1PlusAge65Plus();

    /**
     * Sets the number of 2-or-more person family with one or more people age 65 and over.
     * 
     * @param fam2PlusPersons1PlusAge65Plus
     *            the number of 2-or-more person family with one or more people age 65 and over
     */
    void setFam2PlusPersons1PlusAge65Plus(int fam2PlusPersons1PlusAge65Plus);

    /**
     * Returns the number of 2-or-more person non-family households with one or more people age 65 and over.
     * 
     * @return the number of 2-or-more person non-family households with one or more people age 65 and over
     */
    int getNonFam2PlusPersons1PlusAge65Plus();

    /**
     * Sets the number of 2-or-more person non-family households with one or more people age 65 and over.
     * 
     * @param nonFam2PlusPersons1PlusAge65Plus
     *            the number of 2-or-more person non-family households with one or more people age 65 and over
     */
    void setNonFam2PlusPersons1PlusAge65Plus(int nonFam2PlusPersons1PlusAge65Plus);

    /**
     * Returns the number of 1-person households with no people age 65 and over.
     * 
     * @return the number of 1-person households with no people age 65 and over
     */
    int getHhs1PersonAge65Under();

    /**
     * Sets the number of 1-person households with no people age 65 and over.
     * 
     * @param hhs1PersonAge65Under
     *            the number of 1-person households with no people age 65 and over
     */
    void setHhs1PersonAge65Under(int hhs1PersonAge65Under);

    /**
     * Returns the number of 2-or-more person family households with no people age 65 and over.
     * 
     * @return the number of 2-or-more person family households with no people age 65 and over
     */
    int getFam2PlusPersonsNoAge65Plus();

    /**
     * Sets the number of 2-or-more person family households with no people age 65 and over.
     * 
     * @param fam2PlusPersonsNoAge65Plus
     *            the number of 2-or-more person family households with no people age 65 and over
     */
    void setFam2PlusPersonsNoAge65Plus(int fam2PlusPersonsNoAge65Plus);

    /**
     * Returns the number of 2-or-more person non-family households with no people age 65 and over.
     * 
     * @return the number of 2-or-more person non-family households with no people age 65 and over
     */
    int getNonFam2PlusPersonsNoAge65Plus();

    /**
     * Sets the number of 2-or-more person non-family households with no people age 65 and over.
     * 
     * @param nonFam2PlusPersonsNoAge65Plus
     *            the number of 2-or-more person non-family households with no people age 65 and over
     */
    void setNonFam2PlusPersonsNoAge65Plus(int nonFam2PlusPersonsNoAge65Plus);

    /**
     * Returns the number of households with no workers.
     * 
     * @return the number of households with no workers
     */
    int getNumOfWorkers0();

    /**
     * Sets the number of households with no workers.
     * 
     * @param numOfWorkers0
     *            the number of households with no workers
     */
    void setNumOfWorkers0(int numOfWorkers0);

    /**
     * Returns the number of households with one worker.
     * 
     * @return the number of households with one worker
     */
    int getNumOfWorkers1();

    /**
     * Sets the number of households with one worker.
     * 
     * @param numOfWorkers1
     *            the number of households with one worker.
     */
    void setNumOfWorkers1(int numOfWorkers1);

    /**
     * Returns the number of households with two workers.
     * 
     * @return the number of households with two workers
     */
    int getNumOfWorkers2();

    /**
     * Sets the number of households with two workers.
     * 
     * @param numOfWorkers2
     *            the number of households with two workers
     */
    void setNumOfWorkers2(int numOfWorkers2);

    /**
     * Returns the number of households with three and plus workers
     * 
     * @return the number of households with three and plus workers
     */
    int getNumOfWorkers3Plus();

    /**
     * Sets the number of households with three and plus workers
     * 
     * @param numOfWorkers3Plus
     *            the number of households with three and plus workers
     */
    void setNumOfWorkers3Plus(int numOfWorkers3Plus);

    /**
     * Returns the number of households with household income under $30,000.
     * 
     * @return the number of households with household income under $30,000
     */
    int getIncomeUnder30k();

    /**
     * Sets the number of households with household income under $30,000.
     * 
     * @param incomeUnder30k
     *            the number of households with household income under $30,000
     */
    void setIncomeUnder30k(int incomeUnder30k);

    /**
     * Returns the number of households with household income equal to or greater than $30,000 and less than $60,000.
     * 
     * @return the number of households with household income equal to or greater than $30,000 and less than $60,000
     */
    int getIncome30kto60k();

    /**
     * Sets the number of households with household income equal to or greater than $30,000 and less than $60,000.
     * 
     * @param income30kto60k
     *            the number of households with household income equal to or greater than $30,000 and less than $60,000
     */
    void setIncome30kto60k(int income30kto60k);

    /**
     * Returns the number of households with household income equal to or greater than $60,000 and less than $100,000.
     * 
     * @return the number of households with household income equal to or greater than $60,000 and less than $100,000
     */
    int getIncome60kto100k();

    /**
     * Sets the number of households with household income equal to or greater than $60,000 and less than $100,000.
     * 
     * @param income60kto100k
     *            the number of households with household income equal to or greater than $60,000 and less than $100,000
     */
    void setIncome60kto100k(int income60kto100k);

    /**
     * Returns the number of households with household income equal to or greater than $100,000 and less than $150,000.
     * 
     * @return the number of households with household income equal to or greater than $100,000 and less than $150,000
     */
    int getIncome100kto150k();

    /**
     * Sets the number of households with household income equal to or greater than $100,000 and less than $150,000.
     * 
     * @param income100kto150k
     *            the number of households with household income equal to or greater than $100,000 and less than $150,000
     */
    void setIncome100kto150k(int income100kto150k);

    /**
     * Returns the number of households with household income equal to or greater than $150,000.
     * 
     * @return the number of households with household income equal to or greater than $150,000
     */
    int getIncome150kPlus();

    /**
     * Sets the number of households with household income equal to or greater than $150,000.
     * 
     * @param income150kPlus
     *            the number of households with household income equal to or greater than $150,000
     */
    void setIncome150kPlus(int income150kPlus);

    /**
     * Returns the number of households with household income less than $10,000.
     * 
     * @return the number of households with household income less than $10,000
     */
    int getIncomeUnder10k();

    /**
     * Sets the number of households with household income under $10,000.
     * 
     * @param incomeUnder10k
     *            the number of households with household income under $10,000
     */
    void setIncomeUnder10k(int incomeUnder10k);

    /**
     * Returns the number of households with household income equal to or greater than $10,000 and less than $20,000.
     * 
     * @return the number of households with household income equal to or greater than $10,000 and less than $20,000
     */
    int getIncome10kto20k();

    /**
     * Sets the number of households with household income equal to or greater than $10,000 and less than $20,000.
     * 
     * @param income10kto20k
     *            the number of households with household income equal to or greater than $10,000 and less than $20,000
     */
    void setIncome10kto20k(int income10kto20k);

    /**
     * Returns the number of households with household income equal to or greater than $20,000 and less than $30,000.
     * 
     * @return the number of households with household income equal to or greater than $20,000 and less than $30,000
     */
    int getIncome20kto30k();

    /**
     * Sets the number of households with household income equal to or greater than $20,000 and less than $30,000.
     * 
     * @param income20kto30k
     *            the number of households with household income equal to or greater than $20,000 and less than $30,000
     */
    void setIncome20kto30k(int income20kto30k);

    /**
     * Returns the number of households with household income equal to or greater than $30,000 and less than $40,000.
     * 
     * @return the number of households with household income equal to or greater than $30,000 and less than $40,000
     */
    int getIncome30kto40k();

    /**
     * Sets the number of households with household income equal to or greater than $30,000 and less than $40,000.
     * 
     * @param income30kto40k
     *            the number of households with household income equal to or greater than $30,000 and less than $40,000
     */
    void setIncome30kto40k(int income30kto40k);

    /**
     * Returns the number of households with household income equal to or greater than $40,000 and less than $50,000.
     * 
     * @return the number of households with household income equal to or greater than $40,000 and less than $50,000
     */
    int getIncome40kto50k();

    /**
     * Sets the number of households with household income equal to or greater than $40,000 and less than $50,000.
     * 
     * @param income40kto50k
     *            the number of households with household income equal to or greater than $40,000 and less than $50,000
     */
    void setIncome40kto50k(int income40kto50k);

    /**
     * Returns the number of households with household income equal to or greater than $50,000 and less than $60,000.
     * 
     * @return the number of households with household income equal to or greater than $50,000 and less than $60,000
     */
    int getIncome50kto60k();

    /**
     * Sets the number of households with household income equal to or greater than $50,000 and less than $60,000.
     * 
     * @param income50kto60k
     *            the number of households with household income equal to or greater than $50,000 and less than $60,000
     */
    void setIncome50kto60k(int income50kto60k);

    /**
     * Returns the number of households with household income equal to or greater than $60,000 and less than $75,000.
     * 
     * @return the number of households with household income equal to or greater than $60,000 and less than $75,000
     */
    int getIncome60kto75k();

    /**
     * Sets the number of households with household income equal to or greater than $60,000 and less than $75,000.
     * 
     * @param income60kto75k
     *            the number of households with household income equal to or greater than $60,000 and less than $75,000.
     */
    void setIncome60kto75k(int income60kto75k);

    /**
     * Returns the number of households with household income equal to or greater than $75,000 and less than $100,000.
     * 
     * @return the number of households with household income equal to or greater than $75,000 and less than $100,000
     */
    int getIncome75kto100k();

    /**
     * Sets the number of households with household income equal to or greater than $75,000 and less than $100,000.
     * 
     * @param income75kto100k
     *            the number of households with household income equal to or greater than $75,000 and less than $100,000.
     */
    void setIncome75kto100k(int income75kto100k);

    /**
     * Returns the number of households with household income under $60,000.
     * 
     * @return the number of households with household income under $60,000
     */
    int getIncome60kUnder();

    /**
     * Sets the number of households with household income under $60,000.
     * 
     * @param income60kUnder
     *            the number of households with household income under $60,000
     */
    void setIncome60kUnder(int income60kUnder);

    /**
     * Returns the number of single family households.
     * 
     * @return the number of single family households
     */
    int getSingleFamily();

    /**
     * Sets the number of single family households.
     * 
     * @param singleFamily
     *            the number of single family households
     */
    void setSingleFamily(int singleFamily);

    /**
     * Returns the number of multi-family households.
     * 
     * @return the number of multi-family households
     */
    int getMultiUnit();

    /**
     * Sets the number of multi-family households.
     * 
     * @param multiUnit
     *            the number of multi-family households
     */
    void setMultiUnit(int multiUnit);

    /**
     * Returns the number of mobile home households.
     * 
     * @return the number of mobile home households
     */
    int getMobileHome();

    /**
     * Sets the number of mobile home households
     * 
     * @param mobileHome
     *            the number of mobile home households
     */
    void setMobileHome(int mobileHome);

    /**
     * Returns the number of household who own the house.
     * 
     * @return the number of household who own the house
     */
    int getNumOfOwned();

    /**
     * Sets the number of household who own the house
     * 
     * @param numOfOwned
     *            the number of household who own the house
     */
    void setNumOfOwned(int numOfOwned);

    /**
     * Returns the number of household who rent.
     * 
     * @return the number of household who rent
     */
    int getNumOfRent();

    /**
     * Sets the number of household who rent.
     * 
     * @param numOfRent
     *            the number of household who rent
     */
    void setNumOfRent(int numOfRent);

    int getHh1per0wkr();

    void setHh1per0wkr(int hh1per0wkr);

    int getHh1per1wkr();

    void setHh1per1wkr(int hh1per1wkr);

    int getHh2per0wkr();

    void setHh2per0wkr(int hh2per0wkr);

    int getHh2per1wkr();

    void setHh2per1wkr(int hh2per1wkr);

    int getHh2per2wkr();

    void setHh2per2wkr(int hh2per2wkr);

    int getHh3per0wkr();

    void setHh3per0wkr(int hh3per0wkr);

    int getHh3per1wkr();

    void setHh3per1wkr(int hh3per1wkr);

    int getHh3per2wkr();

    void setHh3per2wkr(int hh3per2wkr);

    int getHh3per3wkr();

    void setHh3per3wkr(int hh3per3wkr);

    int getHh4plusper0wkr();

    void setHh4plusper0wkr(int hh4plusper0wkr);

    int getHh4plusper1wkr();

    void setHh4plusper1wkr(int hh4plusper1wkr);

    int getHh4plusper2wkr();

    void setHh4plusper2wkr(int hh4plusper2wkr);

    int getHh4plusper3wkr();

    void setHh4plusper3wkr(int hh4plusper3wkr);

    /*
     * 
     * int getHh1perHhinc0to30k();
     * 
     * void setHh1perHhinc0to30k(int hh1perHhinc0to30k);
     * 
     * 
     * int getHh1perHhinc30kto60k();
     * 
     * 
     * void setHh1perHhinc30kto60k(int hh1perHhinc30kto60k);
     * 
     * 
     * int getHh1perHhinc60kto100k();
     * 
     * 
     * void setHh1perHhinc60kto100k(int hh1perHhinc60kto100k);
     * 
     * 
     * int getHh1perHhinc100kplus();
     * 
     * 
     * void setHh1perHhinc100kplus(int hh1perHhinc100kplus);
     * 
     * 
     * int getHh2perHhinc0to30k();
     * 
     * 
     * void setHh2perHhinc0to30k(int hh2perHhinc0to30k);
     * 
     * 
     * int getHh2perHhinc30kto60k();
     * 
     * 
     * void setHh2perHhinc30kto60k(int hh2perHhinc30kto60k);
     * 
     * 
     * int getHh2perHhinc60kto100k();
     * 
     * 
     * void setHh2perHhinc60kto100k(int hh2perHhinc60kto100k);
     * 
     * 
     * int getHh2perHhinc100kplus();
     * 
     * 
     * void setHh2perHhinc100kplus(int hh2perHhinc100kplus);
     * 
     * 
     * int getHh3perHhinc0to30k();
     * 
     * 
     * void setHh3perHhinc0to30k(int hh3perHhinc0to30k);
     * 
     * 
     * int getHh3perHhinc30kto60k();
     * 
     * 
     * void setHh3perHhinc30kto60k(int hh3perHhinc30kto60k);
     * 
     * 
     * int getHh3perHhinc60kto100k();
     * 
     * 
     * void setHh3perHhinc60kto100k(int hh3perHhinc60kto100k);
     * 
     * 
     * int getHh3perHhinc100kplus();
     * 
     * 
     * void setHh3perHhinc100kplus(int hh3perHhinc100kplus);
     * 
     * 
     * int getHh4plusperHhinc0to30k();
     * 
     * 
     * void setHh4plusperHhinc0to30k(int hh4plusperHhinc0to30k);
     * 
     * 
     * int getHh4plusperHhinc30kto60k();
     * 
     * void setHh4plusperHhinc30kto60k(int hh4plusperHhinc30kto60k);
     * 
     * int getHh4plusperHhinc60kto100k();
     * 
     * 
     * void setHh4plusperHhinc60kto100k(int hh4plusperHhinc60kto100k);
     * 
     * 
     * int getHh4plusperHhinc100kplus();
     * 
     * void setHh4plusperHhinc100kplus(int hh4plusperHhinc100kplus);
     * 
     * 
     * int getHh0wkrHhinc0kto30k();
     * 
     * 
     * void setHh0wkrHhinc0kto30k(int hh0wkrHhinc0kto30k);
     * 
     * 
     * int getHh0wkrHhinc30kto60k();
     * 
     * 
     * void setHh0wkrHhinc30kto60k(int hh0wkrHhinc30kto60k);
     * 
     * 
     * int getHh0wkrHhinc60kto100k();
     * 
     * 
     * void setHh0wkrHhinc60kto100k(int hh0wkrHhinc60kto100k);
     * 
     * 
     * int getHh0wkrHhinc100kPlus();
     * 
     * 
     * void setHh0wkrHhinc100kPlus(int hh0wkrHhinc100kPlus);
     * 
     * 
     * int getHh1wkrHhinc0kto30k();
     * 
     * 
     * void setHh1wkrHhinc0kto30k(int hh1wkrHhinc0kto30k);
     * 
     * 
     * int getHh1wkrHhinc30kto60k();
     * 
     * 
     * void setHh1wkrHhinc30kto60k(int hh1wkrHhinc30kto60k);
     * 
     * 
     * int getHh1wkrHhinc60kto100k();
     * 
     * 
     * void setHh1wkrHhinc60kto100k(int hh1wkrHhinc60kto100k);
     * 
     * 
     * int getHh1wkrHhinc100kPlus();
     * 
     * 
     * void setHh1wkrHhinc100kPlus(int hh1wkrHhinc100kPlus);
     * 
     * 
     * int getHh2wkrHhinc0kto30k();
     * 
     * 
     * void setHh2wkrHhinc0kto30k(int hh2wkrHhinc0kto30k);
     * 
     * 
     * int getHh2wkrHhinc30kto60k();
     * 
     * 
     * void setHh2wkrHhinc30kto60k(int hh2wkrHhinc30kto60k);
     * 
     * 
     * int getHh2wkrHhinc60kto100k();
     * 
     * 
     * void setHh2wkrHhinc60kto100k(int hh2wkrHhinc60kto100k);
     * 
     * 
     * int getHh2wkrHhinc100kPlus();
     * 
     * 
     * void setHh2wkrHhinc100kPlus(int hh2wkrHhinc100kPlus);
     * 
     * int getHh3wkrHhinc0kto30k();
     * 
     * 
     * void setHh3wkrHhinc0kto30k(int hh3wkrHhinc0kto30k);
     * 
     * 
     * int getHh3wkrHhinc30kto60k();
     * 
     * 
     * void setHh3wkrHhinc30kto60k(int hh3wkrHhinc30kto60k);
     * 
     * 
     * int getHh3wkrHhinc60kto100k();
     * 
     * 
     * void setHh3wkrHhinc60kto100k(int hh3wkrHhinc60kto100k);
     * 
     * 
     * int getHh3wkrHhinc100kPlus();
     * 
     * 
     * void setHh3wkrHhinc100kPlus(int hh3wkrHhinc100kPlus);
     */

    int getOccpMng();

    void setOccpMng(int occpMng);

    int getOccpService();

    void setOccpService(int occpService);

    int getOccpSales();

    void setOccpSales(int occpSales);

    int getOccpConstruct();

    void setOccpConstruct(int occpConstruct);

    int getOccpTransport();

    void setOccpTransport(int occpTransport);

    /**
     * Returns the number of people in households.
     * 
     * @return the number of people in households
     */
    int getNumPersonsHhUnivs();

    /**
     * Sets the number of people in households.
     * 
     * @param numPersonsHhUnivs
     *            the number of people in households
     */
    void setNumPersonsHhUnivs(int numPersonsHhUnivs);

    /**
     * Returns the number of people in family households.
     * 
     * @return the number of people in family households
     */
    int getNumPersonsFamilyHh();

    /**
     * Sets the number of people in family households.
     * 
     * @param numPersonsFamilyHh
     *            the number of people in family households
     */
    void setNumPersonsFamilyHh(int numPersonsFamilyHh);

    /**
     * Returns the number of people in non-family households.
     * 
     * @return the number of people in non-family households
     */
    int getNumPersonsNonFamilyHh();

    /**
     * Sets the number of people in non-family households.
     * 
     * @param numPersonsNonFamilyHh
     *            the number of people in non-family households
     */
    void setNumPersonsNonFamilyHh(int numPersonsNonFamilyHh);

    int getNumPersonsIncomeBelowPoverty();

    void setNumPersonsIncomeBelowPoverty(int numPersonsIncomeBelowPoverty);

    /**
     * Returns the number of people in family households universe.
     * 
     * @return the number of people in family households universe
     */
    int getNumPersonsFamilyHhUnivs();

    /**
     * Sets the number of people in family households universe.
     * 
     * @param numPersonsFamilyHhUnivs
     *            the number of people in family households universe
     */
    void setNumPersonsFamilyHhUnivs(int numPersonsFamilyHhUnivs);

    /**
     * Returns the number of people as householders.
     * 
     * @return the number of people as householders
     */
    int getNumOfPersonsAsHolders();

    /**
     * Sets the number of people as householders.
     * 
     * @param numOfPersonsAsHolders
     *            the number of people as householders
     */
    void setNumOfPersonsAsHolders(int numOfPersonsAsHolders);

    /**
     * Returns the number of people as spouses.
     * 
     * @return the number of people as spouses
     */
    int getNumOfPersonsAsSpouse();

    /**
     * Returns the number of people as spouses.
     * 
     * @param numOfPersonsAsSpouse
     *            the number of people as spouses
     */
    void setNumOfPersonsAsSpouse(int numOfPersonsAsSpouse);

    /**
     * Returns the number of people as children.
     * 
     * @return the number of people as children
     */
    int getNumOfPersonsAsChild();

    /**
     * Sets the number of people as children.
     * 
     * @param numOfPersonsAsChild
     *            the number of people as children
     */
    void setNumOfPersonsAsChild(int numOfPersonsAsChild);

    /**
     * Returns the number of people as other type of relatives.
     * 
     * @return the number of people as other type of relatives
     */
    int getNumOfPersonsAsOthers();

    /**
     * Sets the number of people as other type of relatives
     * 
     * @param numOfPersonsAsOthers
     *            the number of people as other type of relatives
     */
    void setNumOfPersonsAsOthers(int numOfPersonsAsOthers);

    /**
     * Returns the number of people not related to the householder.
     * 
     * @return the number of people not related to the householder
     */
    int getNumOfPersonsNotRelatedToHolder();

    /**
     * Sets the number of people not related to the householder.
     * 
     * @param numOfPersonsNotRelatedToHolder
     *            the number of people not related to the householder
     */
    void setNumOfPersonsNotRelatedToHolder(int numOfPersonsNotRelatedToHolder);

    /**
     * Returns the number of total population including group quarters.
     * 
     * @return the number of total population including group quarters
     */
    int getNumOfPersonsGQIncludedUnivs();

    /**
     * Sets the number of total population including group quarters.
     * 
     * @param numOfPersonsGQIncludedUnivs
     *            the number of total population including group quarters
     */
    void setNumOfPersonsGQIncludedUnivs(int numOfPersonsGQIncludedUnivs);

    /**
     * Returns the number of males.
     * 
     * @return the number of males
     */
    int getMale();

    /**
     * Sets the number of males.
     * 
     * @param male
     *            the number of males
     */
    void setMale(int male);

    /**
     * Returns the number of females.
     * 
     * @return the number of females
     */
    int getFemale();

    /**
     * Sets the number of females
     * 
     * @param female
     *            the number of females
     */
    void setFemale(int female);

    /**
     * Returns the number of people with age 3 and over.
     * 
     * @return the number of people with age 3 and over
     */
    int getAge3PlusUnivs();

    /**
     * Sets the number of people age 3 and over.
     * 
     * @param age3PlusUnivs
     *            the number of people age 3 and over
     */
    void setAge3PlusUnivs(int age3PlusUnivs);

    /**
     * Returns the number of people age 18 to 24.
     * 
     * @return the number of people age 18 to 24
     */
    int getAge18to24();

    /**
     * Sets the number of people age 18 to 24.
     * 
     * @param age18to24
     *            the number of people age 18 to 24
     */
    void setAge18to24(int age18to24);

    /**
     * Returns the number of people age 25 to 34.
     * 
     * @return the number of people age 25 to 34
     */
    int getAge25to34();

    /**
     * Sets the number of people age 25 to 34.
     * 
     * @param age25to34
     *            the number of people age 25 to 34
     */
    void setAge25to34(int age25to34);

    /**
     * Returns the number of people age 35 to 49.
     * 
     * @return the number of people age 35 to 49
     */
    int getAge35to49();

    /**
     * Sets the number of people age 35 to 49.
     * 
     * @param age35to49
     *            the number of people age 35 to 49
     */
    void setAge35to49(int age35to49);

    /**
     * Returns the number of people age 50 to 64.
     * 
     * @return the number of people age 50 to 64
     */
    int getAge50to64();

    /**
     * Sets the number of people age 50 to 64.
     * 
     * @param age50to64
     *            the number of people age 50 to 64
     */
    void setAge50to64(int age50to64);

    /**
     * Returns the number of people age 65 to 79.
     * 
     * @return the number of people age 65 to 79
     */
    int getAge65to79();

    /**
     * Sets the number of people age 65 to 79.
     * 
     * @param age65to79
     *            the number of people age 65 to 79
     */
    void setAge65to79(int age65to79);

    /**
     * Returns the number of people age 80 and over.
     * 
     * @return the number of people age 80 and over
     */
    int getAge80Plus();

    /**
     * Sets the number of people age 80 and over.
     * 
     * @param age80Plus
     *            the number of people age 80 and over
     */
    void setAge80Plus(int age80Plus);

    /**
     * Returns the number of people age 17 and under.
     * 
     * @return the number of people age 17 and under
     */
    int getAge17AndUnder();

    /**
     * Sets the number of people age 17 and under.
     * 
     * @param age17AndUnder
     *            the number of people age 17 and under
     */
    void setAge17AndUnder(int age17AndUnder);

    /**
     * Returns the number of people age 18 to 64.
     * 
     * @return the number of people age 18 to 64
     */
    int getAge18to64();

    /**
     * Sets the number of people age 18 to 64.
     * 
     * @param age18to64
     *            the number of people age 18 to 64
     */
    void setAge18to64(int age18to64);

    /**
     * Returns the number of people age 65 and over.
     * 
     * @return the number of people age 65 and over
     */
    int getAge65Plus();

    /**
     * Sets the number of people age 65 and over.
     * 
     * @param age65Plus
     *            the number of people age 65 and over
     */
    void setAge65Plus(int age65Plus);

    /**
     * Returns the number of Hispanic people
     * 
     * @return the number of Hispanic people
     */
    int getHispanic();

    /**
     * Sets the number of Hispanic people
     * 
     * @param hispanic
     *            the number of Hispanic people
     */
    void setHispanic(int hispanic);

    /**
     * Returns the number of White alone people.
     * 
     * @return the number of White alone people
     */
    int getWhiteAlone();

    /**
     * Sets the number of White alone people.
     * 
     * @param whiteAlone
     *            the number of White alone people
     */
    void setWhiteAlone(int whiteAlone);

    /**
     * Returns the number of Black or African American alone people.
     * 
     * @return the number of Black or African American alone people
     */
    int getBlackAlone();

    /**
     * Sets the number of Black or African American alone people.
     * 
     * @param blackAlone
     *            the number of Black or African American alone people
     */
    void setBlackAlone(int blackAlone);

    /**
     * Returns the number of American Indian or Alaska Native alone people.
     * 
     * @return the number of American Indian or Alaska Native alone people
     */
    int getIndiAlaskaAlone();

    /**
     * Sets the number of American Indian or Alaska Native alone people.
     * 
     * @param indiAlaskaAlone
     *            the number of American Indian or Alaska Native alone people
     */
    void setIndiAlaskaAlone(int indiAlaskaAlone);

    /**
     * Returns the number of Asian alone people.
     * 
     * @return the number of Asian alone people
     */
    int getAsianAlone();

    /**
     * Sets the number of Asian alone people.
     * 
     * @param asianAlone
     *            the number of Asian alone people
     */
    void setAsianAlone(int asianAlone);

    /**
     * Returns the number of Native Hawaiian and Other Pacific Islander alone people.
     * 
     * @return the number of Native Hawaiian and Other Pacific Islander alone people
     */
    int getIslanderAlone();

    /**
     * Sets the number of Native Hawaiian and Other Pacific Islander alone people.
     * 
     * @param islanderAlone
     *            the number of Native Hawaiian and Other Pacific Islander alone people
     */
    void setIslanderAlone(int islanderAlone);

    /**
     * Returns the number of other race alone or two plus races people.
     * 
     * @return the number of other race alone or two plus races people
     */
    int getOtherRaceAloneOrTwoPlusRaces();

    /**
     * Sets the number of other race alone or two plus races people.
     * 
     * @param otherRaceAloneOrTwoPlusRaces
     *            the number of other race alone or two plus races people
     */
    void setOtherRaceAloneOrTwoPlusRaces(int otherRaceAloneOrTwoPlusRaces);

    /**
     * Returns the number of people who worked 27 or more weeks a year.
     * 
     * @return the number of people who worked 27 or more weeks a year
     */
    int getWorked27plusWksUnivs();

    /**
     * Sets the number of people who work 27 or more weeks a year
     * 
     * @param worked27plusWksUnivs
     *            the number of people who work 27 or more weeks a year
     */
    void setWorked27plusWksUnivs(int worked27plusWksUnivs);

    /**
     * Returns the number of people who work 27 or more weeks a year and 35 hours or more a week.
     * 
     * @return the number of people who work 27 or more weeks a year and 35 hours or more a week
     */
    int getWorked27plusWks35PlusHrsPerWk();

    /**
     * Sets the number of people who work 27 or more weeks a year and 35 hours or more a week.
     * 
     * @param worked27plusWks35PlusHrsPerWk
     *            the number of people who work 27 or more weeks a year and 35 hours or more a week
     */
    void setWorked27plusWks35PlusHrsPerWk(int worked27plusWks35PlusHrsPerWk);

    /**
     * Returns the number of people who work 27 or more weeks a year and 15 to 34 hours a week.
     * 
     * @return the number of people who work 27 or more weeks a year and 15 to 34 hours a week
     */
    int getWorked27plusWks15to34HrsPerWk();

    /**
     * Sets the number of people who work 27 or more weeks a year and 15 to 34 hours a week.
     * 
     * @param worked27plusWks15to34HrsPerWk
     *            the number of people who work 27 or more weeks a year and 15 to 34 hours a week
     */
    void setWorked27plusWks15to34HrsPerWk(int worked27plusWks15to34HrsPerWk);

    /**
     * Returns the number of people who work 27 or more weeks a year and 1 to 14 hours a week.
     * 
     * @return the number of people who work 27 or more weeks a year and 1 to 14 hours a week
     */
    int getWorked27plusWks1to14HrsPerWk();

    /**
     * Sets the number of people who work 27 or more weeks a year and 1 to 14 hours a week.
     * 
     * @param worked27plusWks1to14HrsPerWk
     *            the number of people who work 27 or more weeks a year and 1 to 14 hours a week
     */
    void setWorked27plusWks1to14HrsPerWk(int worked27plusWks1to14HrsPerWk);

    /**
     * Returns the number of people who work as civilian.
     * 
     * @return the number of people who work as civilian
     */
    int getCivilianEmployed();

    /**
     * Sets the number of people who work as civilian.
     * 
     * @param civilianEmployed
     *            the number of people who work as civilian
     */
    void setCivilianEmployed(int civilianEmployed);

    /**
     * Returns the number of people who work in the armed force.
     * 
     * @return the number of people who work in the armed force
     */
    int getMilitaryEmployed();

    /**
     * Sets the number of people who work in the armed force.
     * 
     * @param militaryEmployed
     *            the number of people who work in the armed force
     */
    void setMilitaryEmployed(int militaryEmployed);

    /**
     * Returns the number of unemployed people.
     * 
     * @return the number of unemployed people
     */
    int getUnemployed();

    /**
     * Sets the number of unemployed people
     * 
     * @param unemployed
     */
    void setUnemployed(int unemployed);

    /**
     * Returns the number of people not in the labor force.
     * 
     * @return the number of people not in the labor force
     */
    int getNotInLaborForce();

    /**
     * Sets the number of people not in the labor force.
     * 
     * @param notInLaborForce
     *            the number of people not in the labor force
     */
    void setNotInLaborForce(int notInLaborForce);

    /**
     * Returns the number of people in nursery to grade 12.
     * 
     * @return the number of people in nursery to grade 12
     */
    int getEnrolledNurseyToGrade12();

    /**
     * Sets the number of people in nursery to grade 12.
     * 
     * @param enrolledNurseyToGrade12
     *            the number of people in nursery to grade 12
     */
    void setEnrolledNurseyToGrade12(int enrolledNurseyToGrade12);

    /**
     * Returns the number of people in college or graduate schools.
     * 
     * @return the number of people in college or graduate schools
     */
    int getEnrollPostSecondary();

    /**
     * Sets the number of people in college or graduate schools.
     */
    void setEnrollPostSecondary(int enrollPostSecondary);

    /**
     * Returns the value to which the specified key is mapped.
     * 
     * @param index
     *            the integer value of the key of validation attribute
     * @return the value to which the specified key is mapped
     */
    double getValMetric(int index);

    /**
     * Returns the universe or percentage value to which the specified key is mapped.
     * 
     * @param index
     *            the integer value of the key of validation attribute
     * @return the universe or percentage value to which the specified key is mapped
     */
    double getValMetricPct(int index);

    /**
     * Returns the number of people age 16 and over.
     * 
     * @return the number of people age 16 and over
     */
    int getAge16PlusUnivs();

    /**
     * Sets the number of people age 16 and over.
     * 
     * @param age16PlusUnivs
     *            the number of people age 16 and over
     */
    void setAge16PlusUnivs(int age16PlusUnivs);

    /**
     * Returns the value of the universe of the validation statistics to which the specified key is mapped.
     * 
     * @param index
     *            the integer value of the key of validation universe
     * @return the value of the universe of the validation statistics to which the specified key is mapped
     */
    int getUniverse(int index);
}
