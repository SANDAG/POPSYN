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

public interface PersonIf {

	public HouseholdIf getHhObject();
	public int getHhId();
	public short getPnum();
	public PersonTypesOld getPersonType();
	public int getPersonTypeIndex();
	public short getAge();
	public short getEsr();
	public short getEmployed();
	public short getIndusCode();
	public short getWkhp();
	public short getCow();
	public short getSchg();
	public short getSchl();
	public short getGender();
	public int getCampusMaz();
	public short getCampEmpLiv();
	public String toString();    		

}
