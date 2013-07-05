/*
 * Copyright 2011 San Diego Association of Governments (SANDAG)
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
package org.sandag.popsyn.controls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.sandag.popsyn.Version;
import org.sandag.popsyn.domain.Geography;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.enums.GeographyType;
import org.sandag.popsyn.io.IGeographyDao;
import org.sandag.popsyn.io.IReaderDao;

/**
 * The Classifier classifies PUMS HHs/GQs by control category. HHs and GQs are classified separately. This class determines which control definitions
 * each Household object participates in.
 * 
 */
public class Classifier
{

    /**
     * A HashMap object containing a mapping between PUMA IDs and GQ control lists for all relevant controls
     */
    private HashMap<Integer, List<ControlList>> classifiedGQs = null;

    /**
     * A HashMap object containing a mapping between PUMA IDs and HH control lists for all relevant controls
     */
    private HashMap<Integer, List<ControlList>> classifiedHHs = null;

    /**
     * A list of the control definitions
     */
    private List<IControlDefinition<Household>> controlDefs   = null;

    /**
     * A list of Geography objects for all PUMAs
     */
    private List<Geography>                     pumaList      = null;

    /**
     * A list of all Household objects from PUMS
     */
    private List<Household>                     hhs           = null;

    /**
     * Stores parameter/version information for this PopSyn run
     */
    private Version                             version       = null;

    /**
     * The data access object for Household objects
     */
    private IReaderDao                          readerDao     = null;

    /**
     * The data access object for Geography objects
     */
    private IGeographyDao                       geographyDao  = null;

    /**
     * Constructs a new Classifier object with the specified IReaderDao, IGeographyDao, and version object.
     * 
     * @param aReaderDao
     * @param aGeographyDao
     * @param aVersion
     */
    public Classifier(IReaderDao aReaderDao, IGeographyDao aGeographyDao, Version aVersion)
    {
        this.readerDao = aReaderDao;
        this.geographyDao = aGeographyDao;
        this.version = aVersion;
        hhs = readerDao.getAllBySource(version.getSourceId());
        pumaList = geographyDao.getAllZonesByType(GeographyType.PUMA);
    }

    /**
     * Classifies Household objects according to control category. First, the method sets up the data structure into which classified Household
     * objects will be organized. Then, the method loops through every Household/GQ and adds them to the appropriate ControlLists according to the
     * PUMA to which it belongs to and the control categories that it fall into. ControlLists are simply collections of Household objects that belong
     * to a given control category.
     * 
     * @throws Exception
     */
    public void classify() throws Exception
    {
        initClassifiedObjs();
        List<ControlList> classifiedObjs;
        for (Household hh : hhs)
        {

            if (hh.isGroupQuarter())
            {
                classifiedObjs = classifiedGQs.get(hh.getPumaId());
            } else
            {
                classifiedObjs = classifiedHHs.get(hh.getPumaId());
            }
            for (ControlList controlList : classifiedObjs)
            {
                hh.initWgt(version.isUseInitWeight());
                controlList.add(hh);
            }

        }
    }

    /**
     * Sets up the data structure into which classified Household objects will be organized. A ControlList is created for each control definition and
     * PUMA pair. Each PUMA has two ArrayLists that store the ControlLists- one list for HH controls and another list for GQ controls. These
     * ArrayLists are stored in one of two HashMaps that map the lists to the appropriate PUMA ID. Which HashMap an ArrayList is stored in depends on
     * whether HHs or GQs are being dealt with. <BR>
     * <img src="doc-files/Classifier-1.gif">
     * 
     */
    private void initClassifiedObjs()
    {
        classifiedGQs = new HashMap<Integer, List<ControlList>>();
        classifiedHHs = new HashMap<Integer, List<ControlList>>();

        for (Geography puma : pumaList)
        {
            List<ControlList> hhCtrlLists = new ArrayList<ControlList>();
            List<ControlList> gqCtrlLists = new ArrayList<ControlList>();

            ControlList list = null;
            for (IControlDefinition<Household> controlDef : controlDefs)
            {
                list = new ControlList(controlDef, puma.getZone(), version);
                if (controlDef.isGQControl())
                {
                    gqCtrlLists.add(list);
                } else
                {
                    hhCtrlLists.add(list);
                }
            }

            classifiedGQs.put(puma.getZone(), gqCtrlLists);
            classifiedHHs.put(puma.getZone(), hhCtrlLists);
        }
    }

    /**
     * Retrieves a List of GQ control lists for a particular PUMA for all relevant controls
     * 
     * @param puma
     *            an integer representing the PUMA to retrieve GQ control lists for
     * @return returns a List of GQ control lists
     */
    public List<ControlList> getClassifiedGQs(int puma)
    {
        return classifiedGQs.get(puma);
    }

    /**
     * Retrieves a List of HH control lists for a particular PUMA for all relevant controls
     * 
     * @param puma
     *            an integer representing the PUMA to retrieve HH control lists for
     * @return returns a List of HH control lists
     */
    public List<ControlList> getClassifiedHHs(int puma)
    {
        return classifiedHHs.get(puma);
    }

    /**
     * Retrieves a ControlList for a particular PUMA and control definition
     * 
     * @param puma
     *            the PUMA corresponding to the ControlList of interest
     * @param aDef
     *            the control definition corresponding to the ControlList of interest
     * @return returns the ControlList object for the given PUMA/control parameters
     */
    public ControlList getClassifiedObjs(int puma, IControlDefinition<Household> aDef)
    {
        ControlList result = null;
        List<ControlList> classifiedObjs = getClassifiedObjs(aDef).get(puma);
        for (ControlList control : classifiedObjs)
        {
            if (control.getControlDefinition().equals(aDef))
            {
                result = control;
                break;
            }
        }
        return result;
    }

    /**
     * Retrieves a HashMap object containing a mapping between PUMAs and control lists. Returns either GQ control lists or HH control lists depending
     * on whether the argument is a GQ or HH control category.
     * 
     * @param aDef
     *            a control definition
     * @return returns a HashMap object containing a mapping between PUMAs and control lists
     */
    private HashMap<Integer, List<ControlList>> getClassifiedObjs(IControlDefinition<Household> aDef)
    {
        HashMap<Integer, List<ControlList>> result;
        if (aDef.isGQControl())
        {
            result = classifiedGQs;
        } else
        {
            result = classifiedHHs;
        }
        return result;
    }

    /**
     * Gets a list of Geography objects for all PUMAs
     * 
     * @return a list of Geography objects for all PUMAs
     */
    public List<Geography> getPumaList()
    {
        return pumaList;
    }

    /**
     * Sets the list of Geography objects for all PUMAs
     * 
     * @param pumaList
     *            the list of PUMAs to use
     */
    public void setPumaList(List<Geography> pumaList)
    {
        this.pumaList = pumaList;
    }

    /**
     * Gets the list of control definitions
     * 
     * @return returns a list of control definitions
     */
    public List<IControlDefinition<Household>> getControlDefs()
    {
        return controlDefs;
    }

    /**
     * Sets the list of control definitions
     * 
     * @param controlDefs
     *            the control definitions to use
     */
    public void setControlDefs(List<IControlDefinition<Household>> controlDefs)
    {
        this.controlDefs = controlDefs;
    }
}
