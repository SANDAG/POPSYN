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

public class BooleanControlDefinition<T>
        extends AbstractControlDefinition<T, Boolean>
{
    private Boolean check = true;

    public BooleanControlDefinition(Class<T> aClass)
    {
        super(aClass);
    }

    /**
     * Sets the check value (true or false) for this control category
     * @param check the boolean to use for the check value
     */
    public void setCheck(Boolean check)
    {
        this.check = check;
    }

    @Override
    protected final String getMethodName(String getterName)
    {
        char lead = getterName.charAt(0);
        return "is" + Character.toUpperCase(lead) + getterName.substring(1);
    }

    @Override
    protected void validateObject()
    {
        if (null == super.getMethod() || null == check)
            throw new NullPointerException("Field Name and Check Value must be set");
    }

    @Override
    protected boolean isValid(Boolean ctrl)
    {
        return ctrl.booleanValue() == check.booleanValue();
    }
}
