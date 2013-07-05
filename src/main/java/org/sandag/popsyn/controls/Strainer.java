/*
 * Copyright 2011 San Diego Association of Governments (SANDAG)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.sandag.popsyn.controls;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.domain.Household;

/**
 * Puts every Household object contained in a List<ControlList> into a Set of Household objects.
 *
 */
public final class Strainer
{
    private Strainer()
    {
    }

    /**
     * Puts every Household object contained in a List<ControlList> into a Set of Household objects.
     * @param cList the List<ControlList> to extract Households from
     * @param aVersion  the Version object for this PopSyn run
     * @return  returns a Set of Household objects that were contained in the List<ControlList>
     */
    public static Set<Household> strainHouseholds(List<ControlList> cList, Version aVersion)
    {
        Set<Household> outHouseholds = new HashSet<Household>();

        for (ControlList ctlList : cList)
        {
            Set<Household> ctlHouseholds = ctlList.getControlObjects();

            for (Household ctlHousehold : ctlHouseholds)
            {
                outHouseholds.add(ctlHousehold);
            }
        }
        // Household[] hhArray = new Household[outHouseholds.size()];
        return outHouseholds;
    }
}
