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

public interface HouseholdIf {

	public abstract int getId();

	public abstract int getMaz();

	public abstract short getNumPersons();

	public abstract short getNumWorkers();

	public abstract short getHousingType();

	public abstract String getPumsSerialNo();

	public int getIncomeInDollars();

	public void setPersonMap( Map<PersonTypesOld, List<PersonIf>> pMap );
	public List<PersonIf> getPersonTypeListMap( PersonTypesOld type );
	
	public abstract String toString();

}