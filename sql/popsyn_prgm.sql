/*

Creates all necessary views and programming objects for popsyn database.

Author: Gregor Schroeder

*/




/*****************************************************************************/
-- Views


-- Create [popsyn_input].[vi_lu_data] to hold all land use data used to create control targets
IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[popsyn_input].[vi_lu_data]'))
DROP VIEW [popsyn_input].[vi_lu_data]
GO

CREATE VIEW [popsyn_input].[vi_lu_data] AS
-- Series 13 Final forecast data, 13.1
-- Requires Series 13 Final scenarios to be manually inserted into the [ref].[lu_version] table for data to appear 
-- These must have [ref].[lu_version].[lu_major_version] = 13 and [ref].[lu_version].[lu_minor_version] = 1
SELECT
	[lu_version_id]
	,[vi_xref_geography_mgra_13].[mgra_13] AS [mgra]
	,[vi_xref_geography_mgra_13].[taz_13] AS [taz]
	,[puma_2000] AS [puma] -- must use taz-puma lookup instead of mgra-puma lookup otherwise a taz will belong to multiple pumas
	,[vi_xref_geography_mgra_13].[region_2004] AS [region]
	,[mgrabase].[hh]
	,[hh_sf]
	,[hh_mf]
	,[hh_mh]
	,[gq_civ_college] + [gq_civ_other] + [gq_mil] AS [gq_noninst]
	,[gq_civ_college]
	,[gq_mil]
	,[hhwoc]
	,[hhwc]
	,[hhworkers0]
	,[hhworkers1]
	,[hhworkers2]
	,[hhworkers3]
	,[i1] + [i2] AS [hh_income_cat_1]
	,[i3] + [i4] AS [hh_income_cat_2]
	,[i5] + [i6] AS [hh_income_cat_3]
	,[i7] + [i8] AS [hh_income_cat_4]
	,[i9] + [i10] AS [hh_income_cat_5]
	/* Following non-hh targets are given by landuse for gq AND hh combined,
		land use cannot separate them so best we can do is 0 them out if there
		are no households in the zone (only gq or nothing),
		popsyn wants household based numbers (no gq) for demographics */
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [pop] - ([gq_civ_college] + [gq_civ_other] + [gq_mil])
			ELSE 0
			END AS [pop_non_gq]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [male]
			ELSE 0
			END AS [male]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [female]
			ELSE 0
			END AS [female]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [age_0_17]
			ELSE 0
			END AS [age_0_17]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [age_18_24]
			ELSE 0
			END AS [age_18_24]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [age_25_34]
			ELSE 0
			END AS [age_25_34]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [age_35_49]
			ELSE 0
			END AS [age_35_49]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [age_50_64]
			ELSE 0
			END AS [age_50_64]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [age_65_79]
			ELSE 0
			END AS [age_65_79]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [age_80plus]
			ELSE 0
			END AS [age_80plus]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [hisp]
			ELSE 0
			END AS [hisp]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [nhw]
			ELSE 0
			END AS [nhw]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [nhb]
			ELSE 0
			END AS [nhb]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [nho_popsyn]
			ELSE 0
			END AS [nho_popsyn]
	,CASE	WHEN [mgrabase].[hh] > 0 THEN [nha]
			ELSE 0
			END AS [nha]
FROM
	[pila\SdgIntDb].[sr13_final].[dbo].[mgrabase]
INNER JOIN
	[pila\SdgIntDb].[sr13_final].[dbo].[pasef_HHdetail_mgra]
ON 
	[mgrabase].[scenario] = [pasef_HHdetail_mgra].[scenario] 
	AND [mgrabase].[increment] = [pasef_HHdetail_mgra].[year]
	AND [mgrabase].[mgra] = [pasef_HHdetail_mgra].[mgra] 
LEFT OUTER JOIN (
SELECT 
	[scenario]
    ,[year]
    ,[mgra]
    ,SUM(CASE WHEN [sex] = 1 THEN [pop] ELSE 0 END) AS [male]
	,SUM(CASE WHEN [sex] = 2 THEN [pop] ELSE 0 END) AS [female]
	,SUM(CASE WHEN [ethnicity] = 1 THEN [pop] ELSE 0 END) AS [hisp]
	,SUM(CASE WHEN [ethnicity] = 2 THEN [pop] ELSE 0 END) AS [nhw]
	,SUM(CASE WHEN [ethnicity] = 3 THEN [pop] ELSE 0 END) AS [nhb]
	,SUM(CASE WHEN [ethnicity] IN (4, 6, 7, 8) THEN [pop] ELSE 0 END) AS [nho_popsyn]
	,SUM(CASE WHEN [ethnicity] = 5 THEN [pop] ELSE 0 END) AS [nha]
	,SUM(CASE WHEN [age] BETWEEN 0 AND 3 THEN [pop] ELSE 0 END) AS [age_0_17]
	,SUM(CASE WHEN [age] BETWEEN 4 AND 5 THEN [pop] ELSE 0 END) AS [age_18_24]
	,SUM(CASE WHEN [age] BETWEEN 6 AND 7 THEN [pop] ELSE 0 END) AS [age_25_34]
	,SUM(CASE WHEN [age] BETWEEN 8 AND 10 THEN [pop] ELSE 0 END) AS [age_35_49]
	,SUM(CASE WHEN [age] BETWEEN 11 AND 14 THEN [pop] ELSE 0 END) AS [age_50_64]
	,SUM(CASE WHEN [age] BETWEEN 15 AND 17 THEN [pop] ELSE 0 END) AS [age_65_79]
	,SUM(CASE WHEN [age] BETWEEN 18 AND 19 THEN [pop] ELSE 0 END) AS [age_80plus]
FROM 
	[pila\SdgIntDb].[sr13_final].[dbo].[pasef_mgra]
WHERE 
	[ethnicity] > 0 -- ethnicity = 0 is a total, do not include
GROUP BY
	[scenario]
    ,[year]
    ,[mgra]
	) pasef_mgra_agg
ON
	[mgrabase].[scenario] = pasef_mgra_agg.[scenario] 
	AND [mgrabase].[increment] = pasef_mgra_agg.[year]
	AND [mgrabase].[mgra] = pasef_mgra_agg.[mgra]
INNER JOIN
	[data_cafe].[ref].[vi_xref_geography_mgra_13] -- get series 13 mgra to series 13 taz
ON
	[mgrabase].[mgra] = [vi_xref_geography_mgra_13].[mgra_13]
INNER JOIN
	[data_cafe].[ref].[vi_xref_geography_taz_13] -- get series 13 taz to puma year 2000
ON
	[vi_xref_geography_mgra_13].[taz_13] = [vi_xref_geography_taz_13].[taz_13]
INNER JOIN
	[ref].[lu_version] -- this join means a scenario needs to be in this table to appear
ON
	[mgrabase].[scenario] = [lu_version].[lu_scenario_id]
	AND [mgrabase].[increment] = [lu_version].[increment]
	AND [lu_version].[lu_major_version] = 13 -- hardcoded series 13 final major version
	AND [lu_version].[lu_minor_version] = 1 -- hardcoded series 13 final minor version
GO

-- Add metadata for [popsyn_input].[vi_lu_data]
EXECUTE [db_meta].[add_xp] 'popsyn_input.vi_lu_data', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn_input.vi_lu_data', 'ms_description', 'view to hold all land use data used to create control targets'
GO




/*****************************************************************************/
-- Stored Procedures


-- Create [popsyn_input].[generate_control_targets] to populate [popsyn_input].[control_targets] table from [popsyn_input].[lu_data]
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[popsyn_input].[generate_control_targets]') AND type in (N'P', N'PC'))
DROP PROCEDURE [popsyn_input].[generate_control_targets]
GO

CREATE PROCEDURE [popsyn_input].[generate_control_targets]
	@lu_version_id tinyint -- Need to specify [lu_version_id] located in [ref].[lu_version]
AS

BEGIN TRANSACTION generate_control_targets WITH mark

-- See if data even exists
IF NOT EXISTS (SELECT * FROM [ref].[lu_version] WHERE [lu_version_id] = @lu_version_id)
BEGIN
	print 'ERROR: specified [lu_version_id] does not exist in [ref].[lu_version]'
	RETURN
END
IF NOT EXISTS (SELECT * FROM [popsyn_input].[vi_lu_data] WHERE [lu_version_id] = @lu_version_id)
BEGIN
	print 'ERROR: specified [lu_version_id] does not exist in [popsyn_input].[vi_lu_data]'
	RETURN
END

DBCC CHECKIDENT ('popsyn_input.control_targets',RESEED, 0)
-- Insert mgra targets
INSERT INTO [popsyn_input].[control_targets]
SELECT
	tt.[lu_version_id]
	,[target_category_id]
    ,[geography_zone_id]
	,[value]
FROM
	[popsyn_input].[vi_lu_data]
UNPIVOT
(
	[value]
	FOR [target_col_nm] IN (
	  [hh]
	  ,[hh_sf]
      ,[hh_mf]
      ,[hh_mh]
      ,[gq_noninst]
      ,[gq_civ_college]
      ,[gq_mil]
	  ,[pop_non_gq]
	  )
) tt
INNER JOIN
	[ref].[target_category]
ON
	tt.[target_col_nm] = [target_category].[target_category_col_nm]
INNER JOIN
	[data_cafe].[ref].[geography_zone]
ON
	tt.[mgra] = [geography_zone].[zone]
INNER JOIN
	[ref].[lu_version]
ON
	tt.[lu_version_id] = [lu_version].[lu_version_id]
WHERE
	[geography_type_id] = [minor_geography_type_id] -- ensure geography join is on the given mgra geography type
	AND tt.[lu_version_id] = @lu_version_id

IF @@ERROR <> 0
BEGIN
	ROLLBACK TRANSACTION
	RETURN
END


PRINT 'mgra targets inserted for lu_version_id = ' + CAST(@lu_version_id AS nvarchar)


-- Insert taz targets
INSERT INTO [popsyn_input].[control_targets]
SELECT
	tt2.[lu_version_id]
	,[target_category_id]
    ,[geography_zone_id]
	,[value]
FROM (
	SELECT
		[lu_version_id]
		,[taz]
        ,SUM([hhwoc]) AS [hhwoc]
        ,SUM([hhwc]) AS [hhwc]
        ,SUM([hhworkers0]) AS [hhworkers0]
        ,SUM([hhworkers1]) AS [hhworkers1]
        ,SUM([hhworkers2]) AS [hhworkers2]
        ,SUM([hhworkers3]) AS [hhworkers3]
        ,SUM([hh_income_cat_1]) AS [hh_income_cat_1]
        ,SUM([hh_income_cat_2]) AS [hh_income_cat_2]
        ,SUM([hh_income_cat_3]) AS [hh_income_cat_3]
        ,SUM([hh_income_cat_4]) AS [hh_income_cat_4]
        ,SUM([hh_income_cat_5]) AS [hh_income_cat_5]
        ,SUM([male]) AS [male]
        ,SUM([female]) AS [female]
        ,SUM([age_0_17]) AS [age_0_17]
        ,SUM([age_18_24]) AS [age_18_24]
        ,SUM([age_25_34]) AS [age_25_34]
        ,SUM([age_35_49]) AS [age_35_49]
        ,SUM([age_50_64]) AS [age_50_64]
        ,SUM([age_65_79]) AS [age_65_79]
        ,SUM([age_80plus]) AS [age_80plus]
		,SUM([hisp]) AS [hisp]
		,SUM([nhw]) AS [nhw]
		,SUM([nhb]) AS [nhb]
		,SUM([nho_popsyn]) AS [nho_popsyn]
		,SUM([nha]) AS [nha]
	FROM
		[popsyn_input].[vi_lu_data]
	WHERE
		[lu_version_id] = @lu_version_id
	GROUP BY
		[lu_version_id]
		,[taz]
	) tt1
UNPIVOT
(
	[value]
	FOR [target_col_nm] IN (
      [hhwoc]
      ,[hhwc]
      ,[hhworkers0]
      ,[hhworkers1]
      ,[hhworkers2]
      ,[hhworkers3]
      ,[hh_income_cat_1]
      ,[hh_income_cat_2]
      ,[hh_income_cat_3]
      ,[hh_income_cat_4]
      ,[hh_income_cat_5]
      ,[male]
      ,[female]
      ,[age_0_17]
      ,[age_18_24]
      ,[age_25_34]
      ,[age_35_49]
      ,[age_50_64]
      ,[age_65_79]
      ,[age_80plus]
	  ,[hisp]
	  ,[nhw]
	  ,[nhb]
	  ,[nho_popsyn]
	  ,[nha]
	  )
) tt2
INNER JOIN
	[ref].[target_category]
ON
	tt2.[target_col_nm] = [target_category].[target_category_col_nm]
INNER JOIN
	[data_cafe].[ref].[geography_zone]
ON
	tt2.[taz] = [geography_zone].[zone]
INNER JOIN
	[ref].[lu_version]
ON
	tt2.[lu_version_id] = [lu_version].[lu_version_id]
WHERE
	[geography_type_id] = [middle_geography_type_id] -- ensure geography join is on the given taz geography type

IF @@ERROR <> 0
BEGIN
	ROLLBACK TRANSACTION
	RETURN
END

PRINT 'taz targets inserted for lu_version_id = ' + CAST(@lu_version_id AS nvarchar)


-- Insert region targets
INSERT INTO [popsyn_input].[control_targets]
SELECT
	[lu_version_id]
	,[target_category_id]
    ,[geography_zone_id]
	,SUM([value]) AS [value]
FROM (
	SELECT
		[lu_version_id]
		,[region] -- San Diego Region
		,SUM([gq_noninst]) AS [gq_noninst]
		,SUM([pop_non_gq]) AS [pop_non_gq]
	FROM
		[popsyn_input].[vi_lu_data]
	WHERE
		[lu_version_id] = @lu_version_id
	GROUP BY
		[lu_version_id]
		,[region] -- San Diego Region
	) tt1
UNPIVOT
(
	[value]
	FOR [target_col_nm] IN (
	  [gq_noninst]
      ,[pop_non_gq]
	  )
) tt2
INNER JOIN
	[ref].[target_category]
ON
	tt2.[target_col_nm] = [target_category].[target_category_col_nm]
INNER JOIN
	[data_cafe].[ref].[geography_zone]
ON
	tt2.[region] = [geography_zone].[zone]
WHERE
	[geography_type_id] = 4 -- ensure geography join is on San Diego Region, hardcoded
GROUP BY
	[lu_version_id]
	,[target_category_id]
    ,[geography_zone_id]

IF @@ERROR <> 0
BEGIN
	ROLLBACK TRANSACTION
	RETURN
END

PRINT 'region targets inserted for lu_version_id = ' + CAST(@lu_version_id AS nvarchar)


-- Update date targets created
UPDATE
	[ref].[lu_version]
SET
	[popsyn_targets_created] = GETDATE()
WHERE
	[lu_version_id] = @lu_version_id


PRINT 'all targets inserted for lu_version_id = ' + CAST(@lu_version_id AS nvarchar)

IF @@ERROR <> 0
BEGIN
	ROLLBACK TRANSACTION
	RETURN
END

COMMIT TRANSACTION
GO


-- Add metadata for [popsyn_input].[generate_control_targets]
EXECUTE [db_meta].[add_xp] 'popsyn_input.generate_control_targets', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn_input.generate_control_targets', 'ms_description', 'stored procedure to generate popsyn control targets from forecast land use data'
GO




-- Create [popsyn_input].[insert_acs2011_5yr] to populate [popsyn_input].[hh] and [popsyn_input].[person] tables with ACS PUMS 2011 5 year data
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[popsyn_input].[insert_acs2011_5yr]') AND type in (N'P', N'PC'))
DROP PROCEDURE [popsyn_input].[insert_acs2011_5yr]
GO

CREATE PROCEDURE [popsyn_input].[insert_acs2011_5yr]
AS

BEGIN TRANSACTION insert_acs2011_5yr WITH mark

PRINT 'census acs 2011 pums 5 year input data hardcoded as [popsyn_data_source_id] = 1'

-- Populate households
DBCC CHECKIDENT ('popsyn_input.hh',RESEED, 0)
INSERT INTO [popsyn_input].[hh]
SELECT
	1 AS [popsyn_data_source_id]
	,1 AS [region] -- changed from acs pums value of 4 to reflect what is in our geography reference tables
	,[PUMA]
	,[WGTP]
	,[pums_hh_sd].[SERIALNO]
	,[NP]
	,CASE	WHEN [ADJINC]=1102938 THEN CAST((([HINCP]/1.0)*1.016787*1.05156) AS decimal(9,2))
			WHEN [ADJINC]=1063801 THEN CAST((([HINCP]/1.0)*1.018389*1.01265) AS decimal(9,2))
			WHEN [ADJINC]=1048026 THEN CAST((([HINCP]/1.0)*0.999480*1.01651) AS decimal(9,2))
			WHEN [ADJINC]=1039407 THEN CAST((([HINCP]/1.0)*1.007624*1.00000) AS decimal(9,2))
			WHEN [ADJINC]=1018237 THEN CAST((([HINCP]/1.0)*1.018237*0.96942) AS decimal(9,2))
			ELSE 999
			END AS [hh_income_adj] -- adjusted to 2010 dollars
	,[BLD]
	,CASE	WHEN [nwrkrs_esr] IS NULL THEN 0
			ELSE [nwrkrs_esr]
			END
	,[VEH]
	,[HHT]
	,0 AS [gq_type_id] -- Household
	,CASE	WHEN [BLD] IN (2,3) THEN '1'				-- Single-family
			WHEN [BLD] IN (4,5,6,7,8,9) THEN '2'		-- Multi-family
			WHEN [BLD] IN (1,10) THEN '3'				-- Mobile-home
			ELSE '0'
			END AS [hh_type_id]
	,CASE	WHEN [HUPAC] IN (4) THEN '0'				-- No children
			WHEN [HUPAC] IN (1,2,3) THEN '1'			-- 1 or more children
			ELSE 'NULL'
			END AS [hh_child]
FROM
	[pila\SdgIntDb].[census].[acs2011_5yr].[pums_hh_sd] -- 2011 ACS PUMS for san diego
--Setting number of workers in HH based on Employment Status Recode [ESR] attribute in PUMS Person File
LEFT OUTER JOIN ( 
	SELECT
		[SERIALNO]
		,COUNT(*) AS [nwrkrs_esr]
	FROM
		[pila\SdgIntDb].[census].[acs2011_5yr].[pums_pp_sd] -- 2011 ACS PUMS for san diego
	WHERE 
		[ESR] IN (1,2,4,5)
	GROUP BY 
		[SERIALNO]
	) AS hh_workers
ON
	[pums_hh_sd].[SERIALNO] = hh_workers.[SERIALNO]
WHERE
	[pums_hh_sd].[NP] > 0 -- Deleting vacant units
	AND [pums_hh_sd].[TYPE] = 1 -- Deleting gq units

IF @@ERROR <> 0
BEGIN
	ROLLBACK TRANSACTION
	RETURN
END

PRINT 'census acs 2011 pums 5 year input household data inserted into [popsyn_input].[hh]'


-- Populate persons
DBCC CHECKIDENT ('popsyn_input.person',RESEED, 0)
INSERT INTO [popsyn_input].[person]
SELECT 
	tt.[popsyn_data_source_id]
	,[hh_id]
    ,tt.[PUMA]
	,[hh].[WGTP] -- household weight
	,tt.[SERIALNO]
    ,[SPORDER]
    ,[AGEP]
	,[SEX]
	,[WKHP]
	,[ESR]
	,[SCHG]
	,[employed]
	,[WKW]
	,[MIL]
	,[SCHL]
	,[indp02]
	,[indp07]
	,[occp02]
	,[occp10]
	,[socp00]
	,[socp10]
	,[gq_type_id]
	,[soc]
	,CASE	WHEN soc IN (11,13,15,17,27,19,39) THEN '1'
			WHEN soc IN (21,23,25,29,31) THEN '2'
			WHEN soc IN (35,37) THEN '3'
			WHEN soc IN (41,43) THEN '4'
			WHEN soc IN (45,47,49) THEN '5'
			WHEN soc IN (51,53) THEN '6'
			WHEN soc IN (55) THEN '7'
			WHEN soc IN (33) THEN '8'
			ELSE '999'
			END AS [occp]
	,[HISP]
	,[RAC1P]
	,[popsyn_race_id] =	CASE	WHEN [HISP] > 1 THEN 1 -- hispanic
								WHEN [RAC1P] = 1 AND [HISP] <= 1 THEN 2 -- nhw
								WHEN [RAC1P] = 2 AND [HISP] <= 1 THEN 3 -- nhb
								WHEN [RAC1P] IN (3,4,5,7,8,9) AND [HISP] <= 1 THEN 4 -- nho_popsyn
								WHEN [RAC1P] = 6 AND [HISP] <= 1 THEN 5 -- nha
								ELSE 0
								END
FROM (
SELECT
	1 AS [popsyn_data_source_id]
    ,[PUMA]
	,[SERIALNO]
    ,[SPORDER]
    ,[AGEP]
	,[SEX]
	,[WKHP]
	,[ESR]
	,[SCHG]
	,CASE	WHEN [ESR] IN (1,2,4,5) THEN 1
			ELSE 0
			END AS [employed]
	,[WKW]
	,[MIL]
	,[SCHL]
	,[indp02]
	,[indp07]
	,[occp02]
	,[occp10]
	,[socp00]
	,[socp10]
	,CASE	WHEN [ESR] NOT IN (1,2,4,5) THEN '0'
			WHEN LEFT(LTRIM(RTRIM(socp00)),2) = 'N' OR LEFT(LTRIM(RTRIM(socp00)),2) = 'N.' THEN LEFT(LTRIM(RTRIM(socp10)),2)
			ELSE LEFT(LTRIM(RTRIM(socp00)),2)
			END AS [soc]
	,[HISP]
	,[RAC1P]
FROM 
	[pila\SdgIntDb].[census].[acs2011_5yr].[pums_pp_sd] -- 2011 ACS PUMS for san diego
) AS tt
INNER JOIN -- deletes vacant units and non-gq
	[popsyn_input].[hh]
ON
	tt.[popsyn_data_source_id] = [hh].[popsyn_data_source_id]
	AND tt.[SERIALNO] = [hh].[serialno]

IF @@ERROR <> 0
BEGIN
	ROLLBACK TRANSACTION
	RETURN
END

PRINT 'census acs 2011 pums 5 year input person data inserted into [popsyn_input].[person]'


-- Populate GQ households
INSERT INTO [popsyn_input].[hh]
SELECT
	1 AS [popsyn_data_source_id]
	,1 AS [region] -- changed from acs pums value of 4 to reflect what is in our geography reference tables
	,[PUMA]
	,[PWGTP] AS [wgtp] -- person weight
	,[pums_hh_sd].[SERIALNO]
	,[NP]
	,NULL AS [hh_income_adj] -- no income for gq households
	,[BLD]
	,CASE	WHEN [nwrkrs_esr] IS NULL THEN 0
			ELSE [nwrkrs_esr]
			END AS [workers]
	,[VEH]
	,[HHT]
	,CASE	WHEN [SCHG] IN (6,7) THEN 1
			WHEN [MIL] = 1 THEN 2
			ELSE 3
			END AS [gq_type_id]
	,NULL AS [hh_type_id]
	,NULL AS [hh_child]
FROM
	[pila\SdgIntDb].[census].[acs2011_5yr].[pums_hh_sd] -- 2011 ACS PUMS for san diego
--Setting number of workers in HH based on Employment Status Recode [ESR] attribute in PUMS Person File
LEFT OUTER JOIN ( 
	SELECT
		[SERIALNO]
		,MAX([SCHG]) AS [SCHG] -- should just be a single record due to GQ
		,MAX([MIL]) AS [MIL] -- should just be a single record due to GQ
		,MAX([PWGTP]) AS [PWGTP] -- should just be a single record due to GQ
		,SUM(CASE	WHEN [ESR] IN (1,2,4,5) THEN 1
					ELSE 0
					END) AS [nwrkrs_esr] -- should just be 1/0 due to GQ
	FROM
		[pila\SdgIntDb].[census].[acs2011_5yr].[pums_pp_sd] -- 2011 ACS PUMS for san diego
	GROUP BY -- in theory not necessary due to GQ
		[SERIALNO]
	) AS hh_workers
ON
	[pums_hh_sd].[SERIALNO] = hh_workers.[SERIALNO]
WHERE
	[NP] > 0
	AND [TYPE] = 3 -- Non-institutional group quarters only

IF @@ERROR <> 0
BEGIN
	ROLLBACK TRANSACTION
	RETURN
END

PRINT 'census acs 2011 pums 5 year input group quarter household data inserted into [popsyn_input].[hh]'

-- Populate GQ persons
INSERT INTO [popsyn_input].[person]
SELECT 
	tt.[popsyn_data_source_id]
	,[hh_id]
    ,tt.[PUMA]
	,[PWGTP] AS [WGTP] -- person weight as household weight
	,tt.[SERIALNO]
    ,[SPORDER]
    ,[AGEP]
	,[SEX]
	,[WKHP]
	,[ESR]
	,[SCHG]
	,[employed]
	,[WKW]
	,[MIL]
	,[SCHL]
	,[indp02]
	,[indp07]
	,[occp02]
	,[occp10]
	,[socp00]
	,[socp10]
	,[gq_type_id]
	,[soc]
	,CASE	WHEN soc IN (11,13,15,17,27,19,39) THEN '1'
			WHEN soc IN (21,23,25,29,31) THEN '2'
			WHEN soc IN (35,37) THEN '3'
			WHEN soc IN (41,43) THEN '4'
			WHEN soc IN (45,47,49) THEN '5'
			WHEN soc IN (51,53) THEN '6'
			WHEN soc IN (55) THEN '7'
			WHEN soc IN (33) THEN '8'
			ELSE '999'
			END AS [occp]
	,[HISP]
	,[RAC1P]
	,[popsyn_race_id] =	CASE	WHEN [HISP] > 1 THEN 1 -- hispanic
								WHEN [RAC1P] = 1 AND [HISP] <= 1 THEN 2 -- nhw
								WHEN [RAC1P] = 2 AND [HISP] <= 1 THEN 3 -- nhb
								WHEN [RAC1P] IN (3,4,5,7,8,9) AND [HISP] <= 1 THEN 4 -- nho_popsyn
								WHEN [RAC1P] = 6 AND [HISP] <= 1 THEN 5 -- nha
								ELSE 0
								END
FROM (
SELECT
	1 AS [popsyn_data_source_id]
    ,[PUMA]
	,[PWGTP]
	,[SERIALNO]
    ,[SPORDER]
    ,[AGEP]
	,[SEX]
	,[WKHP]
	,[ESR]
	,[SCHG]
	,CASE	WHEN [ESR] IN (1,2,4,5) THEN 1
			ELSE 0
			END AS [employed]
	,[WKW]
	,[MIL]
	,[SCHL]
	,[indp02]
	,[indp07]
	,[occp02]
	,[occp10]
	,[socp00]
	,[socp10]
	,CASE	WHEN [ESR] NOT IN (1,2,4,5) THEN '0'
			WHEN LEFT(LTRIM(RTRIM(socp00)),2) = 'N' OR LEFT(LTRIM(RTRIM(socp00)),2) = 'N.' THEN LEFT(LTRIM(RTRIM(socp10)),2)
			ELSE LEFT(LTRIM(RTRIM(socp00)),2)
			END AS [soc]
	,[HISP]
	,[RAC1P]
FROM 
	[pila\SdgIntDb].[census].[acs2011_5yr].[pums_pp_sd] -- 2011 ACS PUMS for san diego
) AS tt
INNER JOIN -- deletes vacant units, gq hh's now in there
	[popsyn_input].[hh]
ON
	tt.[popsyn_data_source_id] = [hh].[popsyn_data_source_id]
	AND tt.[SERIALNO] = [hh].[SERIALNO]
WHERE
	[NP] > 0
	AND [hh].[gq_type_id] > 0 -- non-institutional GQ

IF @@ERROR <> 0
BEGIN
	ROLLBACK TRANSACTION
	RETURN
END

PRINT 'census acs 2011 pums 5 year input group quarter person data inserted into [popsyn_input].[person]'

UPDATE
	[ref].[popsyn_data_source]
SET
	[popsyn_data_source_inputs_created] = GETDATE()
WHERE
	[popsyn_data_source_id] = 1

IF @@ERROR <> 0
BEGIN
	ROLLBACK TRANSACTION
	RETURN
END

COMMIT TRANSACTION
GO

-- Add metadata for [popsyn_input].[insert_acs2011_5yr]
EXECUTE [db_meta].[add_xp] 'popsyn_input.insert_acs2011_5yr', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn_input.insert_acs2011_5yr', 'ms_description', 'stored procedure to insert census pums acs 2011 5 year data into [popsyn_input].[hh] and [popsyn_input].[person] tables, hardcoded as [popsyn_data_source_id] = 1'
GO




-- Create [popsyn].[validator] to return validation results for a given popsyn run
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[popsyn].[validator]') AND type in (N'P', N'PC'))
DROP PROCEDURE [popsyn].[validator]
GO

CREATE PROCEDURE [popsyn].[validator]
	@popsyn_run_id smallint -- Need to specify [popsyn_run_id] located in [ref].[popsyn_run]
AS

-- only necessary to review mgra and taz targets as the region targets are ignored by popsyn since they are specified at mgra level
DECLARE @minor_geography_type_id smallint = (SELECT [minor_geography_type_id] FROM [ref].[lu_version] INNER JOIN [ref].[popsyn_run] ON [lu_version].[lu_version_id] = [popsyn_run].[lu_version_id] WHERE [popsyn_run_id] = @popsyn_run_id)
DECLARE @mid_geography_type_id smallint = (SELECT [middle_geography_type_id] FROM [ref].[lu_version] INNER JOIN [ref].[popsyn_run] ON [lu_version].[lu_version_id] = [popsyn_run].[lu_version_id] WHERE [popsyn_run_id] = @popsyn_run_id)


UPDATE [ref].[popsyn_run] SET [validated] = NULL WHERE [popsyn_run_id] = @popsyn_run_id 


IF((SELECT MAX([final_weight]) FROM [popsyn].[synpop_hh] WHERE [popsyn_run_id] = @popsyn_run_id) > 500)
	PRINT 'A large household weight (>500) was assigned by popsyn, targets and acs pums in large disagreement proceed with caution.'


IF((SELECT MAX([final_weight]) FROM [popsyn].[synpop_hh] WHERE [popsyn_run_id] = @popsyn_run_id) > (SELECT MAX([n]) FROM [ref].[expansion_numbers]))
BEGIN
	UPDATE [ref].[popsyn_run] SET [validated] = 'A household weight exceeds maximum n in ref.expansion_numbers, add to this table before using ouput.' WHERE [popsyn_run_id] = @popsyn_run_id
	PRINT 'A household weight assigned by popsyn exceeds largest value in ref.expansion_numbers table, add to this table before using output.'
END


CREATE TABLE
	#temp_results
(
	[popsyn_run_id] smallint NOT NULL
	,[target_category_col_nm] nvarchar(50) NOT NULL
	,[balancing_geography] nvarchar(50) NOT NULL
	,[n] int NOT NULL
	,[observed_region_total] int NOT NULL
	,[target_region_total] int NOT NULL
	,[diff_total] int NOT NULL
	,[diff_mean] decimal(14,4) NOT NULL
	,[diff_stdev] decimal(14,4) NOT NULL
	,[diff_max] int NOT NULL
	,[pct_diff_total] decimal(7,4) NOT NULL
)
INSERT INTO #temp_results
-- mgra targets
SELECT
	@popsyn_run_id AS [popsyn_run_id]
	,[target_category_col_nm]
	,'mgra' AS [balancing_geography]
	,COUNT(*) AS [n]
	,SUM(results_mgra.[value]) AS [observed_region_total]
	,SUM([control_targets].[value]) AS [target_region_total]
	,SUM(results_mgra.[value] - [control_targets].[value]) AS [diff_total]
	,ROUND(AVG(1.0 * results_mgra.[value] - [control_targets].[value]), 4) AS [diff_mean]
	,STDEV(results_mgra.[value] - [control_targets].[value]) AS [diff_stdev]
	,MAX(results_mgra.[value] - [control_targets].[value]) AS [diff_max]
	,ROUND(ISNULL(1.0 * SUM(results_mgra.[value] - [control_targets].[value]) / NULLIF(SUM([control_targets].[value]), 0), 0) * 100, 4) AS [pct_diff_total]
FROM 
	[popsyn_input].[control_targets]
INNER JOIN
	[popsyn].[synpop_target_category_results] (@popsyn_run_id, @minor_geography_type_id) AS results_mgra
ON
	[control_targets].[lu_version_id] = results_mgra.[lu_version_id]
	AND [control_targets].[target_category_id] = results_mgra.[target_category_id]
	AND [control_targets].[geography_zone_id] = results_mgra.[geography_zone_id]
INNER JOIN
	[ref].[target_category]
ON
	[control_targets].[target_category_id] = [target_category].[target_category_id]
WHERE
	[control_targets].[lu_version_id] = (SELECT [lu_version_id] FROM [ref].[popsyn_run] WHERE [popsyn_run_id] = @popsyn_run_id)
GROUP BY
	[control_targets].[lu_version_id]
	,[target_category_col_nm]

UNION ALL

-- series 13 taz targets
SELECT
	@popsyn_run_id AS [popsyn_run_id]
	,[target_category_col_nm]
	,'taz' AS balancing_geography
	,COUNT(*) AS [n]
	,SUM(results_taz.[value]) AS [observed_region_total]
	,SUM([control_targets].[value]) AS [target_region_total]
	,SUM(results_taz.[value] - [control_targets].[value]) AS [diff_total]
	,AVG(1.0 * results_taz.[value] - [control_targets].[value]) AS [diff_mean]
	,STDEV(results_taz.[value] - [control_targets].[value]) AS [diff_stdev]
	,MAX(results_taz.[value] - [control_targets].[value]) AS [diff_max]
	,ISNULL(1.0 * SUM(results_taz.[value] - [control_targets].[value]) / NULLIF(SUM([control_targets].[value]), 0), 0) * 100 AS [pct_diff_total]
FROM 
	[popsyn_input].[control_targets]
INNER JOIN
	[popsyn].[synpop_target_category_results] (@popsyn_run_id, @mid_geography_type_id) AS results_taz
ON
	[control_targets].[lu_version_id] = results_taz.[lu_version_id]
	AND [control_targets].[target_category_id] = results_taz.[target_category_id]
	AND [control_targets].[geography_zone_id] = results_taz.[geography_zone_id]
INNER JOIN
	[ref].[target_category]
ON
	[control_targets].[target_category_id] = [target_category].[target_category_id]
WHERE
	[control_targets].[lu_version_id] = (SELECT [lu_version_id] FROM [ref].[popsyn_run] WHERE [popsyn_run_id] = @popsyn_run_id)
GROUP BY
	[control_targets].[lu_version_id]
	,[target_category_col_nm]


IF((SELECT MAX([pct_diff_total]) FROM #temp_results WHERE [target_category_col_nm] IN ('Households', 'Non Institutional Group Quarters - Total', 'pop_non_gq')) >= 1)
BEGIN
	UPDATE [ref].[popsyn_run] SET [validated] = 'Households, gq, or non-gq population difference from target exceeds 1% regionally.' WHERE [popsyn_run_id] = @popsyn_run_id
	PRINT 'Households, gq, or non-gq population difference from target exceeds 1% regionally.'
END


IF((SELECT [validated] FROM [ref].[popsyn_run] WHERE [popsyn_run_id] = @popsyn_run_id) IS NULL)
UPDATE [ref].[popsyn_run] SET [validated] = 'valid' WHERE [popsyn_run_id] = @popsyn_run_id


SELECT 
	*
FROM 
	#temp_results
GO


-- Add metadata for [popsyn].[validator]
EXECUTE [db_meta].[add_xp] 'popsyn.validator', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn.validator', 'ms_description', 'stored procedure to generate validation results for a given popsyn run'
GO




/*****************************************************************************/
-- Functions


-- Create function to transform mgra control targets in [popsyn_input].[control_targets] from long to wide
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[popsyn_input].[control_targets_mgra]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION [popsyn_input].[control_targets_mgra]
GO

CREATE FUNCTION 
	[popsyn_input].[control_targets_mgra] 
(
	@lu_version_id tinyint
)

RETURNS @ret_controltargetsmgra TABLE
(
	[lu_version_id] smallint
	,[mgra] int
	,[taz] int
	,[puma] int
	,[region] int
	,[hh_sf] int
	,[hh_mf] int
	,[hh_mh] int
	,[hh] int
	,[gq_noninst] int
	,[gq_civ_college] int
	,[gq_mil] int
	,[pop_non_gq] int
	,PRIMARY KEY ([lu_version_id],[mgra])
)

BEGIN

DECLARE @minor_geography_type_id smallint = (SELECT [minor_geography_type_id] FROM [ref].[lu_version] WHERE [lu_version_id] = @lu_version_id)
DECLARE @mid_geography_type_id smallint = (SELECT [middle_geography_type_id] FROM [ref].[lu_version] WHERE [lu_version_id] = @lu_version_id)

INSERT INTO @ret_controltargetsmgra
SELECT
	tt2.[lu_version_id]
	,[mgra]
	,[taz]
	,[puma]
	,[region]
	,tt2.[hh_sf]
	,tt2.[hh_mf]
	,tt2.[hh_mh]
	,tt2.[hh]
	,tt2.[gq_noninst]
	,tt2.[gq_civ_college]
	,tt2.[gq_mil]
	,tt2.[pop_non_gq]
FROM (
	SELECT 
		[lu_version_id]
		,[minor_mid_xref].[child_zone] AS [mgra]
		,[minor_mid_xref].[parent_zone] AS [taz]
		,[mid_puma_xref].[parent_zone] AS [puma]
		,[mid_region_xref].[parent_zone] AS [region]
		,[target_category].[target_category_col_nm]
		,[value]
	FROM 
		[popsyn_input].[control_targets]
	INNER JOIN (
		SELECT
			[child_geography_zone_id]
			,[child_zone].[zone] AS [child_zone]
		    ,[parent_geography_zone_id]
			,[parent_zone].[zone] AS [parent_zone]
		FROM 
			[data_cafe].[ref].[geography_xref]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [child_zone]
		ON
			[geography_xref].[child_geography_zone_id] = [child_zone].[geography_zone_id]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [parent_zone]
		ON
			[geography_xref].[parent_geography_zone_id] = [parent_zone].[geography_zone_id]
		WHERE
			[child_zone].[geography_type_id] = @minor_geography_type_id
			AND [parent_zone].[geography_type_id] = @mid_geography_type_id
		) AS [minor_mid_xref]
	ON
		[control_targets].[geography_zone_id] = [minor_mid_xref].[child_geography_zone_id]
	INNER JOIN (
		SELECT
			[child_geography_zone_id]
			,[child_zone].[zone] AS [child_zone]
		    ,[parent_geography_zone_id]
			,[parent_zone].[zone] AS [parent_zone]
		FROM 
			[data_cafe].[ref].[geography_xref]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [child_zone]
		ON
			[geography_xref].[child_geography_zone_id] = [child_zone].[geography_zone_id]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [parent_zone]
		ON
			[geography_xref].[parent_geography_zone_id] = [parent_zone].[geography_zone_id]
		WHERE
			[child_zone].[geography_type_id] = @mid_geography_type_id
			AND [parent_zone].[geography_type_id] = 69 -- hardcoded puma_2000 geography_type_id
		) AS [mid_puma_xref]
	ON
		[minor_mid_xref].[parent_geography_zone_id] = [mid_puma_xref].[child_geography_zone_id]
	INNER JOIN (
		SELECT
			[child_geography_zone_id]
			,[child_zone].[zone] AS [child_zone]
		    ,[parent_geography_zone_id]
			,[parent_zone].[zone] AS [parent_zone]
		FROM 
			[data_cafe].[ref].[geography_xref]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [child_zone]
		ON
			[geography_xref].[child_geography_zone_id] = [child_zone].[geography_zone_id]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [parent_zone]
		ON
			[geography_xref].[parent_geography_zone_id] = [parent_zone].[geography_zone_id]
		WHERE
			[child_zone].[geography_type_id] = @mid_geography_type_id
			AND [parent_zone].[geography_type_id] = 4 -- hardcoded region_2004 geography_type_id
		) AS [mid_region_xref]
	ON
		[minor_mid_xref].[parent_geography_zone_id] = [mid_region_xref].[child_geography_zone_id]
	INNER JOIN
		[ref].[target_category]
	ON
		[control_targets].[target_category_id] = [target_category].[target_category_id]
	WHERE 
		[lu_version_id] = @lu_version_id
) tt1
PIVOT (
	SUM([value])
	FOR 
		[target_category_col_nm] 
	IN (
		[hh_sf]
		,[hh_mf]
		,[hh_mh]
		,[hh]
		,[gq_noninst]
		,[gq_civ_college]
		,[gq_mil]
		,[pop_non_gq]
		)
) tt2
RETURN
END
GO

-- Add metadata for [popsyn_input].[control_targets_mgra]
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets_mgra', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets_mgra', 'ms_description', 'function to create wide form mgra control targets for java program'
GO




-- Create function to transform taz control targets in [popsyn_input].[control_targets] from long to wide
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[popsyn_input].[control_targets_taz]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION [popsyn_input].[control_targets_taz]
GO

CREATE FUNCTION 
	[popsyn_input].[control_targets_taz] 
(
	@lu_version_id tinyint
)

RETURNS @ret_controltargetstaz TABLE
(
	[lu_version_id] smallint
	,[taz] int
	,[puma] int
	,[region] int
	,[hhwoc] int
	,[hhwc] int
	,[hhworkers0] int
	,[hhworkers1] int
	,[hhworkers2] int
	,[hhworkers3] int
	,[hh_income_cat_1] int
	,[hh_income_cat_2] int
	,[hh_income_cat_3] int
	,[hh_income_cat_4] int
	,[hh_income_cat_5] int
	,[male] int
	,[female] int
	,[age_0_17] int
	,[age_18_24] int
	,[age_25_34] int
	,[age_35_49] int
	,[age_50_64] int
	,[age_65_79] int
	,[age_80plus] int
	,[hisp] int
	,[nhw] int
	,[nhb] int
	,[nho_popsyn] int
	,[nha] int
	,PRIMARY KEY ([lu_version_id], [taz])
)

BEGIN

DECLARE @mid_geography_type_id smallint = (SELECT [middle_geography_type_id] FROM [ref].[lu_version] WHERE [lu_version_id] = @lu_version_id)

INSERT INTO @ret_controltargetstaz
SELECT
	tt2.[lu_version_id]
	,[taz]
	,[puma]
	,[region]
	,tt2.[hhwoc]
	,tt2.[hhwc]
	,tt2.[hhworkers0]
	,tt2.[hhworkers1]
	,tt2.[hhworkers2]
	,tt2.[hhworkers3]
	,tt2.[hh_income_cat_1]
	,tt2.[hh_income_cat_2]
	,tt2.[hh_income_cat_3]
	,tt2.[hh_income_cat_4]
	,tt2.[hh_income_cat_5]
	,ISNULL(tt2.[male], 0) AS [male]
	,ISNULL(tt2.[female], 0) AS [female]
	,ISNULL(tt2.[age_0_17], 0) AS [age_0_17]
	,ISNULL(tt2.[age_18_24], 0) AS [age_18_24]
	,ISNULL(tt2.[age_25_34], 0) AS [age_25_34]
	,ISNULL(tt2.[age_35_49], 0) AS [age_35_49]
	,ISNULL(tt2.[age_50_64], 0) AS [age_50_64]
	,ISNULL(tt2.[age_65_79], 0) AS [age_65_79]
	,ISNULL(tt2.[age_80plus], 0) AS [age_80plus]
	,ISNULL(tt2.[hisp], 0) AS [hisp]
	,ISNULL(tt2.[nhw], 0) AS [nhw]
	,ISNULL(tt2.[nhb], 0) AS [nhb]
	,ISNULL(tt2.[nho_popsyn], 0) AS [nho_popsyn]
	,ISNULL(tt2.[nha], 0) AS [nha]
FROM (
	SELECT 
		[lu_version_id]
		,[mid_puma_xref].[child_zone] AS [taz]
		,[mid_puma_xref].[parent_zone] AS [puma]
		,[mid_region_xref].[parent_zone] AS [region]
		,[target_category].[target_category_col_nm]
		,[value]
	FROM 
		[popsyn_input].[control_targets]
	INNER JOIN (
		SELECT
			[child_geography_zone_id]
			,[child_zone].[zone] AS [child_zone]
		    ,[parent_geography_zone_id]
			,[parent_zone].[zone] AS [parent_zone]
		FROM 
			[data_cafe].[ref].[geography_xref]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [child_zone]
		ON
			[geography_xref].[child_geography_zone_id] = [child_zone].[geography_zone_id]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [parent_zone]
		ON
			[geography_xref].[parent_geography_zone_id] = [parent_zone].[geography_zone_id]
		WHERE
			[child_zone].[geography_type_id] = @mid_geography_type_id
			AND [parent_zone].[geography_type_id] = 69 -- hardcoded puma_2000 geography_type_id
		) AS [mid_puma_xref]
	ON
		[control_targets].[geography_zone_id] = [mid_puma_xref].[child_geography_zone_id]
	INNER JOIN (
		SELECT
			[child_geography_zone_id]
			,[child_zone].[zone] AS [child_zone]
		    ,[parent_geography_zone_id]
			,[parent_zone].[zone] AS [parent_zone]
		FROM 
			[data_cafe].[ref].[geography_xref]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [child_zone]
		ON
			[geography_xref].[child_geography_zone_id] = [child_zone].[geography_zone_id]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [parent_zone]
		ON
			[geography_xref].[parent_geography_zone_id] = [parent_zone].[geography_zone_id]
		WHERE
			[child_zone].[geography_type_id] = @mid_geography_type_id
			AND [parent_zone].[geography_type_id] = 4 -- hardcoded region_2004 geography_type_id
		) AS [mid_region_xref]
	ON
		[control_targets].[geography_zone_id] = [mid_region_xref].[child_geography_zone_id]
	INNER JOIN
		[ref].[target_category]
	ON
		[control_targets].[target_category_id] = [target_category].[target_category_id]
	WHERE 
		[lu_version_id] = @lu_version_id
) tt1
PIVOT (
	SUM([value])
	FOR 
		[target_category_col_nm] 
	IN (
		[hhwoc]
		,[hhwc]
		,[hhworkers0]
		,[hhworkers1]
		,[hhworkers2]
		,[hhworkers3]
		,[hh_income_cat_1]
		,[hh_income_cat_2]
		,[hh_income_cat_3]
		,[hh_income_cat_4]
		,[hh_income_cat_5]
		,[male]
		,[female]
		,[age_0_17]
		,[age_18_24]
		,[age_25_34]
		,[age_35_49]
		,[age_50_64]
		,[age_65_79]
		,[age_80plus]
		,[hisp]
		,[nhw]
		,[nhb]
		,[nho_popsyn]
		,[nha]
		)
) tt2

RETURN
END 
GO

-- Add metadata for [popsyn_input].[control_targets_taz]
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets_taz', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets_taz', 'ms_description', 'function to create wide form taz control targets for java program'
GO




-- Create function to transform region control targets in [popsyn_input].[control_targets] from long to wide
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[popsyn_input].[control_targets_region]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION [popsyn_input].[control_targets_region]
GO

CREATE FUNCTION 
	[popsyn_input].[control_targets_region] 
(
	@lu_version_id tinyint
)
RETURNS TABLE AS

RETURN
(
SELECT
	tt2.[lu_version_id]
	,[zone] AS [region]
	,tt2.[pop_non_gq]
	,tt2.[gq_noninst]
FROM (
	SELECT 
		[lu_version_id]
		,[zone]
		,[target_category].[target_category_col_nm]
		,[value]
	FROM 
		[popsyn_input].[control_targets]
	INNER JOIN
		[data_cafe].[ref].[geography_zone]
	ON
		[control_targets].[geography_zone_id] = [geography_zone].[geography_zone_id]
	INNER JOIN
		[ref].[target_category]
	ON
		[control_targets].[target_category_id] = [target_category].[target_category_id]
	WHERE 
		[geography_type_id] = 4 -- San Diego Region harcoded
		AND [lu_version_id] = @lu_version_id
) tt1
PIVOT (
	SUM([value])
	FOR 
		[target_category_col_nm] 
	IN (
		[pop_non_gq]
		,[gq_noninst]
		)
) tt2
)
GO

-- Add metadata for [popsyn_input].[control_targets_region]
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets_region', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn_input.control_targets_region', 'ms_description', 'function to create wide form region control targets for java program'
GO




-- Create [popsyn].[households_file] to output households input file for the ABM model
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[popsyn].[households_file]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION [popsyn].[households_file]
GO


CREATE FUNCTION
	[popsyn].[households_file]
(
	@popsyn_run_id smallint
)
RETURNS @ret_households_file TABLE
(
	[hhid] [int] IDENTITY (1,1) NOT NULL,
	[household_serial_no] [bigint] NOT NULL,
	[taz] [int] NOT NULL,
	[mgra] [int] NOT NULL,
	[hinccat1] [tinyint] NOT NULL,
	[hinc] [int] NOT NULL,
	[hworkers] [tinyint] NOT NULL,
	[veh] [tinyint] NOT NULL,
	[persons] [tinyint] NOT NULL,
	[hht] [smallint] NOT NULL,
	[bldgsz] [smallint] NOT NULL,
	[unittype] [tinyint] NOT NULL,
	[popsyn_run_id] [tinyint] NOT NULL,
	[poverty] [decimal](7,4) NULL,
	[n] [int] NOT NULL, -- just used to create persons file, do not include when outputting csv
	[final_weight] [int] NOT NULL, -- just used to create persons file, do not include when outputting csv
	[synpop_hh_id] [int] NOT NULL -- just used to create persons file, do not include when outputting csv
)
AS
BEGIN

DECLARE @minor_geography_type_id smallint = (SELECT [minor_geography_type_id] FROM [ref].[lu_version] INNER JOIN [ref].[popsyn_run] ON [lu_version].[lu_version_id] = [popsyn_run].[lu_version_id]  WHERE [popsyn_run_id] = @popsyn_run_id)
DECLARE @middle_geography_type_id smallint = (SELECT [middle_geography_type_id] FROM [ref].[lu_version] INNER JOIN [ref].[popsyn_run] ON [lu_version].[lu_version_id] = [popsyn_run].[lu_version_id]  WHERE [popsyn_run_id] = @popsyn_run_id);

INSERT INTO @ret_households_file
SELECT
	[serialno] AS [household_serial_no]
	,[parent_zone] AS [taz]
	,[child_zone] AS [mgra]
	,[hinccat1] = CASE	WHEN [hh_income_adj] < 30000 THEN 1
						WHEN [hh_income_adj] >= 30000 AND [hh_income_adj] < 60000 THEN 2
						WHEN [hh_income_adj] >= 60000 AND [hh_income_adj] < 100000 THEN 3
						WHEN [hh_income_adj] >= 100000 AND [hh_income_adj] < 150000 THEN 4
						WHEN [hh_income_adj] >= 150000 THEN 5
						ELSE 1 -- set gq to 1 was how it was done traditionally, should probably be set to some null value but need to asses impact on model and reporting
						END
	,ROUND(ISNULL([hh_income_adj], 0),0) AS [hinc] -- group quarters are 0, see above note
	,[workers]
	,[veh]
	,[np] AS [persons]
	,[hht]
	,[bld]
	,[unittype] = CASE	WHEN [gq_type_id] = 0  THEN 0 -- households
						WHEN [gq_type_id] BETWEEN 1 AND 3 THEN 1 -- institutional group quarters
						ELSE NULL
						END -- we do not have any institutional group quarters
	,@popsyn_run_id AS [popsyn_run_id]
	,[poverty] = CASE	WHEN [gq_type_id] BETWEEN 1 AND 3 THEN -99 -- no income calculations for group quarters, set to this since abm model can't handle nulls
						WHEN [gq_type_id] = 0 THEN ROUND(1.0 * [hh_income_adj] /[income_threshold], 4)
						ELSE NULL
						END -- not actually used in model just for reporting purposes
	,[n]
	,[final_weight]
	,[synpop_hh_id]
FROM
	[popsyn].[synpop_hh]
INNER JOIN (
		SELECT
			[child_geography_zone_id]
			,[child_zone].[zone] AS [child_zone]
		    ,[parent_geography_zone_id]
			,[parent_zone].[zone] AS [parent_zone]
		FROM 
			[data_cafe].[ref].[geography_xref]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [child_zone]
		ON
			[geography_xref].[child_geography_zone_id] = [child_zone].[geography_zone_id]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [parent_zone]
		ON
			[geography_xref].[parent_geography_zone_id] = [parent_zone].[geography_zone_id]
		WHERE
			[child_zone].[geography_type_id] = @minor_geography_type_id
			AND [parent_zone].[geography_type_id] = @middle_geography_type_id
	) AS [minor_mid_xref]
ON
	[synpop_hh].[mgra] = [minor_mid_xref].[child_zone]
INNER JOIN
	[ref].[popsyn_run]
ON
	[synpop_hh].[popsyn_run_id] = [popsyn_run].[popsyn_run_id]
INNER JOIN -- need base acs hh data
	[popsyn_input].[hh]
ON
	[popsyn_run].[popsyn_data_source_id] = [hh].[popsyn_data_source_id]
	AND [synpop_hh].[hh_id] = [hh].[hh_id]
LEFT OUTER JOIN ( -- need base acs number of seniors in the household
	SELECT
		[popsyn_data_source_id]
		,[hh_id]
		,COUNT(*) AS [hh_age65plus]
	FROM
		[popsyn_input].[person]
	WHERE
		[AGEP] >= 65
	GROUP BY
		[popsyn_data_source_id]
		,[hh_id]
	) AS num_seniors
ON
	[popsyn_run].[popsyn_data_source_id] = num_seniors.[popsyn_data_source_id]
	AND [synpop_hh].[hh_id] = num_seniors.[hh_id]
INNER JOIN -- poverty calculation
	[ref].[fed_poverty_threshold_2010]
ON
	CASE	WHEN [hh].[np] > 9 THEN 9 
			ELSE [hh].[np] 
			END							= [fed_poverty_threshold_2010].[hh_persons]
	AND CASE	WHEN [hh].[hh_child] IS NULL THEN 0
				WHEN [hh].[hh_child] > 8 THEN 8 
				WHEN [hh].[np] = [hh].[hh_child] THEN [hh].[hh_child] - 1 -- all child households treated as one adult, rest children
				ELSE [hh].[hh_child] 
				END						= [fed_poverty_threshold_2010].[hh_children]
	AND CASE	WHEN num_seniors.[hh_age65plus] = 2 AND [hh].[NP] = 2 THEN 1
				WHEN (num_seniors.[hh_age65plus] > 0 AND [hh].[NP] > 2) OR num_seniors.[hh_age65plus] IS NULL THEN 0 -- quirk of how poverty is defined, doesn't care about seniors in 3+ person households
				ELSE num_seniors.[hh_age65plus]  
				END						= [fed_poverty_threshold_2010].[hh_age65plus]
INNER JOIN
	[ref].[expansion_numbers] -- expand households based on weight
ON
	[expansion_numbers].[n] BETWEEN 1 AND [synpop_hh].[final_weight]
WHERE
	[synpop_hh].[popsyn_run_id] = @popsyn_run_id

RETURN
END
GO


-- Add metadata for [popsyn].[households_file]
EXECUTE [db_meta].[add_xp] 'popsyn.households_file', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn.households_file', 'ms_description', 'function to output households input file for ABM'
GO




-- Create [popsyn].[persons_file] to output persons input file for the ABM model
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[popsyn].[persons_file]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION popsyn.[persons_file]
GO


CREATE FUNCTION
	[popsyn].[persons_file]
(
	@popsyn_run_id smallint
)
RETURNS @ret_persons_file TABLE
(
	[hhid] [int] NOT NULL,
	[perid] [int] IDENTITY(1,1) NOT NULL,
	[household_serial_no] [bigint] NOT NULL,
	[pnum] [tinyint] NOT NULL,
	[age] [tinyint] NOT NULL,
	[sex] [tinyint] NOT NULL,
	[military] [tinyint] NOT NULL,
	[pemploy] [tinyint] NOT NULL,
	[pstudent] [tinyint] NOT NULL,
	[ptype] [tinyint] NOT NULL,
	[educ] [tinyint] NOT NULL,
	[grade] [tinyint] NOT NULL,
	[occen5] [smallint] NOT NULL,
	[occsoc5] [nvarchar](15) NOT NULL,
	[indcen] [smallint] NOT NULL,
	[weeks] [tinyint] NOT NULL,
	[hours] [tinyint] NOT NULL,
	[rac1p] [tinyint] NOT NULL,
	[hisp] [tinyint] NOT NULL,
	[popsyn_run_id] [smallint] NOT NULL
)
AS
BEGIN

INSERT INTO @ret_persons_file
SELECT
	[hhid]
	,[serialno] AS [household_serial_no]
	,[sporder] AS [pnum]
	,[agep] AS [age]
	,[sex] AS [sex]
	,[military] = CASE	WHEN [mil] = 1 THEN 1
						WHEN [mil] IN (2,3) THEN 2
						WHEN [mil] = 4 THEN 3
						WHEN [mil] = 5 THEN 4
						ELSE 0
						END
	,[pemploy] = CASE	WHEN [esr] IN (1,2,4,5) AND [wkw] IN (1,2,3,4) AND [wkhp] >= 35 THEN 1
							WHEN [esr] IN (1,2,4,5) AND ([wkw] IN (5,6) OR [wkhp] < 35) THEN 2
							WHEN [esr] IN (3,6) THEN 3
							ELSE 4
							END
	,[pstudent] = CASE	WHEN [schg] > 0 AND [schg] < 6 THEN 1
							WHEN [schg] IN (6,7) THEN 2
							ELSE 3
							END
	-- order of ptype case statement matters
	,[ptype] = CASE	WHEN [agep] < 6 THEN 8
						WHEN [agep] >= 6 AND [agep] <= 15 THEN 7
						WHEN [esr] IN (1,2,4,5) AND [wkw] IN (1,2,3,4) AND [wkhp] >= 35 THEN 1 -- PEMPLOY = 1
						WHEN [schg] IN (6,7) OR ([agep]>=20 AND [schg] > 0 AND [schg] < 6) THEN 3 -- PSTUDENT = 2 OR AGEP >= 20 AND PSTUDENT = 1
						WHEN [schg] > 0 AND [schg] < 6 THEN 6 -- PSTUDENT = 1
						WHEN [esr] IN (1,2,4,5) AND ([wkw] IN (5,6) OR [wkhp] < 35) THEN 2 -- PEMPLOY = 2
						WHEN [agep] < 65 THEN 4
						ELSE 5
						END
	,[schl] AS [educ]
	,[schg] AS [grade]
	,[occen5] = CASE	WHEN [occp02] = 'N.A.' THEN [occp10]
						WHEN [occp02] = 'N.A.//' THEN [occp10]
						WHEN LEN([occp02]) = 0 OR LEN([occp10]) = 0 THEN 0 
						ELSE [occp02]
						END
	,[occsoc5] = CASE	WHEN [socp00] = 'N.A.' THEN LEFT([socp10], 2) + '-' + RIGHT([socp10], 4)
						WHEN [socp00] = 'N.A.//' THEN LEFT([socp10], 2) + '-' + RIGHT([socp10], 4)
						WHEN LEN([socp00]) = 6 THEN LEFT([socp00], 2) + '-' + RIGHT([socp00], 4)
						WHEN LEN([socp00]) =  0 OR LEN([socp10]) = 0 THEN '00-0000'
						ELSE [socp00]
						END
	,[indcen] = CASE	WHEN [indp02] = 'N.A.' THEN [indp07]
						WHEN [indp02] = 'N.A.//' THEN [indp07]
						WHEN [indp02] = 'N.A.' AND [indp07] = '6672' THEN '6675'
						WHEN len([indp02]) = 0 THEN 0
						WHEN len([indp07]) = 0 THEN 0            
						ELSE [indp02]
						END
	,[wkw] AS [weeks]
	,[wkhp] AS [hours]
	,[rac1p]
	,[hisp]
	,@popsyn_run_id AS [popsyn_run_id]
FROM
	[popsyn].[synpop_person]
INNER JOIN
	[popsyn].[synpop_hh]
ON
	[synpop_person].[popsyn_run_id] = [synpop_hh].[popsyn_run_id]
	AND [synpop_person].[synpop_hh_id] = [synpop_hh].[synpop_hh_id]
INNER JOIN
	[ref].[popsyn_run]
ON
	[synpop_person].[popsyn_run_id] = [popsyn_run].[popsyn_run_id]
INNER JOIN
	[popsyn_input].[person]
ON
	[popsyn_run].[popsyn_data_source_id] = [person].[popsyn_data_source_id]
	AND [synpop_person].[person_id] = [person].[person_id]
INNER JOIN
	[ref].[expansion_numbers] -- expand households based on weight
ON
	[expansion_numbers].[n] BETWEEN 1 AND [synpop_hh].[final_weight]
INNER JOIN
	[popsyn].[households_file](@popsyn_run_id)
ON
	[synpop_person].[synpop_hh_id] = [households_file].[synpop_hh_id]
	AND [expansion_numbers].[n] = [households_file].[n]
WHERE
	[synpop_person].[popsyn_run_id] = @popsyn_run_id
ORDER BY
	[hhid]

RETURN
END
GO


-- Add metadata for [popsyn].[persons_file]
EXECUTE [db_meta].[add_xp] 'popsyn.persons_file', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn.persons_file', 'ms_description', 'function to output persons input file for ABM'
GO




-- Create [popsyn].[synpop_target_category_results] to output the synthetic population's results by target category and geography
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[popsyn].[synpop_target_category_results]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION [popsyn].[synpop_target_category_results]
GO


CREATE FUNCTION
	[popsyn].[synpop_target_category_results]
(
	@popsyn_run_id smallint
	,@geography_type_id int
)
RETURNS @ret_synpop_target_category_results TABLE
(
	[lu_version_id] [smallint] NOT NULL
	,[target_category_id] [smallint] NOT NULL
	,[geography_zone_id] [int] NOT NULL
	,[value] [int] NOT NULL
	,PRIMARY KEY ([lu_version_id],[target_category_id],[geography_zone_id])
)
AS
BEGIN

DECLARE @minor_geography_type_id smallint = (SELECT [minor_geography_type_id] FROM [ref].[lu_version] INNER JOIN [ref].[popsyn_run] ON [lu_version].[lu_version_id] = [popsyn_run].[lu_version_id]  WHERE [popsyn_run_id] = @popsyn_run_id)

INSERT INTO @ret_synpop_target_category_results
-- household results
SELECT
	[lu_version_id]
	,[target_category_id]
	,[parent_geography_zone_id]
	,[value]
FROM (
	SELECT
		[lu_version_id]
		,[parent_geography_zone_id]
		,SUM([final_weight]) AS [hh]
		,SUM([final_weight] * CASE WHEN [hh_type_id] = 1 THEN 1 ELSE 0 END) AS [hh_sf]
		,SUM([final_weight] * CASE WHEN [hh_type_id] = 2 THEN 1 ELSE 0 END) AS [hh_mf]
		,SUM([final_weight] * CASE WHEN [hh_type_id] = 3 THEN 1 ELSE 0 END) AS [hh_mh]
		,SUM([final_weight] * CASE WHEN [hh_child] = 0 THEN 1 ELSE 0 END) AS [hhwoc]
		,SUM([final_weight] * CASE WHEN [hh_child] = 1 THEN 1 ELSE 0 END) AS [hhwc]
		,SUM([final_weight] * CASE WHEN [workers] = 0 THEN 1 ELSE 0 END) AS [hhworkers0]
		,SUM([final_weight] * CASE WHEN [workers] = 1 THEN 1 ELSE 0 END) AS [hhworkers1]
		,SUM([final_weight] * CASE WHEN [workers] = 2 THEN 1 ELSE 0 END) AS [hhworkers2]
		,SUM([final_weight] * CASE WHEN [workers] >= 3 THEN 1 ELSE 0 END) AS [hhworkers3]
		,SUM([final_weight] * CASE WHEN [hh_income_adj] < 30000 THEN 1 ELSE 0 END) AS [hh_income_cat_1]
		,SUM([final_weight] * CASE WHEN [hh_income_adj] >= 30000 AND [hh_income_adj] < 60000 THEN 1 ELSE 0 END) AS [hh_income_cat_2]
		,SUM([final_weight] * CASE WHEN [hh_income_adj] >= 60000 AND [hh_income_adj] < 100000 THEN 1 ELSE 0 END) AS [hh_income_cat_3]
		,SUM([final_weight] * CASE WHEN [hh_income_adj] >= 100000 AND [hh_income_adj] < 150000 THEN 1 ELSE 0 END) AS [hh_income_cat_4]
		,SUM([final_weight] * CASE WHEN [hh_income_adj] >= 150000 THEN 1 ELSE 0 END) AS [hh_income_cat_5]
	FROM
		[popsyn].[synpop_hh]
	INNER JOIN
		[ref].[popsyn_run]
	ON
		[synpop_hh].[popsyn_run_id] = [popsyn_run].[popsyn_run_id]
	INNER JOIN
		[popsyn_input].[hh]
	ON
		[popsyn_run].[popsyn_data_source_id] = [hh].[popsyn_data_source_id]
		AND [synpop_hh].[hh_id] = [hh].[hh_id]
	INNER JOIN (
		SELECT
			[child_geography_zone_id]
			,[child_zone].[zone] AS [child_zone]
		    ,[parent_geography_zone_id]
			,[parent_zone].[zone] AS [parent_zone]
		FROM 
			[data_cafe].[ref].[geography_xref]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [child_zone]
		ON
			[geography_xref].[child_geography_zone_id] = [child_zone].[geography_zone_id]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [parent_zone]
		ON
			[geography_xref].[parent_geography_zone_id] = [parent_zone].[geography_zone_id]
		WHERE
			[child_zone].[geography_type_id] = @minor_geography_type_id
			AND [parent_zone].[geography_type_id] = @geography_type_id
	) AS [minor_geotype_xref]
	ON
		[synpop_hh].[mgra] = [minor_geotype_xref].[child_zone]
	WHERE
		[synpop_hh].[popsyn_run_id] = @popsyn_run_id
		AND [popsyn_run].[popsyn_run_id] = @popsyn_run_id
		AND [gq_type_id] = 0
	GROUP BY
		[lu_version_id]
		,[parent_geography_zone_id]
	) AS hh_popsyn
UNPIVOT (
	[value]
	FOR [target_col_nm] IN (
		[hh]
		,[hh_sf]
		,[hh_mf]
		,[hh_mh]
		,[hhwoc]
		,[hhwc]
		,[hhworkers0]
		,[hhworkers1]
		,[hhworkers2]
		,[hhworkers3]
		,[hh_income_cat_1]
		,[hh_income_cat_2]
		,[hh_income_cat_3]
		,[hh_income_cat_4]
		,[hh_income_cat_5]
		)
	) tt1
INNER JOIN
	[ref].[target_category]
ON
	tt1.[target_col_nm] = [target_category].[target_category_col_nm]

UNION ALL

-- person results
SELECT
	[lu_version_id]
	,[target_category_id]
	,[parent_geography_zone_id]
	,[value]
FROM (
	SELECT
		[lu_version_id]
		,[parent_geography_zone_id]
		,SUM([synpop_hh].[final_weight]) AS [pop_non_gq]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [sex] = 1 THEN 1 ELSE 0 END) AS [male]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [sex] = 2 THEN 1 ELSE 0 END) AS [female]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [agep] <= 17 THEN 1 ELSE 0 END) AS [age_0_17]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [agep] BETWEEN 18 AND 24 THEN 1 ELSE 0 END) AS [age_18_24]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [agep] BETWEEN 25 AND 34 THEN 1 ELSE 0 END) AS [age_25_34]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [agep] BETWEEN 35 AND 49 THEN 1 ELSE 0 END) AS [age_35_49]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [agep] BETWEEN 50 AND 64 THEN 1 ELSE 0 END) AS [age_50_64]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [agep] BETWEEN 65 AND 79 THEN 1 ELSE 0 END) AS [age_65_79]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [agep] >= 80 THEN 1 ELSE 0 END) AS [age_80plus]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [popsyn_race_id] = 1 THEN 1 ELSE 0 END) AS [hisp]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [popsyn_race_id] = 2 THEN 1 ELSE 0 END) AS [nhw]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [popsyn_race_id] = 3 THEN 1 ELSE 0 END) AS [nhb]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [popsyn_race_id] = 4 THEN 1 ELSE 0 END) AS [nho_popsyn]
		,SUM([synpop_hh].[final_weight] * CASE WHEN [popsyn_race_id] = 5 THEN 1 ELSE 0 END) AS [nha]
	FROM
		[popsyn].[synpop_person]
	INNER JOIN
		[ref].[popsyn_run]
	ON
		[synpop_person].[popsyn_run_id] = [popsyn_run].[popsyn_run_id]
	INNER JOIN
		[popsyn].[synpop_hh]
	ON
		[synpop_person].[popsyn_run_id] = [synpop_hh].[popsyn_run_id]
		AND [synpop_person].[synpop_hh_id] = [synpop_hh].[synpop_hh_id]
	INNER JOIN
		[popsyn_input].[person]
	ON
		[popsyn_run].[popsyn_data_source_id] = [person].[popsyn_data_source_id]
		AND [synpop_person].[person_id] = [person].[person_id]
	INNER JOIN (
		SELECT
			[child_geography_zone_id]
			,[child_zone].[zone] AS [child_zone]
		    ,[parent_geography_zone_id]
			,[parent_zone].[zone] AS [parent_zone]
		FROM 
			[data_cafe].[ref].[geography_xref]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [child_zone]
		ON
			[geography_xref].[child_geography_zone_id] = [child_zone].[geography_zone_id]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [parent_zone]
		ON
			[geography_xref].[parent_geography_zone_id] = [parent_zone].[geography_zone_id]
		WHERE
			[child_zone].[geography_type_id] = @minor_geography_type_id
			AND [parent_zone].[geography_type_id] = @geography_type_id
	) AS [minor_geotype_xref]
	ON
		[synpop_hh].[mgra] = [minor_geotype_xref].[child_zone]
	WHERE
		[synpop_person].[popsyn_run_id] = @popsyn_run_id
		AND [synpop_hh].[popsyn_run_id] = @popsyn_run_id
		AND [popsyn_run].[popsyn_run_id] = @popsyn_run_id
		AND [gq_type_id] = 0
	GROUP BY
		[lu_version_id]
		,[parent_geography_zone_id]
	) AS person_popsyn
UNPIVOT (
	[value]
	FOR [target_col_nm] IN (
		[pop_non_gq]
		,[male]
		,[female]
		,[age_0_17]
		,[age_18_24]
		,[age_25_34]
		,[age_35_49]
		,[age_50_64]
		,[age_65_79]
		,[age_80plus]
		,[hisp]
		,[nhw]
		,[nhb]
		,[nho_popsyn]
		,[nha]
		)
	) tt2
INNER JOIN
	[ref].[target_category]
ON
	tt2.[target_col_nm] = [target_category].[target_category_col_nm]

UNION ALL

-- gq results
SELECT
	[lu_version_id]
	,[target_category_id]
	,[parent_geography_zone_id]
	,[value]
FROM (
	SELECT
		[lu_version_id]
		,[parent_geography_zone_id]
		,SUM([final_weight]) AS [gq_noninst]
		,SUM([final_weight] * CASE WHEN [gq_type_id] = 1 THEN 1 ELSE 0 END) AS [gq_civ_college]
		,SUM([final_weight] * CASE WHEN [gq_type_id] = 2 THEN 1 ELSE 0 END) AS [gq_mil]
	FROM
		[popsyn].[synpop_hh]
	INNER JOIN
		[ref].[popsyn_run]
	ON
		[synpop_hh].[popsyn_run_id] = [popsyn_run].[popsyn_run_id]
	INNER JOIN
		[popsyn_input].[hh]
	ON
		[popsyn_run].[popsyn_data_source_id] = [hh].[popsyn_data_source_id]
		AND [synpop_hh].[hh_id] = [hh].[hh_id]
	INNER JOIN (
		SELECT
			[child_geography_zone_id]
			,[child_zone].[zone] AS [child_zone]
		    ,[parent_geography_zone_id]
			,[parent_zone].[zone] AS [parent_zone]
		FROM 
			[data_cafe].[ref].[geography_xref]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [child_zone]
		ON
			[geography_xref].[child_geography_zone_id] = [child_zone].[geography_zone_id]
		INNER JOIN
			[data_cafe].[ref].[geography_zone] AS [parent_zone]
		ON
			[geography_xref].[parent_geography_zone_id] = [parent_zone].[geography_zone_id]
		WHERE
			[child_zone].[geography_type_id] = @minor_geography_type_id
			AND [parent_zone].[geography_type_id] = @geography_type_id
	) AS [minor_geotype_xref]
	ON
		[synpop_hh].[mgra] = [minor_geotype_xref].[child_zone]
	WHERE
		[synpop_hh].[popsyn_run_id] = @popsyn_run_id
		AND [popsyn_run].[popsyn_run_id] = @popsyn_run_id
		AND [gq_type_id] > 0 -- group quarter households
	GROUP BY
		[lu_version_id]
		,[parent_geography_zone_id]
	) AS hh_popsyn
UNPIVOT (
	[value]
	FOR [target_col_nm] IN (
		[gq_noninst]
		,[gq_civ_college]
		,[gq_mil]
		)
	) tt1
INNER JOIN
	[ref].[target_category]
ON
	tt1.[target_col_nm] = [target_category].[target_category_col_nm]

RETURN
END
GO


-- Add metadata for [popsyn].[synpop_target_category_results]
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_target_category_results', 'subsystem', 'popsyn'
EXECUTE [db_meta].[add_xp] 'popsyn.synpop_target_category_results', 'ms_description', 'function to output popsyn results by target category at a specified geography'
GO