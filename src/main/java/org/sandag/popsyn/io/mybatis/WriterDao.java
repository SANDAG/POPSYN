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
package org.sandag.popsyn.io.mybatis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.domain.IGeoWeightedObject;
import org.sandag.popsyn.enums.GeographyType;
import org.sandag.popsyn.io.IWriterDao;

/**
 * This class implements the IWriterDao interface.
 * 
 */
public class WriterDao
        extends SqlSessionDaoSupport
        implements IWriterDao
{

    private static final Logger LOGGER      = Logger.getLogger(WriterDao.class);
    private static final String DOMAIN_PATH = "org.sandag.popsyn.io.mybatis";

    public int persistHouseholds(Integer zoneId, GeographyType geoType,
            Set<IGeoWeightedObject> households, Version version, boolean writeAllocated)

    {
        int status = 0;

        try
        {
            String filePath = writeHouseholdsToFile(version, zoneId, households, writeAllocated);
            if (writeAllocated)
            {
                getSqlSession()
                        .selectOne(DOMAIN_PATH + ".Household.bulkPersistAllocated", filePath);
            } else
            {
                getSqlSession().selectOne(DOMAIN_PATH + ".Household.bulkPersist", filePath);
            }
            (new File(filePath)).delete();
        } catch (IOException ioe)
        {
            LOGGER.fatal(ioe);
            status = -1;
        }

        return status;
    }

    public int persistHouseholds(Integer zoneId, GeographyType geoType,
            ArrayList<IGeoWeightedObject> households, Version version, boolean writeAllocated)

    {
        int status = 0;

        try
        {
            String filePath = writeHouseholdsToFile(version, zoneId, households, writeAllocated);
            if (writeAllocated)
            {
                getSqlSession()
                        .selectOne(DOMAIN_PATH + ".Household.bulkPersistAllocated", filePath);
            } else
            {
                getSqlSession().selectOne(DOMAIN_PATH + ".Household.bulkPersist", filePath);
            }
            (new File(filePath)).delete();
        } catch (IOException ioe)
        {
            LOGGER.fatal(ioe);
            status = -1;
        }

        return status;
    }

    /**
     * Writes the discretized households of each run of PopSyn II to a temporary file.
     * 
     * @param version
     *            the Version of the run
     * @param zoneId
     *            unique identifier of a zone
     * @param households
     *            a set of balanced households
     * @param writeAllocated
     *            a boolean while true means inserting allocated household, and false mean inserting discretized household
     * @return a string of the file path
     * @throws IOException
     */
    private String writeHouseholdsToFile(Version version, Integer zoneId,
            Set<IGeoWeightedObject> households, boolean writeAllocated) throws IOException
    {

        File file = File.createTempFile("popsyn", null, new File(version.getBulkLoadTempDir()));
        FileWriter fWriter = new FileWriter(file);
        BufferedWriter bWriter = new BufferedWriter(fWriter);

        for (IGeoWeightedObject household : households)
            if (!writeAllocated)
            {
                if (household.getWeight() > 0)
                    bWriter.write(version.getId() + "," + zoneId + "," + household.getId() + ","
                            + version.getSourceId() + "," + household.getWeight() + ","
                            + version.getBalanceGeography().getValue() + "\n");
            } else
            {
                bWriter.write(version.getId() + "," + household.getZone() + "," + household.getId()
                        + "," + version.getSourceId() + ","
                        + version.getAllocationGeography().getValue() + "\n");
            }

        bWriter.close();
        fWriter.close();

        return file.getPath();

    }

    private String writeHouseholdsToFile(Version version, Integer zoneId,
            ArrayList<IGeoWeightedObject> households, boolean writeAllocated) throws IOException
    {

        File file = File.createTempFile("popsyn", null, new File(version.getBulkLoadTempDir()));
        FileWriter fWriter = new FileWriter(file);
        BufferedWriter bWriter = new BufferedWriter(fWriter);

        for (IGeoWeightedObject household : households)
            if (!writeAllocated)
            {
                if (household.getWeight() > 0)
                    bWriter.write(version.getId() + "," + zoneId + "," + household.getId() + ","
                            + version.getSourceId() + "," + household.getWeight() + ","
                            + version.getBalanceGeography().getValue() + "\n");
            } else
            {
                bWriter.write(version.getId() + "," + household.getZone() + "," + household.getId()
                        + "," + version.getSourceId() + ","
                        + version.getAllocationGeography().getValue() + "\n");
            }

        bWriter.close();
        fWriter.close();

        return file.getPath();

    }
}
