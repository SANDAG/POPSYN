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

package org.sandag.popsyn.core;

public enum PersonTypesOld {
	
	FT_WORKER		    ( "ft-worker",   	     1 ),
	PT_WORKER           ( "pt-worker",   		 2 ),
	UNIV_STUDENT        ( "univ student",        3 ),
	NON_WORKING_ADULT   ( "non worker",          4 ),
	RETIREE             ( "retiree",             5 ),
	PRE_DRIVING_STUDENT ( "pre-driving student", 6 ),
	DRIVING_STUDENT     ( "driving age student", 7 ),
	PRESCHOOL           ( "preschool",           8 );
	
	private final String label;
	private final int index;
	
	PersonTypesOld( String label, int index ) {
		this.label = label;
		this.index = index;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	public int getIndex() {
		return index;
	}
	
}
