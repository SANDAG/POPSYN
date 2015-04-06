/*

Creates all necessary schemas and tables for popsyn.

Author: Gregor Schroeder

*/




/*****************************************************************************/
-- Create schemas if they do not already exist


IF NOT EXISTS (SELECT schema_name FROM information_schema.schemata WHERE schema_name='ref')
BEGIN
EXEC ('CREATE SCHEMA [ref]')
END

IF NOT EXISTS (SELECT schema_name FROM information_schema.schemata WHERE schema_name='popsyn')
BEGIN
EXEC ('CREATE SCHEMA [popsyn]')
END


IF NOT EXISTS (SELECT schema_name FROM information_schema.schemata WHERE schema_name='popsyn_staging')
BEGIN
EXEC ('CREATE SCHEMA [popsyn_staging]')
END


IF NOT EXISTS (SELECT schema_name FROM information_schema.schemata WHERE schema_name='popsyn_input')
BEGIN
EXEC ('CREATE SCHEMA [popsyn_input]')
END




/*****************************************************************************/
-- Create popsyn specific ref tables if they do not already exist
SET NOCOUNT ON;


-- lu_version
IF OBJECT_ID('ref.lu_version','U') IS NULL
BEGIN

CREATE TABLE 
	[ref].[lu_version] (
		[lu_version_id] [smallint] IDENTITY(1,1) NOT NULL,
		[lu_major_version] [tinyint] NOT NULL,
		[lu_minor_version] [tinyint] NOT NULL,
		[lu_scenario_id] [smallint] NOT NULL,
		[increment] [smallint] NOT NULL,
		[minor_geography_type_id] [tinyint] NOT NULL,
		[middle_geography_type_id] [tinyint] NOT NULL,
		[major_geography_type_id] [tinyint] NOT NULL,
		[lu_scenario_desc] [nvarchar](50) NOT NULL,
		[popsyn_targets_created] [smalldatetime] NULL,
		[mgra_based_input_file_created] [smalldatetime] NULL,
		CONSTRAINT [pk_luversion] PRIMARY KEY CLUSTERED ([lu_version_id]),
		CONSTRAINT [ixuq_luversion] UNIQUE ([lu_major_version],[lu_minor_version],[lu_scenario_id],[increment]) WITH (DATA_COMPRESSION = PAGE)
	) 
WITH 
	(DATA_COMPRESSION = PAGE);

-- Filling with Series_13 final scenarios by default
INSERT INTO
	[ref].[lu_version]
VALUES
	(13, 1, 0, 2012, 90, 34, 64, 'Final SR13', NULL, NULL),
	(13, 1, 0, 2020, 90, 34, 64, 'Final SR13', NULL, NULL),
	(13, 1, 0, 2025, 90, 34, 64, 'Final SR13', NULL, NULL),
	(13, 1, 0, 2030, 90, 34, 64, 'Final SR13', NULL, NULL),
	(13, 1, 0, 2035, 90, 34, 64, 'Final SR13', NULL, NULL),
	(13, 1, 0, 2040, 90, 34, 64, 'Final SR13', NULL, NULL),
	(13, 1, 0, 2045, 90, 34, 64, 'Final SR13', NULL, NULL),
	(13, 1, 0, 2050, 90, 34, 64, 'Final SR13', NULL, NULL)
END


-- gq_type
IF OBJECT_ID('ref.gq_type','U') IS NULL
BEGIN

CREATE TABLE 
	[ref].[gq_type] (
		[gq_type_id] [tinyint] NOT NULL,
		[gq_type_desc] [nvarchar](50) NOT NULL,
		CONSTRAINT [pk_gqtype] PRIMARY KEY CLUSTERED ([gq_type_id])
	)
WITH 
	(DATA_COMPRESSION = PAGE);

INSERT INTO
	[ref].[gq_type]
VALUES
	(0, 'Household')
	,(1, 'Non Institutional Group Quarters - University')
	,(2, 'Non Institutional Group Quarters - Military')
	,(3, 'Non Institutional Group Quarters - Other')
;
	
END


-- hh_type
IF OBJECT_ID('ref.hh_type','U') IS NULL
BEGIN

CREATE TABLE 
	[ref].[hh_type] (
		[hh_type_id] [tinyint] NOT NULL,
		[hh_type_desc] [nvarchar](50) NOT NULL,
		CONSTRAINT [pk_hhtype] PRIMARY KEY CLUSTERED ([hh_type_id])
	)
WITH 
	(DATA_COMPRESSION = PAGE);

INSERT INTO
	[ref].[hh_type]
VALUES
	(0, 'Other')
	,(1, 'Households - Single Family')
	,(2, 'Households - Multi Family')
	,(3, 'Households - Mobile Home')
;
	
END


-- popsyn_data_source
IF OBJECT_ID('ref.popsyn_data_source','U') IS NULL
BEGIN

CREATE TABLE 
	[ref].[popsyn_data_source] (
		[popsyn_data_source_id] [tinyint] NOT NULL,
		[popsyn_data_source_desc] [nvarchar](50) NOT NULL,
		[popsyn_data_source_inputs_created] [smalldatetime] NULL,
		CONSTRAINT [pk_popsyndatasource] PRIMARY KEY CLUSTERED ([popsyn_data_source_id])
	)
WITH 
	(DATA_COMPRESSION = PAGE);

-- Filling with ACS PUMS 2011 5 yr. by default
INSERT INTO
	[ref].[popsyn_data_source]
VALUES
	(1, 'ACS PUMS 2011 5 yr.', NULL)
;
	
END


-- popsyn_race
IF OBJECT_ID('ref.popsyn_race','U') IS NULL
BEGIN

CREATE TABLE 
	[ref].[popsyn_race] (
		[popsyn_race_id] [tinyint] NOT NULL,
		[popsyn_race_desc] [nvarchar](100) NOT NULL,
		CONSTRAINT [pk_popsynrace] PRIMARY KEY CLUSTERED ([popsyn_race_id])
	)
WITH 
	(DATA_COMPRESSION = PAGE);

INSERT INTO
	[ref].[popsyn_race]
VALUES
	(1, 'Ethnicity - Hispanic')
	,(2, 'Race - Non Hispanic White')
	,(3, 'Race - Non Hispanic Black')
	,(4, 'Race - Non Hispanic Pacific Islander, Other, Indian, 2 or more races')
	,(5, 'Race - Non Hispanic Asian')
;
	
END


-- popsyn_run
IF OBJECT_ID('ref.popsyn_run','U') IS NULL
BEGIN

CREATE TABLE 
	[ref].[popsyn_run] (
		[popsyn_run_id] [smallint] IDENTITY(1,1) NOT NULL,
		[popsyn_data_source_id] [tinyint] NOT NULL,
		[lu_version_id] [smallint] NOT NULL,
		[user_name] [nvarchar](100) NOT NULL,
		[start_time] [smalldatetime] NOT NULL,
		[end_time] [smalldatetime] NULL,
		[validated] [nvarchar](100) NULL,
		[puma] [smallint] NULL,
		[taz] [smallint] NULL,
		CONSTRAINT [pk_popsynrun] PRIMARY KEY CLUSTERED ([popsyn_run_id]),
		CONSTRAINT [fk_popsynrun_popsyndatasource] FOREIGN KEY ([popsyn_data_source_id]) REFERENCES [ref].[popsyn_data_source] ([popsyn_data_source_id]),
		CONSTRAINT [fk_popsynrun_luversion] FOREIGN KEY ([lu_version_id]) REFERENCES [ref].[lu_version] ([lu_version_id])
	) 
WITH 
	(DATA_COMPRESSION = PAGE);
	
END


-- target_category
IF OBJECT_ID('ref.target_category','U') IS NULL
BEGIN

CREATE TABLE 
	[ref].[target_category] (
		[target_category_id] [smallint] IDENTITY(1,1) NOT NULL,
		[target_category_col_nm] [nvarchar](50) NOT NULL,
		[target_category_desc] [nvarchar](100) NOT NULL,
		CONSTRAINT [pk_targetcategory] PRIMARY KEY CLUSTERED ([target_category_id]),
		CONSTRAINT [ixuq_targetcategory] UNIQUE ([target_category_col_nm]) WITH (DATA_COMPRESSION = PAGE) 
	) 
WITH 
	(DATA_COMPRESSION = PAGE);

-- Filling with default categories to balance on
INSERT INTO
	[ref].[target_category]
VALUES
	('hh_sf', 'Households - Single Family'),
	('hh_mf', 'Households - Multi Family'),
	('hh_mh', 'Households - Mobile Home'),
	('hh', 'Households'),
	('gq_noninst', 'Non Institutional Group Quarters - Total'),
	('gq_civ_college', 'Non Institutional Group Quarters - University'),
	('gq_mil', 'Non Institutional Group Quarters - Military'),
	('hhwoc', 'Household Children - No Children'),
	('hhwc', 'Household Children - Children'),
	('hhworkers0', 'Household Workers - Zero Workers'),
	('hhworkers1', 'Household Workers - One Worker'),
	('hhworkers2', 'Household Workers - Two Workers'),
	('hhworkers3', 'Household Workers - Three Workers'),
	('hh_income_cat_1', 'Household Income - <30k'),
	('hh_income_cat_2', 'Household Income - 30k-60k'),
	('hh_income_cat_3', 'Household Income - 60k-100k'),
	('hh_income_cat_4', 'Household Income - 100k-150k'),
	('hh_income_cat_5', 'Household Income - >=150k'),
	('pop_non_gq', 'Population - Total Minus Group Quarters'),
	('male', 'Gender - Male'),
	('female', 'Gender - Female'),
	('age_0_17', 'Age - 0-17'),
	('age_18_24', 'Age - 18-24'),
	('age_25_34', 'Age - 25-34'),
	('age_35_49', 'Age - 35-49'),
	('age_50_64', 'Age - 50-64'),
	('age_65_79', 'Age - 65-79'),
	('age_80plus', 'Age - 80+'),
	('hisp', 'Ethnicity - Hispanic'),
	('nhw', 'Race - Non Hispanic White'),
	('nhb', 'Race - Non Hispanic Black'),
	('nho_popsyn', 'Race - Non Hispanic Pacific Islander, Other, Indian, 2 or more races'),
	('nha', 'Race - Non Hispanic Asian')
END




/*****************************************************************************/
-- Create popsyn_input tables


-- control_targets
IF OBJECT_ID('popsyn_input.control_targets','U') IS NULL
BEGIN

CREATE TABLE 
	[popsyn_input].[control_targets] (
		[lu_version_id] [smallint] NOT NULL,
		[control_targets_id] [int] IDENTITY(1,1) NOT NULL,
		[target_category_id] [smallint] NOT NULL,
		[geography_zone_id] [int] NOT NULL,
		[value] [int] NOT NULL,
		CONSTRAINT [pk_controltargets] PRIMARY KEY CLUSTERED ([lu_version_id],[control_targets_id]),
		CONSTRAINT [fk_controltargets_luversion] FOREIGN KEY ([lu_version_id]) REFERENCES [ref].[lu_version] ([lu_version_id]),
		CONSTRAINT [fk_controltargets_targetcategory] FOREIGN KEY ([target_category_id]) REFERENCES [ref].[target_category] ([target_category_id])
	) 
WITH 
	(DATA_COMPRESSION = PAGE);
	
END

CREATE UNIQUE NONCLUSTERED INDEX
	[ixuq_controltargets]
ON
	[popsyn_input].[control_targets] (
		[lu_version_id]
		,[target_category_id]
		,[geography_zone_id]
		)
INCLUDE (
	[value]
	)
WITH
	(DATA_COMPRESSION = PAGE);

CREATE NONCLUSTERED INDEX
	[ix_controltargets_geozone]
ON
	[popsyn_input].[control_targets] (
		[lu_version_id]
		,[geography_zone_id]
		)
WITH
	(DATA_COMPRESSION = PAGE);


-- hh
IF OBJECT_ID('popsyn_input.hh','U') IS NULL
BEGIN

CREATE TABLE 
	[popsyn_input].[hh] (
		[popsyn_data_source_id] [tinyint] NOT NULL,
		[hh_id] [int] IDENTITY(1,1) NOT NULL,
		[region] [smallint] NOT NULL,
		[puma] [smallint] NOT NULL,
		[wgtp] [smallint] NOT NULL,
		[serialno] [bigint] NOT NULL,
		[np] [smallint] NOT NULL,
		[hh_income_adj] [int] NULL,
		[workers] [tinyint] NOT NULL,
		[gq_type_id] [tinyint] NOT NULL,
		[hh_type_id] [tinyint] NULL,
		[hh_child] [tinyint] NULL,
		CONSTRAINT [pk_hh] PRIMARY KEY CLUSTERED ([popsyn_data_source_id],[hh_id]),
		CONSTRAINT [ixuq_hh] UNIQUE ([popsyn_data_source_id],[serialno]) WITH (DATA_COMPRESSION = PAGE),
		CONSTRAINT [fk_hh_popsyndatasource] FOREIGN KEY ([popsyn_data_source_id]) REFERENCES [ref].[popsyn_data_source] ([popsyn_data_source_id]),
		CONSTRAINT [fk_hh_hhtype] FOREIGN KEY ([hh_type_id]) REFERENCES [ref].[hh_type] ([hh_type_id]),
		CONSTRAINT [fk_hh_gqtype] FOREIGN KEY ([gq_type_id]) REFERENCES [ref].[gq_type] ([gq_type_id])
	)
WITH 
	(DATA_COMPRESSION = PAGE);
	
END

CREATE NONCLUSTERED INDEX
	[ix_hh_puma]
ON
	[popsyn_input].[hh] (
		[popsyn_data_source_id]
		,[puma]
		)
WITH
	(DATA_COMPRESSION = PAGE);

CREATE NONCLUSTERED INDEX
	[ix_hh_hhtype]
ON
	[popsyn_input].[hh] (
		[popsyn_data_source_id]
		,[hh_type_id]
		)
WITH
	(DATA_COMPRESSION = PAGE);

CREATE NONCLUSTERED INDEX
	[ix_hh_gqtype]
ON
	[popsyn_input].[hh] (
		[popsyn_data_source_id]
		,[gq_type_id]
		)
WITH
	(DATA_COMPRESSION = PAGE);


-- person
IF OBJECT_ID('popsyn_input.person','U') IS NULL
BEGIN

CREATE TABLE 
	[popsyn_input].[person] (
		[popsyn_data_source_id] [tinyint] NOT NULL,
		[person_id] [int] IDENTITY(1,1) NOT NULL,
		[hh_id] [int] NOT NULL,
		[puma] [smallint] NOT NULL,
		[wgtp] [smallint] NOT NULL,
		[serialno] [bigint] NOT NULL,
		[sporder] [tinyint] NOT NULL,
		[agep] [tinyint] NOT NULL,
		[sex] [tinyint] NOT NULL,
		[wkhp] [tinyint] NOT NULL,
		[esr] [tinyint] NOT NULL,
		[schg] [tinyint] NOT NULL,
		[employed] [tinyint] NOT NULL,
		[wkw] [tinyint] NOT NULL,
		[mil] [tinyint] NOT NULL,
		[schl] [tinyint] NOT NULL,
		[indp02] [nvarchar](50) NOT NULL,
		[indp07] [nvarchar](50) NOT NULL,
		[occp02] [nvarchar](50) NOT NULL,
		[occp10] [nvarchar](50) NOT NULL,
		[socp00] [nvarchar](50) NOT NULL,
		[socp10] [nvarchar](50) NOT NULL,
		[gq_type_id] [tinyint] NOT NULL,
		[soc] [tinyint] NOT NULL,
		[occp] [smallint] NOT NULL,
		[hisp] [tinyint] NOT NULL,
		[rac1p] [tinyint] NOT NULL,
		[popsyn_race_id] [tinyint] NOT NULL,
		CONSTRAINT [pk_person] PRIMARY KEY CLUSTERED ([popsyn_data_source_id],[person_id]),
		CONSTRAINT [ixuq_person] UNIQUE ([popsyn_data_source_id],[hh_id],[sporder]) WITH (DATA_COMPRESSION = PAGE),
		CONSTRAINT [fk_person_popsyndatasource] FOREIGN KEY ([popsyn_data_source_id]) REFERENCES [ref].[popsyn_data_source] ([popsyn_data_source_id]),
		CONSTRAINT [fk_person_hh] FOREIGN KEY ([popsyn_data_source_id],[hh_id]) REFERENCES [popsyn_input].[hh] ([popsyn_data_source_id],[hh_id]),
		CONSTRAINT [fk_person_popsynrace] FOREIGN KEY ([popsyn_race_id]) REFERENCES [ref].[popsyn_race] ([popsyn_race_id])
	)
WITH 
	(DATA_COMPRESSION = PAGE);
	
END

CREATE NONCLUSTERED INDEX
	[ix_person_puma]
ON
	[popsyn_input].[person] (
		[popsyn_data_source_id]
		,[puma]
		)
WITH
	(DATA_COMPRESSION = PAGE);

CREATE NONCLUSTERED INDEX
	[ix_person_popsynrace]
ON
	[popsyn_input].[person] (
		[popsyn_data_source_id]
		,[popsyn_race_id]
		)
WITH
	(DATA_COMPRESSION = PAGE);


/*****************************************************************************/
-- Create popsyn tables


-- synpop_hh
IF OBJECT_ID('popsyn.synpop_hh','U') IS NULL
BEGIN

CREATE TABLE 
	[popsyn].[synpop_hh] (
		[popsyn_run_id] [smallint] NOT NULL,
		[synpop_hh_id] [int] NOT NULL,
		[hh_id] [int] NOT NULL,
		[final_weight] [int] NOT NULL,
		[mgra] [smallint] NOT NULL,
		CONSTRAINT [pk_synpophh] PRIMARY KEY CLUSTERED ([popsyn_run_id],[synpop_hh_id]),
		CONSTRAINT [fk_synpophh_popsynrun] FOREIGN KEY ([popsyn_run_id]) REFERENCES [ref].[popsyn_run] ([popsyn_run_id])
	) 
WITH 
	(DATA_COMPRESSION = PAGE);

CREATE NONCLUSTERED INDEX
	[ix_synpophh_hhid]
ON
	[popsyn].[synpop_hh] (
		[popsyn_run_id]
		,[hh_id]
		)
WITH
	(DATA_COMPRESSION = PAGE);

CREATE NONCLUSTERED INDEX
	[ix_synpophh_mgra]
ON
	[popsyn].[synpop_hh] (
		[popsyn_run_id]
		,[mgra]
		)
WITH
	(DATA_COMPRESSION = PAGE);

END


-- synpop_person
IF OBJECT_ID('popsyn.synpop_person','U') IS NULL
BEGIN

CREATE TABLE 
	[popsyn].[synpop_person] (
		[popsyn_run_id] [smallint] NOT NULL,
		[synpop_person_id] [int] IDENTITY (1,1) NOT NULL,
		[synpop_hh_id] [int] NOT NULL,
		[person_id] [int] NOT NULL,
		CONSTRAINT [pk_synpopperson] PRIMARY KEY CLUSTERED ([popsyn_run_id],[synpop_person_id]),
		CONSTRAINT [fk_synpopperson_popsynrun] FOREIGN KEY ([popsyn_run_id]) REFERENCES [ref].[popsyn_run] ([popsyn_run_id]),
		CONSTRAINT [fk_synpopperson_synpophh] FOREIGN KEY ([popsyn_run_id],[synpop_hh_id]) REFERENCES [popsyn].[synpop_hh] ([popsyn_run_id],[synpop_hh_id])
	)
WITH 
	(DATA_COMPRESSION = PAGE);

CREATE NONCLUSTERED INDEX
	[ix_synpopperson_personid]
ON
	[popsyn].[synpop_person] (
		[popsyn_run_id]
		,[person_id]
		)
WITH
	(DATA_COMPRESSION = PAGE);

CREATE NONCLUSTERED INDEX
	[ix_synpopperson_hhid]
ON
	[popsyn].[synpop_person] (
		[popsyn_run_id]
		,[synpop_hh_id]
		)
WITH
	(DATA_COMPRESSION = PAGE);
	
END
GO