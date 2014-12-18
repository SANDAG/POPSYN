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

import java.util.List;
import java.util.Map;

import org.sandag.popsyn.core.PersonTypesOld;

public abstract class AbstractHousehold implements HouseholdIf {
	
	// id is the unique id for al hhs, tempid is th id from the synthetic population generator.
	// a household record for any given tempid can have a finalweight > 1.
	protected final int id;
	protected final int tempid;
	protected final double serialno;
	protected final int maz;
	protected final short np;
	protected final short nwrkrs_esr;
	protected final int hincp;
	protected final short fam_type;
	
	protected Map<PersonTypesOld, List<PersonIf>> personMap;
	
	
	public AbstractHousehold( int id, int tempid, int maz, double serialno, short np, short nwrkrs_esr, int hincp, short fam_type ) {
		this.id = id;
		this.tempid = tempid;
		this.maz = maz;
		this.serialno = serialno;
		this.np = np;
		this.nwrkrs_esr = nwrkrs_esr;
		this.hincp = hincp;
		this.fam_type = fam_type;
	}

	public int getId() {
		return id;
	}
	
	public int getTempid() {
		return id;
	}
	
	public int getMaz() {
		return maz;
	}
	
	public short getNumPersons() {
		return np;
	}
	
	public short getNumWorkers() {
		return nwrkrs_esr;
	}
	
	public short getHousingType() {
		return fam_type;
	}
	
	public String getPumsSerialNo() {
		return Double.toString( serialno );
	}
	
	public int getIncomeInDollars() {
		return hincp;
	}

	public void setPersonMap( Map<PersonTypesOld, List<PersonIf>> pMap ) {
		personMap = pMap;
	}

	public List<PersonIf> getPersonTypeListMap( PersonTypesOld type ) {
		return personMap.get( type );
	}
	
	public String toString() {    		
		String formatLength = "%20"; 
		return 
		"\n\tHH Id:             " + String.format( formatLength+"d", id ) +
		"\n\tHH Tempid:         " + String.format( formatLength+"d", tempid ) +
		"\n\tHH Serialno:       " + String.format( formatLength+".0f", serialno ) +
		"\n\tHH MAZ:            " + String.format( formatLength+"d", maz ) +
		"\n\tHH Num Persons:    " + String.format( formatLength+"d", np ) +
		"\n\tHH Num Workers:    " + String.format( formatLength+"d", nwrkrs_esr ) +
		"\n\tHH Income:         " + String.format( formatLength+"d", hincp ) +
		"\n\tHH Dwelling Type:  " + String.format( formatLength+"d", fam_type );
	}
	
}
