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

import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.enums.ControlCategoryType;

/**
 * This interface sets the definition for PopSyn II controls. For example, a control variable could be household size. An implementation of this
 * interface would define the field on the household object, and the min and max (plus other definitions) for the control variable.
 * 
 * This interface assumes all definitions will be Integer based fields.
 * 
 * @author Clint Daniels, SANDAG
 * @author Wu Sun, SANDAG
 * 
 */
public interface IControlDefinition<T>
        extends Comparable<IControlDefinition<T>>
{
    /**
     * A human readable description of the control variable. This should mostly be used for reporting purposes.
     * @param desc   a human readable description of the control variable
     */
    void setDescription(String desc);

    /**
     * The field on the Household object for which an implementation is setting the definition.
     * @param fieldName The name of the field on the Household object for which the definition applies
     */
    void setFieldName(String fieldName) throws NoSuchMethodException;

    /**
     * Gets the description of the control variable.
     * @return  returns the description of the control variable
     */
    String getDescription();

    /**
     * Gets the field name on the Household object that an implementation is setting the definition for.
     * @return  returns the name of the field on the Household object that is defined by the implementation
     */
    String getFieldName();

    /**
     * Gets the type argument (such as Household) being used with this control definition
     * @return 
     */
    Class<T> getType();

    /**
     * Sets the control definition's GQ status (whether the control category applies to GQs)
     * @param gQControl a boolean where true indicates that the control category applies to GQs
     */
    void setGQControl(boolean gQControl);

    /**
     * Determines whether the control category applies to GQs
     * @return  returns a boolean where true indicates that the control category applies to GQs
     */
    boolean isGQControl();

    /**
     * Checks whether a specific balance object participates in the control definition
     * @param hh    the Household object to check
     * @return  returns a boolean where true indicates the object is a participant
     */
    boolean isParticipant(Household hh);

    /**
     * Gets the ControlCategoryType object that corresponds to the control
     * @return  returns a ControlCategoryType object
     */
    ControlCategoryType getCategory();

    /**
     * Sets the ControlCategoryType object for this control
     * @param category  the ControlCategoryType object to use
     */
    void setCategory(ControlCategoryType category);

    /**
     * Returns the participation factor for the object under inspection. 
     * Return value of 0 means the object does not meet the control definition
     * parameters.
     * 
     * @param controlObj
     *            An object to test against the implementations definition.
     * @return 0 if the argument object does not fulfill the definition. Positive integer if the argument fulfills the value. Some group
     *         implementations may return any positive integer which indicates the number of subobjects which meet the definition.
     * @throws Exception
     */
    int getParticipationFactor(T controlObj);

    /**
     * Prints various attributes of the control definition
     */
    void print();

    /**
     * Gets the rank of the control category
     * @return  Returns an integer that equals the rank of the control category
     */
    int getRank();
}
