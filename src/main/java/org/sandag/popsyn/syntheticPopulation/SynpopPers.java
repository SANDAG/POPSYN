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

public class SynpopPers extends AbstractPerson {
	
	public SynpopPers( HouseholdIf hh, short pnum, short age, short gender, short wkhp, short cow, short schg, short schl, short employed, short esr, short indusCode, int campusMaz, short campEmpLiv ) {
		super( hh, pnum, age, gender, wkhp, cow, schg, schl, employed, esr, indusCode, campusMaz, campEmpLiv );
	}

	
	@Override
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
