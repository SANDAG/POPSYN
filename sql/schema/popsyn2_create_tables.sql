/*
POPSYN MASTER TABLES
*/
-- VERSION --
CREATE TABLE [version](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[source_id] [int] NOT NULL,
	[max_loop] [int] NOT NULL,
	[convergence_criteria] [float] NOT NULL,
	[major_version] [smallint] NOT NULL,
	[minor_version] [int] NOT NULL,
	[description] [varchar](255) NOT NULL,
	[target_year] [int] NOT NULL,
	[target_geography] [smallint] NOT NULL,
	[balance_geography] [smallint] NOT NULL,
	[start_time] [datetime] NOT NULL,
	[end_time] [datetime] NULL,
	[contact] [varchar](25) NOT NULL,
	[validate_source_id] [int] NULL,
	[validation_count] [int] NULL,
	[target_method] [varchar](25) NULL,
	[regenerate_targets] [bit] NULL,
	[select_zones] [varchar](50) NULL,
	[work_directory] [varchar](100) NULL,
	[bulk_load_tmp_dir] [varchar](100) NULL,
	[allocation_geography] [smallint] NULL,
	[small_value] [bigint] NULL,
	[large_value] [bigint] NULL,
	[puma_start_zone] [smallint] NULL,
	[num_external_zones] [smallint] NULL,
	[hh_allocate_attribute] [varchar](50) NULL,
	[gq_allocate_attribute] [varchar](50) NULL,
	[use_init_weight] [bit] NULL,
	[balance_selection_factor] [float] NULL,
	[balance_all_puma_samples] [bit] NULL,
	[fix_random_seed] [bit] NULL,
	[random_seed] [int] NULL,
	[acs_year] [int] NULL,
	[pop_syn_version] [float] NULL,
 CONSTRAINT [PK_run_version] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

-- SOURCE TABLE --
CREATE TABLE [source](
	[id] [int] IDENTITY(1,3) NOT NULL,
	[year] [smallint] NOT NULL,
	[source_type] [varchar](10) NOT NULL,
 CONSTRAINT [PK_source] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

/*
Create Geography Tables
*/
-- GEOGRAPHY TABLE --
CREATE TABLE [geography](
	[geography] [varchar](50) NOT NULL,
	[alias] [varchar](15) NULL,
CONSTRAINT [PK_geography] PRIMARY KEY CLUSTERED 
(
	[geography] ASC
))
GO

-- GEOGRAPHY TYPE TABLE --
CREATE TABLE [geography_type](
	[id] [smallint] IDENTITY(33,3) NOT NULL,
	[geography] [varchar](50) NOT NULL,
	[version] [smallint] NOT NULL,
 CONSTRAINT [PK_geography_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

-- GEOGRAPHY ZONE TABLE --
CREATE TABLE [geography_zone](
	[zone] [int] NOT NULL,
	[geo_type_id] [smallint] NOT NULL,
	[alias] [varchar](75) NULL,
	[shape] [geometry] NOT NULL,
	[centroid] [geometry] NULL,
 CONSTRAINT [PK_geography_zone] PRIMARY KEY CLUSTERED 
(
	[zone] ASC,
	[geo_type_id] ASC
))
GO

/*
LOOK-UP TABLES
*/
-- DWELLING TYPES --
CREATE TABLE [dwelling_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_dwelling_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

-- EMPLOYMENT TYPES --
CREATE TABLE [employment_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_employment_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

-- GRADE TYPES --
CREATE TABLE [grade_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_grade_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

--OCCUPATION TYPES --
CREATE TABLE [occupation_type](
	[occupation_id] [smallint] NOT NULL,
	[description] [nvarchar](60) NULL,
 CONSTRAINT [PK_occupation_type] PRIMARY KEY CLUSTERED 
(
	[occupation_id] ASC
))
GO

-- PERSON TYPES --
CREATE TABLE [person_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_person_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

-- RACE TYPES --
CREATE TABLE [race_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_race_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

-- RELATIONSHIP TYPES --
CREATE TABLE [relationship_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_relationship_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

-- TARGET CATEGORIES --
CREATE TABLE [target_category](
	[id] [smallint] IDENTITY(1,3) NOT NULL,
	[name] [varchar](32) NOT NULL,
 CONSTRAINT [PK_target_category] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

-- TARGET LAND USES VERSIONS --
CREATE TABLE [target_lu_version](
	[lu_major_version] [smallint] NOT NULL,
	[lu_minor_version] [smallint] NOT NULL,
	[source] [nvarchar](120) NULL,
 CONSTRAINT [PK_target_lu_maj_version] PRIMARY KEY CLUSTERED 
(
	[lu_major_version] ASC,
	[lu_minor_version] ASC
))
GO

-- UNIT TYPES --
CREATE TABLE [unit_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_unit_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

-- WORK HOUR TYPES --
CREATE TABLE [work_hour_type](
	[id] [smallint] NOT NULL,
	[type] [varchar](25) NOT NULL,
 CONSTRAINT [PK_work_hour_type] PRIMARY KEY CLUSTERED 
(
	[id] ASC
))
GO

/*
INPUT TABLES
*/
-- HOUSEHOLDS --
CREATE TABLE [household](
	[source_id] [int] NOT NULL,
	[serial_no] [bigint] NOT NULL,
	[puma] [int] NOT NULL,
	[adj_income] [int] NOT NULL,
	[family] [bit] NOT NULL,
	[owner_occ] [bit] NOT NULL,
	[unit_type] [smallint] NOT NULL,
	[dwelling_type] [smallint] NOT NULL,
	[vehicle] [smallint] NOT NULL,
	[init_weight] [int] NOT NULL,
 CONSTRAINT [PK_houshold] PRIMARY KEY CLUSTERED 
(
	[source_id] ASC,
	[serial_no] ASC
)) 
GO

-- PERSON TABLE --
CREATE TABLE [person](
	[source_id] [int] NOT NULL,
	[household_serial_no] [bigint] NOT NULL,
	[person_id] [int] NOT NULL,
	[age] [smallint] NOT NULL,
	[relationship] [smallint] NOT NULL,
	[employ_type] [smallint] NOT NULL,
	[work_hour_type] [smallint] NOT NULL,
	[military] [bit] NOT NULL,
	[grade] [smallint] NOT NULL,
	[male] [bit] NOT NULL,
	[race] [smallint] NOT NULL,
	[person_type] [smallint] NOT NULL,
	[hispanic] [bit] NOT NULL,
	[married] [bit] NOT NULL,
	[poverty] [bit] NOT NULL,
	[earnings] [int] NULL,
	[income_total] [int] NULL,
	[occupation] [smallint] NOT NULL,
	[occcen5] [smallint] NOT NULL,
	[indcen] [smallint] NOT NULL,
	[wgt] [int] NULL,
 CONSTRAINT [PK_person] PRIMARY KEY CLUSTERED 
(
	[source_id] ASC,
	[household_serial_no] ASC,
	[person_id] ASC
))
GO

-- TARGETS --
CREATE TABLE [target](
	[category] [smallint] NOT NULL,
	[geo_type_id] [smallint] NOT NULL,
	[geo_zone] [int] NOT NULL,
	[year] [smallint] NOT NULL,
	[value] [real] NOT NULL,
	[lu_revision_no] [smallint] NOT NULL,
	[lu_major_version] [smallint] NOT NULL,
	[lu_minor_version] [smallint] NOT NULL,
 CONSTRAINT [PK_target_1] PRIMARY KEY CLUSTERED 
(
	[category] ASC,
	[geo_type_id] ASC,
	[geo_zone] ASC,
	[year] ASC,
	[lu_revision_no] ASC,
	[lu_major_version] ASC,
	[lu_minor_version] ASC
))
GO

-- TARGET GROWTH FACTOR --
CREATE TABLE [target_growth_factor](
	[category] [int] NOT NULL,
	[geoType] [int] NOT NULL,
	[geoZone] [int] NOT NULL,
	[baseYear] [int] NOT NULL,
	[growthYear] [int] NOT NULL,
	[lu_revision_no] [smallint] NULL,
	[lu_major_version] [smallint] NULL,
	[lu_minor_version] [smallint] NULL,
	[value] [float] NOT NULL,
 CONSTRAINT [PK_growthFactor] PRIMARY KEY CLUSTERED 
(
	[category] ASC,
	[geoType] ASC,
	[geoZone] ASC,
	[baseYear] ASC,
	[growthYear] ASC
))
GO

/*
OUTPUT TABLES
*/
-- HOUSEHOLD ALLOCATED --
CREATE TABLE [household_allocated](
	[run_version] [int] NOT NULL,
	[zone] [int] NOT NULL,
	[household_serial_no] [nchar](50) NOT NULL,
	[source_id] [int] NOT NULL,
	[geo_type_i] [int] NOT NULL
)
GO

-- HOUSEHOLD DISCRETIZED --
CREATE TABLE [household_discretized](
	[run_version] [int] NOT NULL,
	[zone] [int] NOT NULL,
	[household_serial_no] [nchar](50) NOT NULL,
	[source_id] [int] NOT NULL,
	[weight] [float] NOT NULL,
	[geo_type_id] [int] NOT NULL,
 CONSTRAINT [PK_household_discretized] PRIMARY KEY CLUSTERED 
(
	[run_version] ASC,
	[zone] ASC,
	[household_serial_no] ASC,
	[source_id] ASC
))
GO

/*
VALIDATION TABLES
*/
-- CENSUS VALIDATION --
CREATE TABLE [census_validation](
	[puma] [int] NOT NULL,
	[hhs] [int] NULL,
	[famHhs] [int] NULL,
	[nonFamHhs] [int] NULL,
	[hhsbyHolderAge15to64] [int] NULL,
	[hhsbyHolderAge65Plus] [int] NULL,
	[hh1Person] [int] NULL,
	[hh2Persons] [int] NULL,
	[hh3Persons] [int] NULL,
	[hh4PlusPersons] [int] NULL,
	[famHhsbyHolderAge15to64] [int] NULL,
	[famHhsbyHolderAge65Plus] [int] NULL,
	[nonFamHhsbyHolderAge15to64] [int] NULL,
	[nonFamHhsbyHolderAge65Plus] [int] NULL,
	[famHhsOwnChildUnder18] [int] NULL,
	[famHhsNoOwnChildUnder18] [int] NULL,
	[famHhsAge18UnderPres] [int] NULL,
	[famHhsNoAge18UnderPres] [int] NULL,
	[nonFamHhsAge18UnderPres] [int] NULL,
	[nonFamHhsNoAge18UnderPres] [int] NULL,
	[hhs1PlusAge65Plus] [int] NULL,
	[hhsNoAge65Plus] [int] NULL,
	[hhs1PersonAge65Plus] [int] NULL,
	[fam2PlusPersons1PlusAge65Plus] [int] NULL,
	[nonFam2PlusPersons1PlusAge65Plus] [int] NULL,
	[hhs1PersonAge65Under] [int] NULL,
	[fam2PlusPersonsNoAge65Plus] [int] NULL,
	[nonFam2PlusPersonsNoAge65Plus] [int] NULL,
	[numOfWorkers0] [int] NULL,
	[numOfWorkers1] [int] NULL,
	[numOfWorkers2] [int] NULL,
	[numOfWorkers3Plus] [int] NULL,
	[incomeUnder30k] [int] NULL,
	[income30kto60k] [int] NULL,
	[income60kto100k] [int] NULL,
	[income100kto150k] [int] NULL,
	[income150kPlus] [int] NULL,
	[incomeUnder10k] [int] NULL,
	[income10kto20k] [int] NULL,
	[income20kto30k] [int] NULL,
	[income30kto40k] [int] NULL,
	[income40kto50k] [int] NULL,
	[income50kto60k] [int] NULL,
	[income60kto75k] [int] NULL,
	[income75kto100k] [int] NULL,
	[income60kUnder] [int] NULL,
	[singleFamily] [int] NULL,
	[multiUnit] [int] NULL,
	[mobileHome] [int] NULL,
	[numOfOwned] [int] NULL,
	[numOfRent] [int] NULL,
	[hh1per0wkr] [int] NULL,
	[hh1per1wkr] [int] NULL,
	[hh2per0wkr] [int] NULL,
	[hh2per1wkr] [int] NULL,
	[hh2per2wkr] [int] NULL,
	[hh3per0wkr] [int] NULL,
	[hh3per1wkr] [int] NULL,
	[hh3per2wkr] [int] NULL,
	[hh3per3wkr] [int] NULL,
	[hh4plusper0wkr] [int] NULL,
	[hh4plusper1wkr] [int] NULL,
	[hh4plusper2wkr] [int] NULL,
	[hh4plusper3wkr] [int] NULL,
	[hh1perHhinc0to30k] [int] NULL,
	[hh1perHhinc30kto60k] [int] NULL,
	[hh1perHhinc60kto100k] [int] NULL,
	[hh1perHhinc100kplus] [int] NULL,
	[hh2perHhinc0to30k] [int] NULL,
	[hh2perHhinc30kto60k] [int] NULL,
	[hh2perHhinc60kto100k] [int] NULL,
	[hh2perHhinc100kplus] [int] NULL,
	[hh3perHhinc0to30k] [int] NULL,
	[hh3perHhinc30kto60k] [int] NULL,
	[hh3perHhinc60kto100k] [int] NULL,
	[hh3perHhinc100kPlus] [int] NULL,
	[hh4plusperHhinc0to30k] [int] NULL,
	[hh4plusperHhinc30kto60k] [int] NULL,
	[hh4plusperHhinc60kto100k] [int] NULL,
	[hh4plusperHhinc100kplus] [int] NULL,
	[hh0wkrHhinc0kto30k] [int] NULL,
	[hh0wkrHhinc30kto60k] [int] NULL,
	[hh0wkrHhinc60kto100k] [int] NULL,
	[hh0wkrHhinc100kPlus] [int] NULL,
	[hh1wkrHhinc0kto30k] [int] NULL,
	[hh1wkrHhinc30kto60k] [int] NULL,
	[hh1wkrHhinc60kto100k] [int] NULL,
	[hh1wkrHhinc100kPlus] [int] NULL,
	[hh2wkrHhinc0kto30k] [int] NULL,
	[hh2wkrHhinc30kto60k] [int] NULL,
	[hh2wkrHhinc60kto100k] [int] NULL,
	[hh2wkrHhinc100kPlus] [int] NULL,
	[hh3wkrHhinc0kto30k] [int] NULL,
	[hh3wkrHhinc30kto60k] [int] NULL,
	[hh3wkrHhinc60kto100k] [int] NULL,
	[hh3wkrHhinc100kPlus] [int] NULL,
	[numPersonsHh] [int] NULL,
	[numPersonsFamilyHh] [int] NULL,
	[numPersonsNonFamilyHh] [int] NULL,
	[numPersonsIncomeBelowPoverty] [int] NULL,
	[numOfPersonsAsHolders] [int] NULL,
	[numOfPersonsAsSpouse] [int] NULL,
	[numOfPersonsAsChild] [int] NULL,
	[numOfPersonsAsOthers] [int] NULL,
	[numOfPersonsNotRelatedToHolder] [int] NULL,
	[numOfPersonsGQIncluded] [int] NULL,
	[male] [int] NULL,
	[female] [int] NULL,
	[age0to5] [int] NULL,
	[age6to11] [int] NULL,
	[age12to15] [int] NULL,
	[age16to17] [int] NULL,
	[age18to24] [int] NULL,
	[age25to34] [int] NULL,
	[age35to49] [int] NULL,
	[age50to64] [int] NULL,
	[age65to79] [int] NULL,
	[age80Plus] [int] NULL,
	[age17AndUnder] [int] NULL,
	[age18to64] [int] NULL,
	[age65Plus] [int] NULL,
	[marriedSpousePres] [int] NULL,
	[hispanic] [int] NULL,
	[whiteAlone] [int] NULL,
	[blackAlone] [int] NULL,
	[indiAlaskaAlone] [int] NULL,
	[asianAlone] [int] NULL,
	[islanderAlone] [int] NULL,
	[otherRaceAloneOrTwoPlusRaces] [int] NULL,
	[worked27plusWks] [int] NULL,
	[worked27plusWks35PlusHrsPerWk] [int] NULL,
	[worked27plusWks15to34HrsPerWk] [int] NULL,
	[worked27plusWks1to14HrsPerWk] [int] NULL,
	[age16Plus] [int] NULL,
	[civilianEmployed] [int] NULL,
	[militaryEmployed] [int] NULL,
	[unemployed] [int] NULL,
	[notInLaborForce] [int] NULL,
	[occpMng] [int] NULL,
	[occpService] [int] NULL,
	[occpSales] [int] NULL,
	[occpConstruct] [int] NULL,
	[occpTransport] [int] NULL,
	[age3Plus] [int] NULL,
	[enrolledNurseyToGrade12] [int] NULL,
	[enrollPostSecondary] [int] NULL,
 CONSTRAINT [pk_census_validation] PRIMARY KEY CLUSTERED 
(
	[puma] ASC
))
GO

-- ACS Validation --
CREATE TABLE [acs_validation](
	[puma] [int] NULL,
	[hhs] [int] NULL,
	[famHhs] [int] NULL,
	[nonFamHhs] [int] NULL,
	[hh1Person] [int] NULL,
	[hh2Persons] [int] NULL,
	[hh3Persons] [int] NULL,
	[hh4plusPersons] [int] NULL,
	[nonFamHhsbyHolderAge15to64] [int] NULL,
	[nonFamHhsbyHolderAge65Plus] [int] NULL,
	[famHhsOwnChildUnder18] [int] NULL,
	[famHhsNoOwnChildUnder18] [int] NULL,
	[famHhsAge18UnderPres] [int] NULL,
	[famHhsNoAge18UnderPres] [int] NULL,
	[nonFamHhsAge18UnderPres] [int] NULL,
	[nonFamHhsNoAge18UnderPres] [int] NULL,
	[hhs1PlusAge65Plus] [int] NULL,
	[hhsNoAge65Plus] [int] NULL,
	[hhs1PersonAge65Plus] [int] NULL,
	[fam2PlusPersons1PlusAge65Plus] [int] NULL,
	[nonFam2PlusPersons1PlusAge65Plus] [int] NULL,
	[hhs1PersonAge65Under] [int] NULL,
	[fam2PlusPersonsNoAge65Plus] [int] NULL,
	[nonFam2PlusPersonsNoAge65Plus] [int] NULL,
	[numOfWorkers0] [int] NULL,
	[numOfWorkers1] [int] NULL,
	[numOfWorkers2] [int] NULL,
	[numOfWorkers3Plus] [int] NULL,
	[incomeUnder30k] [int] NULL,
	[income30kto60k] [int] NULL,
	[income60kto100k] [int] NULL,
	[income100kto150k] [int] NULL,
	[income150kPlus] [int] NULL,
	[incomeUnder10k] [int] NULL,
	[income10kto20k] [int] NULL,
	[income20kto30k] [int] NULL,
	[income30kto40k] [int] NULL,
	[income40kto50k] [int] NULL,
	[income50kto60k] [int] NULL,
	[income60kto75k] [int] NULL,
	[income75kto100k] [int] NULL,
	[income60kUnder] [int] NULL,
	[singleFamily] [int] NULL,
	[multiUnit] [int] NULL,
	[mobileHome] [int] NULL,
	[numOfOwned] [int] NULL,
	[numOfRent] [int] NULL,
	[numPersonsHh] [int] NULL,
	[numPersonsFamilyHh] [int] NULL,
	[numPersonsNonFamilyHh] [int] NULL,
	[numOfPersonsAsHolders] [int] NULL,
	[numOfPersonsAsSpouse] [int] NULL,
	[numOfPersonsAsChild] [int] NULL,
	[numOfPersonsAsOthers] [int] NULL,
	[numOfPersonsNotRelatedToHolder] [int] NULL,
	[numOfPersonsGQIncluded] [int] NULL,
	[male] [int] NULL,
	[female] [int] NULL,
	[age18to24] [int] NULL,
	[age25to34] [int] NULL,
	[age35to49] [int] NULL,
	[age50to64] [int] NULL,
	[age65to79] [int] NULL,
	[age80plus] [int] NULL,
	[age17AndUnder] [int] NULL,
	[age18to64] [int] NULL,
	[age65plus] [int] NULL,
	[hispanic] [int] NULL,
	[whiteAlone] [int] NULL,
	[blackAlone] [int] NULL,
	[indiAlaskaAlone] [int] NULL,
	[asianAlone] [int] NULL,
	[islanderAlone] [int] NULL,
	[otherRaceAloneOrTwoPlusRaces] [int] NULL,
	[worked27plusWks] [int] NULL,
	[worked27plusWks35PlusHrsPerWk] [int] NULL,
	[worked27plusWks15to34HrsPerWk] [int] NULL,
	[worked27plusWks1to14HrsPerWk] [int] NULL,
	[age16Plus] [int] NULL,
	[civilianEmployed] [int] NULL,
	[militaryEmployed] [int] NULL,
	[unemployed] [int] NULL,
	[notInLaborForce] [int] NULL,
	[age3Plus] [int] NULL,
	[enrolledNurseyToGrade12] [int] NULL,
	[enrollPostSecondary] [int] NULL
)
GO