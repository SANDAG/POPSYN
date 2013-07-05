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

import org.sandag.popsyn.typehandler.IHasValue;

public class GroupEnumControlDefinition<T, ControlDataType>
        extends AbstractGroupControlDefinition<T, ControlDataType>
{
    private IHasValue<Integer> defValue = null;

    public GroupEnumControlDefinition(Class<T> aPersistClass)
    {
        super(aPersistClass);
    }

    /**
     * Get the default value for this control category
     * @return  the default value
     */
    public IHasValue<Integer> getDefValue()
    {
        return defValue;
    }

    /**
     * Set the default value for this control category
     * @param defValue the value to set as the default
     */
    public void setDefValue(IHasValue<Integer> defValue)
    {
        this.defValue = defValue;
    }

    @Override
    protected boolean isValid(ControlDataType controlVal)
    {
        return (null != controlVal) && (controlVal.equals(defValue));
    }

    @Override
    protected void validateObject()
    {
        if (null == super.getMethod() || null == defValue || null == super.getSubField())
            throw new NullPointerException(
                    "Field Name, Sub-Field Name, Min Value, and Max Value must be set");
    }

}
