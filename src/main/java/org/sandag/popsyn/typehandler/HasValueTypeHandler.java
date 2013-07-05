/*
 * Copyright 2011 San Diego Association of Governments (SANDAG)
 * 
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package org.sandag.popsyn.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * Abstract class retrieves values by custom type.
 * 
 */
public abstract class HasValueTypeHandler<T>
        implements TypeHandlerCallback, TypeHandler<IHasValue<T>>
{
    /**
     * Returns an integer array of enum values.
     * 
     * @return an integer array of enum values
     */

    public abstract IHasValue<T>[] getEnums();

    @SuppressWarnings("unchecked")
    public void setParameter(ParameterSetter setter, Object param) throws SQLException
    {

        IHasValue<T> parameter = (IHasValue<T>) param;

        if (null == parameter)
        {
            setter.setNull(Types.NULL);
        } else
        {
            setter.setObject(parameter.getValue());
        }
    }

    public IHasValue<T> getResult(ResultGetter getter) throws SQLException
    {
        if (getter.wasNull()) return null;

        int value = getter.getInt();

        for (IHasValue<T> status : getEnums())
        {
            if (status.getValue().equals(value)) return status;
        }

        throw new UnsupportedOperationException("No such status");
    }

    public Object valueOf(String s)
    {
        return s;
    }

    public IHasValue<T> getResult(ResultSet rs, String columnName) throws SQLException
    {
        int value = rs.getInt(columnName);
        if (rs.wasNull()) return null;

        for (IHasValue<T> status : getEnums())
        {
            if (status.getValue().equals(value)) return status;
        }

        throw new UnsupportedOperationException("No such status");
    }

    public IHasValue<T> getResult(CallableStatement cs, int columnIndex) throws SQLException
    {
        int value = cs.getInt(columnIndex);
        if (cs.wasNull()) return null;

        for (IHasValue<T> status : getEnums())
        {
            if (status.getValue().equals(value)) return status;
        }

        throw new UnsupportedOperationException("No such status");
    }

    public void setParameter(PreparedStatement ps, int i, IHasValue<T> parameter, JdbcType jdbcType)
            throws SQLException
    {
        if (null == parameter)
        {
            ps.setNull(i, Types.NULL);
        } else
        {
            ps.setObject(i, parameter.getValue());
        }

    }
}
