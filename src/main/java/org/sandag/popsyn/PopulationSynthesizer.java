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
package org.sandag.popsyn;

import java.util.List;
import org.apache.log4j.Logger;
import org.sandag.popsyn.balancer.Balancer;
import org.sandag.popsyn.controls.Classifier;
import org.sandag.popsyn.controls.IControlDefinition;
import org.sandag.popsyn.domain.Household;
import org.sandag.popsyn.io.IValidatorDao;
import org.sandag.popsyn.io.IVersionManagerDao;
import org.sandag.popsyn.validator.Validator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The main class for generating a synthetic population. A synthetic population represents the 
 * decision-makers whose travel choices the ABM will simulate.  
 * 
 */
public class PopulationSynthesizer
{
    private static final Logger                 LOGGER = Logger
                                                               .getLogger(PopulationSynthesizer.class);
    /**
     * Stores parameter/version information for this PopSyn run
     */
    private Version                             version;
    
    /**
     * A list of the control definitions
     */
    private List<IControlDefinition<Household>> controlDefinitions;
    
    /**
     * The data access object for the Version object
     */
    private IVersionManagerDao                  vMgrDao;
   
    /**
     * The Classifier classifies PUMS households/GQ by control category
     */
    private Classifier                          classifier;
    
    /**
     * Balances PUMS sample household/GQ weights to control targets by TAZ
     */
    private Balancer                            balancer;
    
    /**
     * The data access object for the Validator
     */
    private IValidatorDao                        validatorDao;

    /**
     * Begins execution of PopSyn
     * @param args  not used
     * @throws Exception    
     */
    public static void main(String[] args) throws Exception
    {
        double startTime = System.currentTimeMillis();
        // load
        ApplicationContext appCtx = new ClassPathXmlApplicationContext("applicationContext-mybatis.xml");
        PopulationSynthesizer popSyn = appCtx.getBean(PopulationSynthesizer.class);
        // do work
        popSyn.synthesizePopulation();
        // time stamp
        LOGGER.info("Completed in: "
                + (float) (((System.currentTimeMillis() - startTime) / 1000.0) / 60.0)
                + " minutes.");
    }

    /**
     * Generates a synthetic population.  This method involves classifying 
     * PUMS households, balancing household weights, and validating results
     * against observed data. 
     * @return  Returns an integer, 0, when the method has finished
     * @throws Exception
     */
    public int synthesizePopulation() throws Exception
    {
        vMgrDao.initializeRun(version);

        // load PUMS households and classify
        classifier.setControlDefs(controlDefinitions);
        classifier.classify();
        LOGGER.info("1. households classified");

        // balance
        balancer.balance(classifier);
        LOGGER.info("2. balancer finished");

        // validate, only for non-select zone scenarios
        if (version.getSelectZones().equalsIgnoreCase("NA"))
        {
            Validator validator = new Validator(version, validatorDao);
            validator.reportOutput(validatorDao);
            LOGGER.info("3. validation finished");
        }

        vMgrDao.finalizeRun(version.getId());
        return 0;
    }

    /**
     * Gets the VersionManagerDAO.
     * The VersionManagerDao is the data access object for the Version object.
     * @return  Returns the VersionManagerDAO object being used
     */
    public IVersionManagerDao getVersionMgrDao()
    {
        return vMgrDao;
    }

    /**
     * Sets the VersionManagerDao.
     * The VersionManagerDao is the data access object for the Version object.
     * @param avMgrDao  the VersionManagerDao object to use
     */
    public void setVersionMgrDao(IVersionManagerDao avMgrDao)
    {
        this.vMgrDao = avMgrDao;
    }

    /**
     * Gets the Balancer object.
     * The Balancer balances PUMS sample household/GQ weights to control targets
     * by TAZ.  It also handles the discretizing of household and GQ weights, as
     * well as the allocation of Household objects to the MGRA level.
     * @return  Returns the Balancer object
     */
    public Balancer getBalancer()
    {
        return balancer;
    }

    /**
     * Sets the Balancer object.
     * The Balancer balances PUMS sample household/GQ weights to control targets
     * by TAZ.  It also handles the discretizing of household and GQ weights, as
     * well as the allocation of Household objects to the MGRA level.
     * @param balancer  the Balancer object to use
     */
    public void setBalancer(Balancer balancer)
    {
        this.balancer = balancer;
    }

    /**
     * Gets the Version object associated with this PopSyn run.
     * The Version object records parameter/version information for each run.
     * @return  Returns the Version object being used
     */
    public Version getVersion()
    {
        return version;
    }
    
    /**
     * Sets the Version object associated with this PopSyn run.
     * The Version object records parameter/version information for each run.
     * @param version  the Version object to use
     */
    public void setVersion(Version version)
    {
        this.version = version;
    }

    /**
     * Gets the control definitions.  Control categories are specific 
     * demographic/socioeconomic variables that PopSyn will try to match 
     * at the zonal level. Each control category is defined in control-target-config.xml.
     * @return  Returns the control definitions
     */
    public List<IControlDefinition<Household>> getControlDefinitions()
    {
        return controlDefinitions;
    }

    /**
     * Sets the control definitions to use.  Control categories are specific 
     * demographic/socioeconomic variables that PopSyn will try to match 
     * at the zonal level. Each control category is defined in control-target-config.xml.
     * @return  Returns the control definitions
     */
    public void setControlDefinitions(List<IControlDefinition<Household>> controlDefinitions)
    {
        this.controlDefinitions = controlDefinitions;
    }

    /**
     * Gets the ValidatorDao object.
     * The ValidatorDao is the data access object for the Validator.
     * @return  Returns the ValidatorDAO object
     */
    public IValidatorDao getValidatorDao()
    {
        return validatorDao;
    }

    /**
     * Sets the ValidatorDao object.
     * The ValidatorDao is the data access object for the Validator.
     * @param validatorDao  the ValidatorDao object to use
     */
    public void setValidatorDao(IValidatorDao validatorDao)
    {
        this.validatorDao = validatorDao;
    }

    /**
     * Gets the Classifier object.
     * The Classifier classifies PUMS households/GQ by control category
     * by PUMA.  Households and GQ are classified separately.
     * @return  Returns the Classifier object
     */
    public Classifier getClassifier()
    {
        return classifier;
    }

    /**
     * Sets the Classifier object.
     * The Classifier classifies PUMS households/GQ by control category
     * by PUMA.  Households and GQ are classified separately.
     * @param classifier  the Classifier object to use
     */
    public void setClassifier(Classifier classifier)
    {
        this.classifier = classifier;
    }
}
