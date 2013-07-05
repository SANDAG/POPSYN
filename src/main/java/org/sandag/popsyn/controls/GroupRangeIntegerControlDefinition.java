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

public class GroupRangeIntegerControlDefinition<T>
        extends AbstractGroupControlDefinition<T, Integer>
{
    private Integer minValue = null;
    private Integer maxValue = null;

    public GroupRangeIntegerControlDefinition(Class<T> aPersistClass)
    {
        super(aPersistClass);
    }

    /**
     * Gets the minimum value for this control category
     * @return  an integer equaling the minimum value
     */
    public Integer getMinValue()
    {
        return minValue;
    }

    /**
     * Sets the minimum value for this control category
     * @param minValue  an integer equaling the minimum value
     */
    public void setMinValue(Integer minValue)
    {
        this.minValue = minValue;
    }

    /**
     * Gets the maximum value for this control category
     * @return  an integer equaling the maximum value
     */
    public Integer getMaxValue()
    {
        return maxValue;
    }

    /**
     * Sets the maximum value for this control category
     * @param maxValue  an integer equaling the maximum value
     */
    public void setMaxValue(Integer maxValue)
    {
        this.maxValue = maxValue;
    }

    @Override
    protected boolean isValid(Integer controlVal)
    {
        return (controlVal >= this.getMinValue() && controlVal <= this.getMaxValue());
    }

    @Override
    protected void validateObject()
    {
        if (null == super.getMethod() || null == minValue || null == maxValue
                || null == super.getSubField())
            throw new NullPointerException(
                    "Field Name, Sub-Field Name, Min Value, and Max Value must be set");
    }
}
