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

import java.lang.reflect.Method;
import java.util.List;
import org.apache.log4j.Logger;

public abstract class AbstractGroupControlDefinition<T, ControlDataType>
        extends AbstractControlDefinition<T, ControlDataType>
{
    private static final Logger LOGGER   = Logger.getLogger(AbstractGroupControlDefinition.class);

    private String              subField = null;

    public AbstractGroupControlDefinition(Class<T> aClass)
    {
        super(aClass);
    }

    public String getSubField()
    {
        return subField;
    }

    public void setSubField(String subField)
    {
        this.subField = subField;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getParticipationFactor(T cObj)
    {
        validateObject();

        int pFactor = 0;
        String subFieldGetterName = getSubMethodName(subField);

        try
        {
            List<T> objList = (List<T>) super.getMethod().invoke(cObj, (Object[]) null);

            if (objList.size() > 0)
            {
                T prototype = objList.get(0);
                Method subFieldGetterMtd = prototype.getClass().getMethod(subFieldGetterName,
                        new Class[0]);

                for (T obj : objList)
                {
                    ControlDataType controlVal = (ControlDataType) subFieldGetterMtd.invoke(obj,
                            (Object[]) null);
                    if (isValid(controlVal)) pFactor++;
                }
            }
        } catch (Exception e)
        {
            LOGGER.error(e);
        }

        return pFactor;
    }

    @Override
    protected abstract void validateObject();

    @Override
    protected abstract boolean isValid(ControlDataType controlVal);

    protected String getSubMethodName(String fieldName)
    {
        return getMethodName(fieldName);
    }
}
