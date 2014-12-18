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

public class SynpopHh extends AbstractHousehold {
	
	public SynpopHh( int id, int tempid, int maz, double serialno, short np, short nwrkrs_esr, int hincp, short fam_type ) {
		super( id, tempid, maz, serialno, np, nwrkrs_esr, hincp, fam_type );
	}

}
