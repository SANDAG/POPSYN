/*   
 * Copyright 2014 Parsons Brinckerhoff

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License
   *
   */

package org.sandag.popsyn.syntheticPopulation;

import org.sandag.popsyn.core.PersonTypesOld;

public abstract class AbstractPerson implements PersonIf {
	
    private static final int NUM_HOURS_FULL_TIME = 40;
    private static final int NUM_HOURS_UNIV_STUDENT = 20;
    private static final int MAX_AGE_UNIV_STUDENT = 35;
    
	protected HouseholdIf hh;
	protected final short pnum;
	protected final short age;
	protected final short gender;
	protected final short wkhp;
	protected final short cow;
	protected final short schg;
	protected final short schl;
	protected final short employed;
	protected final short esr;
	protected final short indusCode;
	protected final int campusMaz;
	protected final short campEmpLiv;

	protected PersonTypesOld personType;
	
	public AbstractPerson( HouseholdIf hh, short pnum, short age, short gender, short wkhp, short cow, short schg, short schl, short employed, short esr, short indusCode, int campusMaz, short campEmpLiv ) {
		this.hh = hh;
		this.pnum = pnum;
		this.age = age;
		this.gender = gender;
		this.wkhp = wkhp;
		this.cow = cow;
		this.schg = schg;
		this.schl = schl;
		this.esr = esr;
		this.employed = employed;
		this.indusCode = indusCode;
		this.campusMaz = campusMaz;
		this.campEmpLiv = campEmpLiv;

		setPersonType();
	}

	
	private void setPersonType() {
		
		// schg=5 -> student in 9th - 12th grade
		// schg=6 -> college undergrad
		// schg=7 -> college graduate or professional school
		if ( schg >= 6 && age >= 18 && age < MAX_AGE_UNIV_STUDENT && wkhp < NUM_HOURS_UNIV_STUDENT ) {
			personType = PersonTypesOld.UNIV_STUDENT;
		}
		else if ( age >= 16 && age <= 18 ) {
			personType = PersonTypesOld.DRIVING_STUDENT;
		}
		else if ( age >= 6 && age < 16 ) {
			personType = PersonTypesOld.PRE_DRIVING_STUDENT;
		}
		else if ( age < 6 ) {
			personType = PersonTypesOld.PRESCHOOL;
		}
		else if ( age > 65 && ( esr == 0 || esr == 3 || esr == 6 ) ) {
			personType = PersonTypesOld.RETIREE;
		}
		else if ( esr == 1 || esr == 2 || esr == 4 || esr == 5 ) {
    		if ( wkhp < NUM_HOURS_FULL_TIME )    			
    			personType = PersonTypesOld.PT_WORKER;
    		else
    			personType = PersonTypesOld.FT_WORKER;
    	}
		else {
			personType = PersonTypesOld.NON_WORKING_ADULT;
		}
		
	}
	
	
	/**
	 * @return hh object
	 */
	public HouseholdIf getHhObject() {
		return hh;
	}
	
	/**
	 * @return hh object id
	 */
	public int getHhId() {
		return hh.getId();
	}
	
	/**
	 * @return person number in the household
	 */
	public short getPnum() {
		return pnum;
	}
	
	public PersonTypesOld getPersonType() {
		return personType;
	}
	
	public int getPersonTypeIndex() {
		return personType.getIndex();
	}
	
	/**
	 * @return age in years, 0-99
	 */
	public short getAge() {
		return age;
	}
	
	/**
	 * @return the pums esr value
	 */
	public short getEsr() {
		return esr;
	}
	
	/**
	 * @return 1 if employed, 0 otherwise
	 */
	public short getEmployed() {
		return employed;
	}
	
	/**
	 * @return number of hours worked per week past 12 months
	 */
	public short getWkhp() {
		return wkhp;
	}
	
	/**
	 * @return class of worker
	 */
	public short getCow() {
		return cow;
	}
	
	/**
	 * @return grade in school - 1=preschool, 2=kindergarten, 3=1st, 4=2nd, ..., 14=12th, 15=undergrad, 16=grad
	 */
	public short getSchg() {
		return schg;
	}
	
	/**
	 * @return educ attainment (year completed)
	 *
 	 * 1=none, 2=preschool, 3=kindergarten, 4=1st, 5=2nd, ..., 15=12th no diploma, 16=hs diploma,
	 * 17=ged, 18=some college, < 1 yr, 19=1 or more years of college no degree, 20=associates, 21=bachelors, 22=masters,
	 * 23=prof degree beyond bachelors, 24=doctorate
	 */
	public short getSchl() {
		return schl;
	}
	
	/**
	 * 
	 * @return gender of person, 1=male, 2=female
	 */
	public short getGender() {
		return gender;
	}
	
	/**
	 * @return 2 digit naics code
	 */
	public short getIndusCode() {
		return indusCode;
	}

	/**
	 * @return MAZ for campus ASU student attends
	 */
	public int getCampusMaz() {
		return campusMaz;
	}

	
	/**
	 * @return code for university student type: campus_employment_living
	 * 
	 * 1=c1_e1_l1, 2=c1_e2_l1, 3=c1_e2_l1, 4=c1_e1_l2, ..., 36=c4_e3_l3
	 * 
	 * campuses (1=downtown, 2=polytechnic, 3=tempe, 4=west), 
	 * employment (1=on-campus, 2=off-campus, 3=unemployed), 
	 * living (1=with family, 2=rental, 3=campus housing)
	 *  
	 */
	public short getCampEmpLiv() {
		return campEmpLiv;
	}


	    	
	
	public String toString() {    		
		String formatLength = "%20"; 
		return 
		"\n\tHH Id:             " + String.format( formatLength+"d", hh.getId() ) +
		"\n\tPerson Num:        " + String.format( formatLength+"d", pnum ) +
		"\n\tPerson Type:       " + String.format( formatLength+"s", personType.toString() ) +
		"\n\tAge:               " + String.format( formatLength+"d", age ) +
		"\n\tGender:            " + String.format( formatLength+"d", gender ) +
		"\n\tWkhp:              " + String.format( formatLength+"d", wkhp ) +
		"\n\tCow:               " + String.format( formatLength+"d", cow ) +
		"\n\tSchg:              " + String.format( formatLength+"d", schg ) +
		"\n\tSchl:              " + String.format( formatLength+"d", schl ) +
		"\n\tEsr:               " + String.format( formatLength+"d", esr ) +
		"\n\tIndusCode:         " + String.format( formatLength+"d", indusCode ) +
		"\n\tCampusMaz:         " + String.format( formatLength+"d", campusMaz ) +
		"\n\tCampEmpLiv:        " + String.format( formatLength+"d", campEmpLiv );
	}
	
}
