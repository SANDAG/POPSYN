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

public class EnumControlDefinition<T, ControlDataType>
        extends AbstractControlDefinition<T, ControlDataType>
{
    private IHasValue<Integer> defValue = null;

    public EnumControlDefinition(final Class<T> aPersistClass)
    {
        super(aPersistClass);
    }

    /**
     * Set the default value for this control category
     * @param aDefValue the value to set as the default
     */
    public void setDefValue(IHasValue<Integer> aDefValue)
    {
        this.defValue = aDefValue;
    }

    /**
     * Get the default value for this control category
     * @return  the default value
     */
    public IHasValue<Integer> getDefValue()
    {
        return this.defValue;
    }

    @Override
    protected void validateObject()
    {
        if (null == super.getMethod() || null == defValue)
            throw new NullPointerException("Field Name and Definition Value must be set");
    }

    @Override
    protected boolean isValid(ControlDataType controlVal)
    {
        return (null != controlVal) && (controlVal.equals(defValue));
    }
}
