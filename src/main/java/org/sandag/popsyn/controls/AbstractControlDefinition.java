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
import org.apache.log4j.Logger;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.enums.ControlCategoryType;

/**
 * This abstract class, which implements the IControlDefinition interface, sets the
 * definition for PopSyn controls.
 *
 * @param <T>   takes a generic type argument such as Household
 * @param <ControlDataType>
 */
public abstract class AbstractControlDefinition<T, ControlDataType>
        implements IControlDefinition<T>
{
    private static final Logger LOGGER          = Logger.getLogger(AbstractControlDefinition.class);

    private Class<T>            persistentClass = null;
    protected String            fieldName       = null;
    private String              description     = null;
    protected Method            method          = null;
    private ControlCategoryType category        = null;
    private boolean             gQControl       = false;
    private int                 rank;

    public AbstractControlDefinition(Class<T> aClass)
    {
        this.persistentClass = aClass;
    }

    @SuppressWarnings("unused")
    private AbstractControlDefinition()
    {

    }

    public void setFieldName(String aFieldName) throws NoSuchMethodException
    {
        this.fieldName = aFieldName;
        method = persistentClass.getMethod(this.getMethodName(aFieldName));
    }

    public String getFieldName()
    {
        return this.fieldName;
    }

    public void setDescription(String aDesc)
    {
        this.description = aDesc;
    }

    public String getDescription()
    {
        return this.description;
    }

    public Class<T> getType()
    {
        return persistentClass;
    }

    /**
     * Gets the method on the Household object that is associated with this control category
     * @return the method
     */
    protected Method getMethod()
    {
        return this.method;
    }

    public ControlCategoryType getCategory()
    {
        return category;
    }

    public void setCategory(ControlCategoryType aCategory)
    {
        this.category = aCategory;
    }

    /**
     * Takes a String, capitalizes the first letter, and appends "get" to the front
     * @param getterName    the String to generate a getter name for
     * @return  a String in the form of a getter name
     */
    protected String getMethodName(String getterName)
    {
        char lead = getterName.charAt(0);
        return "get" + Character.toUpperCase(lead) + getterName.substring(1);
    }

    @SuppressWarnings("unchecked")
    protected ControlDataType getValue(T cObj, Method aMethod) throws Exception
    {
        return (ControlDataType) aMethod.invoke(cObj, (Object[]) null);
    }

    public int getParticipationFactor(T controlObj)
    {
        validateObject();

        int pFactor = 0;
        ControlDataType controlVal = null;

        try
        {
            controlVal = this.getValue(controlObj, getMethod());
        } catch (Exception e)
        {
            LOGGER.error(e);
        }

        if (isValid(controlVal))
        {
            pFactor = 1;
        }

        return pFactor;
    }

    /**
     * Validates the control definition to check whether all required attributes have been set
     */
    protected abstract void validateObject();

    /**
     * Returns a boolean indicating whether the control definition is valid given the set attributes
     * @param ctrl  the ControlDataType that applies to the control definition
     * @return  returns a boolean where true indicates a valid control definition
     */
    protected abstract boolean isValid(ControlDataType ctrl);

    public void print()
    {
        LOGGER.info("Control Definition:");
        LOGGER.info("category:" + category);
        LOGGER.info("description:" + description);
        LOGGER.info("object:" + persistentClass.getSimpleName());
        LOGGER.info("field name:" + fieldName);
        LOGGER.info("GQControl:" + gQControl);
    }

    public void setGQControl(boolean gQControl)
    {
        this.gQControl = gQControl;
    }

    public boolean isGQControl()
    {
        return gQControl;
    }

    public boolean isParticipant(Household hh)
    {
        boolean result = false;
        if (hh.isGroupQuarter() && gQControl)
        {
            result = true;
        } else if (!hh.isGroupQuarter() && !gQControl)
        {
            result = true;
        }
        return result;
    }

    /**
     * Compares two control definitions see which one has a higher rank
     */
    public int compareTo(IControlDefinition<T> def)
    {
        return def.getRank() - this.getRank();
    }

    /**
     * Gets the rank of the control definition.  Returns an integer equaling the rank.
     */
    public int getRank()
    {
        return rank;
    }

    /**
     * Sets the rank of the control definition 
     * @param rank  an integer equaling the rank to set the control definition to
     */
    public void setRank(int rank)
    {
        this.rank = rank;
    }
}
