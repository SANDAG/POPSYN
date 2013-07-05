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

import java.util.Date;
import org.sandag.popsyn.enums.ControlCategoryType;
import org.sandag.popsyn.enums.GeographyType;

/**
 * This is primarily a metadata class containing parameters for each run of PopSyn II. This class also maintains persistent information on runtime and
 * identifiers.
 * 
 * @author Daniels, Clint
 * @since 1.0
 * 
 */
public class Version
{
    /**
     * Unique identifier for the PopSyn run. This value should only be assigned by the database.
     */
    private Integer             id                     = null;

    /**
     * The maximum number of loops to make in the balancer if the households do not converge.
     * The larger the number, the fewer non-converged zones there will be, but run-times will be longer.
     */
    private int                 maxLoop                = 50;

    /**
     * The maximum difference between the control value and balancer value at which a variable is considered converged.
     * The greater the value, the fewer non-converged zones there will be, but run times will be longer.  
     */
    private double              convergenceCriteria    = -0.01;

    private double              smallValue             = -99999999.0;

    private int                 targetMajorVersion     = -1;
    private int                 targetMinorVersion     = -1;

    private String              description            = null;

    /**
     * The year that the targets pertain to
     */
    private int                 targetYear             = -1;
    
    /**
     * The PUMS data source to use (Census, ACS)
     */
    private int                 sourceId               = -1;

    /**
     * The validation data source to use (Census, ACS)
     */
    private int                 validateSourceId               = -1; 
    
    /**
     * The geography type that targets are available for (typically TAZ)
     */
    private GeographyType       targetGeography        = null;
    
    /**
     * The geography type that HHs/GQs are allocated to (typically MGRA)
     */
    private GeographyType       allocationGeography    = null;
    
    /**
     * The geographic level at which balancing occurs (typically TAZ)
     */
    private GeographyType       balanceGeography       = null;
    
    /**
     * The geography type that PUMS records are available for (typically PUMA)
     */
    private GeographyType       pumsGeography          = null;
    
    /**
     * The geographic level at which validation occurs (typically TAZ)
     */
    private GeographyType       validationGeography    = null;

    /**
     * The start time for this PopSyn run
     */
    private Date                startTime              = null;
    
    /**
     * The end time for this PopSyn run
     */
    private Date                endTime                = null;

   /**
    * The username of the computer that is running PopSyn
    */
    private String              contact                = System.getProperty("user.name");

    /**
     * Number of external zones (TAZs)
     */
    private int                 numExtTaz              = -1;
    
    /**
     * Represents the lowest PUMA ID
     */
    private int                 pumaStart              = -1;
    
    /**
     * The total number of PUMAs
     */
    private int                 numPumas               = -1;
    
    /**
     * The number of validation metrics to use 
     */
    private int                 numValidationMetrics   = -1;

    // TODO: change version manager DAO
    /**
     * The fixed seed given to the Random object when it is instantiated
     */
    private long                randomSeed             = -1;
    
    /**
     * The directory to write output CSV files to
     */
    private String              workDir                = null;
    /**
     * The directory to write temporary output CSV files to
     */
    private String              bulkLoadTempDir                = null;
    
    /**
     * The number of validation metrics to use 
     */
    private int                 valCount               = -1;
    
    /**
     * The target category to use when allocating GQs from TAZ to MGRA
     */
    private ControlCategoryType gqAlAtt                = null;

    /**
     * The target category to use when allocating HHs from TAZ to MGRA
     */
    private ControlCategoryType hhAlAtt                = null;
    
    /**
     * The balance selection factor is multiplied by the zonal household or GQ total
     * to yield the total number of HHs and GQs to be selected for balancing
     */
    private double              balanceSelectionFactor = -1;
    
    /**
     * A boolean that determines whether Household objects start with their initial PUMS weights.
     * If false, all weights are set to 1 instead of using initial PUMS weights.
     */
    private boolean             useInitWeight          = false;
    
    /**
     * An option that increases the sample of hh/gq used for balancing in each TAZ 
     * to include all hh/gq in the corresponding PUMA
     */
    private boolean             balanceAllPumaSamples  = false;
    
    /**
     * This boolean indicates whether random seeds are fixed to force the 
     * random numbers generated to be the same in each run
     */
    private boolean             fixRandomSeed          = false;
    
    /**
     * The ACS year. When the acsYear property is set to 0, PUMS households from all ACS years can be used.
     */
    private int                 acsYear                = -1;
    
    /**
     * Determines whether target generation method is set to "standard" or "growth factor"
     */
    private String              targetMethod           = null;
    
    /**
     * Determines whether to regenerate ACS targets.  When set to "true", targets are regenerated before balancing.
     */
    private String              regenerateTargets      = null;
    
    /**
     * Determines whether all zones are balanced or whether only specified zones are balanced.
     * When set to "NA", all zones are balanced.  When set to zone numbers, only the specified zones are balanced.
     */
    private String              selectZones            = null;

    /**
     * Gets the unique identifier for the PopSyn run 
     * @return  returns an Integer equaling the version ID, a unique identifier for the PopSyn run
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * Sets the unique identifier for the PopSyn run
     * @param anId  an Integer equaling the version ID, a unique identifier for the PopSyn run
     */
    public void setId(Integer anId)
    {
        this.id = anId;
    }
    
    /**
     * Gets the maximum number of loops to make in the balancer
     * @return  returns an integer equaling the maximum number of loops
     */
    public int getMaxLoop()
    {
        return maxLoop;
    }

    /**
     * Sets the maximum number of loops to make in the balancer
     * @param maxLoop   an integer equaling the maximum number of loops
     */
    public void setMaxLoop(int maxLoop)
    {
        this.maxLoop = maxLoop;
    }

    /**
     * Gets the maximum difference between the control value and balancer value at which
     * a variable is considered converged
     * @return  returns a double equaling the convergence criteria
     */
    public double getConvergenceCriteria()
    {
        return convergenceCriteria;
    }

    /**
     * Sets the maximum difference between the control value and balancer value at which
     * a variable is considered converged
     * @param convergenceCriteria   a double equaling the convergence criteria
     */
    public void setConvergenceCriteria(double convergenceCriteria)
    {
        this.convergenceCriteria = convergenceCriteria;
    }

    /**
     * Checks whether a value satisfies the convergence criteria
     * @param testValue a double to check the convergence of
     * @return  returns a boolean where true indicates that the convergence criteria has been met
     */
    public boolean isConverged(double testValue)
    {
        return testValue <= convergenceCriteria;
    }

    public void setSmallValue(double theSmallValue)
    {
        this.smallValue = theSmallValue;
    }

    public double getSmallValue()
    {
        return smallValue;
    }

    public int getTargetMajorVersion()
    {
        return targetMajorVersion;
    }

    public void setTargetMajorVersion(int aTargetMajorVersion)
    {
        this.targetMajorVersion = aTargetMajorVersion;
    }

    public int getTargetMinorVersion()
    {
        return targetMinorVersion;
    }

    public void setTargetMinorVersion(int aTargetMinorVersion)
    {
        this.targetMinorVersion = aTargetMinorVersion;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Gets the year that the targets pertain to
     * @return  returns an int equaling the target year
     */
    public int getTargetYear()
    {
        return targetYear;
    }

    /**
     * Sets the year that the targets pertain to
     * @param targetYear    an int equaling the target year
     */
    public void setTargetYear(int targetYear)
    {
        this.targetYear = targetYear;
    }

    /**
     * Gets the ID of the PUMS data source
     * @return  returns an ID corresponding to the PUMS data source
     */
    public int getSourceId()
    {
        return sourceId;
    }

    /**
     * Sets the ID of the PUMS data source
     * @param sourceId  an ID corresponding to the PUMS data source
     */
    public void setSourceId(int sourceId)
    {
        this.sourceId = sourceId;
    }

    
    
    public int getValidateSourceId()
    {
        return validateSourceId;
    }

    public void setValidateSourceId(int validateSourceId)
    {
        this.validateSourceId = validateSourceId;
    }

    /**
     * Gets the target GeographyType
     * @return  a GeographyType object that corresponds to the geographic type that targets are available for
     */
    public GeographyType getTargetGeography()
    {
        return targetGeography;
    }

    /**
     * Sets the target GeographyType
     * @param targetGeography   a GeographyType object that corresponds to the geographic type that targets are available for
     */
    public void setTargetGeography(GeographyType targetGeography)
    {
        this.targetGeography = targetGeography;
    }

    /**
     * Gets the balance GeographyType
     * @return  returns a GeographyType object that corresponds to the geographic level at which balancing occurs
     */
    public GeographyType getBalanceGeography()
    {
        return balanceGeography;
    }

    /**
     * Sets the balance GeographyType
     * @param balanceGeography  a GeographyType object that corresponds to the geographic level at which balancing occurs
     */
    public void setBalanceGeography(GeographyType balanceGeography)
    {
        this.balanceGeography = balanceGeography;
    }

    /**
     * Gets the start time for the PopSyn run
     * @return  returns a Date object corresponding to the start time
     */
    public Date getStartTime()
    {
        return startTime;
    }

    /**
     * Sets the start time for the PopSyn run
     * @param startTime a Date object corresponding to the start time
     */
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    /**
     * Gets the end time for the PopSyn run
     * @return  returns a Date object corresponding to the end time
     */
    public Date getEndTime()
    {
        return endTime;
    }

    /**
     * Sets the end time for the PopSyn run
     * @param endTime   returns a Date object corresponding to the end time
     */
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    /**
     * Gets the contact person (username) associated with this PopSyn run
     * @return the contact person (username)
     */
    public String getContact()
    {
        return contact;
    }

    /**
     * Gets the number of external zones
     * @return  returns an integer equaling the number of external zones
     */
    public int getNumExtTaz()
    {
        return numExtTaz;
    }

    /**
     * Sets the number of external zones
     * @param numExtTaz an integer equaling the number of external zones
     */
    public void setNumExtTaz(int numExtTaz)
    {
        this.numExtTaz = numExtTaz;
    }

    /**
     * Gets the PUMA to start at when a starting PUMA is needed to begin a loop
     * @return  an integer equaling the PUMA ID
     */
    public int getPumaStart()
    {
        return pumaStart;
    }

    /**
     * Sets the PUMA to start at when a starting PUMA is needed to begin a loop
     * @param pumaStart an integer equaling the PUMA ID
     */
    public void setPumaStart(int pumaStart)
    {
        this.pumaStart = pumaStart;
    }

    /**
     * Gets the total number of PUMAs
     * @return  an integer equaling the number of PUMAs
     */
    public int getNumPumas()
    {
        return numPumas;
    }

    /**
     * Sets the total number of PUMAs
     * @param numPumas  an integer equaling the number of PUMAs
     */
    public void setNumPumas(int numPumas)
    {
        this.numPumas = numPumas;
    }

    /**
     * Gets the validation GeographyType
     * @return  returns a GeographyType object that corresponds to the geographic level at which validation occurs
     */
    public GeographyType getValidationGeography()
    {
        return validationGeography;
    }

    /**
     * Sets the validation GeographyType
     * @param validationGeography   a GeographyType object that corresponds to the geographic level at which validation occurs
     */
    public void setValidationGeography(GeographyType validationGeography)
    {
        this.validationGeography = validationGeography;
    }

    /**
     * Gets the fixed seed given to the Random object when it is instantiated
     * @return  returns a long equaling the fixed seed
     */
    public long getRandomSeed()
    {
        return randomSeed;
    }

    /**
     * Sets the fixed seed given to the Random object when it is instantiated
     * @param randomSeed    a long equaling the fixed seed
     */
    public void setRandomSeed(long randomSeed)
    {
        this.randomSeed = randomSeed;
    }

    /**
     * Gets the PUMS GeographyType
     * @return  returns a GeographyType object that corresponds to the geographic level at which PUMS records are available
     */
    public GeographyType getPumsGeography()
    {
        return pumsGeography;
    }

    /**
     * Sets the PUMS GeographyType
     * @param pumsGeography a GeographyType object that corresponds to the geographic level at which PUMS records are available
     */
    public void setPumsGeography(GeographyType pumsGeography)
    {
        this.pumsGeography = pumsGeography;
    }

    /**
     * Gets an integer equaling the number of validation metrics
     * @return  returns an integer equaling the number of validation metrics
     */
    public int getNumValidationMetrics()
    {
        return numValidationMetrics;
    }

    /**
     * Sets an integer equaling the number of validation metrics
     * @param numValidationMetrics  an integer equaling the number of validation metrics
     */
    public void setNumValidationMetrics(int numValidationMetrics)
    {
        this.numValidationMetrics = numValidationMetrics;
    }

    /**
     * Gets the working directory, the directory where CSV files are outputted
     * @return  returns a String identifying the working directory
     */
    public String getWorkDir()
    {
        return workDir;
    }

    /**
     * Sets the working directory, the directory where CSV files are outputted
     * @param workDir   a String identifying the working directory
     */
    public void setWorkDir(String workDir)
    {
        this.workDir = workDir;
    }

    
    
    public String getBulkLoadTempDir()
    {
        return bulkLoadTempDir;
    }

    public void setBulkLoadTempDir(String bulkLoadTempDir)
    {
        this.bulkLoadTempDir = bulkLoadTempDir;
    }

    /**
     * Gets the number of validation metrics
     * @return  returns an integer equaling the number of validation metrics
     */
    public int getValCount()
    {
        return valCount;
    }

    /**
     * Sets the number of validation metrics
     * @param valCount  an integer equaling the number of validation metrics
     */
    public void setValCount(int valCount)
    {
        this.valCount = valCount;
    }

    /**
     * Gets the allocation GeographyType
     * @return  returns a GeographyType object that corresponds to the geographic level that HHs/GQs are allocated to
     */
    public GeographyType getAllocationGeography()
    {
        return allocationGeography;
    }

    /**
     * Sets the allocation GeographyType
     * @param allocationGeography   a GeographyType object that corresponds to the geographic level that HHs/GQs are allocated to
     */
    public void setAllocationGeography(GeographyType allocationGeography)
    {
        this.allocationGeography = allocationGeography;
    }

    /**
     * Gets the target category used to allocate GQs from TAZ to MGRA
     * @return  returns a ControlCategoryType that corresponds to this target category
     */
    public ControlCategoryType getGqAlAtt()
    {
        return gqAlAtt;
    }

    /**
     * Sets the target category used to allocate GQs from TAZ to MGRA
     * @param gqAlAtt   a ControlCategoryType that corresponds to this target category
     */
    public void setGqAlAtt(ControlCategoryType gqAlAtt)
    {
        this.gqAlAtt = gqAlAtt;
    }

    /**
     * Gets the target category used to allocate HHs from TAZ to MGRA
     * @return  returns a ControlCategoryType that corresponds to this target category
     */
    public ControlCategoryType getHhAlAtt()
    {
        return hhAlAtt;
    }

    /**
     * Sets the target category used to allocate HHs from TAZ to MGRA
     * @param hhAlAtt   a ControlCategoryType that corresponds to this target category
     */
    public void setHhAlAtt(ControlCategoryType hhAlAtt)
    {
        this.hhAlAtt = hhAlAtt;
    }

    /**
     * Gets the balance selection factor, which helps determine the number of HHs/GQs selected for balancing
     * @return  returns a double equaling the balance selection factor
     */
    public double getBalanceSelectionFactor()
    {
        return balanceSelectionFactor;
    }

    /**
     * Sets the balance selection factor, which helps determine the number of HHs/GQs selected for balancing
     * @param balanceSelectionFactor    a double equaling the balance selection factor
     */
    public void setBalanceSelectionFactor(double balanceSelectionFactor)
    {
        this.balanceSelectionFactor = balanceSelectionFactor;
    }

    /**
     * Determines whether Household objects start with their initial PUMS weights
     * @return  returns a boolean where true indicates that Household objects start with their initial weights (rather than 1)
     */
    public boolean isUseInitWeight()
    {
        return useInitWeight;
    }

    /**
     * Sets the boolean that determines whether Household objects start with their initial PUMS weights
     * @param useInitWeight a boolean where true indicates that Household objects start with their initial weights (rather than 1)
     */
    public void setUseInitWeight(boolean useInitWeight)
    {
        this.useInitWeight = useInitWeight;
    }

    /**
     * Determines whether PopSyn is increasing the sample of hh/gq used for balancing in each TAZ 
     * to include all hh/gq in the corresponding PUMA
     * @return  returns a boolean indicating whether all hh/gq in the PUMA are selected for balancing
     */
    public boolean isBalanceAllPumaSamples()
    {
        return balanceAllPumaSamples;
    }

    /**
     * Sets the boolean that determines whether increases the sample of hh/gq used for balancing in each TAZ 
     * to include all hh/gq in the corresponding PUMA
     * @param balanceAllPumaSamples a boolean indicating whether all hh/gq in the PUMA are selected for balancing
     */
    public void setBalanceAllPumaSamples(boolean balanceAllPumaSamples)
    {
        this.balanceAllPumaSamples = balanceAllPumaSamples;
    }

    /**
     * Checks if PopSyn is fixing random seeds to force the random numbers generated
     * to be the same in each run
     * @return  returns a boolean indicating whether random seeds are fixed
     */
    public boolean isFixRandomSeed()
    {
        return fixRandomSeed;
    }

    /**
     * Sets the boolean that determines if PopSyn is fixing random seeds to force the random numbers generated
     * to be the same in each run
     * @param fixRandomSeed a boolean indicating whether random seeds are fixed
     */
    public void setFixRandomSeed(boolean fixRandomSeed)
    {
        this.fixRandomSeed = fixRandomSeed;
    }

    /**
     * Gets the ACS year.  When the acsYear property is set to 0, PUMS households from all ACS years can be used.
     * @return  returns an integer equaling the ACS year
     */
    public int getAcsYear()
    {
        return acsYear;
    }

    /**
     * Sets the ACS year.  When the acsYear property is set to 0, PUMS households from all ACS years can be used.
     * @param acsYear   an integer equaling the ACS year
     */
    public void setAcsYear(int acsYear)
    {
        this.acsYear = acsYear;
    }

    public String getTargetMethod()
    {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod)
    {
        this.targetMethod = targetMethod;
    }

    /**
     * Sets the field that determines whether ACS targets are regenerated before balancing
     * @param regenerateTargets returns a String that triggers target regeneration when it equals "true"
     */
    public void setRegenerateTargets(String regenerateTargets)
    {
        this.regenerateTargets = regenerateTargets;
    }

    /**
     * Gets the field that determines whether ACS targets are regenerated before balancing
     * @return  a String that triggers target regeneration when it equals "true"
     */
    public String getRegenerateTargets()
    {
        return regenerateTargets;
    }

    /**
     * Gets the zones selected for balancing, if applicable 
     * @return  returns a string listing zones selected for balancing.
     * Returns 'NA' if all zones will be balanced
     */
    public String getSelectZones()
    {
        return selectZones;
    }

    /**
     * Sets the zones to select for balancing
     * @param selectZones   a string listing zones selected for balancing, or 'NA' if all zones should be balanced
     */
    public void setSelectZones(String selectZones)
    {
        this.selectZones = selectZones;
    }

}
